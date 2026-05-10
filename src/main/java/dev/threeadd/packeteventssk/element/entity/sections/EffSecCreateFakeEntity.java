package dev.threeadd.packeteventssk.element.entity.sections;

import ch.njol.skript.Skript;
import ch.njol.skript.bukkitutil.EntityUtils;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.entity.EntityType;
import ch.njol.skript.lang.*;
import ch.njol.skript.util.Direction;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.player.UserProfile;
import com.github.shanebeee.skr.Registration;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import me.tofaa.entitylib.EntityLib;
import me.tofaa.entitylib.wrapper.WrapperEntity;
import me.tofaa.entitylib.wrapper.WrapperLivingEntity;
import me.tofaa.entitylib.wrapper.WrapperPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class EffSecCreateFakeEntity extends EffectSection {

    public static void register(Registration reg) {
        reg.newSection(EffSecCreateFakeEntity.class, "(make|create|spawn) [a] [new] fake %entitytype% [entity] [%-direction% %-location%] [for %-players%] [and store (it|the result) in %-objects%]")
                .name("Fake Entity - Create Fake Entity")
                .description("""
                       Create a new fake entity from an entity type.
                       This creates its own internal event, which means previous event-values will not work.
                       """)
                .examples("""
                        command test:
                            trigger:
                                set {_p} to player
                                spawn a new fake text display entity at player for players:
                                    set fake display content of fake entity to "<RAINBOW>HEYY IM FOLLOWING YOU"
                                    set fake display billboard of fake entity to center
                        
                                    set fake display teleport interpolation duration of fake entity to 1 second
                        
                                    loop 5 times:
                                        teleport fake entity the fake entity to {_p}
                                        wait 1 second
                        
                                    kill fake entity the fake entity
                        """)
                .since("1.0.0")
                .register();

        reg.newEventValue(CreateFakeEntityEvent.class, WrapperEntity.class)
                .converter(CreateFakeEntityEvent::getWrapperEntity)
                .register();
    }

    private Trigger trigger;
    private Expression<EntityType> entityTypeExpr;
    private @Nullable Expression<Location> locationExpr;
    private @Nullable Expression<Player> viewerExpr;
    private @Nullable Expression<Object> storeExpr;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions,
                        int matchedPattern,
                        Kleenean isDelayed,
                        SkriptParser.ParseResult parseResult,
                        @Nullable SectionNode sectionNode,
                        @Nullable List<TriggerItem> triggerItems) {

        this.entityTypeExpr = (Expression<EntityType>) expressions[0];

        if (expressions[1] != null) {
            if (expressions[2] == null) {
                Skript.error("Invalid Direction Location");
                return false;
            }

            this.locationExpr = Direction.combine(
                    (Expression<Direction>) expressions[1],
                    (Expression<Location>) expressions[2]
            );
        }

        if (expressions[3] != null) {
            this.viewerExpr = (Expression<Player>) expressions[3];
        }

        if (expressions[4] != null) {
            this.storeExpr = (Expression<Object>) expressions[4];
            if (!Changer.ChangerUtils.acceptsChange(storeExpr, Changer.ChangeMode.SET, WrapperEntity.class)) {
                Skript.error(this.storeExpr.toString(null, Skript.debug()) + " cannot be set to store a fake entity");
                return false;
            }
        }

        if (sectionNode == null) return true;

        this.trigger = loadCode(sectionNode, "create fake entity", CreateFakeEntityEvent.class);
        return true;
    }

    @Override
    protected TriggerItem walk(@NotNull Event event) {
        WrapperEntity entity = createEntity(event);
        if (entity == null) {
            return getNext();
        }

        if (this.storeExpr != null) {
            this.storeExpr.change(event, new Object[]{entity}, Changer.ChangeMode.SET);
        }

        if (this.trigger == null) {
            return super.walk(event, true);
        }

        CreateFakeEntityEvent createFakeEntityEvent = new CreateFakeEntityEvent(entity);
        Variables.withLocalVariables(event, createFakeEntityEvent, () -> TriggerItem.walk(this.trigger, createFakeEntityEvent));

        return super.walk(event, true);
    }

    private @Nullable WrapperEntity createEntity(@NotNull Event event) {
        EntityType type = this.entityTypeExpr.getSingle(event);
        if (type == null) return null;

        org.bukkit.entity.EntityType bukkitType = EntityUtils.toBukkitEntityType(type.data);
        com.github.retrooper.packetevents.protocol.entity.type.EntityType packetEventsType =
                SpigotConversionUtil.fromBukkitEntityType(bukkitType);

        UUID uuid = UUID.randomUUID();
        int entityId = EntityLib.getPlatform().getEntityIdProvider().provide(uuid, packetEventsType);

        WrapperEntity entity;
        if (packetEventsType == EntityTypes.PLAYER) {
            UserProfile profile = new UserProfile(uuid, "test"); // TODO remove this test stuff
            entity = new WrapperPlayer(profile, entityId);
        } else if (isLivingEntity(bukkitType)) {
            entity = new WrapperLivingEntity(entityId, uuid, packetEventsType);
        } else {
            entity = new WrapperEntity(entityId, uuid, packetEventsType);
        }

        if (this.locationExpr != null) {
            Location location = this.locationExpr.getSingle(event);
            if (location == null) return null;
            entity.spawn(SpigotConversionUtil.fromBukkitLocation(location));
        }

        if (this.viewerExpr != null) {
            Player[] players = this.viewerExpr.getAll(event);
            for (Player player : players) {
                entity.addViewer(player.getUniqueId());
            }
        }

        return entity;
    }

    private boolean isLivingEntity(org.bukkit.entity.EntityType bukkitType) {
        try {
            Class<? extends Entity> entityClass = bukkitType.getEntityClass();
            return entityClass != null && LivingEntity.class.isAssignableFrom(entityClass);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        String entityType = this.entityTypeExpr.toString(event, debug);
        String locationPart = this.locationExpr != null ? " at " + this.locationExpr.toString(event, debug) : "";
        String viewerPart = this.viewerExpr != null ? " for " + this.viewerExpr.toString(event, debug) : "";
        String storagePart = this.storeExpr != null ? " and store it in " + this.storeExpr.toString(event, debug) : "";
        return String.format("create fake %s entity%s%s%s", entityType, locationPart, viewerPart, storagePart);
    }

    public static class CreateFakeEntityEvent extends Event {
        private final WrapperEntity wrapperEntity;

        public CreateFakeEntityEvent(WrapperEntity wrapperEntity) {
            this.wrapperEntity = wrapperEntity;
        }

        public WrapperEntity getWrapperEntity() {
            return this.wrapperEntity;
        }

        @Override
        public @NotNull HandlerList getHandlers() {
            throw new IllegalStateException();
        }
    }
}