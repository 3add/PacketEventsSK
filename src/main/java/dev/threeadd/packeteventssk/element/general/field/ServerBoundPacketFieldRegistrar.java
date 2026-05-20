package dev.threeadd.packeteventssk.element.general.field;

import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.InteractionHand;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientSelectBundleItem;
import dev.threeadd.packeteventssk.api.util.ConversionUtil;
import dev.threeadd.packeteventssk.api.util.field.FieldRegistrar;
import org.bukkit.util.Vector;

public class ServerBoundPacketFieldRegistrar implements FieldRegistrar {

    @Override
    public void register() {
        PacketFieldRegistry.INSTANCE.builder(PacketType.Play.Client.SELECT_BUNDLE_ITEM, WrapperPlayClientSelectBundleItem.class)
                .requiredField(Number.class, WrapperPlayClientSelectBundleItem::getSlotId,
                        (w, id) -> w.setSlotId(id.intValue()),
                        "slot id", "id")
                .requiredField(Number.class, WrapperPlayClientSelectBundleItem::getSelectedItemIndex,
                        (w, index) -> w.setSelectedItemIndex(index.intValue()),
                        "selected item index", "selected index", "item index", "index")
                .constructor(values -> new WrapperPlayClientSelectBundleItem(
                        values.getRequired("slot id", Number.class).intValue(),
                        values.getRequired("selected item index", Number.class).intValue()
                ))
                .build();

        PacketFieldRegistry.INSTANCE.builder(PacketType.Play.Client.INTERACT_ENTITY, WrapperPlayClientInteractEntity.class)
                .requiredField(Number.class, WrapperPlayClientInteractEntity::getEntityId,
                        (w, id) -> w.setEntityId(id.intValue()),
                        "entity id", "id")
                .requiredField(InteractionHand.class, WrapperPlayClientInteractEntity::getHand, WrapperPlayClientInteractEntity::setHand,
                        "interaction hand", "hand")
                .requiredField(Vector.class,
                        w -> ConversionUtil.toBukkitVector(w.getLocation()),
                        (w, newLoc) -> w.setLocation(ConversionUtil.toPeVectorD(newLoc)),
                        "location vector", "location", "loc")
                .requiredField(Boolean.class, w -> w.isSneaking().orElse(false), WrapperPlayClientInteractEntity::setSneaking,
                        "sneaking state", "sneaking")
                .constructor(values -> new WrapperPlayClientInteractEntity(
                        values.getRequired("entity id", Number.class).intValue(),
                        values.getRequired("interaction hand", InteractionHand.class),
                        ConversionUtil.toPeVectorD(values.getRequired("location vector", Vector.class)),
                        values.getRequired("sneaking state", Boolean.class)
                ))
                .build();
    }
}
