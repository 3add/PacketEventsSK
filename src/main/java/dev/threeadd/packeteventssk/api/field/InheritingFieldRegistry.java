package dev.threeadd.packeteventssk.api.field;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A field registry that merges parent-schema fields into child schemas at build time,
 * enabling Skript expressions to find inherited fields by walking the type hierarchy.
 *
 * <p>Subclasses must implement {@link #getParentKey} and {@link #getBaseKey}.
 * {@code getBaseKey()} is the root of the hierarchy, its schema is used as the
 * parse-time fallback when a type expression is not a compile-time literal.
 */
public abstract class InheritingFieldRegistry<K, BaseType> extends BaseFieldRegistry<K, BaseType> {

    protected final Map<Class<? extends BaseType>, K> classToTypeMap = new HashMap<>();

    /** Returns the parent key for {@code key}, or {@code null} when {@code key} is the root. */
    @Nullable
    protected abstract K getParentKey(K key);

    @Override
    public @Nullable FieldSchema<K, BaseType> getSchema(K key) {
        K current = key;
        while (current != null) {
            FieldSchema<K, BaseType> schema = super.getSchema(current);
            if (schema != null) return schema;
            current = getParentKey(current);
        }
        return null;
    }

    @Override
    public <T extends BaseType> InheritingBuilder<T> builder(K key, Class<T> clazz) {
        this.classToTypeMap.put(clazz, key);
        return new InheritingBuilder<>(key, clazz);
    }

    /**
     * Resolves an accessor by walking the Java class hierarchy of a live wrapper
     * instance, used by {@code AbstractExprField} for runtime field resolution.
     */
    @Nullable
    public FieldAccessor<BaseType, ?> getAccessor(Class<?> clazz, String fieldName) {
        Class<?> current = clazz;
        while (current != null && current != Object.class) {
            K type = this.classToTypeMap.get(current);
            if (type != null) {
                FieldSchema<K, BaseType> schema = getSchema(type);
                if (schema != null) {
                    FieldAccessor<BaseType, ?> accessor = schema.getAccessor(fieldName);
                    if (accessor != null) return accessor;
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

            // Prepend parent accessors so subtype schemas are a strict superset
            Class<?> current = this.clazz.getSuperclass();
            while (current != null && current != Object.class) {
                K parentKey = InheritingFieldRegistry.this.classToTypeMap.get(current);
                if (parentKey != null && InheritingFieldRegistry.this.REGISTRY.containsKey(parentKey)) {
                    mergedAccessors.addAll(InheritingFieldRegistry.this.REGISTRY.get(parentKey).accessors());
                    break;
                }
                current = current.getSuperclass();
            }

            mergedAccessors.addAll(this.accessors);
            InheritingFieldRegistry.this.REGISTRY.put(this.type, new FieldSchema<>(this.type, (List) mergedAccessors, this.constructor));
        }
    }
}