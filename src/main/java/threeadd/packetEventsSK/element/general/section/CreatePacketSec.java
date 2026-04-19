package threeadd.packetEventsSK.element.general.section;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.Section;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.util.Kleenean;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;
import threeadd.packetEventsSK.util.registry.PacketRegistry;

import java.util.List;
import java.util.WeakHashMap;

@SuppressWarnings("unused")
@Name("General - Create Packet")
@Description("Create a new packet from a packet type.")
@Examples("""
        command killTargetForMe:
            trigger:
                create a new destroy entities packet:
                    add target entity of player to packet entities of the packet
                    send packet the packet to the player
        """)
@Since("1.0.0")
public class CreatePacketSec extends Section {

    private static final WeakHashMap<Event, PacketWrapper<?>> lastPackets = new WeakHashMap<>();

    public static void register(SyntaxRegistry registry) {
        registry.register(
                SyntaxRegistry.SECTION,
                SyntaxInfo.builder(CreatePacketSec.class)
                        .supplier(CreatePacketSec::new)
                        .addPatterns("(make|create) [a] [new] %packettype%:")
                        .build()
        );
    }

    public static PacketWrapper<?> getLastPacket(Event event) {
        return lastPackets.get(event);
    }

    private Literal<PacketTypeCommon> packetTypeLiteral;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions,
                        int matchedPattern,
                        Kleenean isDelayed,
                        SkriptParser.ParseResult parseResult,
                        @Nullable SectionNode sectionNode,
                        @Nullable List<TriggerItem> triggerItems) {

        this.packetTypeLiteral = (Literal<PacketTypeCommon>) expressions[0];

        if (!PacketRegistry.hasEmptyWrapper(packetTypeLiteral.getSingle())) {
            Skript.error("PacketEventsSK doesn't have a proper implementation for the " + packetTypeLiteral.getSingle() + " packet");
            return false;
        }

        if (sectionNode != null) {
            loadOptionalCode(sectionNode);
        }
        return true;
    }

    @Override
    protected TriggerItem walk(@NotNull Event event) {
        PacketWrapper<?> packet = createPacket(event);
        if (packet == null) {
            return getNext();
        }

        lastPackets.put(event, packet);
        return walk(event, true);
    }

    private @Nullable PacketWrapper<?> createPacket(@NotNull Event event) {
        PacketTypeCommon type = packetTypeLiteral.getSingle(event);
        if (type == null) return null;
        return PacketRegistry.createEmpty(type);
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        PacketTypeCommon type = packetTypeLiteral.getSingle();
        return "create " + (type != null ? type.getName() : "unknown") + " packet";
    }
}