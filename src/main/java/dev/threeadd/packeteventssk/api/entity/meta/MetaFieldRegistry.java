package dev.threeadd.packeteventssk.api.entity.meta;

import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import dev.threeadd.packeteventssk.api.util.field.BaseFieldRegistry;
import dev.threeadd.packeteventssk.api.util.field.FieldAccessor;
import dev.threeadd.packeteventssk.api.util.field.FieldRegistrar;
import dev.threeadd.packeteventssk.api.util.field.FieldSchema;
import me.tofaa.entitylib.meta.EntityMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MetaFieldRegistry extends BaseFieldRegistry<EntityType, EntityMeta> {
    public static final MetaFieldRegistry INSTANCE = new MetaFieldRegistry();

    private final Map<Class<? extends EntityMeta>, EntityType> classToTypeMap = new HashMap<>();

    @Override
    public List<FieldRegistrar> getRegistrars() {
        return List.of(
                new BaseMetaRegistrar(),
                new DisplayMetaRegistrar(),
                new InteractionMetaRegistrar(),
                new ItemMetaRegistrar(),
                new LivingMetaRegistrar()
        );
    }

    @Override
    public <M extends EntityMeta> Builder<M> builder(EntityType type, Class<M> metaClass) {
        classToTypeMap.put(metaClass, type);
        return super.builder(type, metaClass);
    }

    public FieldAccessor<EntityMeta, ?> getAccessor(Class<? extends EntityMeta> metaClass, String fieldName) {
        Class<?> current = metaClass;
        while (current != null && EntityMeta.class.isAssignableFrom(current)) {
            EntityType type = classToTypeMap.get(current);
            if (type != null) {
                FieldSchema<EntityType, EntityMeta> schema = getSchema(type);
                if (schema != null) {
                    FieldAccessor<EntityMeta, ?> accessor = schema.getAccessor(fieldName);
                    if (accessor != null) {
                        return accessor;
                    }
                }
            }
            current = current.getSuperclass();
        }
        return null;
    }
}