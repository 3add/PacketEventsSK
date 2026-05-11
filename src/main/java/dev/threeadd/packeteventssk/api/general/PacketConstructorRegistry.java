package dev.threeadd.packeteventssk.api.general;

import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import dev.threeadd.packeteventssk.api.util.ConversionUtil;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import me.tofaa.entitylib.meta.EntityMeta;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.sign.Side;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class PacketConstructorRegistry {

    private static final Map<PacketTypeCommon, PacketDefinition> REGISTRY = new HashMap<>();

    static {
        builder(PacketType.Play.Server.ENTITY_VELOCITY, WrapperPlayServerEntityVelocity.class)
                .requiredField("entity id", Number.class, WrapperPlayServerEntityVelocity::getEntityId,
                        (w, id) -> w.setEntityId(id.intValue()))
                .requiredField("vector", Vector.class, w -> ConversionUtil.toBukkitVector(w.getVelocity()),
                        (w, vector) -> w.setVelocity(ConversionUtil.toPeVectorD(vector)))
                .constructor(values -> {
                    Vector vec = values.get("vector", Vector.class);
                    return new WrapperPlayServerEntityVelocity(
                            values.get("entity id", Number.class).intValue(),
                            new Vector3d(vec.getX(), vec.getY(), vec.getZ())
                    );
                })
                .build();

        builder(PacketType.Play.Server.GAME_TEST_HIGHLIGHT_POS, WrapperPlayServerGameTestHighlightPos.class)
                .requiredField("position", Vector.class, w -> ConversionUtil.toBukkitVector(w.getAbsolutePos()),
                        (w, vector) -> w.setAbsolutePos(ConversionUtil.toPeVectorI(vector)))
                .optionalField("relative position", Vector.class, w -> ConversionUtil.toBukkitVector(w.getRelativePos()),
                        (w, vector) -> w.setRelativePos(ConversionUtil.toPeVectorI(vector)))
                .constructor(values -> new WrapperPlayServerGameTestHighlightPos(
                        ConversionUtil.toPeVectorI(values.get("position", Vector.class)),
                        ConversionUtil.toPeVectorI(values.get("relative position", Vector.class)))
                )
                .build();

        builder(PacketType.Play.Server.ENTITY_METADATA, WrapperPlayServerEntityMetadata.class)
                .requiredField("entity id", Number.class, WrapperPlayServerEntityMetadata::getEntityId,
                        (w, id) -> w.setEntityId(id.intValue()))
                .requiredField("entity meta", EntityMeta.class, w -> {
                    EntityMeta meta = new EntityMeta(w.getEntityId());
                    meta.getMetadata().setMetaFromPacket(w);
                    return meta;
                }, WrapperPlayServerEntityMetadata::setEntityMetadata)
                .constructor(values -> new WrapperPlayServerEntityMetadata(
                        values.get("entity id", Number.class).intValue(),
                        values.get("entity meta", EntityMeta.class))
                )
                .build();

        builder(PacketType.Play.Server.BLOCK_CHANGE, WrapperPlayServerBlockChange.class)
                .requiredField("block position", Vector.class, w -> ConversionUtil.toBukkitVector(w.getBlockPosition()),
                        (w, vector) -> w.setBlockPosition(ConversionUtil.toPeVectorI(vector)))
                .requiredField("block state", BlockData.class, w -> SpigotConversionUtil.toBukkitBlockData(w.getBlockState()),
                        (w, blockData) -> w.setBlockState(SpigotConversionUtil.fromBukkitBlockData(blockData)))
                .constructor(values -> new WrapperPlayServerBlockChange(
                        ConversionUtil.toPeVectorI(values.get("block position", Vector.class)),
                        SpigotConversionUtil.fromBukkitBlockData(values.get("block state", BlockData.class))
                ))
                .build();

        builder(PacketType.Play.Server.OPEN_SIGN_EDITOR, WrapperPlayServerOpenSignEditor.class)
                .requiredField("block position", Vector.class, w -> ConversionUtil.toBukkitVector(w.getPosition()),
                        (w, vector) -> w.setPosition(ConversionUtil.toPeVectorI(vector)))
                .requiredField("sign side", Side.class, w -> w.isFrontText() ? Side.FRONT : Side.BACK
                        , (w, side) -> w.setFrontText(side == Side.FRONT))
                .constructor(values -> new WrapperPlayServerOpenSignEditor(
                        ConversionUtil.toPeVectorI(values.get("block position", Vector.class)),
                        values.get("sign side", Side.class) == Side.FRONT
                ))
                .build();

        builder(PacketType.Play.Server.CLOSE_WINDOW, WrapperPlayServerCloseWindow.class)
                .constructor(_ -> new WrapperPlayServerCloseWindow())
                .build();

        // TODO: Populate more packets
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

    public record PacketField<T>(String name, Class<T> expectedType, boolean isOptional, Function<PacketWrapper<?>, T> getter, BiConsumer<PacketWrapper<?>, T> setter) {}

    public record PacketDefinition(List<PacketField<?>> fields, Function<PacketValues, PacketWrapper<?>> constructor) {
        public PacketField<?> getField(String name) {
            for (PacketField<?> field : fields) {
                if (field.name().equalsIgnoreCase(name)) return field;
            }
            return null;
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
        public <T> PacketBuilder<W> requiredField(String name, Class<T> type, Function<W, T> getter, BiConsumer<W, T> setter) {
            this.fields.add(new PacketField<>(name, type, false, (Function<PacketWrapper<?>, T>) getter, (BiConsumer<PacketWrapper<?>, T>) setter));
            return this;
        }

        @SuppressWarnings("unchecked")
        public <T> PacketBuilder<W> optionalField(String name, Class<T> type, Function<W, T> getter, BiConsumer<W, T> setter) {
            this.fields.add(new PacketField<>(name, type, true, (Function<PacketWrapper<?>, T>) getter, (BiConsumer<PacketWrapper<?>, T>) setter));
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

            REGISTRY.put(this.type, new PacketDefinition(this.fields, (Function) this.packetConstructor));
        }
    }

    public static class PacketValues {
        private final Map<String, Object> values;

        public PacketValues(Map<String, Object> values) {
            this.values = values;
        }

        public <T> T get(String key, Class<T> clazz) {
            return clazz.cast(values.get(key));
        }

        public <T> T getOrElse(String key, Class<T> clazz, T defaultValue) {
            Object val = values.get(key);
            return clazz.isInstance(val) ? clazz.cast(val) : defaultValue;
        }

        public boolean has(String key) {
            return values.containsKey(key);
        }
    }
}