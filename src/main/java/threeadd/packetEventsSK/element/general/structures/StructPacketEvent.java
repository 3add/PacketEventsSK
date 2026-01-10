package threeadd.packetEventsSK.element.general.structures;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.Trigger;
import ch.njol.skript.lang.parser.ParserInstance;
import ch.njol.skript.lang.util.SimpleEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.script.Script;
import org.skriptlang.skript.lang.structure.Structure;
import threeadd.packetEventsSK.PacketEventsSK;
import threeadd.packetEventsSK.element.general.api.PacketEventRegistry;
import threeadd.packetEventsSK.element.general.api.PacketEventRegistry.ListenerData;
import threeadd.packetEventsSK.element.general.api.PacketTriggerEvent;

import java.util.Locale;

@Name("General - On Packet")
@Description("Listen to incoming/outgoing packets, more on [the wiki](https://github.com/3add/PacketEventsSK/wiki/Events)")
@Example("""
        on interact entity receive netty processed:
           if packet entity id of event-packet is not {-interactables::%player's uuid%}:
              stop
        
           send "Welcome %player's name%"
        """)
public class StructPacketEvent extends Structure {

    static {
        Skript.registerStructure(StructPacketEvent.class, "[on] [packet] %packettype% [:(sync|async|netty) processed]");
        ParserInstance.registerData(PacketEventParserData.class, PacketEventParserData::new);
    }

    private ProcessWay processWay = ProcessWay.NETTY;
    private EntryContainer container;
    private PacketTypeCommon packetType;
    private Trigger trigger;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Literal<?>[] args, int matchedPattern, SkriptParser.ParseResult parseResult, @UnknownNullability EntryContainer entryContainer) {
        this.container = entryContainer;

        Literal<PacketTypeCommon> packetTypeLiteral = (Literal<PacketTypeCommon>) args[0];
        PacketTypeCommon packet = packetTypeLiteral.getSingle();

        if (packet == null) {
            Skript.error("Couldn't find that packet type");
            return false;
        }

        if (parseResult.hasTag("async")
                ||parseResult.hasTag("sync")
                || parseResult.hasTag("netty")) {

            this.processWay = ProcessWay.valueOf(parseResult.tags.getLast().toUpperCase(Locale.ENGLISH));
        }

        this.packetType = packet;
        return true;
    }

    @Override
    public boolean load() {
        getParser().setCurrentEvent("PacketTriggerEvent", PacketTriggerEvent.class);
        Script currentScript = getParser().getCurrentScript();

        SectionNode triggerNode = this.container.getSource();

        PacketEventParserData data = getParser().getData(PacketEventParserData.class);

        if (packetType.getWrapperClass() != null)
            data.packetType = this.packetType;

        data.processWay = this.processWay;

        Trigger trigger = new Trigger(currentScript, "on packet " + packetType.getName(), new SimpleEvent(), ScriptLoader.loadItems(triggerNode));
        trigger.setLineNumber(triggerNode.getLine());

        PacketEventRegistry.addTrigger(new ListenerData(data), trigger);
        this.trigger = trigger;

        ParserInstance.get().deleteCurrentEvent();
        data.clear();
        return true;
    }

    @Override
    public void unload() {
        PacketEventRegistry.removeTrigger(trigger);
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "on " + packetType.getName() + " packet event";
    }

    public enum ProcessWay {
        NETTY,
        SYNC,
        ASYNC;

        public void process(Trigger trigger, Event event) {
            switch (this) {
                case NETTY -> trigger.execute(event);
                case SYNC -> Bukkit.getScheduler().runTask(PacketEventsSK.getInstance(), () -> trigger.execute(event));
                case ASYNC -> Bukkit.getScheduler().runTaskAsynchronously(PacketEventsSK.getInstance(), () -> trigger.execute(event));
            }
        }
    }

    public static class PacketEventParserData extends ParserInstance.Data {

        private @Nullable ProcessWay processWay;
        private @Nullable PacketTypeCommon packetType;

        public PacketEventParserData(ParserInstance parserInstance) {
            super(parserInstance);
        }

        public @Nullable PacketTypeCommon getPacketType() {
            return packetType;
        }

        public @Nullable ProcessWay getProcessWay() {
            return processWay;
        }

        private void clear() {
            packetType = null;
            processWay = null;
        }
    }
}
