package dev.threeadd.packeteventssk.api.util;

import com.github.retrooper.packetevents.util.adventure.AdventureNbtUtil;
import com.shanebeestudios.skbee.api.nbt.NBTCompound;
import com.shanebeestudios.skbee.api.nbt.NBTContainer;

/**
 * Conversion utilities that depend on SkBee's NBT-API.
 * This class is only loaded when SkBee is confirmed to be present.
 */
public class SkBeeConversionUtil {

    @SuppressWarnings("deprecation") // this is what nbt uses
    public static NBTCompound toNbtApiNBTCompound(com.github.retrooper.packetevents.protocol.nbt.NBTCompound peCompound) {
        String snbt = AdventureNbtUtil.toString(peCompound);
        return new NBTContainer(snbt);
    }

    public static com.github.retrooper.packetevents.protocol.nbt.NBTCompound toPeNBTCompound(NBTCompound nbtApiCompound) {
        String snbt = nbtApiCompound.toString();
        return (com.github.retrooper.packetevents.protocol.nbt.NBTCompound) AdventureNbtUtil.fromString(snbt);
    }
}
