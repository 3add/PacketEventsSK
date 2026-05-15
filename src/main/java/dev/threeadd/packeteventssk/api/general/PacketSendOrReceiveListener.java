package dev.threeadd.packeteventssk.api.general;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.event.ProtocolPacketEvent;
import com.github.retrooper.packetevents.protocol.PacketSide;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import dev.threeadd.packeteventssk.PacketEventsSK;
import dev.threeadd.packeteventssk.element.general.event.EvtPacketSendOrReceive.ProcessType;
import org.bukkit.Bukkit;
import org.jspecify.annotations.NonNull;

import java.lang.reflect.InvocationTargetException;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class PacketSendOrReceiveListener implements PacketListener {

    // listener instances
    private static final Map<PacketListenerPriority, PacketSendOrReceiveListener> INSTANCES = new ConcurrentHashMap<>();
    // listening to specific packets
    private static final Map<PacketListenerPriority, Map<PacketTypeCommon, Set<ProcessType>>> ACTIVE_LISTENERS = new ConcurrentHashMap<>();
    // listening to all packets
    private static final Map<PacketListenerPriority, Set<ProcessType>> GLOBAL_LISTENERS = new ConcurrentHashMap<>();

    private final PacketListenerPriority priority;

    private PacketSendOrReceiveListener(PacketListenerPriority priority) {
        this.priority = priority;
    }

    public static void registerListener(PacketTypeCommon type, ProcessType way, PacketListenerPriority priority) {
        if (type == null) {
            GLOBAL_LISTENERS.computeIfAbsent(priority, _ -> ConcurrentHashMap.newKeySet()).add(way);
        } else {
            ACTIVE_LISTENERS.computeIfAbsent(priority, _ -> new ConcurrentHashMap<>())
                    .computeIfAbsent(type, _ -> ConcurrentHashMap.newKeySet())
                    .add(way);
        }

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

        event.markForReEncode(false); // false by default, only overridden in netty processed if modified

        Set<ProcessType> waysToTrigger = EnumSet.noneOf(ProcessType.class);

        Map<PacketTypeCommon, Set<ProcessType>> specificListeners = ACTIVE_LISTENERS.get(priority);
        if (specificListeners != null) {
            Set<ProcessType> specificWays = specificListeners.get(type);
            if (specificWays != null) {
                waysToTrigger.addAll(specificWays);
            }
        }

        Set<ProcessType> globalWays = GLOBAL_LISTENERS.get(priority);
        if (globalWays != null) {
            waysToTrigger.addAll(globalWays);
        }

        if (waysToTrigger.isEmpty()) return;

        PacketWrapper<?> wrapper = getWrapper(type, event);
        if (wrapper == null) return;

        if (waysToTrigger.contains(ProcessType.NETTY)) {
            PacketSendOrReceiveEvent.NettyPacketEvent nettyEvent = new PacketSendOrReceiveEvent.NettyPacketEvent(event, wrapper, priority);
            Bukkit.getPluginManager().callEvent(nettyEvent);

            if (nettyEvent.isCancelled()) {
                event.setCancelled(true);
            }

            if (nettyEvent.isModified()) {
                event.markForReEncode(true);
            }
        }

        if (waysToTrigger.contains(ProcessType.SYNC)) {
            PacketSendOrReceiveEvent.SyncPacketEvent syncEvent = new PacketSendOrReceiveEvent.SyncPacketEvent(event, wrapper, priority);
            Bukkit.getScheduler().runTask(PacketEventsSK.getInstance(), () -> Bukkit.getPluginManager().callEvent(syncEvent));
        }

        if (waysToTrigger.contains(ProcessType.ASYNC)) {
            PacketSendOrReceiveEvent.AsyncPacketEvent asyncEvent = new PacketSendOrReceiveEvent.AsyncPacketEvent(event, wrapper, priority);
            Bukkit.getScheduler().runTaskAsynchronously(PacketEventsSK.getInstance(), () -> Bukkit.getPluginManager().callEvent(asyncEvent));
        }
    }

    private static PacketWrapper<?> getWrapper(PacketTypeCommon type, ProtocolPacketEvent event) {
        Class<? extends PacketWrapper<?>> clazz = type.getWrapperClass();
        if (clazz == null) return null; // only way this should return null

        try {
            if (event instanceof PacketSendEvent sendEvent && type.getSide().equals(PacketSide.SERVER)) {
                return clazz.getConstructor(PacketSendEvent.class).newInstance(sendEvent);
            } else if (event instanceof PacketReceiveEvent sendEvent && type.getSide().equals(PacketSide.CLIENT)) {
                return clazz.getConstructor(PacketReceiveEvent.class).newInstance(sendEvent);
            }
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodError | NoSuchMethodException e) {
            throw new IllegalStateException("Couldn't create packet for: " + type);
        }

        throw new IllegalStateException("Couldn't create packet for: " + type);
    }
}