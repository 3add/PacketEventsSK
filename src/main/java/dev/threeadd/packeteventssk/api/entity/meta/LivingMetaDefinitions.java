package dev.threeadd.packeteventssk.api.entity.meta;

import me.tofaa.entitylib.meta.types.LivingEntityMeta;

public class LivingMetaDefinitions {

    public static void register() {
        MetaDefinitionRegistry.INSTANCE.builder(LivingEntityMeta.class)
                .optionalField(Number.class,
                        LivingEntityMeta::getHealth,
                        (meta, newNum) -> meta.setHealth(newNum.intValue()),
                "heath", "hp")
                .build();
    }
}
