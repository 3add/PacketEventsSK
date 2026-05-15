package dev.threeadd.packeteventssk.element.general.event;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.parser.ParserInstance;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.shanebeee.skr.Registration;
import dev.threeadd.packeteventssk.api.general.PacketSendOrReceiveEvent;
import dev.threeadd.packeteventssk.api.general.PacketSendOrReceiveListener;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class EvtPacketSendOrReceive extends SkriptEvent {

    @SuppressWarnings("unchecked")
    public static void register(Registration reg) {
        ParserInstance.registerData(PacketSendOrReceiveParserData.class, PacketSendOrReceiveParserData::new);

        reg.newEvent(EvtPacketSendOrReceive.class, new Class[]{
                        PacketSendOrReceiveEvent.NettyPacketEvent.class,
                        PacketSendOrReceiveEvent.SyncPacketEvent.class,
                        PacketSendOrReceiveEvent.AsyncPacketEvent.class
                }, "([any] packet|%-packettype%) [(:(sync|async|netty)) processed] [with packet[events] priority (:(lowest|low|normal|high|highest|monitor))]")
                .name("General - On Packet")
                .description("Listen to incoming/outgoing packets, more on [the wiki](https://github.com/3add/PacketEventsSK/wiki/Events)")
                .examples("""
                        on serverbound interact entity packet netty processed:
                            cancel packet
                        """,
                        """
                        # can be used to see which packets get sent in certain circumstances
                        on any packet:
                            send packet type of event-packet to console
                        """)
                .since("1.0.0", "1.0.1 altered", "1.1.0 (changed from struct to event)", "1.1.1 (added packet priority, added listening to all packets and fixed bugs)")
                .register();

        reg.newEventValue(PacketSendOrReceiveEvent.class, PacketWrapper.class)
                .converter(PacketSendOrReceiveEvent::getWrapper)
                .register();

        reg.newEventValue(PacketSendOrReceiveEvent.class, Player.class)
                .converter(event -> event.getEvent().getPlayer())
                .register();
    }

    private @Nullable PacketTypeCommon packetType;
    private ProcessType processType = ProcessType.NETTY;
    private PacketListenerPriority priority = null; // dynamic default

    @Override
    public boolean init(Literal<?>[] args, int matchedPattern, SkriptParser.ParseResult parseResult) {
        this.packetType = (args.length > 0 && args[0] != null) ? (PacketTypeCommon) args[0].getSingle() : null;

        for (String rawTag : parseResult.tags) {
            String tag = rawTag.trim().toLowerCase(Locale.ENGLISH);
            switch (tag) {
                case "sync", "async", "netty" ->
                        this.processType = ProcessType.valueOf(tag.toUpperCase(Locale.ENGLISH));
                case "lowest", "low", "normal", "high", "highest", "monitor" ->
                        this.priority = PacketListenerPriority.valueOf(tag.toUpperCase(Locale.ENGLISH));
            }
        }

        // no priority provided, use defaults based on processType
        if (this.priority == null) {
            if (this.processType == ProcessType.NETTY) {
                this.priority = PacketListenerPriority.NORMAL;
            } else {
                this.priority = PacketListenerPriority.MONITOR;
            }
        }

        // validate event setup
        if ((this.processType == ProcessType.SYNC || this.processType == ProcessType.ASYNC) && this.priority != PacketListenerPriority.MONITOR) {
            Skript.error("You can only listen " + this.processType.toString().toLowerCase(Locale.ENGLISH) + " to packets using the \"monitor\" priority as they can't modify or cancel the packet.");
            return false;
        }

        PacketSendOrReceiveListener.registerListener(packetType, this.processType, this.priority);
        return true;
    }

    @Override
    public boolean load() {

        PacketSendOrReceiveParserData data = getParser().getData(PacketSendOrReceiveParserData.class);
        data.packetType = this.packetType;
        data.processType = this.processType;
        data.priority = this.priority;

        return super.load();
    }

    @Override
    public boolean check(Event event) {
        if (event instanceof PacketSendOrReceiveEvent packetEvent) {
            if (this.packetType != null && packetEvent.getEvent().getPacketType() != this.packetType) {
                return false;
            }

            if (packetEvent.getPriority() != this.priority) {
                return false;
            }

            ProcessType way = switch (event) {
                case PacketSendOrReceiveEvent.NettyPacketEvent _ -> ProcessType.NETTY;
                case PacketSendOrReceiveEvent.SyncPacketEvent _ -> ProcessType.SYNC;
                case PacketSendOrReceiveEvent.AsyncPacketEvent _ -> ProcessType.ASYNC;
                default -> null;
            };

            return way == this.processType;
        }
        return false;
    }

    @Override
    public boolean canExecuteAsynchronously() {
        return true;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        String packetTypeName = (this.packetType != null ? this.packetType.getName().toLowerCase(Locale.ENGLISH).replace("_", " ") : "any");
        String processTypeName = this.processType.name().toLowerCase(Locale.ENGLISH);
        String priorityName = (this.priority != null ? this.priority.name().toLowerCase(Locale.ENGLISH) : "normal");
        return String.format("on %s packet %s processed with priority %s", packetTypeName, processTypeName, priorityName);
    }

    public enum ProcessType {
        NETTY,
        SYNC,
        ASYNC
    }

    public static class PacketSendOrReceiveParserData extends ParserInstance.Data {

        private @Nullable PacketTypeCommon packetType;
        private @Nullable EvtPacketSendOrReceive.ProcessType processType;
        private @Nullable PacketListenerPriority priority;

        public PacketSendOrReceiveParserData(ParserInstance parserInstance) {
            super(parserInstance);
        }

        public @Nullable PacketTypeCommon getPacketType() {
            return packetType;
        }

        public @Nullable EvtPacketSendOrReceive.ProcessType getProcessType() {
            return processType;
        }

        public @Nullable PacketListenerPriority getPriority() {
            return priority;
        }
    }
}