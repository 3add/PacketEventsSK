package dev.threeadd.packeteventssk.api.simple;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.player.GameMode;
import com.github.retrooper.packetevents.protocol.player.TextureProperty;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.protocol.player.UserProfile;
import com.github.retrooper.packetevents.protocol.world.Difficulty;
import com.github.retrooper.packetevents.protocol.world.WorldBlockPosition;
import com.github.retrooper.packetevents.protocol.world.dimension.DimensionType;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChangeGameState.Reason;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoUpdate.PlayerInfo;
import com.google.common.hash.Hashing;
import dev.threeadd.packeteventssk.api.entity.Skin;
import dev.threeadd.packeteventssk.api.entity.SkinManager;
import dev.threeadd.packeteventssk.api.util.ConversionUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class PlayerSkinManager {

    // UUID -> (Viewer Player UUID -> Custom Skin)
    private static final Map<UUID, Map<UUID, Skin>> skinMap = new HashMap<>();

    private static final Map<UUID, Skin> globalSkinMap = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(PlayerSkinManager.class);

    public static void setGlobalSkin(Player target, Skin skin) {
        if (target == null || skin == null) return;

        if (skin.equals(SkinManager.getPlayer(Bukkit.getPlayer(target.getUniqueId())))) {
            // no need to save if it's the default skin
            globalSkinMap.remove(target.getUniqueId());
        } else {
            globalSkinMap.put(target.getUniqueId(), skin);
        }

        // Update for all current online players
        updateSkin(target, Bukkit.getOnlinePlayers());
    }

    public static void setSkinForViewers(Player target, Collection<? extends Player> viewers, Skin skin) {
        if (target == null || viewers == null || skin == null) return;

        Map<UUID, Skin> targetMap = skinMap.computeIfAbsent(target.getUniqueId(), k -> new HashMap<>());
        for (Player Viewer : viewers) {
            targetMap.put(Viewer.getUniqueId(), skin);
        }

        updateSkin(target, viewers);
    }

    public static @Nullable Skin getSkinForViewer(UUID targetId, UUID viewerId) {
        Map<UUID, Skin> viewers = skinMap.get(targetId);
        if (viewers != null && viewers.containsKey(viewerId)) {
            return viewers.get(viewerId);
        }

        if (globalSkinMap.containsKey(targetId)) {
            return globalSkinMap.get(targetId);
        }

        return null;
    }

    public static void clearAllSkins(Player target) {
        Map<UUID, Skin> viewers = skinMap.remove(target.getUniqueId());
        globalSkinMap.remove(target.getUniqueId());
        if (viewers != null) {
            for (UUID viewerId : viewers.keySet()) {
                Player player = Bukkit.getPlayer(viewerId);
                if (player == null) continue;

                updateSkin(target, List.of(player));
            }
        }
    }

    private static void updateSkin(Player target, Collection<? extends Player> viewers) {
        User targetUser = PacketEvents.getAPI().getPlayerManager().getUser(target);

        log.error("target: {}, viewers: {}", target.getName(), viewers.stream().map(Player::getName).toList());

        // unregister player for all players with the old data
        WrapperPlayServerPlayerInfoRemove infoRemove = new WrapperPlayServerPlayerInfoRemove(targetUser.getUUID());
        for (Player viewer : viewers) {
            if (viewer.canSee(target)) {
                PacketEvents.getAPI().getPlayerManager().sendPacket(viewer, infoRemove);
            }
        }

        WrapperPlayServerDestroyEntities destroyEntities = new WrapperPlayServerDestroyEntities(target.getEntityId());
        WrapperPlayServerSpawnEntity spawnEntity = new WrapperPlayServerSpawnEntity(target.getEntityId(), target.getUniqueId(), EntityTypes.PLAYER, ConversionUtil.toPeLocation(target.getLocation()),
                target.getBodyYaw(), 0 /* not relevant for players */, ConversionUtil.toPeVectorD(target.getVelocity()));

        // re-register the profile for viewer players
        for (Player viewer : viewers) {
            if (viewer.canSee(target)) {
                PacketEvents.getAPI().getPlayerManager().sendPacket(viewer, destroyEntities);
                reRegisterProfile(target, viewer);
                PacketEvents.getAPI().getPlayerManager().sendPacket(viewer, spawnEntity);
            }
        }

        // Refresh misc player things AFTER sending game profile
        if (viewers.contains(target)) {
            refreshPlayer(target);
        }
    }

    private static void reRegisterProfile(Player target, Player viewer) {
        User targetUser = PacketEvents.getAPI().getPlayerManager().getUser(target);

        List<TextureProperty> properties;
        Skin customSkin = getSkinForViewer(target.getUniqueId(), viewer.getUniqueId());
        if (customSkin != null) {
            properties = customSkin.properties();
        } else {
            properties = targetUser.getProfile().getTextureProperties();
        }

        UserProfile profile = new UserProfile(targetUser.getProfile().getUUID(), targetUser.getProfile().getName(), properties);

        PlayerInfo playerInfo = new PlayerInfo(profile, true, target.getPing(), GameMode.valueOf(target.getGameMode().name()),
                target.displayName(), ChatSessionListener.getChatSession(target.getUniqueId()));

        WrapperPlayServerPlayerInfoUpdate infoUpdate = new WrapperPlayServerPlayerInfoUpdate(
                EnumSet.allOf(WrapperPlayServerPlayerInfoUpdate.Action.class), playerInfo);

        PacketEvents.getAPI().getPlayerManager().sendPacket(viewer, infoUpdate);
    }

    private static void refreshPlayer(Player player) {
        User user = PacketEvents.getAPI().getPlayerManager().getUser(player);

        World world = player.getWorld();
        DimensionType dimensionType = user.getDimensionType();
        String worldName = dimensionType.getName().getKey();
        Difficulty difficulty = Difficulty.valueOf(world.getDifficulty().name());
        long hashedSeed = Hashing.sha256().hashLong(world.getSeed()).asLong();
        GameMode gameMode = GameMode.valueOf(player.getGameMode().name());
        @Nullable GameMode prevGameMode = player.getPreviousGameMode() != null ? GameMode.valueOf(player.getPreviousGameMode().name()) : null;
        @Nullable WorldBlockPosition position = player.getLastDeathLocation() != null ? ConversionUtil.toWorldBlockPosition(player.getLastDeathLocation()) : null;

        WrapperPlayServerRespawn respawn = new WrapperPlayServerRespawn(
                dimensionType, worldName, difficulty,
                hashedSeed, gameMode, prevGameMode,
                false, false, WrapperPlayServerRespawn.KEEP_ALL_DATA,
                position, null);

        PacketEvents.getAPI().getPlayerManager().sendPacket(player, respawn);

        player.teleportAsync(player.getLocation()); // replaces WrapperPlayServerPlayerPositionAndLook cause it's advertised as buggy if done using the packet

        WrapperPlayServerChangeGameState startLoadingChunksPacket = new WrapperPlayServerChangeGameState(
                Reason.START_LOADING_CHUNKS,
                0.0f);
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, startLoadingChunksPacket);
    }
}
