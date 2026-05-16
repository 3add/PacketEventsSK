package dev.threeadd.packeteventssk.api.general.packet.definition;

import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import dev.threeadd.packeteventssk.api.util.properties.AbstractPropertyRegistry;

public class PacketDefinitionRegistry extends AbstractPropertyRegistry<PacketTypeCommon, PacketWrapper<?>> {
    public static final PacketDefinitionRegistry INSTANCE = new PacketDefinitionRegistry();

    public <W extends PacketWrapper<?>> Builder<W> builder(PacketTypeCommon type, Class<W> wrapperClass) {
        return INSTANCE.createBuilder(type);
    }
}