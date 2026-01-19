package threeadd.packetEventsSK.element.team.expressions.prop;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import threeadd.packetEventsSK.element.team.api.FakeTeam;
import threeadd.packetEventsSK.util.expressions.CustomPropertyExpression;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@SuppressWarnings("unused")
@Name("Fake Team - Team Entities")
@Description("Represents the entities within a fake team")
@Example("""
        command glowGreen:
            trigger:
                set glowing state of player to true for player
                create new fake team named player's name for players:
                    set the fake team color of the fake team to green
                    add player to fake team entities of the fake team
        """)
@Since("1.0.0")
public class FakeTeamEntitiesProp extends CustomPropertyExpression<FakeTeam, Entity> {

    static {
        PropertyExpression.register(FakeTeamEntitiesProp.class, Entity.class, "fake[ ]team[ ]entities", "faketeam");
    }

    public FakeTeamEntitiesProp() {
        super(Entity.class, FakeTeam.class, false);
    }

    @Override
    protected boolean initialize(SkriptParser.ParseResult parseResult) {
        return true;
    }

    @Override
    protected List<Entity> getMany(Event event) {
        FakeTeam team = getValueOrNull(0, FakeTeam.class, event);
        if (team == null) return null;

        return team.getEntities().stream()
                .map(id -> {
                    if (id.length() == 36) {
                        return Bukkit.getEntity(UUID.fromString(id));
                    } else {
                        return Bukkit.getPlayer(id);
                    }
                })
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
            return CollectionUtils.array(Entity[].class);

        return null;
    }

    @Override
    public void change(Event event, Object @Nullable [] delta, Changer.ChangeMode mode) {
        if (delta == null) return;

        List<FakeTeam> teams = getValuesOrNull(0, FakeTeam.class, event);
        List<Entity> entities = getDeltaValuesOrNull(delta, Entity.class);

        if (teams == null || entities == null) return;

        for (FakeTeam team : teams)
            modify(team, mode, entities);
    }

    private static void modify(FakeTeam team, Changer.ChangeMode mode, List<Entity> entities) {

        String[] entityIds = entities.stream()
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
}
