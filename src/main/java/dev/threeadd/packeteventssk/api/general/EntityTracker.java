package dev.threeadd.packeteventssk.api.general;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.event.UserDisconnectEvent;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetPassengers;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import me.tofaa.entitylib.EntityLib;
import me.tofaa.entitylib.wrapper.WrapperEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.LinkedHashSet;
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
            return entityId;
        }

        @Nullable
        public EntityType getType() {
            return type;
        }

        public void setType(@Nullable EntityType type) {
            this.type = type;
        }

        public Set<Integer> getFakePassengers() {
            return fakePassengers;
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
            case PacketType.Play.Server.SPAWN_ENTITY -> handleSpawn(event);
            case PacketType.Play.Server.SET_PASSENGERS -> handleSetPassengers(event);
            case PacketType.Play.Server.DESTROY_ENTITIES -> handleDestroy(event); // memory cleanup
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

        if (containsFakeEntities) {
            TrackedEntity trackedVehicle = playerCache.computeIfAbsent(vehicleId, id -> new TrackedEntity(id, null));
            trackedVehicle.clearFakePassengers();
            for (int id : rawPassengers) {
                if (EntityLib.getApi().getEntity(id) != null) {
                    trackedVehicle.addFakePassenger(id);
                }
            }
        } else {
            TrackedEntity trackedVehicle = playerCache.get(vehicleId);
            if (trackedVehicle != null && !trackedVehicle.getFakePassengers().isEmpty()) {
                Player player = event.getPlayer();
                if (player != null) {
                    Set<Integer> updatedPassengers = new LinkedHashSet<>();
                    for (int id : rawPassengers) {
                        updatedPassengers.add(id);
                    }
                    for (int passengerId : trackedVehicle.getFakePassengers()) {
                        WrapperEntity activeFakeEntity = EntityLib.getApi().getEntity(passengerId);
                        if (activeFakeEntity != null && activeFakeEntity.getViewers().contains(player.getUniqueId())) {
                            updatedPassengers.add(passengerId);
                        }
                    }
                    packet.setPassengers(updatedPassengers.stream().mapToInt(Integer::intValue).toArray());
                }
            }
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