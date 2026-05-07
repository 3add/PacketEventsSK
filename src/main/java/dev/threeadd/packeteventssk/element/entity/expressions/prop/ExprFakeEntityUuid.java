package dev.threeadd.packeteventssk.element.entity.expressions.prop;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import com.github.shanebeee.skr.Registration;
import me.tofaa.entitylib.wrapper.WrapperEntity;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class ExprFakeEntityUuid extends SimplePropertyExpression<WrapperEntity, UUID> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprFakeEntityUuid.class, UUID.class, "fake [entity] uuid", "fakeentity")
                .name("Fake Entity - Entity UUID")
                .description("Get the entity uuid used to represent this fake entity")
                .examples("""
                        command spawnfakeplayer:
                            trigger:
                                set {_p} to player
                                create new fake player entity at player for all players:
                                    set fake skin of the fake entity to {_p}'s skin
                                    add fake uuid of the fake entity to {-uuid::*}
                        
                        command lookup <text>:
                            trigger:
                                loop {-uuid::*}:
                                    if {-uuid::*} contains arg-1 parsed as uuid:
                                        send "Found %fake entity with uuid loop-value%"
                        """)
                .since("1.0.0")
                .register();
    }

    @Override
    public @Nullable UUID convert(WrapperEntity entity) {
        return entity.getUuid();
    }

    @Override
    public Class<? extends UUID> getReturnType() {
        return UUID.class;
    }

    @Override
    protected String getPropertyName() {
        return "fake uuid";
    }
}
