package dev.threeadd.packeteventssk.api.field.doc;

import dev.threeadd.packeteventssk.api.field.FieldAccessor;
import dev.threeadd.packeteventssk.api.field.FieldSchema;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Utility for building Skript documentation strings from field registries.
 */
public final class FieldDescriptionBuilder {

    private FieldDescriptionBuilder() {}

    /**
     * Builds a documentation block where each schema is described independently.
     *
     * @param schemas all schemas to include
     * @param typeLabel converts a schema to its display string (e.g. "clientbound foo bar packet")
     */
    public static <K, W> String buildFlat(Collection<FieldSchema<K, W>> schemas, Function<FieldSchema<K, W>, String> typeLabel) {

        StringBuilder sb = new StringBuilder();
        for (FieldSchema<K, W> schema : schemas) {
            String fields = schema.getReadableFields();
            if (fields.isEmpty()) continue;
            sb.append("* **").append(typeLabel.apply(schema)).append("** fields:\n")
                    .append(fields).append("\n");
        }
        return sb.toString();
    }

    /**
     * Builds a documentation block that shows each schema's own fields only,
     * with an inheritance point pointing to the closest parent schema.
     *
     * @param schemas all schemas, sorted however the caller prefers
     * @param typeLabel converts a schema to its display string (e.g. "fake player entity")
     * @param showRequired whether to mark required fields with {@code *}
     */
    public static <K, W> String buildHierarchical(List<FieldSchema<K, W>> schemas, Function<FieldSchema<K, W>, String> typeLabel, boolean showRequired) {

        StringBuilder sb = new StringBuilder();

        for (FieldSchema<K, W> schema : schemas) {
            Set<String> myFieldNames = schema.accessors().stream()
                    .map(FieldAccessor::name)
                    .collect(Collectors.toSet());

            FieldSchema<K, W> displayParent = null;
            int bestParentSize = -1;
            for (FieldSchema<K, W> other : schemas) {
                if (other == schema) continue;
                Set<String> otherNames = other.accessors().stream()
                        .map(FieldAccessor::name)
                        .collect(Collectors.toSet());
                if (myFieldNames.containsAll(otherNames) && myFieldNames.size() > otherNames.size()
                        && otherNames.size() > bestParentSize) {
                    bestParentSize = otherNames.size();
                    displayParent = other;
                }
            }

            Set<String> parentFieldNames = displayParent != null
                    ? displayParent.accessors().stream().map(FieldAccessor::name).collect(Collectors.toSet())
                    : Collections.emptySet();

            StringBuilder fieldLines = new StringBuilder();
            for (FieldAccessor<W, ?> field : schema.accessors()) {
                if (parentFieldNames.contains(field.name())) continue;

                fieldLines.append("  - `").append(field.name());
                if (showRequired && !field.isOptional()) fieldLines.append("*");
                if (field.aliases().length > 0) {
                    fieldLines.append(" (").append(String.join(", ", field.aliases())).append(")");
                }
                fieldLines.append("`\n");
            }

            if (fieldLines.isEmpty()) continue;

            sb.append("* **").append(typeLabel.apply(schema)).append("** fields:\n");
            if (displayParent != null) {
                sb.append("  - *(Inherits all fields from **")
                        .append(typeLabel.apply(displayParent))
                        .append("**)*\n");
            }
            sb.append(fieldLines).append("\n");
        }
        return sb.toString();
    }
}