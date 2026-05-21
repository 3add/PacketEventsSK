package dev.threeadd.packeteventssk.element.entity.field.meta;

import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import dev.threeadd.packeteventssk.api.util.field.FieldRegistrar;
import dev.threeadd.packeteventssk.api.util.field.InheritingFieldRegistry;
import me.tofaa.entitylib.meta.EntityMeta;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MetaFieldRegistry extends InheritingFieldRegistry<EntityType, EntityMeta> {
    public static final MetaFieldRegistry INSTANCE = new MetaFieldRegistry();

    @Override
    public List<FieldRegistrar> getRegistrars() {
        return List.of(
                new BaseMetaFieldRegistrar(),
                new DisplayMetaFieldRegistrar(),
                new InteractionMetaFieldRegistrar(),
                new ItemMetaFieldRegistrar(),
                new LivingMetaFieldRegistrar()
        );
    }

    @Override
    protected @Nullable EntityType getParentKey(EntityType key) {
        if (key == EntityTypes.ENTITY) {
            return null; // ENTITY is the absolute root of the hierarchy
        }

        EntityMeta dummyMeta = EntityMeta.createMeta(0, key);
        Class<?> current = dummyMeta.getClass().getSuperclass();

        while (current != null && current != Object.class) {
            EntityType registeredParentKey = classToTypeMap.get(current);
            if (registeredParentKey != null) {
                return registeredParentKey;
            }
            current = current.getSuperclass();
        }

        return null;
    }
}