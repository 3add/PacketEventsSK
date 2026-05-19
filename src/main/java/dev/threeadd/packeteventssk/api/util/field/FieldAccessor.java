package dev.threeadd.packeteventssk.api.util.field;

import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Function;

public record FieldAccessor<O, T>(
        String name,
        String[] aliases,
        Class<T> expectedType,
        boolean isOptional,
        Function<O, T> getter,
        @Nullable BiConsumer<O, T> setter
) {
    /**
     * Checks if the provided input string matches either the primary name or any assigned aliases,
     * ignoring case sensitivity.
     *
     * @param input the string signature to test
     * @return true if this accessor answers to the given name
     */
    public boolean matches(String input) {
        if (name.equalsIgnoreCase(input)) return true;
        for (String alias : aliases) {
            if (alias.equalsIgnoreCase(input)) return true;
        }
        return false;
    }
}