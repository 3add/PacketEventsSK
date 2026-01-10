package threeadd.packetEventsSK.element.general;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.registrations.EventValues;
import com.github.retrooper.packetevents.protocol.PacketSide;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.protocol.player.DiggingAction;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import me.tofaa.entitylib.meta.EntityMeta;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import threeadd.packetEventsSK.element.entity.api.skin.Skin;
import threeadd.packetEventsSK.util.DebugUtil;
import threeadd.packetEventsSK.element.general.api.PacketTriggerEvent;
import threeadd.packetEventsSK.util.registry.PacketTypeRegistry;

import java.util.Locale;

@SuppressWarnings("unused")
public class Types {
    static {
        EventValues.registerEventValue(PacketTriggerEvent.class, Player.class, event -> event.getEvent().getPlayer());
        EventValues.registerEventValue(PacketTriggerEvent.class, PacketWrapper.class, PacketTriggerEvent::getWrapper);

        Classes.registerClass(new ClassInfo<>(PacketWrapper.class, "packet")
                .user("packet")
                .name("General - Packet")
                .description("A packet sent by the client or server")
                .examples("""
                        on interact entity receive netty processed:
                           if packet entity id of event-packet is not {-interactables::%player's uuid%}:
                              stop
                        
                           send "Welcome %player's name%"
                        """)
                .since("1.0")
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
        );

        Classes.registerClass(new ClassInfo<>(PacketTypeCommon.class, "packettype")
                .user("packet ?type")
                .name("General - Packet Type")
                .description("Represents a specific type of packet (e.g. chunk data send)")
                .examples("""
                        on interact entity receive netty processed:
                           if packet entity id of event-packet is not {-interactables::%player's uuid%}:
                              stop
                        F
                           send "Welcome %player's name%"
                        """)
                .since("1.0")
                .parser(new Parser<>() {

                    @Override
                    public @Nullable PacketTypeCommon parse(String input, ParseContext context) {
                        input = input.trim();
                        boolean isSend;
                        String name;

                        if (input.toLowerCase(Locale.ENGLISH).endsWith(" send")) {
                            isSend = true;
                            name = input.substring(0, input.length() - 5);
                        } else if (input.toLowerCase(Locale.ENGLISH).endsWith(" receive")) {
                            isSend = false;
                            name = input.substring(0, input.length() - 8);
                        } else {
                            return null;
                        }

                        return PacketTypeRegistry.getPacket(name, isSend);
                    }

                    @Override
                    public String toString(PacketTypeCommon type, int flags) {
                        return type.getName() + (type.getSide().equals(PacketSide.SERVER) ? " send" : " receive");
                    }

                    @Override
                    public String toVariableNameString(PacketTypeCommon type) {
                        return type.getName();
                    }
                })
        );

        Classes.registerClass(new ClassInfo<>(Skin.class, "skin")
                .user("skin")
                .name("Skin")
                .description("A player skin (texture property list)")
                .examples("""
                        command skinMeNotchForMe:
                            trigger:
                                fetch skin of player named "notch" and store it in {_skin}
                                set displayed skin of player to {_skin} for player
                        """)
                .since("1.0")
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
        );

        Classes.registerClass(new ClassInfo<>(EntityMeta.class, "fakeentitymeta")
                .user("fake[ -]?entity[ -]?meta[ -]?data")
                .name("Fake Entity - Entity Meta Data")
                .description("The entity meta of a fake entity or outbound packet")
                .examples() // TODO
                .since("1.0")
                .parser(new Parser<>() {
                    @Override
                    public boolean canParse(ParseContext context) {
                        return false;
                    }

                    @Override
                    public String toString(EntityMeta meta, int flags) {
                        return "fake entity meta data";
                    }

                    @Override
                    public String toVariableNameString(EntityMeta meta) {
                        return "fakeentitymetadata:" + meta.hashCode();
                    }
                })
        );

        Classes.registerClass(new ClassInfo<>(DiggingAction.class, "diggingaction")
                .user("digging ?action")
                .name("General - Diggin Action")
                .description("An action in a digging packet")
                .examples("""
                        
                        """) // TODO example
                .since("1.0")
                .parser(new Parser<>() {

                    @Override
                    public @Nullable DiggingAction parse(String s, ParseContext context) {

                        for (DiggingAction value : DiggingAction.values()) {
                            if (value.name().equalsIgnoreCase(s))
                                return value;
                        }

                        return null;
                    }

                    @Override
                    public String toString(DiggingAction diggingAction, int flags) {
                        return "digging action " + diggingAction;
                    }

                    @Override
                    public String toVariableNameString(DiggingAction diggingAction) {
                        return "diggingactiion:" + diggingAction.hashCode();
                    }
                })
        );
    }
}
