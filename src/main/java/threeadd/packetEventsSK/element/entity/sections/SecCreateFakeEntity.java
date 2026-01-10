package threeadd.packetEventsSK.element.entity.sections;

import ch.njol.skript.Skript;
import ch.njol.skript.bukkitutil.EntityUtils;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.entity.EntityType;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.util.Direction;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import threeadd.packetEventsSK.util.section.ReturningSection;

import java.util.UUID;

@SuppressWarnings("unused")
@Name("Fake Entity - Create Fake Entity")
@Description("Create a new fake entity from an entity type.")
@Examples("""
        command test:
            trigger:
                spawn a new fake text display entity at player for players:
                    set fake display content of fake entity to minimessage from "Hello"
                    set fake display billboard of fake entity to center

                    set fake display teleport interpolation duration of fake entity to 1 second

                    loop 5 times:
                        teleport fake entity the fake entity to player
                        wait 1 second

                    kill fake entity the fake entity
        """)
@Since("1.0.0")
public class SecCreateFakeEntity extends ReturningSection<WrapperEntity> {

    private static final Logger log = LoggerFactory.getLogger(SecCreateFakeEntity.class);

    public static class FakeEntityBuilder extends ReturningSection.LastBuilderExpression<WrapperEntity, SecCreateFakeEntity> {}

    static {
        register(
                SecCreateFakeEntity.class,
                WrapperEntity.class,
                FakeEntityBuilder.class,
                new String[]{
                        "[the] fake[ ]entity [builder]"
                },
                "(make|create|spawn) [a] [new] fake %entitytype% [entity] [%-direction% %-location%] [for %-players%]"
        );
    }

    private Expression<EntityType> entityTypeExpr;
    private @Nullable Expression<Location> locationExpr = null;
    private @Nullable Expression<Player> playerExpr = null;

    @SuppressWarnings("unchecked")
    @Override
    protected boolean initialize() {
        this.entityTypeExpr = (Expression<EntityType>) exprs[0];

        if (exprs[1] != null) {
            if (exprs[2] == null) {
                Skript.error("Invalid Direction Location");
                return false;
            }

            this.locationExpr = Direction.combine((Expression<Direction>) exprs[1], (Expression<Location>) exprs[2]);
        }

        if (exprs[3] != null)
            this.playerExpr = (Expression<Player>) exprs[3];

        return true;
    }

    @Override
    public WrapperEntity createNewValue(@NotNull Event event) {
        EntityType type = entityTypeExpr.getSingle(event);
        if (type == null) return null;

        org.bukkit.entity.EntityType bukkitType = EntityUtils.toBukkitEntityType(type.data);
        com.github.retrooper.packetevents.protocol.entity.type.EntityType packetEventsType =
                SpigotConversionUtil.fromBukkitEntityType(bukkitType);

        UUID uuid = UUID.randomUUID();
        int entityId = EntityLib.getPlatform().getEntityIdProvider().provide(uuid, packetEventsType);

        WrapperEntity entity;
        if (packetEventsType != EntityTypes.PLAYER)
            entity = new WrapperEntity(entityId, uuid, packetEventsType);
        else {
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
            for (Player player : players)
                entity.addViewer(player.getUniqueId());
        }

        return entity;
    }

    @Override
    public String toString(Event e, boolean debug) { return "create fake entity"; }
}