package dev.threeadd.packeteventssk.element.simple.api.glow;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import me.tofaa.entitylib.meta.EntityMeta;

import java.util.UUID;

public class GlowingEntityListener implements PacketListener {

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (!event.getPacketType().equals(PacketType.Play.Server.ENTITY_METADATA))
            return;

        WrapperPlayServerEntityMetadata packet = new WrapperPlayServerEntityMetadata(event);
        UUID playerUUID = event.getUser().getUUID();
        int entityId = packet.getEntityId();

        if (GlowingEntityManager.isGlowingFor(entityId, playerUUID)) {
            EntityMeta meta = new EntityMeta(packet.getEntityId());
            meta.getMetadata().setMetaFromPacket(packet);
            meta.setGlowing(true);

            packet.setEntityMetadata(meta.entityData());
        }

        event.markForReEncode(true);
    }
}
