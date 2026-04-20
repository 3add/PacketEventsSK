package dev.threeadd.packeteventssk.element.general.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;
import dev.threeadd.packeteventssk.element.general.section.CreatePacketSec;

@Name("General - Last Packet")
@Description("Represents the last packet created in the current event, this only works inside a create packet section")
@Examples(
        """
        command killTargetForMe:
            trigger:
                create a new destroy entities send:
                    add target entity of player to packet entities of the packet
                    send packet the packet to the player
                
                broadcast "%the last packet%"
        """)
@Since("1.0.0")
public class ExprLastPacket extends SimpleExpression<PacketWrapper> {

    public static void register(SyntaxRegistry registry) {
        registry.register(
                SyntaxRegistry.EXPRESSION,
                SyntaxInfo.Expression.builder(ExprLastPacket.class, PacketWrapper.class)
                        .supplier(ExprLastPacket::new)
                        .addPatterns("[the] [last] packet")
                        .build()
        );
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern,
                        Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        return true;
    }

    @Override
    @SuppressWarnings("rawtypes")
    protected PacketWrapper @Nullable [] get(@NotNull Event event) {
        PacketWrapper<?> packet = CreatePacketSec.getLastPacket(event);
        if (packet == null) return null;
        return new PacketWrapper[]{packet};
    }

    @Override
    public boolean isSingle() { return true; }

    @Override
    @SuppressWarnings("rawtypes")
    public @NotNull Class<? extends PacketWrapper> getReturnType() { return PacketWrapper.class; }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean debug) { return "the last packet"; }
}