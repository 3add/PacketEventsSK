package dev.threeadd.packeteventssk.element.general.field.meta;

import ch.njol.skript.util.Timespan;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import dev.threeadd.packeteventssk.api.util.ConversionUtil;
import dev.threeadd.packeteventssk.api.util.field.ConstructionContext;
import dev.threeadd.packeteventssk.api.util.field.FieldRegistrar;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import io.github.retrooper.packetevents.util.SpigotReflectionUtil;
import me.tofaa.entitylib.meta.EntityMeta;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Pose;

import java.util.function.Function;

public class BaseMetaFieldRegistrar implements FieldRegistrar {

    @Override
    public boolean register() {
        MetaFieldRegistry.INSTANCE.builder(EntityTypes.ENTITY, EntityMeta.class)
                .optionalField(Number.class,
                        EntityMeta::getEntityId,
                        null,
                        "entity id", "id")
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
                        "custom name visible", "custom name visible state")
                .optionalField(Boolean.class,
                        EntityMeta::isFlyingWithElytra,
                        EntityMeta::setFlyingWithElytra,
                        "elytra flying", "elytra flying state")
                .optionalField(Boolean.class,
                        EntityMeta::isOnFire,
                        EntityMeta::setOnFire,
                        "on fire", "on fire state")
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
                        "invisible", "invisible state")
                .optionalField(Pose.class,
                        meta -> SpigotConversionUtil.toBukkitPose(meta.getPose()),
                        (meta, newPose) -> meta.setPose(SpigotConversionUtil.fromBukkitPose(newPose)),
                        "pose")
                .optionalField(Boolean.class,
                        EntityMeta::isSilent,
                        EntityMeta::setSilent,
                        "silent", "silent state")
                .optionalField(Boolean.class,
                        EntityMeta::isSneaking,
                        EntityMeta::setSneaking,
                        "sneaking", "sneaking state")
                .optionalField(Boolean.class,
                        EntityMeta::isSprinting,
                        EntityMeta::setSprinting,
                        "sprinting", "sprinting state")
                .optionalField(Boolean.class,
                        EntityMeta::isSwimming,
                        EntityMeta::setSwimming,
                        "swimming", "swimming state")
                .constructor(BASE_ENTITY_META_CONSTRUCTOR)
                .build();

        return true;
    }

    @SuppressWarnings("UnstableApiUsage")
    static final Function<ConstructionContext<EntityType, EntityMeta>, EntityMeta> BASE_ENTITY_META_CONSTRUCTOR = context -> {

        Number entityId = context.getOptional("entity id", Number.class);
        if (entityId == null) {
            entityId = SpigotReflectionUtil.generateEntityId();
        }
        EntityMeta meta = EntityMeta.createMeta(entityId.intValue(), context.getKey());

        Timespan airTime = context.getOptional("air time", Timespan.class);
        if (airTime != null) {
            meta.setAirTicks((short) ConversionUtil.toTicks(airTime));
        }

        Component customName = context.getOptional("custom name", Component.class);
        if (customName != null) {
            meta.setCustomName(customName);
        }

        Boolean customNameVisible = context.getOptional("custom name visible state", Boolean.class);
        if (customNameVisible != null) {
            meta.setCustomNameVisible(customNameVisible);
        }

        Boolean elytraFlying = context.getOptional("elytra flying state", Boolean.class);
        if (elytraFlying != null) {
            meta.setFlyingWithElytra(elytraFlying);
        }

        Boolean onFire = context.getOptional("on fire", Boolean.class);
        if (onFire != null) {
            meta.setOnFire(onFire);
        }

        Timespan frozenTime = context.getOptional("frozen time", Timespan.class);
        if (frozenTime != null) {
            meta.setTicksFrozenInPowderedSnow((short) ConversionUtil.toTicks(frozenTime));
        }

        Boolean glowing = context.getOptional("glowing", Boolean.class);
        if (glowing != null) {
            meta.setGlowing(glowing);
        }

        Boolean gravity = context.getOptional("gravity", Boolean.class);
        if (gravity != null) {
            meta.setHasNoGravity(!gravity);
        }

        Boolean invisible = context.getOptional("invisible", Boolean.class);
        if (invisible != null) {
            meta.setInvisible(invisible);
        }

        Pose pose = context.getOptional("pose", Pose.class);
        if (pose != null) {
            meta.setPose(SpigotConversionUtil.fromBukkitPose(pose));
        }

        Boolean silent = context.getOptional("silent", Boolean.class);
        if (silent != null) {
            meta.setSilent(silent);
        }

        Boolean sneaking = context.getOptional("sneaking", Boolean.class);
        if (sneaking != null) {
            meta.setSneaking(sneaking);
        }

        Boolean sprinting = context.getOptional("sprinting", Boolean.class);
        if (sprinting != null) {
            meta.setSprinting(sprinting);
        }

        Boolean swimming = context.getOptional("swimming", Boolean.class);
        if (swimming != null) {
            meta.setSwimming(swimming);
        }

        return meta;
    };
}
