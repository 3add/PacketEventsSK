package dev.threeadd.packeteventssk.api.general;

import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityVelocity;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerGameTestHighlightPos;
import me.tofaa.entitylib.meta.EntityMeta;

import java.util.*;
import java.util.function.Function;

public class PacketConstructorRegistry {

    private static final Map<PacketTypeCommon, PacketDefinition> REGISTRY = new HashMap<>();

    static {
        register(PacketType.Play.Server.ENTITY_VELOCITY)
                .withField("entity id", Number.class, w -> ((WrapperPlayServerEntityVelocity) w).getEntityId())
                .withField("x", Number.class, w -> ((WrapperPlayServerEntityVelocity) w).getVelocity().getX())
                .withField("y", Number.class, w -> ((WrapperPlayServerEntityVelocity) w).getVelocity().getY())
                .withField("z", Number.class, w -> ((WrapperPlayServerEntityVelocity) w).getVelocity().getZ())
                .construct(values -> new WrapperPlayServerEntityVelocity(
                        values.get("entity id", Number.class).intValue(),
                        new Vector3d(values.get("x", Number.class).floatValue(),
                                values.get("y", Number.class).floatValue(),
                                values.get("z", Number.class).floatValue())
                ));

        register(PacketType.Play.Server.GAME_TEST_HIGHLIGHT_POS)
                .withField("x", Number.class, w -> ((WrapperPlayServerGameTestHighlightPos) w).getAbsolutePos().getX())
                .withField("y", Number.class, w -> ((WrapperPlayServerGameTestHighlightPos) w).getAbsolutePos().getY())
                .withField("z", Number.class, w -> ((WrapperPlayServerGameTestHighlightPos) w).getAbsolutePos().getZ())
                .withOptionalField("relative x", Number.class, w -> ((WrapperPlayServerGameTestHighlightPos) w).getRelativePos().getX())
                .withOptionalField("relative y", Number.class, w -> ((WrapperPlayServerGameTestHighlightPos) w).getRelativePos().getY())
                .withOptionalField("relative z", Number.class, w -> ((WrapperPlayServerGameTestHighlightPos) w).getRelativePos().getZ())
                .construct(values -> new WrapperPlayServerGameTestHighlightPos(
                        new Vector3i(values.get("x", Number.class).intValue(),
                                values.get("y", Number.class).intValue(),
                                values.get("z", Number.class).intValue()),
                        new Vector3i(values.getOrElse("relative x", Number.class, 0).intValue(),
                                values.getOrElse("relative y", Number.class, 0).intValue(),
                                values.getOrElse("relative z", Number.class, 0).intValue()
                        )));
        register(PacketType.Play.Server.ENTITY_METADATA)
                .withField("entity id",  Number.class, w -> ((WrapperPlayServerEntityMetadata) w).getEntityId())
                .withField("entity meta", EntityMeta.class, w -> {
                    WrapperPlayServerEntityMetadata packet = (WrapperPlayServerEntityMetadata) w;
                    EntityMeta meta = new EntityMeta(packet.getEntityId());
                    meta.getMetadata().setMetaFromPacket(packet);
                    return meta;
                })
                .construct(values -> new WrapperPlayServerEntityMetadata(values.get("entity id", Number.class).intValue(), values.get("entity meta", EntityMeta.class)));

        // TODO: Populate more packets
    }

    public static PacketBuilder register(PacketTypeCommon type) {
        return new PacketBuilder(type);
    }

    public static PacketDefinition getDefinition(PacketTypeCommon type) {
        return REGISTRY.get(type);
    }

    public static Collection<PacketDefinition> getAllDefinitions() {
        return REGISTRY.values();
    }

    // ADDED: getter function
    public record PacketField(String name, Class<?> expectedType, boolean isOptional, Function<PacketWrapper<?>, Object> getter) {}

    public record PacketDefinition(List<PacketField> fields, Function<PacketValues, PacketWrapper<?>> constructor) {
        public PacketField getField(String name) {
            for (PacketField field : fields) {
                if (field.name().equalsIgnoreCase(name)) return field;
            }
            return null;
        }
    }

    public static class PacketBuilder {
        private final PacketTypeCommon type;
        private final List<PacketField> fields = new ArrayList<>();

        public PacketBuilder(PacketTypeCommon type) {
            this.type = type;
        }

        public PacketBuilder withField(String name, Class<?> type, Function<PacketWrapper<?>, Object> getter) {
            this.fields.add(new PacketField(name, type, false, getter));
            return this;
        }

        public PacketBuilder withOptionalField(String name, Class<?> type, Function<PacketWrapper<?>, Object> getter) {
            this.fields.add(new PacketField(name, type, true, getter));
            return this;
        }

        public void construct(Function<PacketValues, PacketWrapper<?>> constructor) {
            REGISTRY.put(this.type, new PacketDefinition(this.fields, constructor));
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