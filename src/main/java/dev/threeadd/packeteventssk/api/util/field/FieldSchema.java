package dev.threeadd.packeteventssk.api.util.field;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public record FieldSchema<K, O>(
        K type,
        List<FieldAccessor<O, ?>> accessors,
        Function<InitializationContext, O> constructor,
        FieldRegistrar registrar // the registrar that registered this field
) {
    public FieldAccessor<O, ?> getAccessor(String name) {
        for (FieldAccessor<O, ?> accessor : accessors) {
            if (accessor.matches(name)) return accessor;
        }
        return null;
    }

    public String getReadableFields() {
        List<String> fieldStrings = new ArrayList<>();
        for (FieldAccessor<O, ?> accessor : accessors) {
            if (accessor.aliases().length > 0) {
                fieldStrings.add(accessor.name() + " (" + String.join(", ", accessor.aliases()) + ")");
            } else {
                fieldStrings.add(accessor.name());
            }
        }

        if (fieldStrings.isEmpty()) {
            return "";
        }

        String joined = String.join("`\n  - `", fieldStrings);
        return "  - `" + joined + "`";
    }
}