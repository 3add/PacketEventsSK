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
                                create a new destroy entities send packet:
                                    add (entity id of target entity of player) to packet entity ids of the packet
                                    send packet the packet to the player
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