package dev.threeadd.packeteventssk.api.field;

import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

public abstract class BaseFieldRegistry<K, BaseType> {
    protected final Map<K, FieldSchema<K, BaseType>> REGISTRY = new HashMap<>();

    public abstract List<FieldRegistrar> getRegistrars();

    public void registerAll() {
        for (FieldRegistrar registrar : getRegistrars()) {
            registrar.register();
        }
    }

    public FieldSchema<K, BaseType> getSchema(K key) {
        return this.REGISTRY.get(key);
    }

    public Collection<FieldSchema<K, BaseType>> getAllSchemas() {
        return this.REGISTRY.values();
    }

    /**
     * Returns the key whose schema represents the common base for all wrappers in this
     * registry (e.g. {@code EntityTypes.ENTITY}). When non-null, non-literal type
     * expressions in {@code AbstractSectionExprNew} / {@code AbstractExprField} are
     * accepted at parse time and validated against that schema.
     */
    public @Nullable K getBaseKey() {
        return null;
    }

    /**
     * Returns the base schema, or {@code null} if {@link #getBaseKey()}
     * is null or has no registered schema yet.
     */
    @Nullable
    public FieldSchema<K, BaseType> getBaseSchema() {
        K key = getBaseKey();
        return key != null ? getSchema(key) : null;
    }

    // This class argument is not useless, this way java recognizes the type in the lambdas for the builder
    public <T extends BaseType> Builder<T> builder(K key, Class<T> clazz) {
        return new Builder<>(key);
    }

    public class Builder<T extends BaseType> {
        protected final K type;
        protected final List<FieldAccessor<BaseType, ?>> accessors = new ArrayList<>();
        protected @Nullable Function<ConstructionContext<K, T>, T> constructor;

        public Builder(K type) {
            this.type = type;
        }

        @SuppressWarnings("unchecked")
        public <V> Builder<T> requiredField(Class<V> expectedType, Function<T, V> getter, @Nullable BiConsumer<T, V> setter, String... names) {
            this.accessors.add(new FieldAccessor<>(names[0], Arrays.copyOfRange(names, 1, names.length), expectedType, false, (Function<BaseType, V>) getter, (BiConsumer<BaseType, V>) setter));
            return this;
        }

        @SuppressWarnings("unchecked")
        public <V> Builder<T> optionalField(Class<V> expectedType, Function<T, V> getter, @Nullable BiConsumer<T, V> setter, String... names) {
            this.accessors.add(new FieldAccessor<>(names[0], Arrays.copyOfRange(names, 1, names.length), expectedType, true, (Function<BaseType, V>) getter, (BiConsumer<BaseType, V>) setter));
            return this;
        }

        public Builder<T> constructor(@Nullable Function<ConstructionContext<K, T>, T> constructor) {
            this.constructor = constructor;
            return this;
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        public void build() {
            BaseFieldRegistry.this.REGISTRY.put(this.type, new FieldSchema(this.type, this.accessors, this.constructor));
        }
    }
}