package threeadd.packetEventsSK.element.entity.sections;

import ch.njol.skript.Skript;
import ch.njol.skript.bukkitutil.EntityUtils;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.entity.EntityType;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.Section;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.util.Direction;
import ch.njol.util.Kleenean;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.player.UserProfile;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import me.tofaa.entitylib.EntityLib;
import me.tofaa.entitylib.wrapper.WrapperEntity;
import me.tofaa.entitylib.wrapper.WrapperPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;
import java.util.WeakHashMap;

@SuppressWarnings("unused")
@Name("Fake Entity - Create Fake Entity")
@Description("Create a new fake entity from an entity type.")
@Examples({
        """
        command test:
            trigger:
                spawn a new fake text display entity at player for players:
                    set fake display content of fake entity to "<RAINBOW>HEYY IM FOLLLOWING YOU"
                    set fake display billboard of fake entity to center
         
                    set fake display teleport interpolation duration of fake entity to 1 second
         
                    loop 5 times:
                        teleport fake entity the fake entity to player
                        wait 1 second
         
                    kill fake entity the fake entity
        """
})
@Since("1.0.0")
public class CreateFakeEntitySec extends Section {

    private static final Logger log = LoggerFactory.getLogger(CreateFakeEntitySec.class);
    private static final WeakHashMap<Event, WrapperEntity> lastEntities = new WeakHashMap<>();

    public static void register(SyntaxRegistry registry) {
        registry.register(
                SyntaxRegistry.SECTION,
                SyntaxInfo.builder(CreateFakeEntitySec.class)
                        .supplier(CreateFakeEntitySec::new)
                        .addPatterns("(make|create|spawn) [a] [new] fake %entitytype% [entity] [%-direction% %-location%] [for %-players%]:")
                        .build()
        );
    }

    public static WrapperEntity getLastEntity(Event event) {
        return lastEntities.get(event);
    }

    private Expression<EntityType> entityTypeExpr;
    private @Nullable Expression<Location> locationExpr;
    private @Nullable Expression<Player> playerExpr;

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
            this.playerExpr = (Expression<Player>) expressions[3];
        }

        if (sectionNode != null) {
            loadOptionalCode(sectionNode);
        }
        return true;
    }

    @Override
    protected TriggerItem walk(@NotNull Event event) {
        WrapperEntity entity = createEntity(event);
        if (entity == null) {
            return getNext();
        }

        lastEntities.put(event, entity);
        return walk(event, true);
    }

    private @Nullable WrapperEntity createEntity(@NotNull Event event) {
        EntityType type = entityTypeExpr.getSingle(event);
        if (type == null) return null;

        org.bukkit.entity.EntityType bukkitType = EntityUtils.toBukkitEntityType(type.data);
        com.github.retrooper.packetevents.protocol.entity.type.EntityType packetEventsType =
                SpigotConversionUtil.fromBukkitEntityType(bukkitType);

        UUID uuid = UUID.randomUUID();
        int entityId = EntityLib.getPlatform().getEntityIdProvider().provide(uuid, packetEventsType);

        WrapperEntity entity;
        if (packetEventsType != EntityTypes.PLAYER) {
            entity = new WrapperEntity(entityId, uuid, packetEventsType);
        } else {
            UserProfile profile = new UserProfile(uuid, "test");
            entity = new WrapperPlayer(profile, entityId);
        }

        if (locationExpr != null) {
            Location location = locationExpr.getSingle(event);
            if (location == null) return null;
            entity.spawn(SpigotConversionUtil.fromBukkitLocation(location));
        }

        if (playerExpr != null) {
            Player[] players = playerExpr.getArray(event);
            for (Player player : players) {
                entity.addViewer(player.getUniqueId());
            }
        }

        return entity;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "create fake entity";
    }
}