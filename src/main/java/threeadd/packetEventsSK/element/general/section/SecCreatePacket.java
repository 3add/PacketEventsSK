package threeadd.packetEventsSK.element.general.section;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Literal;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import threeadd.packetEventsSK.util.registry.PacketRegistry;
import threeadd.packetEventsSK.util.section.ReturningSection;

@SuppressWarnings("unused")
@Name("General - Create Packet")
@Description("Create a new packet from a packet type.")
@Examples("""
        command killTargetForMe:
            trigger:
                create a new destroy entities send packet:
                    add target entity of player to packet entities of the packet
                    send packet the packet to the player
        """)
@Since("1.0.0")
public class SecCreatePacket extends ReturningSection<PacketWrapper<?>> {

    @SuppressWarnings("unchecked")
    private static final Class<PacketWrapper<?>> clazz = (Class<PacketWrapper<?>>) (Class<?>) PacketWrapper.class;

    public static class PacketBuilder extends LastBuilderExpression<PacketWrapper<?>, SecCreatePacket> {}

    static {
        register(SecCreatePacket.class, clazz, PacketBuilder.class,
                new String[]{
                        "[the] packet [builder]"
                },
                "(make|create) [a] [new] %packettype% packet"
        );
    }

    private Literal<PacketTypeCommon> packetTypeLiteral;

    @SuppressWarnings("unchecked")
    @Override
    protected boolean initialize() {
        this.packetTypeLiteral = (Literal<PacketTypeCommon>) exprs[0];

        if (!PacketRegistry.hasEmptyWrapper(packetTypeLiteral.getSingle())) {
            Skript.error("PacketEventsSK doesn't have a proper implementation for the " + packetTypeLiteral.getSingle() + " packet");
            return false;
        }

        return true;
    }


    @Override
    public PacketWrapper<?> createNewValue(@NotNull Event event) {
        PacketTypeCommon type = packetTypeLiteral.getSingle(event);
        if (type == null) return null;

        return PacketRegistry.createEmpty(type);
    }

    @Override
    public String toString(Event e, boolean debug) {
        return "create " + packetTypeLiteral.getSingle().getName() + " packet";
    }
}