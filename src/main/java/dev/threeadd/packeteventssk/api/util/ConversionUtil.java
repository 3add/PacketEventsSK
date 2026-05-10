package dev.threeadd.packeteventssk.api.util;

import com.github.retrooper.packetevents.protocol.world.WorldBlockPosition;
import com.github.retrooper.packetevents.resources.ResourceLocation;
import com.github.retrooper.packetevents.util.Quaternion4f;
import com.github.retrooper.packetevents.util.Vector3f;
import com.github.retrooper.packetevents.util.Vector3i;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.util.Vector;
import org.joml.Quaternionf;

public class ConversionUtil {

    public static Vector toBukkitVector(com.github.retrooper.packetevents.protocol.world.Location location) {
        return new Vector(location.getX(), location.getY(), location.getZ());
    }

    public static Vector toBukkitVector(Vector3i peVector) {
        return new Vector(peVector.getX() + 0.5, peVector.getY() + 0.5, peVector.getZ() + 0.5);
    }

    public static Vector toBukkitVector(Vector3f vector3f) {
        return new Vector(vector3f.x, vector3f.y, vector3f.z);
    }

    public static Vector3f toPeVectorF(Vector vector) {
        return new Vector3f((float) vector.getX(), (float) vector.getY(), (float) vector.getZ());
    }

    public static Vector3i toPeVectorI(Vector vector) {
        return new Vector3i(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
    }

    public static Quaternionf toBukkitQuaternionf(Quaternion4f quaternion4f) {
        return new Quaternionf(quaternion4f.getX(), quaternion4f.getY(), quaternion4f.getZ(), quaternion4f.getW());
    }

    public static Quaternion4f toPeQuaternion4f(Quaternionf quaternionf) {
        return new Quaternion4f(quaternionf.x(), quaternionf.y(), quaternionf.z(), quaternionf.w());
    }

    public static ResourceLocation getWorldKey(World world) {
        NamespacedKey worldKey = world.getKey();
        return new ResourceLocation(worldKey.getNamespace(), worldKey.getKey());
    }

    public static WorldBlockPosition toWorldBlockPosition(Location location) {
        ResourceLocation key = getWorldKey(location.getWorld());
        Vector3i vector3i = new Vector3i(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        return new WorldBlockPosition(key, vector3i);
    }
}
