package dev.threeadd.packeteventssk.api.general;

import com.github.retrooper.packetevents.event.ProtocolPacketEvent;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jspecify.annotations.NonNull;

public abstract class PacketSendOrReceiveEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final ProtocolPacketEvent event;
    private final PacketWrapper<?> wrapper;
    private boolean modified = false;

    public PacketSendOrReceiveEvent(ProtocolPacketEvent event, PacketWrapper<?> wrapper, boolean isAsync) {
        super(isAsync);
        this.event = event;
        this.wrapper = wrapper;
    }

    public ProtocolPacketEvent getEvent() {
        return event;
    }

    public PacketWrapper<?> getWrapper() {
        return wrapper;
    }

    public void setModified(boolean modified) {
        this.modified = modified;
    }

    public boolean isModified() {
        return modified;
    }

    @Override
    public boolean isCancelled() {
        return event.isCancelled();
    }

    @Override
    public void setCancelled(boolean state) {
        event.setCancelled(state);
    }

    @Override
    public @NonNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public static class NettyPacketEvent extends PacketSendOrReceiveEvent {
        public NettyPacketEvent(ProtocolPacketEvent event, PacketWrapper<?> wrapper) {
            super(event, wrapper, true);
        }
    }

    public static class SyncPacketEvent extends PacketSendOrReceiveEvent {
        public SyncPacketEvent(ProtocolPacketEvent event, PacketWrapper<?> wrapper) {
            super(event, wrapper, false);
        }
    }

    public static class AsyncPacketEvent extends PacketSendOrReceiveEvent {
        public AsyncPacketEvent(ProtocolPacketEvent event, PacketWrapper<?> wrapper) {
            super(event, wrapper, true);
        }
    }
}