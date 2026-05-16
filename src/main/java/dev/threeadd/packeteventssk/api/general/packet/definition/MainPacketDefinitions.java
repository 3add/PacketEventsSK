package dev.threeadd.packeteventssk.api.general.packet.definition;

import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientSelectBundleItem;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import dev.threeadd.packeteventssk.api.general.EntityTracker;
import dev.threeadd.packeteventssk.api.util.ConversionUtil;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import me.tofaa.entitylib.meta.EntityMeta;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.sign.Side;
import org.bukkit.util.Vector;

import java.util.Arrays;

public class MainPacketDefinitions {
    
    public static void register() {
        PacketDefinitionRegistry.builder(PacketType.Play.Server.ENTITY_VELOCITY, WrapperPlayServerEntityVelocity.class)
                .requiredField(Number.class, WrapperPlayServerEntityVelocity::getEntityId,
                        (w, id) -> w.setEntityId(id.intValue()),
                        "entity id", "id")
                .requiredField(Vector.class, w -> ConversionUtil.toBukkitVector(w.getVelocity()),
                        (w, vector) -> w.setVelocity(ConversionUtil.toPeVectorD(vector)),
                        "velocity vector", "vector", "velocity")
                .constructor(values -> new WrapperPlayServerEntityVelocity(
                        values.get("entity id", Number.class).intValue(),
                        ConversionUtil.toPeVectorD(values.get("velocity vector", Vector.class)))
                )
                .build();

        PacketDefinitionRegistry.builder(PacketType.Play.Server.GAME_TEST_HIGHLIGHT_POS, WrapperPlayServerGameTestHighlightPos.class)
                .requiredField(Vector.class, w -> ConversionUtil.toBukkitVector(w.getAbsolutePos()),
                        (w, vector) -> w.setAbsolutePos(ConversionUtil.toPeVectorI(vector)),
                        "absolute position", "absolute pos", "abs position", "abs pos", "position", "pos")
                .optionalField(Vector.class, w -> ConversionUtil.toBukkitVector(w.getRelativePos()),
                        (w, vector) -> w.setRelativePos(ConversionUtil.toPeVectorI(vector)),
                        "relative position", "relative pos", "rel position", "rel pos")
                .constructor(values -> new WrapperPlayServerGameTestHighlightPos(
                        ConversionUtil.toPeVectorI(values.get("absolute position", Vector.class)),
                        ConversionUtil.toPeVectorI(values.get("relative position", Vector.class)))
                )
                .build();

        PacketDefinitionRegistry.builder(PacketType.Play.Server.ENTITY_METADATA, WrapperPlayServerEntityMetadata.class)
                .requiredField(Number.class, WrapperPlayServerEntityMetadata::getEntityId,
                        (w, id) -> w.setEntityId(id.intValue()),
                        "entity id", "id")
                .requiredField(EntityMeta.class, w -> {
                            EntityType type = EntityTracker.getType(w.getEntityId());
                            if (type == null) {
                                throw new IllegalStateException("Failed to find entity type of entity with id " + w.getEntityId());
                            }

                            EntityMeta meta = EntityMeta.createMeta(w.getEntityId(), type);
                            meta.getMetadata().setMetaFromPacket(w);
                            return meta;
                        }, WrapperPlayServerEntityMetadata::setEntityMetadata,
                        "entity metadata", "entity meta", "metadata", "meta")
                .constructor(values -> new WrapperPlayServerEntityMetadata(
                        values.get("entity id", Number.class).intValue(),
                        values.get("entity metadata", EntityMeta.class))
                )
                .build();

        PacketDefinitionRegistry.builder(PacketType.Play.Server.BLOCK_CHANGE, WrapperPlayServerBlockChange.class)
                .requiredField(Vector.class, w -> ConversionUtil.toBukkitVector(w.getBlockPosition()),
                        (w, vector) -> w.setBlockPosition(ConversionUtil.toPeVectorI(vector)),
                        "block position", "block pos", "position", "pos")
                .requiredField(BlockData.class, w -> SpigotConversionUtil.toBukkitBlockData(w.getBlockState()),
                        (w, blockData) -> w.setBlockState(SpigotConversionUtil.fromBukkitBlockData(blockData)),
                        "block state", "state", "block data", "data")
                .constructor(values -> new WrapperPlayServerBlockChange(
                        ConversionUtil.toPeVectorI(values.get("block position", Vector.class)),
                        SpigotConversionUtil.fromBukkitBlockData(values.get("block state", BlockData.class))
                ))
                .build();

        PacketDefinitionRegistry.builder(PacketType.Play.Server.OPEN_SIGN_EDITOR, WrapperPlayServerOpenSignEditor.class)
                .requiredField(Vector.class, w -> ConversionUtil.toBukkitVector(w.getPosition()),
                        (w, vector) -> w.setPosition(ConversionUtil.toPeVectorI(vector)),
                        "block position", "block pos", "position", "pos")
                .requiredField(Side.class, w -> w.isFrontText() ? Side.FRONT : Side.BACK,
                        (w, side) -> w.setFrontText(side == Side.FRONT),
                        "sign side", "side")
                .constructor(values -> new WrapperPlayServerOpenSignEditor(
                        ConversionUtil.toPeVectorI(values.get("block position", Vector.class)),
                        values.get("sign side", Side.class) == Side.FRONT
                ))
                .build();

        PacketDefinitionRegistry.builder(PacketType.Play.Server.CLOSE_WINDOW, WrapperPlayServerCloseWindow.class)
                .constructor(k -> new WrapperPlayServerCloseWindow())
                .build();

        PacketDefinitionRegistry.builder(PacketType.Play.Server.DESTROY_ENTITIES, WrapperPlayServerDestroyEntities.class)
                .requiredField(Number[].class,
                        w -> Arrays.stream(w.getEntityIds()).boxed().toArray(Number[]::new),
                        (w, ids) -> w.setEntityIds(Arrays.stream(ids).mapToInt(Number::intValue).toArray()),
                        "entity ids", "ids")
                .constructor(values -> new WrapperPlayServerDestroyEntities(
                        Arrays.stream(values.get("entity ids", Number[].class)).mapToInt(Number::intValue).toArray()
                ))
                .build();

        PacketDefinitionRegistry.builder(PacketType.Play.Client.SELECT_BUNDLE_ITEM, WrapperPlayClientSelectBundleItem.class)
                .requiredField(Number.class, WrapperPlayClientSelectBundleItem::getSlotId,
                        (w, id) -> w.setSlotId(id.intValue()),
                        "slot id", "id")
                .requiredField(Number.class, WrapperPlayClientSelectBundleItem::getSelectedItemIndex,
                        (w, index) -> w.setSelectedItemIndex(index.intValue()),
                        "selected item index", "selected index", "item index", "index")
                .constructor(values -> new WrapperPlayClientSelectBundleItem(
                        values.get("slot id", Number.class).intValue(),
                        values.get("selected item index", Number.class).intValue()
                ))
                .build();
    }
}
