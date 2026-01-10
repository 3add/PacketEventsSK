package threeadd.packetEventsSK.element.entity.api.skin;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.TextureProperty;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.util.MojangAPIUtil;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

public class Skins {

    private static final Cache<String, Skin> skinCache = CacheBuilder.newBuilder()
            .expireAfterAccess(Duration.ofMinutes(5))
            .build();

    public static Skin getPlayer(final Player player) {
        User user = PacketEvents.getAPI().getPlayerManager().getUser(player);
        List<TextureProperty> properties = user.getProfile().getTextureProperties();

        return new Skin(properties);
    }

    public static Skin getOfflinePlayer(final String playerName) {

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getName().equalsIgnoreCase(playerName)) {
                return getPlayer(player);
            }
        }

        Skin skin = skinCache.getIfPresent(playerName);
        if (skin != null) return skin;

        UUID uuid =  MojangAPIUtil.requestPlayerUUID(playerName);
        List<TextureProperty> properties = MojangAPIUtil.requestPlayerTextureProperties(uuid);

        skin = new Skin(properties);
        skinCache.put(playerName, skin);

        return skin;
    }
}