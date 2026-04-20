package threeadd.packetEventsSK.element.general.effect;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.SkriptParser;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;
import threeadd.packetEventsSK.element.general.api.PacketTriggerEvent;
import threeadd.packetEventsSK.element.general.structures.PacketEventStruct.PacketEventParserData;
import threeadd.packetEventsSK.element.general.structures.PacketEventStruct.ProcessWay;
import threeadd.packetEventsSK.util.effect.CustomEffect;

@SuppressWarnings("unused")
@Name("General - Cancel Packet")
@Description("""
        Used to cancel the packet in a packet receive/send event.
        This just means that the packet won't be processed/sent.
        """)
@Example("""
        on chunk data send netty processed:
            if player's name isn't "3add":
                stop
            cancel the packet
            send "You can't view my chunks 3add!"
        """)
@Since("1.0.0")
public class EffCancelPacket extends CustomEffect {

    public static void register(SyntaxRegistry registry) {
        registry.register(
                SyntaxRegistry.EFFECT,
                SyntaxInfo.builder(EffCancelPacket.class)
                        .supplier(EffCancelPacket::new)
                        .addPatterns("cancel [the] packet")
                        .build()
        );
    }

    @Override
    protected boolean initialize(SkriptParser.ParseResult parseResult) {
        PacketEventParserData data = getParser().getData(PacketEventParserData.class);
        if (data == null) {
            Skript.error("Can't cancel packets outside of packet events.");
            return false;
        }

        ProcessWay way = data.getProcessWay();
        if (way != ProcessWay.NETTY) {
            Skript.error("Can't cancel packets outside of netty processed packet events, this is because they have probably already been processed on the netty thread.");
            return false;
        }

        return true;
    }

    @Override
    protected void execute(Event event) {
        if (event instanceof PacketTriggerEvent triggerEvent)
            triggerEvent.setCancelled(true);
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "cancel packet";
    }
}
