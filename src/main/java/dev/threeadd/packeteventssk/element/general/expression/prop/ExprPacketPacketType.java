package dev.threeadd.packeteventssk.element.general.expression.prop;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.shanebeee.skr.Registration;
import org.jspecify.annotations.Nullable;

public class ExprPacketPacketType extends SimplePropertyExpression<PacketWrapper<?>, PacketTypeCommon> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprPacketPacketType.class, PacketTypeCommon.class, "packet[ ]type", "packet")
                .name("General - Packet Type")
                .description("The packet type of a packet")
                .examples("""
                        # can be used to see which packets get sent in certain circumstances
                        on any packet:
                            send packet type of event-packet to console
                        """)
                .since("1.1.1")
                .register();
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public @Nullable PacketTypeCommon convert(PacketWrapper<?> packet) {
        return packet.getPacketTypeData().getPacketType();
    }

    @Override
    public Class<? extends PacketTypeCommon> getReturnType() {
        return PacketTypeCommon.class;
    }

    @Override
    protected String getPropertyName() {
        return "packet type";
    }
}
