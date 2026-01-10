package threeadd.packetEventsSK.element.simple.api;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.player.GameMode;
import com.github.retrooper.packetevents.protocol.player.TextureProperty;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.protocol.player.UserProfile;
import com.github.retrooper.packetevents.protocol.world.Difficulty;
import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.protocol.world.WorldBlockPosition;
import com.github.retrooper.packetevents.protocol.world.dimension.DimensionType;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChangeGameState.Reason;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoUpdate.PlayerInfo;
import com.google.common.hash.Hashing;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import threeadd.packetEventsSK.element.entity.api.skin.Skin;
import threeadd.packetEventsSK.element.entity.api.skin.Skins;
import threeadd.packetEventsSK.util.ConversionUtil;

import java.util.*;
import java.util.List;

public class PlayerSkinManager {

    // UUID -> (Viewer Player UUID -> Custom Skin)
    private static final Map<UUID, Map<UUID, Skin>> skinMap = new HashMap<>();

    private static final Map<UUID, Skin> globalSkinMap = new HashMap<>();

    public static void setGlobalSkin(UUID targetId, Skin skin) {
        if (targetId == null || skin == null) return;

        if (skin.equals(Skins.getPlayer(Bukkit.getPlayer(targetId)))) {
            // no need to save if it's the default skin
            globalSkinMap.remove(targetId);
        }
        else {
            globalSkinMap.put(targetId, skin);
        }

        // Update for all current online players
        for (Player online : Bukkit.getOnlinePlayers()) {
            updateSkin(targetId, online.getUniqueId());
        }
    }

    public static void setSkinForViewer(UUID targetId, UUID viewerId, Skin skin) {
        if (targetId == null || viewerId == null || skin == null) return;

        skinMap.computeIfAbsent(targetId, k -> new HashMap<>()).put(viewerId, skin);
        updateSkin(targetId, viewerId);
    }

    public static void setSkinForViewers(UUID targetId, Collection<UUID> viewers, Skin skin) {
        if (targetId == null || viewers == null || skin == null) return;

        Map<UUID, Skin> targetMap = skinMap.computeIfAbsent(targetId, k -> new HashMap<>());
        for (UUID viewerId : viewers) {
            targetMap.put(viewerId, skin);
            updateSkin(targetId, viewerId);
        }
    }

    public static void removeSkinForViewer(UUID targetId, UUID viewerId) {
        if (targetId == null || viewerId == null) return;

        Map<UUID, Skin> viewers = skinMap.get(targetId);
        if (viewers != null) {
            viewers.remove(viewerId);
            if (viewers.isEmpty()) {
                skinMap.remove(targetId);
            }
        }

        // Update triggers a revert to original if no custom skin is found in the map
        updateSkin(targetId, viewerId);
    }

    public static @Nullable Skin getSkinForViewer(UUID targetId, UUID viewerId) {
        Map<UUID, Skin> viewers = skinMap.get(targetId);
        if (viewers != null && viewers.containsKey(viewerId)) {
            return viewers.get(viewerId);
        }

        if (globalSkinMap.containsKey(targetId))
            return globalSkinMap.get(targetId);

        return null;
    }

    public static void clearAllSkins(UUID targetId) {
        Map<UUID, Skin> viewers = skinMap.remove(targetId);
        if (viewers != null) {
            for (UUID viewerId : viewers.keySet()) {
                updateSkin(targetId, viewerId);
            }
        }
    }

    private static void updateSkin(UUID targetId, UUID viewerId) {
        Player target = Bukkit.getPlayer(targetId);
        Player viewer = Bukkit.getPlayer(viewerId);

        if (target == null || viewer == null) return;

        User targetUser = PacketEvents.getAPI().getPlayerManager().getUser(target);

        List<TextureProperty> properties;
        Skin customSkin = getSkinForViewer(targetId, viewerId);
        if (customSkin !=  null) {
            properties = customSkin.getProperties();
        } else {
            properties = targetUser.getProfile().getTextureProperties();
        }

        UserProfile profile = new UserProfile(targetUser.getProfile().getUUID(), targetUser.getProfile().getName(), properties);

        if (targetId.equals(viewerId)) {
            sendSelfUpdate(target, targetUser, profile);
        } else {
            sendRemoteUpdate(target, targetUser, viewer, profile);
        }
    }

