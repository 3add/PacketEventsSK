package dev.threeadd.packeteventssk.api.entity.meta;

import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import dev.threeadd.packeteventssk.api.util.field.FieldRegistrar;
import me.tofaa.entitylib.meta.other.InteractionMeta;

public class InteractionMetaRegistrar implements FieldRegistrar {

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
                .build(this);

        return true;
    }
}
