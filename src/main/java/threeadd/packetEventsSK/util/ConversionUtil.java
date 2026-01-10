package threeadd.packetEventsSK.util;

import com.github.retrooper.packetevents.protocol.player.GameMode;
import com.github.retrooper.packetevents.protocol.world.WorldBlockPosition;
import com.github.retrooper.packetevents.resources.ResourceLocation;
import com.github.retrooper.packetevents.util.Quaternion4f;
import com.github.retrooper.packetevents.util.Vector3f;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity.InteractAction;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.util.Vector;
import org.joml.Quaternionf;

public class ConversionUtil {

    public static Entity getEntityById(int id) {
        return Bukkit.getWorlds().stream()
                .flatMap(world -> world.getEntities().stream())
                .filter(entity -> entity.getEntityId() == id)
                .findAny().orElseThrow(() -> new IllegalArgumentException("Unknow entity with id " + id));
    }

    public static InteractAction toPeAction(ClickType clickType) {
        return switch (clickType) {
            case LEFT -> InteractAction.ATTACK;
            case RIGHT -> InteractAction.INTERACT;
            default -> throw new IllegalArgumentException("Not a convertable clickType " + clickType);
        };
    }

    public static ClickType toBukkitClick(InteractAction peAction) {
        return switch (peAction) {
            case INTERACT, INTERACT_AT -> ClickType.RIGHT;
            case ATTACK -> ClickType.LEFT;
        };
    }

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

    public static GameMode toPeGameMode(org.bukkit.GameMode bukkitGameMode) {
        return GameMode.valueOf(bukkitGameMode.name());
    }

    public static org.bukkit.GameMode toBukkitGameMode(GameMode peGameMode) {
        return org.bukkit.GameMode.valueOf(peGameMode.name());
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
