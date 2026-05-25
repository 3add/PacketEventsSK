package dev.threeadd.packeteventssk.element.general;

import ch.njol.skript.classes.Parser;
import ch.njol.skript.classes.Serializer;
import ch.njol.skript.lang.ParseContext;
import ch.njol.yggdrasil.Fields;
import com.github.retrooper.packetevents.protocol.PacketSide;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.protocol.player.InteractionHand;
import com.github.retrooper.packetevents.protocol.player.TextureProperty;
import com.github.retrooper.packetevents.protocol.world.blockentity.BlockEntityType;
import com.github.retrooper.packetevents.protocol.world.blockentity.BlockEntityTypes;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.shanebeee.skr.Registration;
import com.github.shanebeee.skr.skript.EnumWrapper;
import dev.threeadd.packeteventssk.api.entity.Skin;
import dev.threeadd.packeteventssk.api.general.packet.PacketTypeRegistry;
import dev.threeadd.packeteventssk.api.util.DebugUtil;
import org.bukkit.block.sign.Side;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.converter.Converters;

import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class Types {

    @SuppressWarnings("UnstableApiUsage")
    public static void register(Registration reg) {
        reg.newType(PacketWrapper.class, "packet")
                .user("packet")
                .name("General - Packet")
                .description("A packet sent by the client or server")
                .examples("""
                        on serverbound interact entity packet netty processed:
                            cancel packet
                        """)
                .since("1.0.0")
                .parser(new Parser<PacketWrapper<?>>() {

                    @Override
                    public boolean canParse(ParseContext context) {
                        return false;
                    }

                    @Override
                    public String toString(PacketWrapper<?> packet, int flags) {
                        return DebugUtil.getDebugString(packet);
                    }

                    @Override
                    public String toVariableNameString(PacketWrapper<?> packet) {
                        return "packet:" + packet.hashCode(); // TODO improve
                    }
                })
                .register();

        Converters.registerConverter(PacketWrapper.class, PacketTypeCommon.class, packet -> packet.getPacketTypeData().getPacketType());

        reg.newType(PacketTypeCommon.class, "packettype")
                .user("packet ?types?")
                .name("General - Packet Type")
                .description("Represents a specific type of packet (e.g. clientbound chunk data packet)")
                .examples("""
                        on any packet:
                            if event-packet is clientbound:
                                send packet type of event-packet to console
                        """)
                .since("1.0.0")
                .supplier(() -> PacketTypeRegistry.getAllPackets().iterator())
                .parser(new Parser<>() {

                    @Override
                    public @Nullable PacketTypeCommon parse(String input, ParseContext context) {
                        input = input.trim();
                        if (input.toLowerCase(Locale.ENGLISH).endsWith(" packet")) {
                            input = input.substring(0, input.length() - " packet".length())
                                    .trim()
                                    .toLowerCase(Locale.ENGLISH);
                        }

                        boolean isClientBound;
                        String name;

                        if (input.startsWith("clientbound ")) {
                            isClientBound = true;
                            name = input.substring("clientbound ".length());
                        } else if (input.startsWith("serverbound ")) {
                            isClientBound = false;
                            name = input.substring("serverbound ".length());
                        } else {
                            return null;
                        }

                        return PacketTypeRegistry.getPacket(name, isClientBound);
                    }

                    @Override
                    public String toString(PacketTypeCommon type, int flags) {
                        return (type.getSide().equals(PacketSide.SERVER) ? "clientbound" : "serverbound")
                                + " " + type.getName().toLowerCase(Locale.ENGLISH).replace("_", " ");
                    }

                    @Override
                    public String toVariableNameString(PacketTypeCommon type) {
                        return "packettype:" + type.getName().toLowerCase(Locale.ENGLISH).replace("_", " ");
                    }
                })
                // TODO add serialization when skript fixes yggdrasil
                .register();

        reg.newType(BlockEntityType.class, "blockentitytype")
                .user("block ?entit(y|ies) types?")
                .name("General - Block Entity Type")
                .description("Represents a type of block entity (e.g. chest, sign, etc.)")
                .examples("""
                        # Snippet from https://docs.packeteventssk.threeadd.dev/examples/mod-detection.html
                        set {_setTextPacket} to a new clientbound block entity data packet:
                            block position: {_pos}
                            block entity type: sign block entity type
                            nbt compound: createSignNBT({_keybind})
                        """)
                .since("1.1.0")
                .supplier(() -> BlockEntityTypes.values().iterator())
                .parser(new Parser<>() {

                    @Override
                    public BlockEntityType parse(String input, ParseContext context) {
                        input = input.trim().replace(" ", "_").toLowerCase(Locale.ENGLISH); // has to be lowercase
                        if (input.endsWith("_block_entity_type")) {
                            input = input.substring(0, input.length() - "_block_entity_type".length());
                        }
                        return BlockEntityTypes.getByName(input);
                    }

                    @Override
                    public String toString(BlockEntityType type, int flags) {
                        return type.getName().getKey().toLowerCase(Locale.ENGLISH).replace("_", " ") + " block entity type";
                    }

                    @Override
                    public String toVariableNameString(BlockEntityType type) {
                        return "blockentitytype:" + type.getName().getKey().toLowerCase(Locale.ENGLISH).replace("_", " ");
                    }
                })
                .serializer(new Serializer<>() {

                    @Override
                    public Fields serialize(BlockEntityType o) {
                        Fields fields = new Fields();
                        fields.putObject("name", o.getName().getKey().toLowerCase(Locale.ENGLISH));

                        return fields;
                    }

                    @Override
                    public BlockEntityType deserialize(Fields fields) throws StreamCorruptedException {
                        String name = fields.getObject("name", String.class);
                        if (name == null) {
                            throw new StreamCorruptedException("Missing block entity type name");
                        }

                        return BlockEntityTypes.getByName(name);
                    }

                    @Override
                    public boolean mustSyncDeserialization() {
                        return true;
                    }

                    @Override
                    protected boolean canBeInstantiated() {
                        return false;
                    }
                })
                .register();

        EnumWrapper<Side> SIGN_SIDE_ENUM = new EnumWrapper<>(Side.class);
        reg.newEnumType(Side.class, SIGN_SIDE_ENUM, "signside")
                .user("sign ?sides?")
                .name("General - Sign Side")
                .description("Represents a side of a sign block (front or back)")
                .examples("""
                        # Snippet from https://docs.packeteventssk.threeadd.dev/examples/mod-detection.html
                        set {_setTextPacket} to a new clientbound block entity data packet:
                            block position: {_pos}
                            block entity type: sign block entity type
                            nbt compound: createSignNBT({_keybind})
                        """)
                .since("1.1.0")
                .register();

        EnumWrapper<InteractionHand> interactionHand = new EnumWrapper<>(InteractionHand.class);
        reg.newEnumType(InteractionHand.class, interactionHand, "interactionhand")
                .user("interaction ?hands?")
                .name("General - Interaction Hand")
                .description("Represents an interaction hand (main hand or off hand)")
                .examples("""
                        # Snippet from https://docs.packeteventssk.threeadd.dev/examples/welcome-display.html
                        on serverbound interact entity:
                            set {_id} to packet field entity id of event-packet
                            set {_hand} to packet field hand of event-packet
                            set {_sneaking} to packet field sneaking state of event-packet
                        
                            if all:
                                {_id} is {-interactables::%player's uuid%}
                                # this packet is sent for each hand when just regular clicking
                                # "main hand" is parsed as equipment slot if literal so we parse from text
                                {_hand} is "main hand" parsed as interaction hand
                                {_sneaking} is false
                            then:
                                send "<rainbow>welcome player!"
                        """)
                .since("1.1.2")
                .register();

        reg.newType(Skin.class, "skin")
                .user("skin")
                .name("Skin")
                .description("A player skin (texture property list)")
                .examples("""
                        command skinMeNotchForMe:
                            trigger:
                                fetch skin of player named "notch" and store it in {_skin}
                                set displayed skin of player to {_skin} for player
                        """)
                .since("1.0.0")
                .parser(new Parser<>() {

                    @Override
                    public boolean canParse(ParseContext context) {
                        return false;
                    }

                    @Override
                    public String toString(Skin skin, int flags) {
                        return skin.properties().stream()
                                .map(prop -> "value: '" + prop.getValue() + "', signature: '" + prop.getSignature() + "'")
                                .collect(Collectors.joining(", "));
                    }

                    @Override
                    public String toVariableNameString(Skin skin) {
                        return "skin:" + toString(skin, 0);
                    }
                })
                .serializer(new Serializer<>() {

                    @Override
                    public Fields serialize(Skin o) {
                        Fields fields = new Fields();

                        String[] values = o.properties().stream().map(TextureProperty::getValue).toArray(String[]::new);
                        String[] signatures = o.properties().stream().map(TextureProperty::getSignature).toArray(String[]::new);

                        fields.putObject("values", values);
                        fields.putObject("signatures", signatures);

                        return fields;
                    }

                    @Override
                    protected Skin deserialize(Fields fields) throws StreamCorruptedException {
                        String[] values = fields.getObject("values", String[].class);
                        String[] signatures = fields.getObject("signatures", String[].class);

                        if (values == null || values.length == 0) {
                            throw new StreamCorruptedException("Skin properties cannot be empty");
                        }

                        List<TextureProperty> properties = new ArrayList<>();
                        for (int i = 0; i < values.length; i++) {
                            String value = values[i];
                            String signature = (signatures != null && i < signatures.length) ? signatures[i] : null;

                            properties.add(new TextureProperty("textures", value, signature));
                        }

                        return new Skin(properties);
                    }

                    @Override
                    public boolean mustSyncDeserialization() {
                        return true;
                    }

                    @Override
                    protected boolean canBeInstantiated() {
                        return false;
                    }
                })
                .register();
    }
}
