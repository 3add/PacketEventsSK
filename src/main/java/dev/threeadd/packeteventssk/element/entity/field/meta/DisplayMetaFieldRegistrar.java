package dev.threeadd.packeteventssk.element.entity.field.meta;

import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.util.ColorRGB;
import ch.njol.skript.util.Timespan;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import dev.threeadd.packeteventssk.api.util.ConversionUtil;
import dev.threeadd.packeteventssk.api.util.field.ConstructionContext;
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
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Vector;
import org.joml.Quaternionf;

import java.util.function.BiConsumer;

// Should fully match: https://minecraft.wiki/w/Java_Edition_protocol/Entity_metadata#Display
// including order of fields
public class DisplayMetaFieldRegistrar implements FieldRegistrar {

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void register() {

        // complete
        MetaFieldRegistry.INSTANCE.builder(EntityTypes.DISPLAY, AbstractDisplayMeta.class)
                .optionalField(Timespan.class,
                        meta -> ConversionUtil.toTimespan(meta.getInterpolationDelay()),
                        (meta, newTime) -> meta.setInterpolationDelay((int) ConversionUtil.toTicks(newTime)),
                        "display interpolation delay")
                .optionalField(Timespan.class,
                        meta -> ConversionUtil.toTimespan(meta.getTransformationInterpolationDuration()),
                        (meta, newTime) -> meta.setTransformationInterpolationDuration((int) ConversionUtil.toTicks(newTime)),
                        "display transform interpolation duration")
                .optionalField(Timespan.class,
                        meta -> ConversionUtil.toTimespan(meta.getPositionRotationInterpolationDuration()),
                        (meta, newTime) -> meta.setPositionRotationInterpolationDuration((int) ConversionUtil.toTicks(newTime)),
                        "display teleport interpolation duration")
                .optionalField(Vector.class,
                        meta -> ConversionUtil.toBukkitVector(meta.getTranslation()),
                        (meta, newVector) -> meta.setTranslation(ConversionUtil.toPeVectorF(newVector)),
                        "display translation")
                .optionalField(Vector.class,
                        meta -> ConversionUtil.toBukkitVector(meta.getScale()),
                        (meta, newVector) -> meta.setScale(ConversionUtil.toPeVectorF(newVector)),
                        "display scale")
                .optionalField(Quaternionf.class,
                        meta -> ConversionUtil.toBukkitQuaternionf(meta.getLeftRotation()),
                        (meta, newQuaternionf) -> meta.setLeftRotation(ConversionUtil.toPeQuaternion4f(newQuaternionf)),
                        "display left rotation")
                .optionalField(Quaternionf.class,
                        meta -> ConversionUtil.toBukkitQuaternionf(meta.getRightRotation()),
                        (meta, newQuaternionf) -> meta.setRightRotation(ConversionUtil.toPeQuaternion4f(newQuaternionf)),
                        "display right rotation")
                .optionalField(Display.Billboard.class,
                        meta -> ConversionUtil.toBillboard(meta.getBillboardConstraints()),
                        (meta, newBillboard) -> meta.setBillboardConstraints(ConversionUtil.toBillboardConstraints(newBillboard)),
                        "display billboard constraints", "display billboard")
                .optionalField(Number.class,
                        AbstractDisplayMeta::getBrightnessOverride,
                        (meta, newNum) -> meta.setBrightnessOverride(newNum.intValue()),
                        "display brightness override", "display brightness")
                .optionalField(Number.class,
                        AbstractDisplayMeta::getViewRange,
                        (meta, newNum) -> meta.setViewRange(newNum.intValue()),
                        "display view range")
                .optionalField(Number.class,
                        AbstractDisplayMeta::getShadowRadius,
                        (meta, newNum) -> meta.setShadowRadius(newNum.floatValue()),
                        "display shadow radius")
                .optionalField(Number.class,
                        AbstractDisplayMeta::getShadowStrength,
                        (meta, newNum) -> meta.setShadowRadius(newNum.floatValue()),
                        "display shadow strength")
                .optionalField(Number.class,
                        AbstractDisplayMeta::getWidth,
                        (meta, newNum) -> meta.setWidth(newNum.intValue()),
                        "display width")
                .optionalField(Number.class,
                        AbstractDisplayMeta::getHeight,
                        (meta, newNum) -> meta.setHeight(newNum.intValue()),
                        "display height")
                .optionalField(ch.njol.skript.util.Color.class,
                        meta -> ColorRGB.fromBukkitColor(Color.fromARGB(meta.getGlowColorOverride())),
                        (meta, newColor) -> meta.setGlowColorOverride(newColor.asARGB()),
                        "display glow color override", "display glow color")
                .build();

        // complete
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

        // complete
        MetaFieldRegistry.INSTANCE.builder(EntityTypes.ITEM_DISPLAY, ItemDisplayMeta.class)
                .optionalField(ItemType.class,
                        meta -> new ItemType(SpigotConversionUtil.toBukkitItemStack(meta.getItem())),
                        (meta, item) -> meta.setItem(SpigotConversionUtil.fromBukkitItemStack(item.getRandom())),
                        "display item", "display item stack")
                .optionalField(ItemDisplay.ItemDisplayTransform.class,
                        meta -> ConversionUtil.toItemDisplayTransform(meta.getDisplayType()),
                        (meta, newTransform) -> meta.setDisplayType(ConversionUtil.toDisplayType(newTransform)),
                        "display item transform")
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

        // complete aside from text opacity (see the to do)
        MetaFieldRegistry.INSTANCE.builder(EntityTypes.TEXT_DISPLAY, TextDisplayMeta.class)
                .optionalField(Component.class,
                        TextDisplayMeta::getText,
                        TextDisplayMeta::setText,
                        "display text", "display content")
                .optionalField(Number.class,
                        TextDisplayMeta::getLineWidth,
                        (w, newNum) -> w.setLineWidth(newNum.intValue()),
                        "display line width")
                .optionalField(ch.njol.skript.util.Color.class,
                        meta -> ColorRGB.fromBukkitColor(Color.fromARGB(meta.getBackgroundColor())),
                        (meta, newColor) -> meta.setBackgroundColor(newColor.asARGB()),
                        "display background color")
                // TODO text opacity is a byte which I haven't decided on how I want to do them
                .optionalField(Boolean.class,
                        TextDisplayMeta::isShadow,
                        TextDisplayMeta::setShadow,
                        "display text shadowed state", "display shadowed state", "display shadowed")
                .optionalField(Boolean.class,
                        TextDisplayMeta::isSeeThrough,
                        TextDisplayMeta::setSeeThrough,
                        "display see through state", "display see through")
                .optionalField(Boolean.class,
                        TextDisplayMeta::isUseDefaultBackground,
                        TextDisplayMeta::setUseDefaultBackground,
                        "display use default background state", "display use default background")
                .optionalField(TextDisplay.TextAlignment.class,
                        display -> {
                            if (display.isAlignLeft())
                                return TextDisplay.TextAlignment.LEFT;
                            else if  (display.isAlignRight())
                                return TextDisplay.TextAlignment.RIGHT;

                            return TextDisplay.TextAlignment.CENTER;
                        }, (w, newAlign) -> {
                            switch (newAlign) {
                                case LEFT -> w.setAlignLeft(true);
                                case RIGHT -> w.setAlignRight(true);
                                case CENTER -> {
                                    w.setAlignLeft(false);
                                    w.setAlignRight(false);
                                }
                            }
                        }, "display text alignment", "display alignment")
                .constructor(context -> {
                    TextDisplayMeta meta = (TextDisplayMeta) BaseMetaFieldRegistrar.BASE_ENTITY_META_CONSTRUCTOR.apply((ConstructionContext) context);
                    DISPLAY_CONSUMER.accept((ConstructionContext) context, meta);

                    Component text = context.getOptional("display text", Component.class);
                    if (text != null) {
                        meta.setText(text);
                    }

                    Number lineWidth = context.getOptional("display line width", Number.class);
                    if (lineWidth != null) {
                        meta.setLineWidth(lineWidth.intValue());
                    }

                    ch.njol.skript.util.Color color = context.getOptional("display background color", ch.njol.skript.util.Color.class);
                    if (color != null) {
                        meta.setBackgroundColor(color.asARGB());
                    }

                    // text opacity in the future (see the to do)

                    Boolean shadow = context.getOptional("display text shadowed state", Boolean.class);
                    if (shadow != null) {
                        meta.setShadow(shadow);
                    }

                    Boolean seeThrough = context.getOptional("display see through state", Boolean.class);
                    if (seeThrough != null) {
                        meta.setSeeThrough(seeThrough);
                    }

                    Boolean useDefaultBackground = context.getOptional("display use default background state", Boolean.class);
                    if (useDefaultBackground != null) {
                        meta.setUseDefaultBackground(useDefaultBackground);
                    }

                    TextDisplay.TextAlignment alignment = context.getOptional("display text alignment", TextDisplay.TextAlignment.class);
                    if (alignment != null) {
                        switch (alignment) {
                            case LEFT -> meta.setAlignLeft(true);
                            case RIGHT -> meta.setAlignRight(true);
                            case CENTER -> {
                                meta.setAlignLeft(false);
                                meta.setAlignRight(false);
                            }
                        }
                    }

                    return meta;
                })
                .build();
    }

