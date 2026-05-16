package dev.threeadd.packeteventssk.api.general;

import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientSelectBundleItem;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import dev.threeadd.packeteventssk.api.util.ConversionUtil;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import me.tofaa.entitylib.meta.EntityMeta;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.sign.Side;
import org.bukkit.util.Vector;
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class PacketConstructorRegistry {

    private static final Map<PacketTypeCommon, PacketDefinition> REGISTRY = new HashMap<>();

    static {
        builder(PacketType.Play.Server.ENTITY_VELOCITY, WrapperPlayServerEntityVelocity.class)
                .requiredField(Number.class, WrapperPlayServerEntityVelocity::getEntityId,
                        (w, id) -> w.setEntityId(id.intValue()),
                        "entity id", "id")
                .requiredField(Vector.class, w -> ConversionUtil.toBukkitVector(w.getVelocity()),
                        (w, vector) -> w.setVelocity(ConversionUtil.toPeVectorD(vector)),
                        "velocity vector", "vector", "velocity")
                .constructor(values -> new WrapperPlayServerEntityVelocity(
                        values.get("entity id", Number.class).intValue(),
                        ConversionUtil.toPeVectorD(values.get("velocity vector", Vector.class)))
                )
                .build();

        builder(PacketType.Play.Server.GAME_TEST_HIGHLIGHT_POS, WrapperPlayServerGameTestHighlightPos.class)
                .requiredField(Vector.class, w -> ConversionUtil.toBukkitVector(w.getAbsolutePos()),
                        (w, vector) -> w.setAbsolutePos(ConversionUtil.toPeVectorI(vector)),
                        "absolute position", "absolute pos", "abs position", "abs pos", "position", "pos")
                .optionalField(Vector.class, w -> ConversionUtil.toBukkitVector(w.getRelativePos()),
                        (w, vector) -> w.setRelativePos(ConversionUtil.toPeVectorI(vector)),
                        "relative position", "relative pos", "rel position", "rel pos")
                .constructor(values -> new WrapperPlayServerGameTestHighlightPos(
                        ConversionUtil.toPeVectorI(values.get("absolute position", Vector.class)),
                        ConversionUtil.toPeVectorI(values.get("relative position", Vector.class)))
                )
                .build();

        builder(PacketType.Play.Server.ENTITY_METADATA, WrapperPlayServerEntityMetadata.class)
                .requiredField(Number.class, WrapperPlayServerEntityMetadata::getEntityId,
                        (w, id) -> w.setEntityId(id.intValue()),
                        "entity id", "id")
                .requiredField(EntityMeta.class, w -> {
                            EntityType type = EntityTracker.getType(w.getEntityId());
                            if (type == null) {
                                throw new IllegalStateException("Failed to find entity type of entity with id " + w.getEntityId());
                            }

                            EntityMeta meta = EntityMeta.createMeta(w.getEntityId(), type);
                            meta.getMetadata().setMetaFromPacket(w);
                            return meta;
                        }, WrapperPlayServerEntityMetadata::setEntityMetadata,
                        "entity metadata", "entity meta", "metadata", "meta")
                .constructor(values -> new WrapperPlayServerEntityMetadata(
                        values.get("entity id", Number.class).intValue(),
                        values.get("entity metadata", EntityMeta.class))
                )
                .build();

        builder(PacketType.Play.Server.BLOCK_CHANGE, WrapperPlayServerBlockChange.class)
                .requiredField(Vector.class, w -> ConversionUtil.toBukkitVector(w.getBlockPosition()),
                        (w, vector) -> w.setBlockPosition(ConversionUtil.toPeVectorI(vector)),
                        "block position", "block pos", "position", "pos")
                .requiredField(BlockData.class, w -> SpigotConversionUtil.toBukkitBlockData(w.getBlockState()),
                        (w, blockData) -> w.setBlockState(SpigotConversionUtil.fromBukkitBlockData(blockData)),
                        "block state", "state", "block data", "data")
                .constructor(values -> new WrapperPlayServerBlockChange(
                        ConversionUtil.toPeVectorI(values.get("block position", Vector.class)),
                        SpigotConversionUtil.fromBukkitBlockData(values.get("block state", BlockData.class))
                ))
                .build();

        builder(PacketType.Play.Server.OPEN_SIGN_EDITOR, WrapperPlayServerOpenSignEditor.class)
                .requiredField(Vector.class, w -> ConversionUtil.toBukkitVector(w.getPosition()),
                        (w, vector) -> w.setPosition(ConversionUtil.toPeVectorI(vector)),
                        "block position", "block pos", "position", "pos")
                .requiredField(Side.class, w -> w.isFrontText() ? Side.FRONT : Side.BACK,
                        (w, side) -> w.setFrontText(side == Side.FRONT),
                        "sign side", "side")
                .constructor(values -> new WrapperPlayServerOpenSignEditor(
                        ConversionUtil.toPeVectorI(values.get("block position", Vector.class)),
                        values.get("sign side", Side.class) == Side.FRONT
                ))
                .build();

        builder(PacketType.Play.Server.CLOSE_WINDOW, WrapperPlayServerCloseWindow.class)
                .constructor(k -> new WrapperPlayServerCloseWindow())
                .build();

        builder(PacketType.Play.Server.DESTROY_ENTITIES, WrapperPlayServerDestroyEntities.class)
                .requiredField(Number[].class,
                        w -> Arrays.stream(w.getEntityIds()).boxed().toArray(Number[]::new),
                        (w, ids) -> w.setEntityIds(Arrays.stream(ids).mapToInt(Number::intValue).toArray()),
                        "entity ids", "ids")
                .constructor(values -> new WrapperPlayServerDestroyEntities(
                        Arrays.stream(values.get("entity ids", Number[].class)).mapToInt(Number::intValue).toArray()
                ))
                .build();

        builder(PacketType.Play.Client.SELECT_BUNDLE_ITEM, WrapperPlayClientSelectBundleItem.class)
                .requiredField(Number.class, WrapperPlayClientSelectBundleItem::getSlotId,
                        (w, id) -> w.setSlotId(id.intValue()),
                        "slot id", "id")
                .requiredField(Number.class, WrapperPlayClientSelectBundleItem::getSelectedItemIndex,
                        (w, index) -> w.setSelectedItemIndex(index.intValue()),
                        "selected item index", "selected index", "item index", "index")
                .constructor(values -> new WrapperPlayClientSelectBundleItem(
                        values.get("slot id", Number.class).intValue(),
                        values.get("selected item index", Number.class).intValue()
                ))
                .build();
    }

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
        public <T> PacketBuilder<W> optionalField(Class<T> type, Function<W, T> getter, BiConsumer<W, T> setter, String... names) {
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