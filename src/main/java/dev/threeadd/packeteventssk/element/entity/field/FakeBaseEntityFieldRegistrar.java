package dev.threeadd.packeteventssk.element.entity.field;

import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.world.Location;
import dev.threeadd.packeteventssk.api.util.ConversionUtil;
import dev.threeadd.packeteventssk.api.util.field.ConstructionContext;
import dev.threeadd.packeteventssk.api.util.field.FieldRegistrar;
import me.tofaa.entitylib.EntityLib;
import me.tofaa.entitylib.meta.EntityMeta;
import me.tofaa.entitylib.wrapper.WrapperEntity;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

public class FakeBaseEntityFieldRegistrar implements FieldRegistrar {

    @Override
    public void register() {

        FakeEntityFieldRegistry.INSTANCE.builder(EntityTypes.ENTITY, WrapperEntity.class)
                .requiredField(Player[].class,
                        w -> w.getViewers().stream().map(Bukkit::getPlayer).filter(Objects::nonNull).toArray(Player[]::new),
                        FakeBaseEntityFieldRegistrar::setViewers,
                        "entity viewers", "viewers")
                .requiredField(org.bukkit.Location.class,
                        w -> ConversionUtil.toBukkitLocation(w.getLocation()),
                        (w, newVec) -> w.setLocation(ConversionUtil.toPeLocation(newVec)),
                        "entity location", "location")
                .optionalField(Number.class, WrapperEntity::getEntityId, null, "entity id", "id")
                .optionalField(EntityMeta.class, WrapperEntity::getEntityMeta, null, "entity meta data", "meta data", "meta")
                .optionalField(EntityType.class, WrapperEntity::getEntityType, null, "entity type", "type")
                .optionalField(UUID.class, WrapperEntity::getUuid, null, "entity uuid", "uuid")
                .constructor(context -> {
                    CommonEntityData data = CommonEntityData.extract(context);

                    WrapperEntity entity = new WrapperEntity(data.entityId(), data.uuid(), context.getKey(), data.meta());

                    finalizeEntitySetup(entity, context);

                    return entity;
                })
                .build();
    }

    public record CommonEntityData(int entityId, UUID uuid, @Nullable EntityMeta meta) {
        public static CommonEntityData extract(ConstructionContext<EntityType, ?> context) {
            EntityType type = context.getKey();

            UUID uuid = context.getOptional("entity uuid", UUID.class);
            if (uuid == null) {
                uuid = EntityLib.getPlatform().getEntityUuidProvider().provide(type);
            }

            EntityMeta meta = context.getOptional("entity meta data", EntityMeta.class);
            int entityId;

            if (meta != null) {
                entityId = meta.createPacket().getEntityId();
            } else {
                Number providedId = context.getOptional("entity id", Number.class);
                entityId = (providedId != null) ? providedId.intValue() : EntityLib.getPlatform().getEntityIdProvider().provide(uuid, type);
                meta = new EntityMeta(entityId);
            }

            return new CommonEntityData(entityId, uuid, meta);
        }
    }

    public static void finalizeEntitySetup(WrapperEntity entity, ConstructionContext<EntityType, ?> context) {
        Player[] viewers = context.getOptional("viewers", Player[].class);
        if (viewers != null) {
            for (Player newViewer : viewers) {
                entity.addViewerSilently(newViewer.getUniqueId());
            }
        }

        org.bukkit.Location location = context.getRequired("entity location", org.bukkit.Location.class);
        Location peLocation = ConversionUtil.toPeLocation(location);
        entity.setLocation(peLocation);
        entity.spawn(peLocation);
    }

    private static void setViewers(WrapperEntity w, Player[] viewers) {
        Set.copyOf(w.getViewers()).forEach(w::removeViewer);
        for (Player newViewer : viewers) {
            w.addViewer(newViewer.getUniqueId());
        }
    }
}
