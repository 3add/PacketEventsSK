package dev.threeadd.packeteventssk.api.util.field;

import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

public abstract class BaseFieldRegistry<K, BaseType> {
    protected final Map<K, FieldSchema<K, BaseType>> REGISTRY = new HashMap<>();

    public abstract List<FieldRegistrar> getRegistrars();

    public void registerAll() {
        List<FieldRegistrar> registrars = getRegistrars();
        for (FieldRegistrar registrar : registrars) {
            registrar.register();
        }
    }

    public FieldSchema<K, BaseType> getSchema(K key) {
        return REGISTRY.get(key);
    }

    public Collection<FieldSchema<K, BaseType>> getAllSchemas() {
        return REGISTRY.values();
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
            accessors.add(new FieldAccessor<>(names[0], Arrays.copyOfRange(names, 1, names.length), expectedType, false, (Function<BaseType, V>) getter, (BiConsumer<BaseType, V>) setter));
            return this;
        }

        @SuppressWarnings("unchecked")
        public <V> Builder<T> optionalField(Class<V> expectedType, Function<T, V> getter, @Nullable BiConsumer<T, V> setter, String... names) {
            accessors.add(new FieldAccessor<>(names[0], Arrays.copyOfRange(names, 1, names.length), expectedType, true, (Function<BaseType, V>) getter, (BiConsumer<BaseType, V>) setter));
            return this;
        }

        public Builder<T> constructor(@Nullable Function<ConstructionContext<K, T>, T> constructor) {
            this.constructor = constructor;
            return this;
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        public void build() {
            REGISTRY.put(type, new FieldSchema(type, accessors, constructor));
        }
    }
}