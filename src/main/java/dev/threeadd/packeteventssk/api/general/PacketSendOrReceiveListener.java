package dev.threeadd.packeteventssk.api.general;

import com.github.retrooper.packetevents.event.PacketListener;
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

    private static final Map<PacketTypeCommon, Set<ProcessType>> ACTIVE_LISTENERS = new ConcurrentHashMap<>();

    public static void registerListener(PacketTypeCommon type, ProcessType way) {
        ACTIVE_LISTENERS.computeIfAbsent(type, _ -> ConcurrentHashMap.newKeySet()).add(way);
    }

    @Override
    public void onPacketReceive(@NonNull PacketReceiveEvent event) {
        trigger(event);
    }

    @Override
    public void onPacketSend(@NonNull PacketSendEvent event) {
        trigger(event);
    }

    private static void trigger(ProtocolPacketEvent event) {
        PacketTypeCommon type = event.getPacketType();
        Set<ProcessType> ways = ACTIVE_LISTENERS.get(type);

        if (ways == null || ways.isEmpty()) return;

        PacketWrapper<?> wrapper = EventPacketMapper.getWrapper(type).apply(event);
        if (wrapper == null) return;

        if (ways.contains(ProcessType.NETTY)) {
            PacketSendOrReceiveEvent.NettyPacketEvent nettyEvent = new PacketSendOrReceiveEvent.NettyPacketEvent(event, wrapper);
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
                PacketSendOrReceiveEvent.SyncPacketEvent syncEvent = new PacketSendOrReceiveEvent.SyncPacketEvent(event, wrapper);
                Bukkit.getPluginManager().callEvent(syncEvent);
            });
        }

        if (ways.contains(ProcessType.ASYNC)) {
            Bukkit.getScheduler().runTaskAsynchronously(PacketEventsSK.getInstance(), () -> {
                PacketSendOrReceiveEvent.AsyncPacketEvent asyncEvent = new PacketSendOrReceiveEvent.AsyncPacketEvent(event, wrapper);
                Bukkit.getPluginManager().callEvent(asyncEvent);
            });
        }
    }
}