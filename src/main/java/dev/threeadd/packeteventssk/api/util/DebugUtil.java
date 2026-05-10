package dev.threeadd.packeteventssk.api.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class DebugUtil {

    private static final int MAX_DEPTH = 3;
    private static final String INDENT_STEP = "  ";

    public static String getDebugString(Object obj) {
        if (obj == null) return "<none>";

        StringBuilder output = new StringBuilder();

        output.append(obj.getClass().getSimpleName()).append(":\n");

        appendFields(obj, 1, output);

        return output.toString().stripTrailing();
    }

    private static void appendFields(Object obj, int depth, StringBuilder output) {
        if (obj == null || depth > MAX_DEPTH) return;

        String indent = INDENT_STEP.repeat(depth);
        Class<?> currentClass = obj.getClass();

        try {
            for (Method method : currentClass.getDeclaredMethods()) {
                String methodName = method.getName();

                boolean isGetter = (methodName.startsWith("get") || methodName.startsWith("is"));
                boolean noParameters = method.getParameterCount() == 0;

                boolean isInternal = methodName.contains("Handle") || methodName.contains("copy") || methodName.contains("toString") || methodName.contains("PacketEventsData");

                if (isGetter && noParameters && !isInternal) {

                    if (!method.trySetAccessible()) continue;

                    String fieldName = cleanFieldName(methodName);
                    Object value = method.invoke(obj);

                    if (value == null) {
                        output.append(indent).append(fieldName).append(": <none>\n");
                        continue;
                    }

                    if (value instanceof List<?> list) {
                        boolean isMetadataList = list.stream().anyMatch(item -> item != null &&
                                (item.getClass().getName().contains("EntityData") || item.getClass().getName().contains("AbstractEntityData")));

                        if (isMetadataList) {
                            output.append(indent).append(fieldName).append(":\n");
                            for (Object item : list) {
                                output.append(indent).append(INDENT_STEP);
                                appendMetadataEntry(item, output);
                            }
                        } else {
                            if (list.isEmpty()) {
                                output.append(indent).append(fieldName).append(": <empty list>\n");
                            } else {
                                String listContent = list.stream()
                                        .map(DebugUtil::valueToString)
                                        .collect(Collectors.joining(", "));
                                output.append(indent).append(fieldName).append(": [").append(listContent).append("]\n");
                            }
                        }

                    } else if (!isSimpleValue(value)) {
                        String valueString = value.toString();
                        boolean badToString = valueString.startsWith(value.getClass().getName() + "@") ||
                                valueString.equals(value.getClass().getName());

                        if (badToString && depth + 1 < MAX_DEPTH) {
                            output.append(indent).append(fieldName).append(":\n");
                            // Removed the extra { } braces as Skripters usually prefer standard YAML-like indentation
                            appendFields(value, depth + 1, output);
                        } else {
                            output.append(indent).append(fieldName).append(": ").append(valueToString(value)).append("\n");
                        }

                    } else {
                        output.append(indent).append(fieldName).append(": ").append(valueToString(value)).append("\n");
                    }
                }
            }
        } catch (Exception e) {
            output.append(indent).append("[Reflection Error: ").append(e.getClass().getSimpleName()).append(" - ").append(e.getMessage()).append("]\n");
        }
    }

    private static String cleanFieldName(String methodName) {
        String fieldName = methodName;
        if (fieldName.startsWith("get")) {
            fieldName = fieldName.substring(3);
        } else if (fieldName.startsWith("is")) {
            fieldName = fieldName.substring(2);
        }

        StringBuilder skriptName = new StringBuilder();
        for (char c : fieldName.toCharArray()) {
            if (Character.isUpperCase(c)) {
                if (!skriptName.isEmpty()) skriptName.append(" ");
                skriptName.append(Character.toLowerCase(c));
            } else {
                skriptName.append(c);
            }
        }
        return skriptName.toString();
    }

    private static boolean isSimpleValue(Object value) {
        Class<?> cls = value.getClass();

        if (cls.isPrimitive() ||
                cls == String.class ||
                value instanceof Number ||
                value instanceof Boolean ||
                value instanceof UUID ||
                cls.isEnum() ||
                value instanceof List ||
                value instanceof Map) {
            return true;
        }

        String className = cls.getName();
        return className.contains("Optional") ||
                className.contains("Component") ||
                className.endsWith("EntityType") ||
                className.endsWith("EntityDataType") ||
                className.endsWith("EntityPose") ||
                className.endsWith("Vector3d") ||
                className.endsWith("Vector3i") ||
                className.endsWith("Location") ||
                className.endsWith("ItemStack") ||
                className.endsWith("ProtocolVersion") ||
                className.endsWith("ItemType");
    }

    private static String valueToString(Object value) {
        return switch (value) {
            case null -> "<none>";
            case Optional<?> optional -> optional.map(DebugUtil::valueToString).orElse("<none>");
            case Enum<?> anEnum -> anEnum.name().toLowerCase(Locale.ENGLISH).replace("_", " ");
            default -> value.toString();
        };

        if (value instanceof Enum<?>) {
            return ((Enum<?>) value).name().toLowerCase(Locale.ENGLISH).replace("_", " ");
        }

        return value.toString();
    }

    private static void appendMetadataEntry(Object data, StringBuilder builder) {
        if (data == null) {
            builder.append("<none> metadata entry\n");
            return;
        }

        try {
            Method getIndex = data.getClass().getMethod("getIndex");
            Method getType = data.getClass().getMethod("getType");
            Method getValue = data.getClass().getMethod("getValue");

            Object index = getIndex.invoke(data);
            Object type = getType.invoke(data);
            Object value = getValue.invoke(data);

            String typeString = Objects.toString(type);
            int start = typeString.indexOf('[');
            int end = typeString.indexOf(']');
            if (start != -1 && end != -1 && end > start) {
                typeString = typeString.substring(start + 1, end);
            }

            builder.append("[").append(index).append(": ")
                    .append(typeString.toLowerCase(Locale.ENGLISH)).append(" = ")
                    .append(valueToString(value))
                    .append("]\n");

        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            builder.append("Unparsable Data: ").append(data).append("\n");
        }
    }
}