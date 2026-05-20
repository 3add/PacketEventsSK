package dev.threeadd.packeteventssk.element.general.field;

import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.world.blockentity.BlockEntityType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBlockEntityData;
import com.shanebeestudios.skbee.api.nbt.NBTCompound;
import dev.threeadd.packeteventssk.api.util.ConversionUtil;
import dev.threeadd.packeteventssk.api.util.SkBeeConversionUtil;
import dev.threeadd.packeteventssk.api.util.field.FieldRegistrar;
import org.bukkit.util.Vector;

/**
 * Registers SkBee-dependent packet definitions into {@link PacketFieldRegistry}.
 * This class is only loaded when SkBee is confirmed to be present at runtime.
 */
public class SkBeePacketFieldRegistrar implements FieldRegistrar {

    @Override
    public void register() {

        PacketFieldRegistry.INSTANCE.builder(PacketType.Play.Server.BLOCK_ENTITY_DATA, WrapperPlayServerBlockEntityData.class)
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
                        ConversionUtil.toPeVectorI(values.getRequired("block position", Vector.class)),
                        values.getRequired("block entity type", BlockEntityType.class),
                        SkBeeConversionUtil.toPeNBTCompound(values.getRequired("nbt compound", NBTCompound.class))
                ))
                .build();
    }
}
