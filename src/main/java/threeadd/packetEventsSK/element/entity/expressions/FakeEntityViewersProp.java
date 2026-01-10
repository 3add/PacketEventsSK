package threeadd.packetEventsSK.element.entity.expressions;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.coll.CollectionUtils;
import me.tofaa.entitylib.wrapper.WrapperEntity;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import threeadd.packetEventsSK.util.expressions.CustomPropertyExpression;

import java.util.*;

@SuppressWarnings("unused")
@Name("Fake Entity - Fake Entity Viewers")
@Description("The players who can actively see a fake entity")
@Example("""
        command test:
            trigger:
                spawn a new fake cow entity at player for players and store it in {-cow}

        command onlyMe:
            trigger:
                loop fake entity viewers of {-cow}:
                    if loop-value is player:
                        continue

                    send "You can't view him!" to loop-value
                    remove loop-value from fake entity viewers of {-cow}
        """)
public class FakeEntityViewersProp extends CustomPropertyExpression<WrapperEntity, Player> {

    static {
        PropertyExpression.register(FakeEntityViewersProp.class, Player.class, "fake[ ]entity[ ]viewers", "fakeentity");
    }

    @SuppressWarnings("unused")
    public FakeEntityViewersProp() {
        super(Player.class, WrapperEntity.class, false);
    }

    @Override
    protected boolean initialize(SkriptParser.ParseResult parseResult) {
        return true;
    }

    @Override
    protected @Nullable List<Player> getMany(Event currentEvent) {
        List<WrapperEntity> source = getValues(0, WrapperEntity.class, currentEvent);

        List<Player> players = new ArrayList<>();
        for (WrapperEntity entity : source) {
            entity.getViewers().stream()
                    .map(Bukkit::getPlayer)
                    .forEach(players::add);
        }

        return players;
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

        List<WrapperEntity> entities = getValuesOrNull(0, WrapperEntity.class, event);
        List<Player> players = getDeltaValuesOrNull(delta, Player.class);;

        if (entities == null || players == null) return;

        for (WrapperEntity entity : entities)
            modify(entity, mode, players);
    }

    private static void modify(WrapperEntity entity, Changer.ChangeMode mode, List<Player> players) {
        switch (mode) {
            case RESET, REMOVE_ALL -> clearViewers(entity);
            case ADD -> {
                for (Player player : players)
                    entity.addViewer(player.getUniqueId());
            }

            case SET -> {
                clearViewers(entity);
                for (Player player : players)
                    entity.addViewer(player.getUniqueId());
            }

            case REMOVE -> {
                for (Player player : players)
                    entity.removeViewer(player.getUniqueId());
            }
        }
    }

    private static void clearViewers(WrapperEntity entity) {
        for (UUID viewerId : Set.copyOf(entity.getViewers()))
            entity.removeViewer(viewerId);
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "fake entity viewers of fake entity";
    }
}
