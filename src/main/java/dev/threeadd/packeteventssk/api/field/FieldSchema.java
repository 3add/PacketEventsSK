package dev.threeadd.packeteventssk.api.field;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public record FieldSchema<K, O>(
        K type,
        List<FieldAccessor<O, ?>> accessors,
        @Nullable Function<ConstructionContext<K, O>, O> constructor
) {
    public FieldAccessor<O, ?> getAccessor(String name) {
        for (FieldAccessor<O, ?> accessor : this.accessors) {
            if (accessor.matches(name)) return accessor;
        }
        return null;
    }

    public String getReadableFields() {
        List<String> fieldStrings = new ArrayList<>();
        for (FieldAccessor<O, ?> accessor : this.accessors) {
            if (accessor.aliases().length > 0) {
                fieldStrings.add(accessor.name() + (!accessor.isOptional() ? "*" : "") + " (" + String.join(", ", accessor.aliases()) + ")");
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