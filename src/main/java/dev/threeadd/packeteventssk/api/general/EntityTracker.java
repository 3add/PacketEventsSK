package dev.threeadd.packeteventssk.api.general;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class EntityTracker implements Listener, PacketListener {

    private static final Map<Integer, EntityType> REAL_ENTITIES = new ConcurrentHashMap<>();

    private static final Cache<Integer, EntityType> FAKE_ENTITIES_CACHE = CacheBuilder.newBuilder()
            .maximumSize(10_000)
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .build();

    public static EntityType getType(int entityId) {
        EntityType type = REAL_ENTITIES.get(entityId);
        if (type != null) {
            return type;
        }

        return FAKE_ENTITIES_CACHE.getIfPresent(entityId);
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.SPAWN_ENTITY) {
            WrapperPlayServerSpawnEntity spawnPacket = new WrapperPlayServerSpawnEntity(event);
            int entityId = spawnPacket.getEntityId();

            if (!REAL_ENTITIES.containsKey(entityId)) {
                FAKE_ENTITIES_CACHE.put(entityId, spawnPacket.getEntityType());
            }
        }
    }

    public static void addRealEntity(int entityId, org.bukkit.entity.EntityType bukkitType) {
        REAL_ENTITIES.put(entityId, SpigotConversionUtil.fromBukkitEntityType(bukkitType));
    }

    public static void removeRealEntity(int entityId) {
        REAL_ENTITIES.remove(entityId);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityAdd(EntityAddToWorldEvent event) {
        EntityTracker.addRealEntity(event.getEntity().getEntityId(), event.getEntity().getType());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityRemove(EntityRemoveFromWorldEvent event) {
        removeRealEntity(event.getEntity().getEntityId());
    }
}