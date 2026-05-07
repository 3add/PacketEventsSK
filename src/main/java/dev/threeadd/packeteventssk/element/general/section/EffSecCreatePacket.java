package dev.threeadd.packeteventssk.element.general.section;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.*;
import ch.njol.util.Kleenean;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.shanebeee.skr.Registration;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EffSecCreatePacket extends EffectSection {

    public static void register(Registration reg) {
        reg.newSection(EffSecCreatePacket.class, "(make|create) [a] [new] %packettype% [and store (it|the result) in %-objects%]")
                .name("General - Create Packet")
                .description("""
                       Create a new packet from a packet type.
                       This creates its own internal event, which means previous event-values will not work.
                       """)
                .examples("""
                        command killTargetForMe:
                            trigger:
                                create a new destroy entities send and store it in {_packet}:
                                    add target entity of player to packet entities of {_packet}
                                    send packet {_packet} to the player
                        """)
                .since("1.0.0")
                .register();

        reg.newEventValue(CreatePacketEvent.class, PacketWrapper.class)
                .converter(CreatePacketEvent::getPacketWrapper)
                .register();
    }

    private Literal<PacketTypeCommon> packetTypeLiteral;
    private @Nullable Expression<Object> storeExpr;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions,
                        int matchedPattern,
                        Kleenean isDelayed,
                        SkriptParser.ParseResult parseResult,
                        @Nullable SectionNode sectionNode,
                        @Nullable List<TriggerItem> triggerItems) {

        this.packetTypeLiteral = (Literal<PacketTypeCommon>) expressions[0];

        if (expressions[1] != null) {
            this.storeExpr = (Expression<Object>) expressions[1];
            if (!Changer.ChangerUtils.acceptsChange(this.storeExpr, Changer.ChangeMode.SET, PacketWrapper.class)) {
                Skript.error(this.storeExpr.toString(null, Skript.debug()) + " cannot be set to store a packet");
                return false;
            }
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

        if (this.storeExpr != null) {
            this.storeExpr.change(event, new Object[]{packet}, Changer.ChangeMode.SET);
        }

        return walk(event, true);
    }

    private @Nullable PacketWrapper<?> createPacket(@NotNull Event event) {
        PacketTypeCommon type = this.packetTypeLiteral.getSingle(event);
        if (type == null) return null;
        return null; // TODO impl a better way to do this
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        PacketTypeCommon type = this.packetTypeLiteral.getSingle();
        String packetType = (type != null ? type.getName() : "unknown");
        return String.format("create %s packet", packetType);
    }

    public static class CreatePacketEvent extends Event {
        private final PacketWrapper<?> packetWrapper;

        public CreatePacketEvent(PacketWrapper<?> packetWrapper) {
            this.packetWrapper = packetWrapper;
        }

        public PacketWrapper<?> getPacketWrapper() {
            return packetWrapper;
        }

        @Override
        public @NotNull HandlerList getHandlers() {
            throw new IllegalStateException();
        }
    }
}