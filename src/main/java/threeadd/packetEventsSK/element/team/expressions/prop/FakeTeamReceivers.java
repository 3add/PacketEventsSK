package threeadd.packetEventsSK.element.team.expressions.prop;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import threeadd.packetEventsSK.element.team.api.FakeTeam;
import threeadd.packetEventsSK.util.expressions.CustomPropertyExpression;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@SuppressWarnings("unused")
@Name("Fake Team - Team Receivers")
@Description("""
        Represents the packet receivers of a fake team
        If you're not within this list, the team properties will be ignored by your client
        """)
@Example("""
        command glowGreen:
            trigger:
                set glowing state of player to true for player
                create new fake team named player's name:
                    add players to fake team viewers of the fake team
                    set the fake team color of the fake team to green
                    add player to fake team entities of the fake team
        """)
public class FakeTeamReceivers extends CustomPropertyExpression<FakeTeam, Player> {

    static {
        PropertyExpression.register(FakeTeamReceivers.class, Player.class, "fake[ ]team[ ]viewers", "faketeam");
    }

    @SuppressWarnings("unused")
    public FakeTeamReceivers() {
        super(Player.class, FakeTeam.class, false);
    }

    @Override
    protected boolean initialize(SkriptParser.ParseResult parseResult) {
        return true;
    }

    @Override
    protected @Nullable List<Player> getMany(Event currentEvent) {
        List<FakeTeam> source = getValues(0, FakeTeam.class, currentEvent);

        return source.stream()
                .flatMap(team -> team.getViewingPlayers().stream())
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
    }

    @Override
    public Class<?> @Nullable [] acceptChange(Changer.ChangeMode mode) {
        if (mode.equals(Changer.ChangeMode.ADD)
                || mode.equals(Changer.ChangeMode.REMOVE)
                || mode.equals(Changer.ChangeMode.SET)
                || mode.equals(Changer.ChangeMode.RESET)
                || mode == Changer.ChangeMode.REMOVE_ALL)
            return CollectionUtils.array(Player[].class);

        return null;
    }

    @Override
    public void change(Event event, Object @Nullable [] delta, Changer.ChangeMode mode) {
        if (delta == null) return;

        List<FakeTeam> teams = getValuesOrNull(0, FakeTeam.class, event);
        List<Player> players = getDeltaValuesOrNull(delta, Player.class);

        if (teams == null || players == null) return;

        for (FakeTeam team : teams)
            modify(team, mode, players);
    }

    private static void modify(FakeTeam team, Changer.ChangeMode mode, List<Player> players) {

        UUID[] uuids = players.stream().map(Player::getUniqueId)
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
    public String toString(@Nullable Event event, boolean debug) {
        return "fake entity viewers of fake entity";
    }
}
