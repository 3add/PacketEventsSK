package dev.threeadd.packeteventssk.element.general.field.meta;

import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import dev.threeadd.packeteventssk.api.util.field.ConstructionContext;
import dev.threeadd.packeteventssk.api.util.field.FieldRegistrar;
import me.tofaa.entitylib.meta.other.InteractionMeta;

public class InteractionMetaFieldRegistrar implements FieldRegistrar {

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public boolean register() {
        MetaFieldRegistry.INSTANCE.builder(EntityTypes.INTERACTION, InteractionMeta.class)
                .optionalField(Number.class,
                        InteractionMeta::getWidth,
                        (meta, newNum) -> meta.setWidth(newNum.intValue()),
                        "interaction width")
                .optionalField(Number.class,
                        InteractionMeta::getHeight,
                        (meta, newNum) -> meta.setHeight(newNum.intValue()),
                        "interaction height")
                .constructor(context -> {

                    InteractionMeta meta = (InteractionMeta) BaseMetaFieldRegistrar.BASE_ENTITY_META_CONSTRUCTOR.apply((ConstructionContext) context);

                    Number width = context.getOptional("interaction width", Number.class);
                    if (width != null) {
                        meta.setWidth(width.intValue());
                    }

                    Number height = context.getOptional("interaction height", Number.class);
                    if (height != null) {
                        meta.setHeight(height.intValue());
                    }

                    return meta;
                })
                .build();

        return true;
    }
}
