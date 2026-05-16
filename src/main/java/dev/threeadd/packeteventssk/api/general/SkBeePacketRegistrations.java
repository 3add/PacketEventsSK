package dev.threeadd.packeteventssk.api.general;

import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.world.blockentity.BlockEntityType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBlockEntityData;
import com.shanebeestudios.skbee.api.nbt.NBTCompound;
import dev.threeadd.packeteventssk.api.util.ConversionUtil;
import dev.threeadd.packeteventssk.api.util.SkBeeConversionUtil;
import org.bukkit.util.Vector;

/**
 * Registers SkBee-dependent packet definitions into {@link PacketConstructorRegistry}.
 * This class is only loaded when SkBee is confirmed to be present at runtime.
 */
public class SkBeePacketRegistrations {

    public static void register() {
        PacketConstructorRegistry.builder(PacketType.Play.Server.BLOCK_ENTITY_DATA, WrapperPlayServerBlockEntityData.class)
                .requiredField(Vector.class, w -> ConversionUtil.toBukkitVector(w.getPosition()),
                        (w, vector) -> w.setPosition(ConversionUtil.toPeVectorI(vector)),
                        "block position", "block pos", "position", "pos")
                .requiredField(BlockEntityType.class, WrapperPlayServerBlockEntityData::getBlockEntityType,
                        WrapperPlayServerBlockEntityData::setType,
                        "block entity type", "entity type", "type")
                .requiredField(NBTCompound.class, w -> SkBeeConversionUtil.toNbtApiNBTCompound(w.getNBT()),
                        (w, nbt) -> w.setNBT(SkBeeConversionUtil.toPeNBTCompound(nbt)),
                        "nbt compound", "nbt", "compound")
                .constructor(values -> new WrapperPlayServerBlockEntityData(
                        ConversionUtil.toPeVectorI(values.get("block position", Vector.class)),
                        values.get("block entity type", BlockEntityType.class),
                        SkBeeConversionUtil.toPeNBTCompound(values.get("nbt compound", NBTCompound.class))
                ))
                .build();
    }
}
