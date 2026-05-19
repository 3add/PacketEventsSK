package dev.threeadd.packeteventssk.element.general.field.packet;

import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import dev.threeadd.packeteventssk.api.util.LogUtil;
import dev.threeadd.packeteventssk.api.util.field.BaseFieldRegistry;
import dev.threeadd.packeteventssk.api.util.field.FieldRegistrar;

import java.util.ArrayList;
import java.util.List;

public class PacketFieldRegistry extends BaseFieldRegistry<PacketTypeCommon, PacketWrapper<?>> {

    public static final PacketFieldRegistry INSTANCE = new PacketFieldRegistry();
    private final List<FieldRegistrar> registrars;

    private PacketFieldRegistry() {
        this.registrars = new ArrayList<>();

        this.registrars.addAll(List.of(
                new ClientBoundPacketFieldRegistrar(),
                new ServerBoundPacketFieldRegistrar()
        ));

        try {
            Class<?> nbtApiClass = Class.forName("com.shanebeestudios.skbee.api.nbt.NBTApi");
            boolean enabled = (boolean) nbtApiClass.getMethod("isEnabled").invoke(null);
            if (enabled) {
                this.registrars.add(new SkBeePacketFieldRegistrar());
                LogUtil.info("Hooked into SkBee NBT using NBT-API");
            }
        } catch (ClassNotFoundException ignored) {
            LogUtil.error("SkBee not found, PacketEventsSK elements depending on NBT will not be registered");
        } catch (Exception e) {
            throw new IllegalStateException("Failed to hook into SkBee NBT", e);
        }
    }

    @Override
    public List<FieldRegistrar> getRegistrars() {
        return registrars;
    }
}