package dev.threeadd.packeteventssk.api.entity.meta;

import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import dev.threeadd.packeteventssk.api.util.field.FieldRegistrar;
import me.tofaa.entitylib.meta.types.LivingEntityMeta;

public class LivingMetaRegistrar implements FieldRegistrar {

    @Override
    public boolean register() {
        MetaFieldRegistry.INSTANCE.builder(EntityTypes.LIVINGENTITY, LivingEntityMeta.class)
                .optionalField(Number.class,
                        LivingEntityMeta::getHealth,
                        (meta, newNum) -> meta.setHealth(newNum.intValue()),
                        "heath", "hp")
                .build(this);

        return true;
    }
}
