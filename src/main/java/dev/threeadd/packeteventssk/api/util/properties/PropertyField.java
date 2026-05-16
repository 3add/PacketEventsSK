package dev.threeadd.packeteventssk.api.util.properties;

import java.util.function.BiConsumer;
import java.util.function.Function;

public record PropertyField<O, T>(
        String name,
        String[] aliases,
        Class<T> expectedType,
        boolean isOptional,
        Function<O, T> getter,
        BiConsumer<O, T> setter
) {
    public boolean matches(String input) {
        if (name.equalsIgnoreCase(input)) return true;
        for (String alias : aliases) {
            if (alias.equalsIgnoreCase(input)) return true;
        }
        return false;
    }
}