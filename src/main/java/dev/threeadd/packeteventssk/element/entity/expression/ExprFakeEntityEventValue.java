package dev.threeadd.packeteventssk.element.entity.expression;

import ch.njol.skript.expressions.base.EventValueExpression;
import com.github.shanebeee.skr.Registration;
import me.tofaa.entitylib.wrapper.WrapperEntity;

public class ExprFakeEntityEventValue extends EventValueExpression<WrapperEntity> {

    public static void register(Registration reg) {
        reg.newEventExpression(ExprFakeEntityEventValue.class, WrapperEntity.class, "[the] fake entity")
                .name("Fake Entity Event Value")
                .description("The fake entity involved in the event. (Only available in fake entity related events)")
                .examples("""
                        command test:
                            trigger:
                                create a new fake zombie entity at player for players:
                                    broadcast the fake entity
                        """)
                .since("1.0.0", "1.1.0 (switched to EventValueExpression)")
                .register();
    }

    public ExprFakeEntityEventValue() {
        super(WrapperEntity.class);
    }
}
