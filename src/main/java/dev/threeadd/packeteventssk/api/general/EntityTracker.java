package dev.threeadd.packeteventssk.api.general;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.event.UserDisconnectEvent;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class EntityTracker implements PacketListener {

    private static final Map<UUID, Map<Integer, EntityType>> PLAYER_ENTITIES = new ConcurrentHashMap<>();

    /**
     * Get the type of entity as viewed by a specific player. This method first checks the cache for the given player and entity id, and if not found, falls back to the global frequency-based method.
     *
     * @param playerUuid The player uuid of the player viewing the owning entity
     * @param entityId   The entity id of the owning entity
     * @return The type of the entity viewed by the player
     */
    public static EntityType getType(UUID playerUuid, int entityId) {
        Map<Integer, EntityType> playerCache = PLAYER_ENTITIES.get(playerUuid);
        if (playerCache != null) {
            EntityType type = playerCache.get(entityId);
            if (type != null) {
                return type;
            }
        }

        return getType(entityId);
    }

    /**
     * Gets the most frequently seen entity type for the given entity id across all players.
     * This is a best-effort method to determine the entity type when no specific player context is available,
     * but it may be inaccurate if multiple entity types with the same id are viewed by different players.
     * Use {@link EntityTracker#getType(UUID, int)} when possible for more accurate results.
     *
     * @param entityId The entity id of the owning entity
     * @return The most frequently seen type for this entity id, or {@link EntityTypes#ENTITY} if no clients are viewing it or if there is a tie in frequencies.
     */
    public static EntityType getType(int entityId) {
        Map<EntityType, Integer> frequencies = new HashMap<>();
        int maxCount = 0;
        EntityType mostFrequent = null;

        for (Map<Integer, EntityType> playerMap : PLAYER_ENTITIES.values()) {
            EntityType type = playerMap.get(entityId);
            if (type != null) {
                int count = frequencies.getOrDefault(type, 0) + 1;
                frequencies.put(type, count);

                if (count > maxCount) {
                    maxCount = count;
                    mostFrequent = type;
                }
            }
        }

        return mostFrequent != null ? mostFrequent : EntityTypes.ENTITY;
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.SPAWN_ENTITY) {
            handleSpawn(event);
        } else if (event.getPacketType() == PacketType.Play.Server.DESTROY_ENTITIES) {
            handleDestroy(event);
        }
    }

    private void handleSpawn(PacketSendEvent event) {
        WrapperPlayServerSpawnEntity spawnPacket = new WrapperPlayServerSpawnEntity(event);
        int entityId = spawnPacket.getEntityId();
        EntityType type = spawnPacket.getEntityType();
        UUID userUuid = event.getUser().getUUID();

        PLAYER_ENTITIES.computeIfAbsent(userUuid, k -> new ConcurrentHashMap<>()).put(entityId, type);
    }

    private void handleDestroy(PacketSendEvent event) {
        WrapperPlayServerDestroyEntities destroyPacket = new WrapperPlayServerDestroyEntities(event);
        UUID userUuid = event.getUser().getUUID();
        Map<Integer, EntityType> playerCache = PLAYER_ENTITIES.get(userUuid);

        if (playerCache != null) {
            for (int id : destroyPacket.getEntityIds()) {
                playerCache.remove(id);
            }
        }
    }

    @Override
    public void onUserDisconnect(UserDisconnectEvent event) {
        if (event.getUser().getUUID() == null) return;
        PLAYER_ENTITIES.remove(event.getUser().getUUID());
    }
}