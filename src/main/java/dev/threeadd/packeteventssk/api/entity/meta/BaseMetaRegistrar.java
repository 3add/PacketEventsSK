package dev.threeadd.packeteventssk.api.entity.meta;

import ch.njol.skript.util.Timespan;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import dev.threeadd.packeteventssk.api.util.ConversionUtil;
import dev.threeadd.packeteventssk.api.util.field.FieldRegistrar;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import me.tofaa.entitylib.meta.EntityMeta;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Pose;

public class BaseMetaRegistrar implements FieldRegistrar {

    @Override
    public boolean register() {
        MetaFieldRegistry.INSTANCE.builder(EntityTypes.ENTITY, EntityMeta.class)
                .optionalField(Timespan.class,
                        meta -> ConversionUtil.toTimespan(meta.getAirTicks()),
                        (meta, timespan) -> meta.setAirTicks((short) ConversionUtil.toTicks(timespan)),
                        "air time", "air ticks")
                .optionalField(Component.class,
                        EntityMeta::getCustomName,
                        EntityMeta::setCustomName,
                        "custom name")
                .optionalField(Boolean.class,
                        EntityMeta::isCustomNameVisible,
                        EntityMeta::setCustomNameVisible,
                        "custom name visible state", "custom name visible")
                .optionalField(Boolean.class,
                        EntityMeta::isFlyingWithElytra,
                        EntityMeta::setFlyingWithElytra,
                        "elytra flying state", "elytra flying")
                .optionalField(Boolean.class,
                        EntityMeta::isOnFire,
                        EntityMeta::setOnFire,
                        "on fire state", "on fire")
                .optionalField(Timespan.class,
                        meta -> ConversionUtil.toTimespan(meta.getTicksFrozenInPowderedSnow()),
                        (meta, timespan) -> meta.setTicksFrozenInPowderedSnow((short) ConversionUtil.toTicks(timespan)),
                        "frozen time", "frozen ticks")
                .optionalField(Boolean.class,
                        EntityMeta::isGlowing,
                        EntityMeta::setGlowing,
                        "glowing", "glowing state")
                .optionalField(Boolean.class,
                        meta -> !meta.hasNoGravity(),
                        (meta, newState) -> meta.setHasNoGravity(!newState),
                        "gravity", "gravity state")
                .optionalField(Boolean.class,
                        EntityMeta::isInvisible,
                        EntityMeta::setInvisible,
                        "invisible state", "invisible")
                .optionalField(Pose.class,
                        meta -> SpigotConversionUtil.toBukkitPose(meta.getPose()),
                        (meta, newPose) -> meta.setPose(SpigotConversionUtil.fromBukkitPose(newPose)),
                        "pose")
                .optionalField(Boolean.class,
                        EntityMeta::isSilent,
                        EntityMeta::setSilent,
                        "silent state", "silent")
                .optionalField(Boolean.class,
                        EntityMeta::isSneaking,
                        EntityMeta::setSneaking,
                        "sneaking state", "sneaking")
                .optionalField(Boolean.class,
                        EntityMeta::isSprinting,
                        EntityMeta::setSprinting,
                        "sprinting state", "sprinting")
                .optionalField(Boolean.class,
                        EntityMeta::isSwimming,
                        EntityMeta::setSwimming,
                        "swimming state", "swimming")
                .build(this);

        return true;
    }
}
