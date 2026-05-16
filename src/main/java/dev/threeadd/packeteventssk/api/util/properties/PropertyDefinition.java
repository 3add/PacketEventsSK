package dev.threeadd.packeteventssk.api.util.properties;

import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

public record PropertyDefinition<K, O>(K type, List<PropertyField<O, ?>> fields,
                                       Function<PropertyValues, O> constructor) {
    public PropertyField<O, ?> getField(String name) {
        for (PropertyField<O, ?> field : fields) {
            if (field.matches(name)) return field;
        }
        return null;
    }

    public String getReadableFields() {
        List<String> fieldStrings = new ArrayList<>();
        for (PropertyField<O, ?> field : fields) {
            if (field.aliases().length > 0) {
                fieldStrings.add(field.name() + " (or: " + String.join(", ", field.aliases()) + ")");
            } else {
                fieldStrings.add(field.name());
            }
        }
        return String.join(" | ", fieldStrings);
    }

    @Override
    public @NonNull String toString() {
        if (type instanceof Enum<?> e) return e.name().replace("_", " ").toLowerCase(Locale.ENGLISH);
        if (type instanceof Class<?> c) return c.getSimpleName().replace("Meta", "").toLowerCase(Locale.ENGLISH);
        return type.toString();
    }
}