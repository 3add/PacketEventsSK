package dev.threeadd.packeteventssk.element.entity.field.meta;

import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import dev.threeadd.packeteventssk.api.util.field.ConstructionContext;
import dev.threeadd.packeteventssk.api.util.field.FieldRegistrar;
import me.tofaa.entitylib.meta.types.LivingEntityMeta;

import java.util.function.BiConsumer;

// Should fully match: https://minecraft.wiki/w/Java_Edition_protocol/Entity_metadata#Living_Entity
// including order of fields
public class LivingMetaFieldRegistrar implements FieldRegistrar {

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void register() {

        // TODO incomplete
        MetaFieldRegistry.INSTANCE.builder(EntityTypes.LIVINGENTITY, LivingEntityMeta.class)
                .optionalField(Number.class,
                        LivingEntityMeta::getHealth,
                        (meta, newNum) -> meta.setHealth(newNum.floatValue()),
                        "health", "hp")
                .constructor(context -> {
                    LivingEntityMeta meta = (LivingEntityMeta) BaseMetaFieldRegistrar.BASE_ENTITY_META_CONSTRUCTOR.apply((ConstructionContext) context);
                    LIVING_ENTITY_CONSUMER.accept(context, meta);

                    return meta;
                })
                .build();
    }

    static final BiConsumer<ConstructionContext<EntityType, LivingEntityMeta>, LivingEntityMeta> LIVING_ENTITY_CONSUMER = (context, meta) -> {
        Number health = context.getOptional("health", Number.class);
        if (health != null) {
            meta.setHealth(health.floatValue());
        }
    };
}
