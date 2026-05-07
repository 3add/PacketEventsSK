package dev.threeadd.packeteventssk.element.simple.expression;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.github.shanebeee.skr.Registration;
import dev.threeadd.packeteventssk.api.simple.GlowingEntityManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ExprGlowReceivers extends PropertyExpression<Entity, Player> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprGlowReceivers.class, Player.class, "glow[ing] [entity][ ]viewers", "entities")
                .name("Simple Glow - Glow Entity Viewers")
                .description("The players that are able to see the entity as glowing.")
                .examples("""
                        command glowMeForMe:
                            trigger:
                                set glow state of player to true for player
                                if glow entity viewers of player contains player:
                                    send "Hey? It worked!"
                        """)
                .since("1.0.0")
                .register();
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        setExpr((Expression<? extends Entity>) expressions[0]);
        return true;
    }

    @Override
    protected Player[] get(Event event, Entity[] source) {
        List<Player> viewers = new ArrayList<>();

        for (Entity entity : source) {
            if (entity == null) continue;

            GlowingEntityManager.getGlowingReceivers(entity.getEntityId()).stream()
                    .map(Bukkit::getPlayer)
                    .filter(Objects::nonNull)
                    .forEach(viewers::add);
        }

        return viewers.toArray(new Player[0]);
    }

    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET
                || mode == ChangeMode.ADD
                || mode == ChangeMode.REMOVE
                || mode == ChangeMode.RESET
                || mode == ChangeMode.REMOVE_ALL) {
            return CollectionUtils.array(Player[].class);
        }
        return null;
    }

    @Override
    public void change(Event event, Object @Nullable [] delta, ChangeMode mode) {
        Entity[] entities = getExpr().getAll(event);
        if (entities == null || entities.length == 0) return;

        List<UUID> receivers = new ArrayList<>();
        if (delta != null && (mode == ChangeMode.SET || mode == ChangeMode.ADD || mode == ChangeMode.REMOVE)) {
            for (Object obj : delta) {
                if (obj instanceof Player player) {
                    receivers.add(player.getUniqueId());
                }
            }
        }

        for (Entity entity : entities) {
            if (entity == null) continue;
            int entityId = entity.getEntityId();

            switch (mode) {
                case SET -> GlowingEntityManager.setGlowingReceivers(entityId, receivers);
                case ADD -> GlowingEntityManager.addGlowingReceivers(entityId, receivers);
                case REMOVE -> GlowingEntityManager.removeGlowingReceivers(entityId, receivers);
                case RESET, REMOVE_ALL -> GlowingEntityManager.clearGlowingReceivers(entityId);
            }
        }
    }

    @Override
    public Class<? extends Player> getReturnType() {
        return Player.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        String entity = getExpr().toString(event, debug);
        return String.format("glowing entity viewers of %s", entity);
    }
}