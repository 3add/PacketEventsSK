package threeadd.packetEventsSK.element.simple.expression;

import ch.njol.skript.classes.Changer.ChangeMode;
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
import threeadd.packetEventsSK.element.simple.api.GlowingEntityManager;
import threeadd.packetEventsSK.util.expressions.CustomPropertyExpression;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("unused")
@Name("Simple Glow - Glow Entity Viewers")
@Description("The players that are able to see the entity as glowing.")
@Example("""
        command glowMeForMe:
            trigger:
                set glow state of player to true for player
                if glow entity viewers of player contains player:
                    send "Hey? It worked!"
        """)
@Since("1.0.0")
public class GlowingReceiversExpr extends CustomPropertyExpression<Entity, Player> {

    static {
        PropertyExpression.register(GlowingReceiversExpr.class, Player.class,
                "[the] glow[ing][ ][entity][ ]viewers", "entity");
    }

    public GlowingReceiversExpr() {
        super(Player.class, Entity.class, false);
    }

    @Override
    protected boolean initialize(SkriptParser.ParseResult parseResult) {
        return true;
    }

    @Override
    protected @Nullable List<Player> getProperties(Entity input) {
        return GlowingEntityManager.getGlowingReceivers(input.getEntityId()).stream()
                .map(Bukkit::getPlayer)
                .toList();
    }

    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        if (mode.equals(ChangeMode.SET)
                || mode.equals(ChangeMode.ADD)
                || mode.equals(ChangeMode.REMOVE)
                || mode.equals(ChangeMode.RESET)
                || mode.equals(ChangeMode.REMOVE_ALL))
            return CollectionUtils.array(Entity[].class);

        return null;
    }

    @Override
    public void change(Event event, Object @Nullable [] delta, ChangeMode mode) {

        Entity entity = getValueOrNull(0, Entity.class, event);

        List<UUID> receivers = new ArrayList<>();
        if (mode.equals(ChangeMode.SET)
                || mode.equals(ChangeMode.ADD)
                || mode.equals(ChangeMode.REMOVE)) {

            receivers.addAll(getDeltaValues(delta, Player.class).stream()
                    .map(Entity::getUniqueId)
                    .toList());
        }

        if (entity == null) return;
        int entityId = entity.getEntityId();

        switch (mode) {
            case SET -> GlowingEntityManager.setGlowingReceivers(entityId, receivers);
            case ADD -> GlowingEntityManager.addGlowingReceivers(entityId, receivers);
            case REMOVE -> GlowingEntityManager.removeGlowingReceivers(entityId, receivers);
            case RESET, REMOVE_ALL -> GlowingEntityManager.clearGlowingReceivers(entityId);
        }
    }
}
