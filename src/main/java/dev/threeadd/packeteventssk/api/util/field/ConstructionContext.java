package dev.threeadd.packeteventssk.api.util.field;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class ConstructionContext<K, BaseType> {
    private final K key;
    private final List<FieldAccessor<BaseType, ?>> registeredAccessors;
    private final Map<String, Object> arguments;

    public ConstructionContext(K key, List<FieldAccessor<BaseType, ?>> registeredAccessors, Map<String, Object> arguments) {
        this.key = key;
        this.registeredAccessors = registeredAccessors;
        this.arguments = arguments;
    }

    public K getKey() {
        return key;
    }

    private void assertAccessorRegistered(String fieldName) {
        for (FieldAccessor<BaseType, ?> accessor : registeredAccessors) {
            if (accessor.matches(fieldName)) return;
        }
        throw new IllegalArgumentException("Attempted to look up field '" + fieldName + "', but it is not defined in this schema.");
    }

    public <T> @NotNull T getRequired(String fieldName, Class<? extends T> clazz) {
        T field = getOptional(fieldName, clazz);
        if (field == null)
            throw new IllegalStateException("Required field '" + fieldName + "' was not provided for type " + key);
        return field;
    }

    public <T> @Nullable T getOptional(String fieldName, Class<? extends T> clazz) {
        assertAccessorRegistered(fieldName);

        FieldAccessor<BaseType, ?> targetAccessor = null;
        for (FieldAccessor<BaseType, ?> accessor : registeredAccessors) {
            if (accessor.matches(fieldName)) {
                targetAccessor = accessor;
                break;
            }
        }

        if (targetAccessor != null) {
            if (arguments.containsKey(targetAccessor.name())) return clazz.cast(arguments.get(targetAccessor.name()));
            for (String alias : targetAccessor.aliases()) {
                if (arguments.containsKey(alias)) return clazz.cast(arguments.get(alias));
            }
            for (Map.Entry<String, Object> entry : arguments.entrySet()) {
                if (targetAccessor.matches(entry.getKey())) return clazz.cast(entry.getValue());
            }
        }

        return null;
    }

    public <T> T getOrElse(String fieldName, Class<T> clazz, T defaultValue) {
        T val = getOptional(fieldName, clazz);
        return val != null ? val : defaultValue;
    }
}