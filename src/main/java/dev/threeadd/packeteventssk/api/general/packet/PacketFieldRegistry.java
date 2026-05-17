package dev.threeadd.packeteventssk.api.general.packet;

import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import dev.threeadd.packeteventssk.api.util.field.BaseFieldRegistry;
import dev.threeadd.packeteventssk.api.util.field.FieldRegistrar;

import java.util.List;

public class PacketFieldRegistry extends BaseFieldRegistry<PacketTypeCommon, PacketWrapper<?>> {
    public static final PacketFieldRegistry INSTANCE = new PacketFieldRegistry();

    @Override
    public List<FieldRegistrar> getRegistrars() {
        return List.of(
                new ClientBoundPacketRegistrar(),
                new ServerBoundPacketRegistrar(),
                new SkBeePacketRegistrar()
        );
    }
}