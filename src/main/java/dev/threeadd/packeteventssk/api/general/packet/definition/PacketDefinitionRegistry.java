package dev.threeadd.packeteventssk.api.general.packet.definition;

import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class PacketDefinitionRegistry {

    private static final Map<PacketTypeCommon, PacketDefinition> REGISTRY = new HashMap<>();

    public static <W extends PacketWrapper<?>> PacketBuilder<W> builder(PacketTypeCommon type, Class<W> wrapperClass) {
        return new PacketBuilder<>(type);
    }

    public static PacketDefinition getDefinition(PacketTypeCommon type) {
        return REGISTRY.get(type);
    }

    public static Collection<PacketDefinition> getAllDefinitions() {
        return REGISTRY.values();
    }

    public record PacketField<T>(String name, String[] aliases, Class<T> expectedType, boolean isOptional,
                                 Function<PacketWrapper<?>, T> getter, BiConsumer<PacketWrapper<?>, T> setter) {
        public boolean matches(String input) {
            if (name.equalsIgnoreCase(input)) return true;
            for (String alias : aliases) {
                if (alias.equalsIgnoreCase(input)) return true;
            }
            return false;
        }
    }

    public record PacketDefinition(PacketTypeCommon type, List<PacketField<?>> fields, Function<PacketValues, PacketWrapper<?>> constructor) {
        public PacketField<?> getField(String name) {
            for (PacketField<?> field : fields) {
                if (field.matches(name)) {
                    return field;
                }
            }
            return null;
        }

        public String getReadableFields() {
            List<String> fieldStrings = new ArrayList<>();
            for (PacketField<?> field : fields) {
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
            return type.getName().replace("_", " ").toLowerCase(Locale.ENGLISH);
        }
    }

    public static class PacketBuilder<W extends PacketWrapper<?>> {
        private final PacketTypeCommon type;
        private final List<PacketField<?>> fields = new ArrayList<>();
        private Function<PacketValues, W> packetConstructor;

        public PacketBuilder(PacketTypeCommon type) {
            this.type = type;
        }

        @SuppressWarnings("unchecked")
        public <T> PacketBuilder<W> requiredField(Class<T> type, Function<W, T> getter, BiConsumer<W, T> setter, String... names) {
            if (names == null || names.length == 0) {
                throw new IllegalArgumentException("Field registration must specify at least a primary name.");
            }
            String primaryName = names[0];
            String[] aliases = Arrays.copyOfRange(names, 1, names.length);

            this.fields.add(new PacketField<>(primaryName, aliases, type, false, (Function<PacketWrapper<?>, T>) getter, (BiConsumer<PacketWrapper<?>, T>) setter));
            return this;
        }

        @SuppressWarnings("unchecked")
        public <T> PacketBuilder<W> optionalField(Class<T> type, Function<W,  @Nullable T> getter, BiConsumer<W, @Nullable T> setter, String... names) {
            if (names == null || names.length == 0) {
                throw new IllegalArgumentException("Field registration must specify at least a primary name.");
            }
            String primaryName = names[0];
            String[] aliases = Arrays.copyOfRange(names, 1, names.length);

            this.fields.add(new PacketField<>(primaryName, aliases, type, true, (Function<PacketWrapper<?>, T>) getter, (BiConsumer<PacketWrapper<?>, T>) setter));
            return this;
        }

        public PacketBuilder<W> constructor(Function<PacketValues, W> constructor) {
            this.packetConstructor = constructor;
            return this;
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        public void build() {
            if (this.packetConstructor == null) {
                throw new IllegalStateException("Cannot build packet definition for '" + type.getName() + "' because no constructor was provided.");
            }

            REGISTRY.put(this.type, new PacketDefinition(this.type, Collections.unmodifiableList(this.fields), (Function) this.packetConstructor));
        }
    }

    public static class PacketValues {
        private final List<PacketField<?>> registeredFields;
        private final Map<String, Object> values;

        public PacketValues(List<PacketField<?>> registeredFields, Map<String, Object> values) {
            this.registeredFields = registeredFields;
            this.values = values;
        }

        private void assertKeyRegistered(String key) {
            for (PacketField<?> field : registeredFields) {
                if (field.matches(key)) return;
            }
            throw new IllegalArgumentException("Attempted to look up field '" + key + "', but it is not registered in this packet definition's fields.");
        }

        public <T> T get(String key, Class<T> clazz) {
            assertKeyRegistered(key);

            PacketField<?> targetField = null;
            for (PacketField<?> field : registeredFields) {
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

            PacketField<?> targetField = null;
            for (PacketField<?> field : registeredFields) {
                if (field.matches(key)) {
                    targetField = field;
                    break;
                }
            }

            if (targetField != null) {
                if (values.containsKey(targetField.name())) return true;
                for (String alias : targetField.aliases()) {
                    if (values.containsKey(alias)) return true;
                }
                for (String k : values.keySet()) {
                    if (targetField.matches(k)) return true;
                }
            }
            return false;
        }
    }
}