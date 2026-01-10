package threeadd.packetEventsSK.element.general.api;

import com.github.retrooper.packetevents.event.ProtocolPacketEvent;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PacketTriggerEvent extends Event implements Cancellable {

    private final ProtocolPacketEvent event;
    private final PacketWrapper<?> wrapper;
    private boolean modified = false;

    public PacketTriggerEvent(ProtocolPacketEvent event, PacketWrapper<?> wrapper) {
        super(true);
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
    public @NotNull HandlerList getHandlers() {
        throw new IllegalArgumentException("This method shouldn't be called");
    }
}
