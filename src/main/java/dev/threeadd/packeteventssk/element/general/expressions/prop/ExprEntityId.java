package dev.threeadd.packeteventssk.element.general.expressions.prop;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import com.github.shanebeee.skr.Registration;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Nullable;

public class ExprEntityId extends SimplePropertyExpression<Entity, Integer> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprEntityId.class, Integer.class, "(protocol|packet) [entity][ ]id", "entity")
                .name("General - Entity Id of Entity")
                .description("Gets the entity id of an entity on the server")
                .examples("""
                        command killTargetForMe:
                            trigger:
                                set {_packet} to a new clientbound destroy entities packet:
                                    entity ids: protocol id of target entity
                        
                                silently send packet {_packet} to the player
                        """)
                .since("1.0.0")
                .register();
    }

    @Override
    public @Nullable Integer convert(Entity entity) {
        return entity.getEntityId();
    }

    @Override
    public Class<? extends Integer> getReturnType() {
        return Integer.class;
    }

    @Override
    protected String getPropertyName() {
        return "protocol entity id";
    }
}