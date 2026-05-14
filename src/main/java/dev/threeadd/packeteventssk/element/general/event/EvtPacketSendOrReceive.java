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
                }, "%packettype% [(:(sync|async|netty)) processed] [with priority (:(lowest|low|normal|high|highest|monitor))]")
                .name("General - On Packet")
                .description("Listen to incoming/outgoing packets, more on [the wiki](https://github.com/3add/PacketEventsSK/wiki/Events)")
                .examples("""
                        on serverbound interact entity packet netty processed:
                            cancel packet
                        """)
                .since("1.0.0", "1.0.1 altered", "1.1.0 (changed from struct to event)")
                .register();

        reg.newEventValue(PacketSendOrReceiveEvent.class, PacketWrapper.class)
                .converter(PacketSendOrReceiveEvent::getWrapper)
                .register();

        reg.newEventValue(PacketSendOrReceiveEvent.class, Player.class)
                .converter(event -> event.getEvent().getPlayer())
                .register();
    }

    private PacketTypeCommon packetType;
    private ProcessType processType = ProcessType.NETTY;
    private PacketListenerPriority priority = PacketListenerPriority.NORMAL;

    @Override
    public boolean init(Literal<?>[] args, int matchedPattern, SkriptParser.ParseResult parseResult) {
        PacketTypeCommon packetType = (PacketTypeCommon) args[0].getSingle();

        if (packetType == null) {
            Skript.error("Couldn't find that packet type");
            return false;
        }

        this.packetType = packetType;

        for (String tag : parseResult.tags) {
            switch (tag) {
                case "sync", "async", "netty" ->
                        this.processType = ProcessType.valueOf(tag.toUpperCase(Locale.ENGLISH));
                case "lowest", "low", "normal", "high", "highest", "monitor" ->
                        this.priority = PacketListenerPriority.valueOf(tag.toUpperCase(Locale.ENGLISH));
            }
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
            if (packetEvent.getEvent().getPacketType() != this.packetType) {
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
        return processType == ProcessType.NETTY || processType == ProcessType.ASYNC;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        String packetType = (this.packetType != null ? this.packetType.getName().toLowerCase(Locale.ENGLISH).replace("_", " ") : "unknown");
        String processType = this.processType.name().toLowerCase(Locale.ENGLISH);
        String priorityName = this.priority.name().toLowerCase(Locale.ENGLISH);
        return String.format("on %s packet %s processed with priority %s", packetType, processType, priorityName);
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