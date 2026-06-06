package dev.threeadd.packeteventssk.api.general.packet;

import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.ProtocolPacketEvent;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jspecify.annotations.NonNull;

public abstract class PacketSendOrReceiveEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    protected final ProtocolPacketEvent event;
    private final PacketWrapper<?> wrapper;
    private final PacketListenerPriority priority;

    public PacketSendOrReceiveEvent(ProtocolPacketEvent event, PacketWrapper<?> wrapper, PacketListenerPriority priority, boolean isAsync) {
        super(isAsync);
        this.event = event;
        this.wrapper = wrapper;
        this.priority = priority;
    }

    public ProtocolPacketEvent getEvent() {
        return this.event;
    }

    public PacketWrapper<?> getWrapper() {
        return this.wrapper;
    }

    public PacketListenerPriority getPriority() {
        return this.priority;
    }

    @Override
    public @NonNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public static class NettyPacketEvent extends PacketSendOrReceiveEvent implements Cancellable {
        public NettyPacketEvent(ProtocolPacketEvent event, PacketWrapper<?> wrapper, PacketListenerPriority priority) {
            super(event, wrapper, priority, true);
        }

        @Override
        public boolean isCancelled() {
            return this.event.isCancelled();
        }

        @Override
        public void setCancelled(boolean state) {
            this.event.setCancelled(state);
        }
    }

    public static class SyncPacketEvent extends PacketSendOrReceiveEvent {
        public SyncPacketEvent(ProtocolPacketEvent event, PacketWrapper<?> wrapper, PacketListenerPriority priority) {
            super(event, wrapper, priority, false);
        }
    }

    public static class AsyncPacketEvent extends PacketSendOrReceiveEvent {
        public AsyncPacketEvent(ProtocolPacketEvent event, PacketWrapper<?> wrapper, PacketListenerPriority priority) {
            super(event, wrapper, priority, true);
        }
    }
}