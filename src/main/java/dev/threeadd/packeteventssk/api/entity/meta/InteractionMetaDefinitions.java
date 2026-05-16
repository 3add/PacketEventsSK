package dev.threeadd.packeteventssk.api.entity.meta;

import me.tofaa.entitylib.meta.other.InteractionMeta;

public class InteractionMetaDefinitions {

    public static void register() {
        MetaDefinitionRegistry.INSTANCE.builder(InteractionMeta.class)
                .optionalField(Number.class,
                        InteractionMeta::getWidth,
                        (meta, newNum) -> meta.setWidth(newNum.intValue()),
                        "interaction width")
                .optionalField(Number.class,
                        InteractionMeta::getHeight,
                        (meta, newNum) -> meta.setHeight(newNum.intValue()),
                        "interaction height")
                .build();
    }
}
