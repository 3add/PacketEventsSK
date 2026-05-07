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
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class ExprFakeTeamEntities extends PropertyExpression<FakeTeam, Entity> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprFakeTeamEntities.class, Entity.class, "fake[ ]team entities", "faketeam")
                .name("Fake Team - Team Entities")
                .description("Represents the entities within a fake team")
                .examples("""
                        command glowGreen:
                            trigger:
                                set glowing state of player to true for player
                                create new fake team named player's name for players:
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
    protected Entity[] get(Event event, FakeTeam[] source) {
        FakeTeam[] teams = getExpr().getAll(event);

        return Arrays.stream(teams)
                .flatMap(team -> team.getEntities().stream())
                .map(id -> {
                    if (id.length() == 36) {
                        return Bukkit.getEntity(UUID.fromString(id));
                    } else {
                        return Bukkit.getPlayer(id);
                    }
                })
                .filter(Objects::nonNull)
                .distinct()
                .toArray(Entity[]::new);
    }

    @Override
    public Class<?> @Nullable [] acceptChange(Changer.ChangeMode mode) {
        if (mode.equals(Changer.ChangeMode.ADD)
                || mode.equals(Changer.ChangeMode.REMOVE)
                || mode.equals(Changer.ChangeMode.SET)
                || mode.equals(Changer.ChangeMode.RESET)
                || mode == Changer.ChangeMode.REMOVE_ALL)
            return CollectionUtils.array(Entity[].class);

        return null;
    }

    @Override
    public void change(Event event, Object @Nullable [] delta, Changer.ChangeMode mode) {
        FakeTeam[] teams = getExpr().getAll(event);

        if (delta == null || delta.length != 1 || !(delta[0] instanceof Entity[] newEntities)) {
            throw new IllegalStateException("Unexpected delta value " + (delta != null ? Arrays.toString(delta) : "none"));
        }

        for (FakeTeam team : teams) {
            modify(team, mode, newEntities);
        }
    }

    private static void modify(FakeTeam team, Changer.ChangeMode mode, Entity... entities) {
        String[] entityIds = Arrays.stream(entities)
                .map(entity -> {
                    if (entity instanceof Player player)
                        return player.getName();
                    else
                        return entity.getUniqueId().toString();
                })
                .toList().toArray(new String[0]);

        switch (mode) {
            case RESET, REMOVE_ALL -> clearEntries(team);
            case ADD -> team.addEntities(entityIds);

            case SET -> {
                team.clearEntities();
                team.addEntities(entityIds);
            }

            case REMOVE -> team.removeEntity(entityIds);
        }
    }

    private static void clearEntries(FakeTeam team) {
        for (String entityId : Set.copyOf(team.getEntities()))
            team.removeEntity(entityId);
    }

    @Override
    public Class<? extends Entity> getReturnType() {
        return Entity.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        String target = getExpr().toString(event, debug);
        return String.format("fake team entities of %s", target);
    }
}
