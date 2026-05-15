package dev.threeadd.packeteventssk.element.general.effect;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.shanebeee.skr.Registration;
import dev.threeadd.packeteventssk.api.general.PacketSendOrReceiveEvent;
import dev.threeadd.packeteventssk.element.general.event.EvtPacketSendOrReceive.PacketSendOrReceiveParserData;
import dev.threeadd.packeteventssk.element.general.event.EvtPacketSendOrReceive.ProcessType;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class EffCancelPacket extends Effect {

    public static void register(Registration reg) {
        reg.newEffect(EffCancelPacket.class, "cancel [the] packet")
                .name("General - Cancel Packet")
                .description("""
                        Used to cancel the packet in a packet receive/send event.
                        This just means that the packet won't be processed/sent.
                        """)
                .examples("""
                        on clientbound chunk data netty processed:
                            if player's name isn't "3add":
                                stop
                            cancel the packet
                            send "You can't view my chunks 3add!"
                        """)
                .since("1.0.0", "1.1.1 (fixed bugs)")
                .register();
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        if (!getParser().isCurrentEvent(PacketSendOrReceiveEvent.class)) {
            Skript.error("You can only cancel packets inside of a packet event.");
            return false;
        }

        PacketSendOrReceiveParserData data = getParser().getData(PacketSendOrReceiveParserData.class);
        ProcessType way = data.getProcessType();

        if (way != ProcessType.NETTY) {
            Skript.error("Can't cancel packets in a " + (way == null ? "unknown" : way.toString().toLowerCase(Locale.ENGLISH)) + " processed event, the packets have already been processed at that point. Use a netty processed event instead.");
            return false;
        } else if (data.getPriority() == PacketListenerPriority.MONITOR) {
            Skript.error("You can't alter packets when using the \"monitor\" listening priority.");
            return false;
        }

        return true;
    }

    @Override
    protected void execute(Event event) {
        if (event instanceof PacketSendOrReceiveEvent.NettyPacketEvent triggerEvent) {
            triggerEvent.setCancelled(true);
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "cancel the packet";
    }
}
