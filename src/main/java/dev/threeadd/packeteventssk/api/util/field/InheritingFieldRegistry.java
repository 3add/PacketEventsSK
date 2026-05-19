package dev.threeadd.packeteventssk.api.util.field;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A specialized field registry that allows schemas to inherit fields from their superclasses.
 */
public abstract class InheritingFieldRegistry<K, BaseType> extends BaseFieldRegistry<K, BaseType> {

    protected final Map<Class<? extends BaseType>, K> classToTypeMap = new HashMap<>();

    protected abstract @Nullable K getParentKey(K key);

    @Override
    public @Nullable FieldSchema<K, BaseType> getSchema(K key) {
        K current = key;
        while (current != null) {
            FieldSchema<K, BaseType> schema = super.getSchema(current);
            if (schema != null) {
                return schema;
            }
            current = getParentKey(current);
        }
        return null;
    }

    @Override
    public <T extends BaseType> InheritingBuilder<T> builder(K key, Class<T> clazz) {
        classToTypeMap.put(clazz, key);
        return new InheritingBuilder<>(key, clazz);
    }

    @Nullable
    public FieldAccessor<BaseType, ?> getAccessor(Class<?> clazz, String fieldName) {
        Class<?> current = clazz;
        while (current != null && current != Object.class) {
            K type = classToTypeMap.get(current);
            if (type != null) {
                FieldSchema<K, BaseType> schema = getSchema(type);
                if (schema != null) {
                    FieldAccessor<BaseType, ?> accessor = schema.getAccessor(fieldName);
                    if (accessor != null) {
                        return accessor;
                    }
                }
            }
            current = current.getSuperclass();
        }
        return null;
    }

    public class InheritingBuilder<T extends BaseType> extends Builder<T> {
        private final Class<T> clazz;

        public InheritingBuilder(K type, Class<T> clazz) {
            super(type);
            this.clazz = clazz;
        }

        @SuppressWarnings({"rawtypes", "unchecked"})
        @Override
        public void build() {
            List<FieldAccessor<BaseType, ?>> mergedAccessors = new ArrayList<>();
            Class<?> current = clazz.getSuperclass();

            while (current != null && current != Object.class) {
                K parentKey = classToTypeMap.get(current);
                if (parentKey != null && REGISTRY.containsKey(parentKey)) {
                    FieldSchema<K, BaseType> parentSchema = REGISTRY.get(parentKey);
                    mergedAccessors.addAll(parentSchema.accessors());
                    break;
                }
                current = current.getSuperclass();
            }

            mergedAccessors.addAll(this.accessors);

            REGISTRY.put(type, new FieldSchema<>(type, (List) mergedAccessors, constructor));
        }
    }
}