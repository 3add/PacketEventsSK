package dev.threeadd.packeteventssk.api.util.properties;

import java.util.List;
import java.util.Map;

public class PropertyValues {
    private final List<PropertyField<?, ?>> registeredFields;
    private final Map<String, Object> values;

    public PropertyValues(List<PropertyField<?, ?>> registeredFields, Map<String, Object> values) {
        this.registeredFields = registeredFields;
        this.values = values;
    }

    private void assertKeyRegistered(String key) {
        for (PropertyField<?, ?> field : registeredFields) {
            if (field.matches(key)) return;
        }
        throw new IllegalArgumentException("Attempted to look up field '" + key + "', but it is not registered.");
    }

    public <T> T get(String key, Class<T> clazz) {
        assertKeyRegistered(key);

        PropertyField<?, ?> targetField = null;
        for (PropertyField<?, ?> field : registeredFields) {
            if (field.matches(key)) {
                targetField = field;
                break;
            }
        }

        if (targetField != null) {
            if (values.containsKey(targetField.name())) return clazz.cast(values.get(targetField.name()));
            for (String alias : targetField.aliases()) {
                if (values.containsKey(alias)) return clazz.cast(values.get(alias));
            }
            for (Map.Entry<String, Object> entry : values.entrySet()) {
                if (targetField.matches(entry.getKey())) return clazz.cast(entry.getValue());
            }
        }
        return null;
    }

    public <T> T getOrElse(String key, Class<T> clazz, T defaultValue) {
        T val = get(key, clazz);
        return val != null ? val : defaultValue;
    }

    public boolean has(String key) {
        assertKeyRegistered(key);
        for (PropertyField<?, ?> field : registeredFields) {
            if (field.matches(key)) {
                if (values.containsKey(field.name())) return true;
                for (String alias : field.aliases()) {
                    if (values.containsKey(alias)) return true;
                }
                for (String k : values.keySet()) {
                    if (field.matches(k)) return true;
                }
                break;
            }
        }
        return false;
    }
}