    private static void sendRemoteUpdate(Player target, User targetUser, Player viewer, UserProfile packetProfile) {
        User viewerUser = PacketEvents.getAPI().getPlayerManager().getUser(viewer);

        WrapperPlayServerPlayerInfoRemove infoRemove = new WrapperPlayServerPlayerInfoRemove(targetUser.getUUID());
        WrapperPlayServerDestroyEntities destroy = new WrapperPlayServerDestroyEntities(targetUser.getEntityId());

        PlayerInfo playerInfo = new PlayerInfo(packetProfile, true, target.getPing(),
                ConversionUtil.toPeGameMode(target.getGameMode()),
                target.displayName(),
                ChatSessionListener.getChatSession(target.getUniqueId()));

        WrapperPlayServerPlayerInfoUpdate infoUpdate = new WrapperPlayServerPlayerInfoUpdate(
                EnumSet.allOf(WrapperPlayServerPlayerInfoUpdate.Action.class), playerInfo);

        Location location = SpigotConversionUtil.fromBukkitLocation(target.getLocation());
        WrapperPlayServerSpawnEntity spawn = new WrapperPlayServerSpawnEntity(
                targetUser.getEntityId(), targetUser.getUUID(), EntityTypes.PLAYER,
                location, 0, 0, null);

        viewerUser.sendPacket(infoRemove);
        viewerUser.sendPacket(destroy);
        viewerUser.sendPacket(infoUpdate);
        viewerUser.sendPacket(spawn);
    }

    private static void sendSelfUpdate(Player player, User user, UserProfile packetProfile) {

        PlayerInfo playerInfo = new PlayerInfo(packetProfile, true, player.getPing(),
                ConversionUtil.toPeGameMode(player.getGameMode()),
                player.displayName(),
                ChatSessionListener.getChatSession(player.getUniqueId()));

        user.sendPacket(new WrapperPlayServerPlayerInfoRemove(user.getUUID()));
        user.sendPacket(new WrapperPlayServerPlayerInfoUpdate(EnumSet.allOf(WrapperPlayServerPlayerInfoUpdate.Action.class), playerInfo));

        World world = player.getWorld();
        DimensionType dimensionType = user.getDimensionType();
        String worldName = dimensionType.getName().getKey();
        Difficulty difficulty = Difficulty.valueOf(world.getDifficulty().name());
        long hashedSeed = Hashing.sha256().hashLong(world.getSeed()).asLong();
        GameMode gameMode = ConversionUtil.toPeGameMode(player.getGameMode());
        @Nullable GameMode prevGameMode = player.getPreviousGameMode() != null ? ConversionUtil.toPeGameMode(player.getPreviousGameMode()) : null;
        @Nullable WorldBlockPosition position = player.getLastDeathLocation() != null ? ConversionUtil.toWorldBlockPosition(player.getLastDeathLocation()) : null;

        WrapperPlayServerRespawn respawnPacket = new WrapperPlayServerRespawn(
                dimensionType, worldName, difficulty,
                hashedSeed, gameMode, prevGameMode,
                false, false, WrapperPlayServerRespawn.KEEP_ALL_DATA,
                position, null);

        WrapperPlayServerChangeGameState startLoadingChunksPacket = new WrapperPlayServerChangeGameState(
                Reason.START_LOADING_CHUNKS,
                0.0f);

        user.sendPacket(respawnPacket);
        user.sendPacket(startLoadingChunksPacket);

        player.teleportAsync(player.getLocation());
    }
}
