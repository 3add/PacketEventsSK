package threeadd.packetEventsSK.element.general.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;
import threeadd.packetEventsSK.element.general.api.PacketTriggerEvent;

@Name("General - Event Packet")
@Description("Represents the packet involved in a packet event. This only works inside a packet event.")
@Examples({
        """
        on packet interact entity receive netty processed:
            if packet entity id of event-packet is not {-interactables::%player's uuid%}:
                stop
            
            send "Welcome %player's name%"
        """})
@Since("1.0.0")
public class ExprEventPacket extends SimpleExpression<PacketWrapper> {

    public static void register(SyntaxRegistry registry) {
        registry.register(
                SyntaxRegistry.EXPRESSION,
                SyntaxInfo.Expression.builder(ExprEventPacket.class, PacketWrapper.class)
                        .supplier(ExprEventPacket::new)
                        .addPatterns("[the] event[-]packet")
                        .build()
        );
    }

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        if (!getParser().isCurrentEvent(PacketTriggerEvent.class)) {
            Skript.error("The expression 'event-packet' can only be used inside a packet event!");
            return false;
        }
        return true;
    }

    @Override
    @SuppressWarnings("rawtypes")
    protected PacketWrapper @Nullable[] get(@NotNull Event event) {
        if (event instanceof PacketTriggerEvent packetEvent) {
            return new PacketWrapper[]{packetEvent.getWrapper()};
        }
        return new PacketWrapper[0];
    }

    @Override
    public boolean isSingle() { return true; }

    @Override
    @SuppressWarnings("rawtypes")
    public @NotNull Class<? extends PacketWrapper> getReturnType() { return PacketWrapper.class; }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean debug) { return "event-packet"; }
}