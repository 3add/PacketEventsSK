package dev.threeadd.packeteventssk.element.team.expressions.prop;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.github.shanebeee.skr.Registration;
import dev.threeadd.packeteventssk.api.team.FakeTeam;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

public class ExprFakeTeamReceivers extends PropertyExpression<FakeTeam, Player> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprFakeTeamReceivers.class, Player.class, "fake[ ]team viewers", "faketeam")
                .name("Fake Team - Team Receivers")
                .description("""
                        Represents the packet receivers of a fake team
                        If you're not within this list, the team properties will be ignored by your client
                        """)
                .examples("""
                        command glowGreen:
                            trigger:
                                set glowing state of player to true for player
                                create new fake team named player's name:
                                    add players to fake team viewers of the fake team
                                    set the fake team color of the fake team to green
                                    add player to fake team entities of the fake team
                        """)
                .since("1.0.0")
                .register();
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        setExpr((Expression<? extends FakeTeam>) expressions[0]);
        return true;
    }

    @Override
    protected Player[] get(Event event, FakeTeam[] teams) {
        return Arrays.stream(teams)
                .flatMap(team -> team.getViewingPlayers().stream())
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .distinct()
                .toArray(Player[]::new);
    }

    @Override
    public Class<?> @Nullable [] acceptChange(Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.ADD
                || mode == Changer.ChangeMode.REMOVE
                || mode == Changer.ChangeMode.SET
                || mode == Changer.ChangeMode.RESET
                || mode == Changer.ChangeMode.REMOVE_ALL) {
            return CollectionUtils.array(Player[].class);
        }
        return null;
    }

    @Override
    public void change(Event event, Object @Nullable [] delta, Changer.ChangeMode mode) {
        FakeTeam[] teams = getExpr().getAll(event);

        Player[] newPlayers;

        if (mode == Changer.ChangeMode.RESET || mode == Changer.ChangeMode.REMOVE_ALL) {
            newPlayers = new Player[0];
        } else {
            if (delta == null || delta.length != 1 || !(delta[0] instanceof Player[])) {
                throw new IllegalStateException("Unexpected delta value " + (delta != null ? Arrays.toString(delta) : "none"));
            }

            newPlayers = (Player[]) delta[0];
        }

        for (FakeTeam team : teams) {
            modify(team, mode, newPlayers);
        }
    }

    private static void modify(FakeTeam team, Changer.ChangeMode mode, Player[] players) {

        UUID[] uuids = Arrays.stream(players).map(Player::getUniqueId)
                .toList().toArray(new UUID[0]);

        switch (mode) {
            case RESET, REMOVE_ALL -> team.clearViewers();
            case ADD -> team.addViewers(uuids);

            case SET -> {
                team.clearViewers();
                team.addViewers(uuids);
            }

            case REMOVE -> {
                for (Player player : players)
                    team.removeViewer(player.getUniqueId());
            }
        }
    }

    @Override
    public Class<? extends Player> getReturnType() {
        return Player.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        String team = getExpr().toString(event, debug);
        return String.format("fake team viewers of %s", team);
    }
}
