package threeadd.packetEventsSK.element.simple.api;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import me.tofaa.entitylib.meta.EntityMeta;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Set;

public class GlowingEntityManager {

    // Entity id -> Player Ids viewers
    private static final Map<Integer, List<UUID>> glowingMap = new ConcurrentHashMap<>();

    private static @Nullable Entity getById(Integer id) {
        return Bukkit.getWorlds().stream().map(World::getEntities)
                .flatMap(Collection::stream)
                .filter(entity -> entity.getEntityId() == id)
                .findAny().orElse(null);
    }

    public static void setGlowingReceivers(int glowingEntityId, List<UUID> glowingReceivers) {
        Entity glowingEntity = getById(glowingEntityId);
        if (glowingEntity == null || glowingReceivers == null) {
            return;
        }

        glowingMap.put(glowingEntityId, new CopyOnWriteArrayList<>(glowingReceivers));
        sendGlowPacket(glowingEntity, glowingReceivers, true);
    }

    public static void setGlowingReceivers(int glowingEntity, UUID glowingReceiver) {
        setGlowingReceivers(glowingEntity, Collections.singletonList(glowingReceiver));
    }

    public static void addGlowingReceivers(int glowingEntityId, Collection<UUID> playersToAdd) {
        Entity glowingEntity = getById(glowingEntityId);
        if (glowingEntity == null || playersToAdd == null || playersToAdd.isEmpty())
            return;

        Set<UUID> newlyAddedReceivers = new HashSet<>();

        List<UUID> receiversList = glowingMap.computeIfAbsent(glowingEntityId, k -> new CopyOnWriteArrayList<>());

        for (UUID playerToAdd : playersToAdd) {
            if (!receiversList.contains(playerToAdd)) {
                receiversList.add(playerToAdd);
                newlyAddedReceivers.add(playerToAdd);
            }
        }

        sendGlowPacket(glowingEntity, newlyAddedReceivers, true);
    }

    public static void addGlowingReceiver(int glowingEntity, UUID playerToAdd) {
        addGlowingReceivers(glowingEntity, Collections.singletonList(playerToAdd));
    }

    public static void removeGlowingReceivers(int glowingEntityId, Collection<UUID> playersToRemove) {
        Entity glowingEntity = getById(glowingEntityId);
        if (glowingEntity == null || playersToRemove == null || playersToRemove.isEmpty())
            return;

        List<UUID> receivers = glowingMap.get(glowingEntityId);

        if (receivers != null) {
            Set<UUID> removedReceivers = new HashSet<>();

            for (UUID playerToRemove : playersToRemove) {
                if (receivers.remove(playerToRemove)) {
                    removedReceivers.add(playerToRemove);
                }
            }

            sendGlowPacket(glowingEntity, removedReceivers, false);

            if (receivers.isEmpty()) {
                glowingMap.remove(glowingEntityId);
            }
        }
    }

    public static void removeGlowingReceiver(int glowingEntity, UUID playerToRemove) {
        removeGlowingReceivers(glowingEntity, Collections.singletonList(playerToRemove));
    }

    public static void clearGlowingReceivers(int glowingEntityId) {
        Entity glowingEntity = getById(glowingEntityId);
        List<UUID> oldReceivers = glowingMap.remove(glowingEntityId);

        if (glowingEntity != null && oldReceivers != null)
            sendGlowPacket(glowingEntity, oldReceivers, false);
    }

    public static boolean isGlowingFor(int entityId, UUID receiverId) {
        List<UUID> receiverIds = glowingMap.get(entityId);
        if (receiverIds == null) return false;

        return receiverIds.contains(receiverId);
    }

    public static List<UUID> getGlowingReceivers(int glowingEntity) {
        return glowingMap.getOrDefault(glowingEntity, Collections.emptyList());
    }

    private static void sendGlowPacket(Entity glowingEntity, Collection<UUID> receivers, boolean shouldGlow) {
        if (glowingEntity == null || receivers == null || receivers.isEmpty()) {
            return;
        }

        EntityMeta meta = new EntityMeta(glowingEntity.getEntityId(), SpigotConversionUtil.getEntityMetadata(glowingEntity));
        meta.setGlowing(shouldGlow);

        WrapperPlayServerEntityMetadata packet = meta.createPacket();

        for (UUID receiverUUID : receivers) {
            Player receiver = Bukkit.getPlayer(receiverUUID);
            if (receiver != null && receiver.isOnline())
                PacketEvents.getAPI().getPlayerManager().getUser(receiver).sendPacket(packet);
        }
    }
}