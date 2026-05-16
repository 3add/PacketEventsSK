package dev.threeadd.packeteventssk.element.entity.expressions.prop;

import ch.njol.skript.bukkitutil.EntityUtils;
import ch.njol.skript.entity.EntityData;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import com.github.shanebeee.skr.Registration;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import me.tofaa.entitylib.wrapper.WrapperEntity;
import org.jspecify.annotations.Nullable;

public class ExprFakeEntityType extends SimplePropertyExpression<WrapperEntity, EntityData<?>> {

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprFakeEntityType.class, (Class) EntityData.class, "fake [entity] type", "fakeentity")
                .name("Fake Entity - Entity Type")
                .description("The entity type of a fake entity")
                .examples("""
                        command spawn:
                            trigger:
                                create a new fake zombie entity at player for players:
                                    if fake entity type of fake entity is zombie:
                                        broadcast "this is a zombie"
                        """)
                .since("1.1.0")
                .register();
    }

    @Override
    public @Nullable EntityData<?> convert(WrapperEntity entity) {
        return EntityUtils.toSkriptEntityData(SpigotConversionUtil.toBukkitEntityType(entity.getEntityType()));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Class<? extends EntityData<?>> getReturnType() {
        return (Class) EntityData.class;
    }

    @Override
    protected String getPropertyName() {
        return "fake entity type";
    }
}
