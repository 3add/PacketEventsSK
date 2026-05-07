package dev.threeadd.packeteventssk.element.entity.expressions.prop;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import com.github.shanebeee.skr.Registration;
import me.tofaa.entitylib.wrapper.WrapperEntity;
import org.jetbrains.annotations.Nullable;

public class ExprFakeEntityId extends SimplePropertyExpression<WrapperEntity, Integer> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprFakeEntityId.class, Integer.class, "fake [entity][ ]id", "fakeentity")
                .name("Fake Entity - Entity Id")
                .description("Get the entity id used to represent this fake entity (integer)")
                .examples("""
                        command spawnfakeplayer:
                            trigger:
                                set {_p} to player
                                create new fake player entity at player for all players:
                                    set fake skin of the fake entity to {_p}'s skin
                                    add the fake entity id of the fake entity to {-id::*}
                        
                        command lookup <text>:
                            trigger:
                                loop {-id::*}:
                                    if {-id::*} contains arg-1 parsed as integer:
                                        send "Found %fake entity with id loop-value%"
                        """)
                .since("1.0.0")
                .register();
    }

    @Override
    public @Nullable Integer convert(WrapperEntity entity) {
        return entity.getEntityId();
    }

    @Override
    public Class<? extends Integer> getReturnType() {
        return Integer.class;
    }

    @Override
    protected String getPropertyName() {
        return "fake entity id";
    }
}
