package threeadd.packetEventsSK.element.simple.api;

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
            EntityMeta meta = new EntityMeta(packet);
            meta.setGlowing(true);

            packet.setEntityMetadata(meta.entityData());
        }

        event.markForReEncode(true);
    }

    // Code below was before I forked EntityLib
    /*
    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (!event.getPacketType().equals(PacketType.Play.Server.ENTITY_METADATA))
            return;

        WrapperPlayServerEntityMetadata packet = new WrapperPlayServerEntityMetadata(event);
        UUID playerUUID = event.getUser().getUUID();
        int entityId = packet.getEntityId();

        if (GlowingEntityManager.isGlowingFor(entityId, playerUUID))
            packet.setEntityMetadata(setGlowingBit(packet.getEntityMetadata(), true));
        EntityMeta meta = (EntityMeta) packet.getEntityMetadata();

        event.markForReEncode(true);
    }
     */

    /*
    public static List<EntityData<?>> setGlowingBit(List<EntityData<?>> dataList, boolean state) {
        final byte GLOWING_BIT_MASK = 0x40; // bit mask

        @SuppressWarnings("unchecked")
        EntityData<Byte> zeroBaseData = (EntityData<Byte>) dataList.stream()
                .filter(data -> data.getIndex() == 0)
                .filter(data -> data.getType().equals(EntityDataTypes.BYTE))
                .findFirst()
                .orElse(null);

        // I believe this can be null because minecraft optimizes entity data packets to only contain "changed" data
        // in which case we just return the original data
        if (zeroBaseData == null)
            return dataList;

        byte currentData = zeroBaseData.getValue();
        byte newData;

        if (state) {
            newData = (byte) (currentData | GLOWING_BIT_MASK);
        } else {
            newData = (byte) (currentData & ~GLOWING_BIT_MASK);
        }

        zeroBaseData.setValue(newData);
        return dataList;
    }
     */
}
