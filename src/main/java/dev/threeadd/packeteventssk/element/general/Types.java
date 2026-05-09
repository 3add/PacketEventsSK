package dev.threeadd.packeteventssk.element.general;

import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import com.github.retrooper.packetevents.protocol.PacketSide;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.shanebeee.skr.Registration;
import dev.threeadd.packeteventssk.api.entity.Skin;
import dev.threeadd.packeteventssk.api.general.PacketTypeRegistry;
import dev.threeadd.packeteventssk.api.util.DebugUtil;
import me.tofaa.entitylib.meta.EntityMeta;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Types {

    public static void register(Registration reg) {
        reg.newType(PacketWrapper.class, "packet")
                .user("packet")
                .name("General - Packet")
                .description("A packet sent by the client or server")
                .examples("""
                        on interact entity receive netty processed:
                           if packet entity id of event-packet is not {-interactables::%player's uuid%}:
                              stop
                        
                           send "Welcome %player's name%"
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
                        return "packet:" + packet.hashCode();
                    }
                })
                .register();

        reg.newType(PacketTypeCommon.class, "packettype")
                .user("packet ?type")
                .name("General - Packet Type")
                .description("Represents a specific type of packet (e.g. clientbound chunk data packet)")
                .examples("""
                        on interact entity receive netty processed:
                           if packet entity id of event-packet is not {-interactables::%player's uuid%}:
                              stop
                        
                           send "Welcome %player's name%"
                        """)
                .since("1.0.0")
                .supplier(() -> {
                    List<PacketTypeCommon> all = new ArrayList<>();
                    all.addAll(PacketTypeRegistry.getAllSendPackets());
                    all.addAll(PacketTypeRegistry.getAllReceivePackets());
                    return all.iterator();
                })
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
                        return "packettype:" + type.getName();
                    }
                })
                .register();

        reg.newType(EntityMeta.class, "entitymeta")
                .user("fake ?entit(y|ies) meta")
                .name("General - Entity Meta")
                .description("The entity meta of a minecraft entity (this can both represent a fake entity's meta or a real entity's meta, but is mostly used for fake entities since the only use for real entities is for packet intercepting).")
                .examples("""
                        command spawn:
                            trigger:
                                create a new fake zombie entity at player for players:
                                    set fake scale attribute of the fake entity to 2
                        """)
                .parser(new Parser<>() {
                    @Override
                    public boolean canParse(ParseContext context) {
                        return false;
                    }

                    @Override
                    public String toString(EntityMeta meta, int flags) {
                        return "entity meta";
                    }

                    @Override
                    public String toVariableNameString(EntityMeta meta) {
                        return "entitymeta:" + meta.hashCode();
                    }
                })
                .since("1.1.0")
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
                        return "skin with properties " + skin.getProperties();
                    }

                    @Override
                    public String toVariableNameString(Skin skin) {
                        return "skin:" + skin.hashCode();
                    }
                })
                .register();
    }
}
