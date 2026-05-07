package dev.threeadd.packeteventssk.element.general.event;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.parser.ParserInstance;
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
                }, "%packettype% [(:(sync|async|netty)) processed]")
                .name("General - On Packet")
                .description("Listen to incoming/outgoing packets, more on [the wiki](https://github.com/3add/PacketEventsSK/wiki/Events)")
                .examples("""
                        on packet interact entity receive netty processed:
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

    private ProcessType processType = ProcessType.NETTY;
    private PacketTypeCommon packetType;

    @Override
    public boolean init(Literal<?>[] args, int matchedPattern, SkriptParser.ParseResult parseResult) {
        PacketTypeCommon packetType = (PacketTypeCommon) args[0].getSingle();

        if (packetType == null) {
            Skript.error("Couldn't find that packet type");
            return false;
        }

        if (parseResult.hasTag("async") || parseResult.hasTag("sync") || parseResult.hasTag("netty")) {
            this.processType = ProcessType.valueOf(parseResult.tags.getLast().toUpperCase(Locale.ENGLISH));
        }

        this.packetType = packetType;

        PacketSendOrReceiveListener.registerListener(packetType, this.processType);
        return true;
    }

    @Override
    public boolean load() {

        PacketSendOrReceiveParserData data = getParser().getData(PacketSendOrReceiveParserData.class);
        data.packetType = this.packetType;
        data.processType = this.processType;

        return super.load();
    }

    @Override
    public boolean check(Event event) {
        if (event instanceof PacketSendOrReceiveEvent packetEvent) {
            if (packetEvent.getEvent().getPacketType() != this.packetType) {
                return false;
            }

            ProcessType way = switch (event) {
                case PacketSendOrReceiveEvent.NettyPacketEvent e -> ProcessType.NETTY;
                case PacketSendOrReceiveEvent.SyncPacketEvent e -> ProcessType.SYNC;
                case PacketSendOrReceiveEvent.AsyncPacketEvent e -> ProcessType.ASYNC;
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
        String packetType = (this.packetType != null ? this.packetType.getName().toLowerCase(Locale.ENGLISH).replace("_", " ") : "unknown");
        String processType = this.processType.name().toLowerCase(Locale.ENGLISH);
        return String.format("on %s packet %s processed", packetType, processType);
    }

    public enum ProcessType {
        NETTY,
        SYNC,
        ASYNC
    }

    public static class PacketSendOrReceiveParserData extends ParserInstance.Data {
        private @Nullable EvtPacketSendOrReceive.ProcessType processType;
        private @Nullable PacketTypeCommon packetType;

        public PacketSendOrReceiveParserData(ParserInstance parserInstance) {
            super(parserInstance);
        }

        public @Nullable PacketTypeCommon getPacketType() {
            return packetType;
        }

        public @Nullable EvtPacketSendOrReceive.ProcessType getProcessType() {
            return processType;
        }
    }
}