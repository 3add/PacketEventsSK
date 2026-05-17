package dev.threeadd.packeteventssk.api.entity.meta;

import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.util.ColorRGB;
import ch.njol.skript.util.Timespan;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import dev.threeadd.packeteventssk.api.util.ConversionUtil;
import dev.threeadd.packeteventssk.api.util.field.FieldRegistrar;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import me.tofaa.entitylib.meta.display.AbstractDisplayMeta;
import me.tofaa.entitylib.meta.display.BlockDisplayMeta;
import me.tofaa.entitylib.meta.display.ItemDisplayMeta;
import me.tofaa.entitylib.meta.display.TextDisplayMeta;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Display;
import org.bukkit.util.Vector;
import org.joml.Quaternionf;

public class DisplayMetaRegistrar implements FieldRegistrar {

    @Override
    public boolean register() {
        MetaFieldRegistry.INSTANCE.builder(EntityTypes.BLOCK_DISPLAY, BlockDisplayMeta.class)
                .optionalField(BlockData.class,
                        meta -> SpigotConversionUtil.toBukkitBlockData(meta.getBlockState()),
                        (meta, data) -> meta.setBlockState(SpigotConversionUtil.fromBukkitBlockData(data)),
                        "display block data", "display block state")
                .build(this);

        MetaFieldRegistry.INSTANCE.builder(EntityTypes.ITEM_DISPLAY, ItemDisplayMeta.class)
                .optionalField(ItemType.class,
                        meta -> new ItemType(SpigotConversionUtil.toBukkitItemStack(meta.getItem())),
                        (meta, item) -> meta.setItem(SpigotConversionUtil.fromBukkitItemStack(item.getRandom())),
                        "display item", "display item stack")
                .build(this);

        MetaFieldRegistry.INSTANCE.builder(EntityTypes.TEXT_DISPLAY, TextDisplayMeta.class)
                .optionalField(ch.njol.skript.util.Color.class,
                        meta -> ColorRGB.fromBukkitColor(Color.fromARGB(meta.getBackgroundColor())),
                        (meta, newColor) -> meta.setBackgroundColor(newColor.asARGB()),
                        "display background color")
                .optionalField(Component.class,
                        TextDisplayMeta::getText,
                        TextDisplayMeta::setText,
                        "display text", "display content")
                .optionalField(Boolean.class,
                        TextDisplayMeta::isShadow,
                        TextDisplayMeta::setShadow,
                        "display text shadow state", "display shadow state", "display shadow")
                .build(this);

        MetaFieldRegistry.INSTANCE.builder(EntityTypes.DISPLAY, AbstractDisplayMeta.class)
                .optionalField(Display.Billboard.class,
                        meta -> ConversionUtil.toBillboard(meta.getBillboardConstraints()),
                        (meta, newBillboard) -> meta.setBillboardConstraints(ConversionUtil.toBillboardConstraints(newBillboard)),
                        "display billboard constraints", "display billboard")
                .optionalField(Number.class,
                        AbstractDisplayMeta::getHeight,
                        (meta, newNum) -> meta.setHeight(newNum.intValue()),
                        "display height")
                .optionalField(Number.class,
                        AbstractDisplayMeta::getWidth,
                        (meta, newNum) -> meta.setWidth(newNum.intValue()),
                        "display width")
                .optionalField(Timespan.class,
                        meta -> ConversionUtil.toTimespan(meta.getInterpolationDelay()),
                        (meta, newTime) -> meta.setInterpolationDelay((int) ConversionUtil.toTicks(newTime)),
                        "display interpolation delay")
                .optionalField(Quaternionf.class,
                        meta -> ConversionUtil.toBukkitQuaternionf(meta.getLeftRotation()),
                        (meta, newQuaternionf) -> meta.setLeftRotation(ConversionUtil.toPeQuaternion4f(newQuaternionf)),
                        "display left rotation")
                .optionalField(Quaternionf.class,
                        meta -> ConversionUtil.toBukkitQuaternionf(meta.getRightRotation()),
                        (meta, newQuaternionf) -> meta.setRightRotation(ConversionUtil.toPeQuaternion4f(newQuaternionf)),
                        "display right rotation")
                .optionalField(Vector.class,
                        meta -> ConversionUtil.toBukkitVector(meta.getScale()),
                        (meta, newVector) -> meta.setScale(ConversionUtil.toPeVectorF(newVector)),
                        "display scale")
                .optionalField(Vector.class,
                        meta -> ConversionUtil.toBukkitVector(meta.getScale()),
                        (meta, newVector) -> meta.setScale(ConversionUtil.toPeVectorF(newVector)),
                        "display scale")
                .optionalField(Timespan.class,
                        meta -> ConversionUtil.toTimespan(meta.getPositionRotationInterpolationDuration()),
                        (meta, newTime) -> meta.setPositionRotationInterpolationDuration((int) ConversionUtil.toTicks(newTime)),
                        "display teleport interpolation duration")
                .optionalField(Timespan.class,
                        meta -> ConversionUtil.toTimespan(meta.getTransformationInterpolationDuration()),
                        (meta, newTime) -> meta.setTransformationInterpolationDuration((int) ConversionUtil.toTicks(newTime)),
                        "display transform interpolation duration")
                .optionalField(Vector.class,
                        meta -> ConversionUtil.toBukkitVector(meta.getTranslation()),
                        (meta, newVector) -> meta.setTranslation(ConversionUtil.toPeVectorF(newVector)),
                        "display translation")
                .optionalField(Number.class,
                        AbstractDisplayMeta::getViewRange,
                        (meta, newNum) -> meta.setViewRange(newNum.intValue()),
                        "display view range")
                .build(this);

        return true;
    }
}
