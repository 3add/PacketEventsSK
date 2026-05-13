package dev.threeadd.packeteventssk.element.entity.expressions.prop;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.github.shanebeee.skr.Registration;
import me.tofaa.entitylib.wrapper.WrapperEntity;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ExprFakeEntityViewers extends PropertyExpression<WrapperEntity, Player> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprFakeEntityViewers.class, Player.class, "fake [entity] viewers", "fakeentity")
                .name("Fake Entity - Fake Entity Viewers")
                .description("The players who can actively see a fake entity")
                .examples("""
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
                .since("1.0.0")
                .register();
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        setExpr((Expression<? extends WrapperEntity>) expressions[0]);
        return true;
    }

    @Override
    protected Player[] get(Event event, WrapperEntity[] source) {
        return Arrays.stream(source)
                .flatMap(entity -> entity.getViewers().stream())
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .toArray(Player[]::new);
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class<? extends Player> getReturnType() {
        return Player.class;
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
        if (delta == null || delta.length == 0) return;

        WrapperEntity[] entities = getExpr().getAll(event);
        List<UUID> playerIds = Arrays.stream(delta)
                .filter(Player.class::isInstance)
                .map(p -> ((Player) p).getUniqueId())
                .toList();

        for (WrapperEntity entity : entities) {
            modify(entity, mode, playerIds);
        }
    }

    private static void modify(WrapperEntity entity, Changer.ChangeMode mode, List<UUID> playerIds) {
        switch (mode) {
            case RESET, REMOVE_ALL -> clearViewers(entity);
            case ADD -> playerIds.forEach(entity::addViewer);
            case SET -> {
                clearViewers(entity);
                playerIds.forEach(entity::addViewer);
            }
            case REMOVE -> playerIds.forEach(entity::removeViewer);
        }
    }

    private static void clearViewers(WrapperEntity entity) {
        Set.copyOf(entity.getViewers()).forEach(entity::removeViewer);
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        String entity = getExpr().toString(event, debug);
        return String.format("fake entity viewers of %s", entity);
    }
}
