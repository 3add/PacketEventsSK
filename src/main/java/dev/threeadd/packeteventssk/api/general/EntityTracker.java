package dev.threeadd.packeteventssk.api.general;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.event.UserDisconnectEvent;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerJoinGame;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetPassengers;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import me.tofaa.entitylib.EntityLib;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class EntityTracker implements PacketListener {

    private static final Map<UUID, Map<Integer, TrackedEntity>> PLAYER_ENTITIES = new ConcurrentHashMap<>();

    public static class TrackedEntity {
        private final int entityId;
        private EntityType type;
        private final Set<Integer> fakePassengers = ConcurrentHashMap.newKeySet();

        public TrackedEntity(int entityId, @Nullable EntityType type) {
            this.entityId = entityId;
            this.type = type;
        }

        public int getEntityId() {
            return this.entityId;
        }

        @Nullable
        public EntityType getType() {
            return this.type;
        }

        public void setType(@Nullable EntityType type) {
            this.type = type;
        }

        public Set<Integer> getFakePassengers() {
            return this.fakePassengers;
        }

        public void addFakePassenger(int passengerId) {
            this.fakePassengers.add(passengerId);
        }

        public void clearFakePassengers() {
            this.fakePassengers.clear();
        }
    }

    @Nullable
    public static Set<Integer> getFakePassengers(UUID playerUuid, int vehicleId) {
        Map<Integer, TrackedEntity> playerCache = PLAYER_ENTITIES.get(playerUuid);
        if (playerCache != null) {
            TrackedEntity tracked = playerCache.get(vehicleId);
            if (tracked != null) {
                return tracked.getFakePassengers();
            }
        }
        return null;
    }

    @Nullable
    public static EntityType getType(UUID playerUuid, int entityId) {
        Map<Integer, TrackedEntity> playerCache = PLAYER_ENTITIES.get(playerUuid);
        if (playerCache != null) {
            TrackedEntity tracked = playerCache.get(entityId);
            if (tracked != null && tracked.getType() != null) {
                return tracked.getType();
            }
        }
        return getType(entityId);
    }

    @Nullable
    public static EntityType getType(int entityId) {
        Map<EntityType, Integer> frequencies = new HashMap<>();
        int maxCount = 0;
        EntityType mostFrequent = null;

        for (Map<Integer, TrackedEntity> playerMap : PLAYER_ENTITIES.values()) {
            TrackedEntity tracked = playerMap.get(entityId);
            if (tracked != null && tracked.getType() != null) {
                EntityType type = tracked.getType();
                int count = frequencies.getOrDefault(type, 0) + 1;
                frequencies.put(type, count);

                if (count > maxCount) {
                    maxCount = count;
                    mostFrequent = type;
                }
            }
        }
        return mostFrequent;
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        switch (event.getPacketType()) {
            // type tracking
            case PacketType.Play.Server.SPAWN_ENTITY -> handleSpawn(event);
            case PacketType.Play.Server.JOIN_GAME -> handleJoinGame(event);

            // passengers tracking
            case PacketType.Play.Server.SET_PASSENGERS -> handleSetPassengers(event);

            // cleanup
            case PacketType.Play.Server.DESTROY_ENTITIES -> handleDestroy(event);
            default -> {}
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void handleSpawn(PacketSendEvent event) {
        WrapperPlayServerSpawnEntity spawnPacket = new WrapperPlayServerSpawnEntity(event);
        int entityId = spawnPacket.getEntityId();
        EntityType type = spawnPacket.getEntityType();
        UUID userUuid = event.getUser().getUUID();
        if (userUuid == null) return;

        PLAYER_ENTITIES.computeIfAbsent(userUuid, k -> new ConcurrentHashMap<>())
                .computeIfAbsent(entityId, id -> new TrackedEntity(id, type))
                .setType(type);
    }

    @SuppressWarnings("ConstantConditions")
    private void handleJoinGame(PacketSendEvent event) {
        WrapperPlayServerJoinGame joinPacket = new WrapperPlayServerJoinGame(event);
        int entityId = joinPacket.getEntityId();
        UUID userUuid = event.getUser().getUUID();
        if (userUuid == null) return;

        PLAYER_ENTITIES.computeIfAbsent(userUuid, k -> new ConcurrentHashMap<>())
                .computeIfAbsent(entityId, id -> new TrackedEntity(id, EntityTypes.PLAYER))
                .setType(EntityTypes.PLAYER);
    }

    @SuppressWarnings("ConstantConditions")
    private void handleDestroy(PacketSendEvent event) {
        WrapperPlayServerDestroyEntities destroyPacket = new WrapperPlayServerDestroyEntities(event);
        UUID userUuid = event.getUser().getUUID();
        if (userUuid == null) return;
        Map<Integer, TrackedEntity> playerCache = PLAYER_ENTITIES.get(userUuid);

        if (playerCache != null) {
            for (int id : destroyPacket.getEntityIds()) {
                playerCache.remove(id);
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void handleSetPassengers(PacketSendEvent event) {
        WrapperPlayServerSetPassengers packet = new WrapperPlayServerSetPassengers(event);
        int vehicleId = packet.getEntityId();
        UUID userUuid = event.getUser().getUUID();
        if (userUuid == null) return;

        Map<Integer, TrackedEntity> playerCache = PLAYER_ENTITIES.computeIfAbsent(userUuid, k -> new ConcurrentHashMap<>());
        int[] rawPassengers = packet.getPassengers();

        boolean containsFakeEntities = false;
        for (int id : rawPassengers) {
            if (EntityLib.getApi().getEntity(id) != null) {
                containsFakeEntities = true;
                break;
            }
        }

        if (!containsFakeEntities) {
            TrackedEntity trackedVehicle = playerCache.get(vehicleId);
            if (trackedVehicle != null && !trackedVehicle.getFakePassengers().isEmpty()) {
                trackedVehicle.clearFakePassengers();
                playerCache.remove(vehicleId);
            }
            return;
        }

        TrackedEntity trackedVehicle = playerCache.computeIfAbsent(vehicleId, id -> new TrackedEntity(id, null));
        trackedVehicle.clearFakePassengers();

        for (int id : rawPassengers) {
            if (EntityLib.getApi().getEntity(id) != null) {
                trackedVehicle.addFakePassenger(id);
            }
        }

        if (trackedVehicle.getFakePassengers().isEmpty()) {
            playerCache.remove(vehicleId);
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onUserDisconnect(UserDisconnectEvent event) {
        UUID uuid = event.getUser().getUUID();
        if (uuid == null) return;

        PLAYER_ENTITIES.remove(uuid);
    }
}