package dev.threeadd.packeteventssk.element.entity.expressions.prop;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import com.github.shanebeee.skr.Registration;
import me.tofaa.entitylib.meta.EntityMeta;
import me.tofaa.entitylib.wrapper.WrapperEntity;
import org.jspecify.annotations.Nullable;

public class ExprFakeEntityMeta extends SimplePropertyExpression<WrapperEntity, EntityMeta> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprFakeEntityMeta.class, EntityMeta.class, "fake [entity] meta", "fakeentity")
                .name("Fake Entity - Fake Entity Meta")
                .description("The fake entity meta of a fake entity")
                // TODO Example
                .since("1.1.0")
                .register();
    }

    @Override
    public @Nullable EntityMeta convert(WrapperEntity entity) {
        return entity.getEntityMeta();
    }

    @Override
    public Class<? extends EntityMeta> getReturnType() {
        return EntityMeta.class;
    }

    @Override
    protected String getPropertyName() {
        return "fake entity meta";
    }
}
