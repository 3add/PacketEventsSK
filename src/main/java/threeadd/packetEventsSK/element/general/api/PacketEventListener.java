package threeadd.packetEventsSK.element.general.api;

import com.github.retrooper.packetevents.event.*;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import org.jetbrains.annotations.NotNull;
import threeadd.packetEventsSK.element.general.api.PacketEventRegistry.RegisteredListener;
import threeadd.packetEventsSK.element.general.structures.PacketEventStruct.ProcessWay;
import threeadd.packetEventsSK.util.registry.EventPacketMapper;

public class PacketEventListener implements PacketListener {

    @Override
    public void onPacketReceive(@NotNull PacketReceiveEvent event) {
        trigger(event);
    }

    @Override
    public void onPacketSend(@NotNull PacketSendEvent event) {
        trigger(event);
    }

    private static void trigger(ProtocolPacketEvent event) {
        if (!PacketEventRegistry.hasTrigger(event.getPacketType()))
            return;

        boolean wasModified = false;
        PacketWrapper<?> wrapper = EventPacketMapper.getWrapper(event.getPacketType()).apply(event);

        for (RegisteredListener listener : PacketEventRegistry.getListeners(event.getPacketType())) {
            ProcessWay way = listener.data().processWay();

            PacketTriggerEvent triggerEvent = new PacketTriggerEvent(event, wrapper);
            way.process(listener.trigger(), triggerEvent); // possible change applied

            if (triggerEvent.isModified())
                wasModified = true;
        }

        if (wasModified)
            event.markForReEncode(true);
    }
}
