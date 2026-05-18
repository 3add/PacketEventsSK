package dev.threeadd.packeteventssk.element.general.field.meta;

import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.util.ColorRGB;
import ch.njol.skript.util.Timespan;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import dev.threeadd.packeteventssk.api.util.ConversionUtil;
import dev.threeadd.packeteventssk.api.util.field.FieldRegistrar;
import dev.threeadd.packeteventssk.api.util.field.ConstructionContext;
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

import java.util.function.BiConsumer;

public class DisplayMetaFieldRegistrar implements FieldRegistrar {

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public boolean register() {
        MetaFieldRegistry.INSTANCE.builder(EntityTypes.BLOCK_DISPLAY, BlockDisplayMeta.class)
                .optionalField(BlockData.class,
                        meta -> SpigotConversionUtil.toBukkitBlockData(meta.getBlockState()),
                        (meta, data) -> meta.setBlockState(SpigotConversionUtil.fromBukkitBlockData(data)),
                        "display block data", "display block state")
                .constructor(context -> {

                    BlockDisplayMeta meta = (BlockDisplayMeta) BaseMetaFieldRegistrar.BASE_ENTITY_META_CONSTRUCTOR.apply((ConstructionContext) context);
                    DISPLAY_CONSUMER.accept((ConstructionContext) context, meta);

                    BlockData blockData = context.getOptional("display block data", BlockData.class);
                    if (blockData != null) {
                        meta.setBlockState(SpigotConversionUtil.fromBukkitBlockData(blockData));
                    }

                    return meta;
                })
                .build();

        MetaFieldRegistry.INSTANCE.builder(EntityTypes.ITEM_DISPLAY, ItemDisplayMeta.class)
                .optionalField(ItemType.class,
                        meta -> new ItemType(SpigotConversionUtil.toBukkitItemStack(meta.getItem())),
                        (meta, item) -> meta.setItem(SpigotConversionUtil.fromBukkitItemStack(item.getRandom())),
                        "display item", "display item stack")
                .constructor(context -> {
                    ItemDisplayMeta meta = (ItemDisplayMeta) BaseMetaFieldRegistrar.BASE_ENTITY_META_CONSTRUCTOR.apply((ConstructionContext) context);
                    DISPLAY_CONSUMER.accept((ConstructionContext) context, meta);

                    ItemType itemType = context.getOptional("display item", ItemType.class);
                    if (itemType != null) {
                        meta.setItem(SpigotConversionUtil.fromBukkitItemStack(itemType.getRandom()));
                    }

                    return meta;
                })
                .build();

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
                .constructor(context -> {
                    TextDisplayMeta meta = (TextDisplayMeta) BaseMetaFieldRegistrar.BASE_ENTITY_META_CONSTRUCTOR.apply((ConstructionContext) context);
                    DISPLAY_CONSUMER.accept((ConstructionContext) context, meta);

                    ch.njol.skript.util.Color color = context.getOptional("display background color", ch.njol.skript.util.Color.class);
                    if (color != null) {
                        meta.setBackgroundColor(color.asARGB());
                    }

                    Component text = context.getOptional("display text", Component.class);
                    if (text != null) {
                        meta.setText(text);
                    }

                    Boolean shadow = context.getOptional("display shadow state", Boolean.class);
                    if (shadow != null) {
                        meta.setShadow(shadow);
                    }

                    return meta;
                })
                .build();

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
                .build();

        return true;
    }

    private static final BiConsumer<ConstructionContext<EntityType, AbstractDisplayMeta>, AbstractDisplayMeta> DISPLAY_CONSUMER = (context, meta) -> {

        Display.Billboard billboard = context.getOrElse("display billboard constraints", Display.Billboard.class, Display.Billboard.CENTER);
        meta.setBillboardConstraints(ConversionUtil.toBillboardConstraints(billboard));

        Number height = context.getOptional("display height", Number.class);
        if (height != null) {
            meta.setHeight(height.intValue());
        }

        Number width = context.getOptional("display width", Number.class);
        if (width != null) {
            meta.setWidth(width.intValue());
        }

        Timespan interpolationDelay = context.getOptional("display interpolation delay", Timespan.class);
        if (interpolationDelay != null) {
            meta.setInterpolationDelay((int) ConversionUtil.toTicks(interpolationDelay));
        }

        Quaternionf leftRotation = context.getOptional("display left rotation", Quaternionf.class);
        if (leftRotation != null) {
            meta.setLeftRotation(ConversionUtil.toPeQuaternion4f(leftRotation));
        }

        Quaternionf rightRotation = context.getOptional("display right rotation", Quaternionf.class);
        if (rightRotation != null) {
            meta.setRightRotation(ConversionUtil.toPeQuaternion4f(rightRotation));
        }

        Vector scale = context.getOptional("display scale", Vector.class);
        if (scale != null) {
            meta.setScale(ConversionUtil.toPeVectorF(scale));
        }

        Timespan teleportInterpolationDuration = context.getOptional("display teleport interpolation duration", Timespan.class);
        if (teleportInterpolationDuration != null) {
            meta.setTransformationInterpolationDuration((int) ConversionUtil.toTicks(teleportInterpolationDuration));
        }

        Timespan transformInterpolationDuration = context.getOptional("display transform interpolation duration", Timespan.class);
        if (transformInterpolationDuration != null) {
            meta.setTransformationInterpolationDuration((int) ConversionUtil.toTicks(transformInterpolationDuration));
        }

        Vector displayTranslation = context.getOptional("display translation", Vector.class);
        if (displayTranslation != null) {
            meta.setTranslation(ConversionUtil.toPeVectorF(displayTranslation));
        }

        Number viewRange = context.getOptional("display view range", Number.class);
        if (viewRange != null) {
            meta.setViewRange(viewRange.intValue());
        }
    };
}
