package dev.threeadd.packeteventssk.api.general;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.event.ProtocolPacketEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import dev.threeadd.packeteventssk.PacketEventsSK;
import dev.threeadd.packeteventssk.element.general.event.EvtPacketSendOrReceive.ProcessType;
import org.bukkit.Bukkit;
import org.jspecify.annotations.NonNull;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class PacketSendOrReceiveListener implements PacketListener {

    private static final Map<PacketListenerPriority, PacketSendOrReceiveListener> INSTANCES = new ConcurrentHashMap<>();
    private static final Map<PacketListenerPriority, Map<PacketTypeCommon, Set<ProcessType>>> ACTIVE_LISTENERS = new ConcurrentHashMap<>();

    private final PacketListenerPriority priority;

    private PacketSendOrReceiveListener(PacketListenerPriority priority) {
        this.priority = priority;
    }

    public static void registerListener(PacketTypeCommon type, ProcessType way, PacketListenerPriority priority) {
        ACTIVE_LISTENERS.computeIfAbsent(priority, _ -> new ConcurrentHashMap<>())
                .computeIfAbsent(type, _ -> ConcurrentHashMap.newKeySet())
                .add(way);

        if (!INSTANCES.containsKey(priority)) {
            PacketSendOrReceiveListener listener = new PacketSendOrReceiveListener(priority);
            INSTANCES.put(priority, listener);
            PacketEvents.getAPI().getEventManager().registerListener(listener, priority);
        }
    }

    @Override
    public void onPacketReceive(@NonNull PacketReceiveEvent event) {
        trigger(event, priority);
    }

    @Override
    public void onPacketSend(@NonNull PacketSendEvent event) {
        trigger(event, priority);
    }

    private static void trigger(ProtocolPacketEvent event, PacketListenerPriority priority) {
        PacketTypeCommon type = event.getPacketType();

        Map<PacketTypeCommon, Set<ProcessType>> listeners = ACTIVE_LISTENERS.get(priority);
        if (listeners == null) return;

        Set<ProcessType> ways = listeners.get(type);
        if (ways == null || ways.isEmpty()) return;

        PacketWrapper<?> wrapper = EventPacketMapper.getWrapper(type).apply(event);
        if (wrapper == null) return;

        if (ways.contains(ProcessType.NETTY)) {
            PacketSendOrReceiveEvent.NettyPacketEvent nettyEvent = new PacketSendOrReceiveEvent.NettyPacketEvent(event, wrapper, priority);
            Bukkit.getPluginManager().callEvent(nettyEvent);

            if (nettyEvent.isCancelled()) {
                event.setCancelled(true);
            }

            if (nettyEvent.isModified()) {
                event.markForReEncode(true);
            }
        }

        if (ways.contains(ProcessType.SYNC)) {
            Bukkit.getScheduler().runTask(PacketEventsSK.getInstance(), () -> {
                event.markForReEncode(false);
                PacketSendOrReceiveEvent.SyncPacketEvent syncEvent = new PacketSendOrReceiveEvent.SyncPacketEvent(event, wrapper, priority);
                Bukkit.getPluginManager().callEvent(syncEvent);
            });
        }

        if (ways.contains(ProcessType.ASYNC)) {
            Bukkit.getScheduler().runTaskAsynchronously(PacketEventsSK.getInstance(), () -> {
                event.markForReEncode(false);
                PacketSendOrReceiveEvent.AsyncPacketEvent asyncEvent = new PacketSendOrReceiveEvent.AsyncPacketEvent(event, wrapper, priority);
                Bukkit.getPluginManager().callEvent(asyncEvent);
            });
        }
    }
}