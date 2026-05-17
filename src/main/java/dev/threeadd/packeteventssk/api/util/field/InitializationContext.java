package dev.threeadd.packeteventssk.api.util.field;

import java.util.List;
import java.util.Map;

public class InitializationContext {
    private final List<FieldAccessor<?, ?>> registeredAccessors;
    private final Map<String, Object> arguments;

    public InitializationContext(List<FieldAccessor<?, ?>> registeredAccessors, Map<String, Object> arguments) {
        this.registeredAccessors = registeredAccessors;
        this.arguments = arguments;
    }

    private void assertAccessorRegistered(String key) {
        for (FieldAccessor<?, ?> accessor : registeredAccessors) {
            if (accessor.matches(key)) return;
        }
        throw new IllegalArgumentException("Attempted to look up field '" + key + "', but it is not defined in this schema.");
    }

    public <T> T get(String key, Class<T> clazz) {
        assertAccessorRegistered(key);

        FieldAccessor<?, ?> targetAccessor = null;
        for (FieldAccessor<?, ?> accessor : registeredAccessors) {
            if (accessor.matches(key)) {
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

    public <T> T getOrElse(String key, Class<T> clazz, T defaultValue) {
        T val = get(key, clazz);
        return val != null ? val : defaultValue;
    }
}