    private static final BiConsumer<ConstructionContext<EntityType, AbstractDisplayMeta>, AbstractDisplayMeta> DISPLAY_CONSUMER = (context, meta) -> {

        Timespan interpolationDelay = context.getOptional("display interpolation delay", Timespan.class);
        if (interpolationDelay != null) {
            meta.setInterpolationDelay((int) ConversionUtil.toTicks(interpolationDelay));
        }

        Timespan transformInterpolationDuration = context.getOptional("display transform interpolation duration", Timespan.class);
        if (transformInterpolationDuration != null) {
            meta.setTransformationInterpolationDuration((int) ConversionUtil.toTicks(transformInterpolationDuration));
        }

        Timespan teleportInterpolationDuration = context.getOptional("display teleport interpolation duration", Timespan.class);
        if (teleportInterpolationDuration != null) {
            meta.setPositionRotationInterpolationDuration((int) ConversionUtil.toTicks(teleportInterpolationDuration));
        }

        Vector displayTranslation = context.getOptional("display translation", Vector.class);
        if (displayTranslation != null) {
            meta.setTranslation(ConversionUtil.toPeVectorF(displayTranslation));
        }

        Vector scale = context.getOptional("display scale", Vector.class);
        if (scale != null) {
            meta.setScale(ConversionUtil.toPeVectorF(scale));
        }

        Quaternionf leftRotation = context.getOptional("display left rotation", Quaternionf.class);
        if (leftRotation != null) {
            meta.setLeftRotation(ConversionUtil.toPeQuaternion4f(leftRotation));
        }

        Quaternionf rightRotation = context.getOptional("display right rotation", Quaternionf.class);
        if (rightRotation != null) {
            meta.setRightRotation(ConversionUtil.toPeQuaternion4f(rightRotation));
        }

        Display.Billboard billboard = context.getOrElse("display billboard constraints", Display.Billboard.class, Display.Billboard.CENTER);
        meta.setBillboardConstraints(ConversionUtil.toBillboardConstraints(billboard));

        Number brightnessOverride = context.getOptional("display brightness override", Number.class);
        if (brightnessOverride != null) {
            meta.setBrightnessOverride(brightnessOverride.intValue());
        }

        Number viewRange = context.getOptional("display view range", Number.class);
        if (viewRange != null) {
            meta.setViewRange(viewRange.intValue());
        }

        Number shadowRadius = context.getOptional("display shadow radius", Number.class);
        if (shadowRadius != null) {
            meta.setShadowRadius(shadowRadius.intValue());
        }

        Number shadowStrength = context.getOptional("display shadow strength", Number.class);
        if (shadowStrength != null) {
            meta.setShadowStrength(shadowStrength.intValue());
        }

        Number width = context.getOptional("display width", Number.class);
        if (width != null) {
            meta.setWidth(width.intValue());
        }

        Number height = context.getOptional("display height", Number.class);
        if (height != null) {
            meta.setHeight(height.intValue());
        }

        ch.njol.skript.util.Color glowColorOverride = context.getOptional("display glow color override", ch.njol.skript.util.Color.class);
        if (glowColorOverride != null) {
            meta.setGlowColorOverride(glowColorOverride.asARGB());
        }
    };
}
