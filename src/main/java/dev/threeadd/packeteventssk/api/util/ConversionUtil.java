package dev.threeadd.packeteventssk.api.util;

import com.github.retrooper.packetevents.protocol.world.WorldBlockPosition;
import com.github.retrooper.packetevents.resources.ResourceLocation;
import com.github.retrooper.packetevents.util.Quaternion4f;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.util.Vector3f;
import com.github.retrooper.packetevents.util.Vector3i;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;
import org.joml.Quaternionf;

public class ConversionUtil {

    public static Vector toBukkitVector(com.github.retrooper.packetevents.protocol.world.Location location) {
        if (location == null) return null;
        return new Vector(location.getX(), location.getY(), location.getZ());
    }

    public static Vector toBukkitVector(Vector3i vector3i) {
        if (vector3i == null) return null;
        return new Vector(vector3i.getX() + 0.5, vector3i.getY() + 0.5, vector3i.getZ() + 0.5);
    }

    public static Vector toBukkitVector(Vector3f vector3f) {
        if (vector3f == null) return null;
        return new Vector(vector3f.x, vector3f.y, vector3f.z);
    }

    public static Vector toBukkitVector(Vector3d vector3d) {
        if (vector3d == null) return null;
        return new Vector(vector3d.x, vector3d.y, vector3d.z);
    }

    public static Vector3f toPeVectorF(Vector vector) {
        if (vector == null) return null;
        return new Vector3f((float) vector.getX(), (float) vector.getY(), (float) vector.getZ());
    }

    public static Vector3d toPeVectorD(Vector vector) {
        if (vector == null) return null;
        return new Vector3d((float) vector.getX(), (float) vector.getY(), (float) vector.getZ());
    }

    public static Vector3i toPeVectorI(Vector vector) {
        if (vector == null) return null;
        return new Vector3i(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
    }

    public static Quaternionf toBukkitQuaternionf(Quaternion4f quaternion4f) {
        if (quaternion4f == null) return null;
        return new Quaternionf(quaternion4f.getX(), quaternion4f.getY(), quaternion4f.getZ(), quaternion4f.getW());
    }

    public static Quaternion4f toPeQuaternion4f(Quaternionf quaternionf) {
        if (quaternionf == null) return null;
        return new Quaternion4f(quaternionf.x(), quaternionf.y(), quaternionf.z(), quaternionf.w());
    }

    public static ResourceLocation getWorldKey(World world) {
        if (world == null) return null;
        return new ResourceLocation(world.getKey().toString());
    }

    public static WorldBlockPosition toWorldBlockPosition(Location location) {
        if (location == null) return null;
        ResourceLocation key = getWorldKey(location.getWorld());
        Vector3i vector3i = new Vector3i(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        return new WorldBlockPosition(key, vector3i);
    }
}
