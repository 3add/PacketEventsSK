package dev.threeadd.packeteventssk.element.general.field.meta;

import ch.njol.skript.registrations.Classes;
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

// Should fully match: https://minecraft.wiki/w/Java_Edition_protocol/Entity_metadata#Entity
// including order of fields
public class BaseMetaFieldRegistrar implements FieldRegistrar {

    @Override
    public boolean register() {

        // complete
        MetaFieldRegistry.Builder<EntityMeta> builder = MetaFieldRegistry.INSTANCE.builder(EntityTypes.ENTITY, EntityMeta.class)
                .optionalField(Number.class,
                        EntityMeta::getEntityId,
                        null,
                        "entity id", "id")
                // start of metadata
                .optionalField(Boolean.class,
                        EntityMeta::isOnFire,
                        EntityMeta::setOnFire,
                        "on fire state", "on fire")
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
                .optionalField(Boolean.class,
                        EntityMeta::isInvisible,
                        EntityMeta::setInvisible,
                        "invisible state", "invisible")
                .optionalField(Boolean.class,
                        EntityMeta::isGlowing,
                        EntityMeta::setGlowing,
                        "glowing state", "glowing")
                .optionalField(Boolean.class,
                        EntityMeta::isFlyingWithElytra,
                        EntityMeta::setFlyingWithElytra,
                        "elytra flying state", "elytra flying")
                /*
                .optionalField(Timespan.class,
                        meta -> ConversionUtil.toTimespan(meta.getAirTicks()),
                        (meta, timespan) -> meta.setAirTicks((short) ConversionUtil.toTicks(timespan)),
                        "air time", "air ticks")
                 TODO re-enable when entity lib fixes (https://github.com/Tofaa2/EntityLib/issues/63)
                 */
                .optionalField(Component.class,
                        EntityMeta::getCustomName,
                        EntityMeta::setCustomName,
                        "custom name")
                .optionalField(Boolean.class,
                        EntityMeta::isCustomNameVisible,
                        EntityMeta::setCustomNameVisible,
                        "custom name visible state", "custom name visible")
                .optionalField(Boolean.class,
                        EntityMeta::isSilent,
                        EntityMeta::setSilent,
                        "silent state", "silent")
                .optionalField(Boolean.class,
                        meta -> !meta.hasNoGravity(),
                        (meta, newState) -> meta.setHasNoGravity(!newState),
                        "gravity state", "gravity")
                // pose (registered conditionally)
                .optionalField(Timespan.class,
                        meta -> ConversionUtil.toTimespan(meta.getTicksFrozenInPowderedSnow()),
                        (meta, timespan) -> meta.setTicksFrozenInPowderedSnow((short) ConversionUtil.toTicks(timespan)),
                        "frozen time", "frozen ticks")
                .constructor(BASE_ENTITY_META_CONSTRUCTOR);

        // only register if an addon provides this type
        if (Classes.getExactClassInfo(Pose.class) != null) {
            builder.optionalField(Pose.class,
                    meta -> SpigotConversionUtil.toBukkitPose(meta.getPose()),
                    (meta, newPose) -> meta.setPose(SpigotConversionUtil.fromBukkitPose(newPose)),
                    "pose");
        }

        builder.build();

        return true;
    }

    @SuppressWarnings("UnstableApiUsage")
    static final Function<ConstructionContext<EntityType, EntityMeta>, EntityMeta> BASE_ENTITY_META_CONSTRUCTOR = context -> {

        Number entityId = context.getOptional("entity id", Number.class);
        if (entityId == null) {
            entityId = SpigotReflectionUtil.generateEntityId();
        }
        EntityMeta meta = EntityMeta.createMeta(entityId.intValue(), context.getKey());

        // start metadata

        Boolean onFire = context.getOptional("on fire state", Boolean.class);
        if (onFire != null) {
            meta.setOnFire(onFire);
        }

        Boolean sneaking = context.getOptional("sneaking state", Boolean.class);
        if (sneaking != null) {
            meta.setSneaking(sneaking);
        }

        Boolean sprinting = context.getOptional("sprinting state", Boolean.class);
        if (sprinting != null) {
            meta.setSprinting(sprinting);
        }

        Boolean swimming = context.getOptional("swimming state", Boolean.class);
        if (swimming != null) {
            meta.setSwimming(swimming);
        }

        Boolean invisible = context.getOptional("invisible state", Boolean.class);
        if (invisible != null) {
            meta.setInvisible(invisible);
        }

        Boolean glowing = context.getOptional("glowing state", Boolean.class);
        if (glowing != null) {
            meta.setGlowing(glowing);
        }

        Boolean elytraFlying = context.getOptional("elytra flying state", Boolean.class);
        if (elytraFlying != null) {
            meta.setFlyingWithElytra(elytraFlying);
        }

        /*
        Timespan airTime = context.getOptional("air time", Timespan.class);
        if (airTime != null) {
            meta.setAirTicks((short) ConversionUtil.toTicks(airTime));
        }
        re-enable when entity lib fixes: https://github.com/Tofaa2/EntityLib/issues/63
         */

        Component customName = context.getOptional("custom name", Component.class);
        if (customName != null) {
            meta.setCustomName(customName);
        }

        Boolean customNameVisible = context.getOptional("custom name visible state", Boolean.class);
        if (customNameVisible != null) {
            meta.setCustomNameVisible(customNameVisible);
        }

        Boolean silent = context.getOptional("silent state", Boolean.class);
        if (silent != null) {
            meta.setSilent(silent);
        }

        Boolean gravity = context.getOptional("gravity state", Boolean.class);
        if (gravity != null) {
            meta.setHasNoGravity(!gravity);
        }

        Pose pose = context.getOptional("pose", Pose.class);
        if (pose != null) {
            meta.setPose(SpigotConversionUtil.fromBukkitPose(pose));
        }

        Timespan frozenTime = context.getOptional("frozen time", Timespan.class);
        if (frozenTime != null) {
            meta.setTicksFrozenInPowderedSnow((short) ConversionUtil.toTicks(frozenTime));
        }

        return meta;
    };
}
