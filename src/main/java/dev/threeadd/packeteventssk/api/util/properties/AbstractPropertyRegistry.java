package dev.threeadd.packeteventssk.api.util.properties;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

public abstract class AbstractPropertyRegistry<K, BaseType> {
    protected final Map<K, PropertyDefinition<K, BaseType>> REGISTRY = new HashMap<>();

    public PropertyDefinition<K, BaseType> getDefinition(K key) {
        return REGISTRY.get(key);
    }

    public Collection<PropertyDefinition<K, BaseType>> getAllDefinitions() {
        return REGISTRY.values();
    }

    protected <T extends BaseType> Builder<T> createBuilder(K key) {
        return new Builder<>(key);
    }

    public class Builder<T extends BaseType> {
        protected final K type;
        protected final List<PropertyField<BaseType, ?>> fields = new ArrayList<>();
        protected Function<PropertyValues, T> constructor;

        public Builder(K type) {
            this.type = type;
        }

        @SuppressWarnings("unchecked")
        public <V> Builder<T> requiredField(Class<V> expectedType, Function<T, V> getter, BiConsumer<T, V> setter, String... names) {
            fields.add(new PropertyField<>(names[0], Arrays.copyOfRange(names, 1, names.length), expectedType, false, (Function<BaseType, V>) getter, (BiConsumer<BaseType, V>) setter));
            return this;
        }

        @SuppressWarnings("unchecked")
        public <V> Builder<T> optionalField(Class<V> expectedType, Function<T, V> getter, BiConsumer<T, V> setter, String... names) {
            fields.add(new PropertyField<>(names[0], Arrays.copyOfRange(names, 1, names.length), expectedType, true, (Function<BaseType, V>) getter, (BiConsumer<BaseType, V>) setter));
            return this;
        }

        public Builder<T> constructor(Function<PropertyValues, T> constructor) {
            this.constructor = constructor;
            return this;
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        public void build() {
            REGISTRY.put(type, new PropertyDefinition<>(type, Collections.unmodifiableList(fields), (Function) constructor));
        }
    }
}