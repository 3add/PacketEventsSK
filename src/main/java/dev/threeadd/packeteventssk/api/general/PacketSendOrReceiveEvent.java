package dev.threeadd.packeteventssk.api.general;

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
        return event;
    }

    public PacketWrapper<?> getWrapper() {
        return wrapper;
    }

    public PacketListenerPriority getPriority() {
        return priority;
    }

    @Override
    public @NonNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public static class NettyPacketEvent extends PacketSendOrReceiveEvent implements Cancellable {
        private boolean modified = false;

        public NettyPacketEvent(ProtocolPacketEvent event, PacketWrapper<?> wrapper, PacketListenerPriority priority) {
            super(event, wrapper, priority, true);
        }

        @Override
        public boolean isCancelled() {
            return event.isCancelled();
        }

        @Override
        public void setCancelled(boolean state) {
            event.setCancelled(state);
        }

        public void setModified(boolean modified) {
            this.modified = modified;
        }

        public boolean isModified() {
            return modified;
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