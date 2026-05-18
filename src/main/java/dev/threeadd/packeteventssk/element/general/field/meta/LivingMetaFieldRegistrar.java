package dev.threeadd.packeteventssk.element.general.field.meta;

import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import dev.threeadd.packeteventssk.api.util.field.ConstructionContext;
import dev.threeadd.packeteventssk.api.util.field.FieldRegistrar;
import me.tofaa.entitylib.meta.types.LivingEntityMeta;

import java.util.function.BiConsumer;

public class LivingMetaFieldRegistrar implements FieldRegistrar {

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public boolean register() {
        MetaFieldRegistry.INSTANCE.builder(EntityTypes.LIVINGENTITY, LivingEntityMeta.class)
                .optionalField(Number.class,
                        LivingEntityMeta::getHealth,
                        (meta, newNum) -> meta.setHealth(newNum.intValue()),
                        "health", "hp")
                .constructor(context -> {
                    LivingEntityMeta meta = (LivingEntityMeta) BaseMetaFieldRegistrar.BASE_ENTITY_META_CONSTRUCTOR.apply((ConstructionContext) context);
                    LIVING_ENTITY_CONSUMER.accept(context, meta);

                    return meta;
                })
                .build();

        return true;
    }

    static final BiConsumer<ConstructionContext<EntityType, LivingEntityMeta>, LivingEntityMeta> LIVING_ENTITY_CONSUMER = (context, meta) -> {
        Number health = context.getOptional("health", Number.class);
        if (health != null) {
            meta.setHealth(health.intValue());
        }
    };
}
