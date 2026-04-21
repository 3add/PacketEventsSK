package dev.threeadd.packeteventssk.util.registry;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import dev.threeadd.packeteventssk.PacketEventsSK;
import dev.threeadd.packeteventssk.element.entity.api.skin.Skin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerSkinRegistry implements Listener {

    private static final Map<UUID, Skin> skins = new HashMap<>();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        cacheSkin(player);

        if (!skins.containsKey(player.getUniqueId())) {
            Bukkit.getScheduler().runTaskLater(PacketEventsSK.getInstance(), () -> cacheSkin(player), 1L);
        }
    }

    private void cacheSkin(Player player) {
        User user = PacketEvents.getAPI().getPlayerManager().getUser(player);
        if (user == null || user.getProfile() == null || user.getProfile().getTextureProperties() == null || user.getProfile().getTextureProperties().isEmpty()) {
            return;
        }

        skins.put(player.getUniqueId(), new Skin(user.getProfile().getTextureProperties()));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        skins.remove(event.getPlayer().getUniqueId());
    }

    public static Skin getSkin(UUID ownerId) {
        Skin skin = skins.get(ownerId);
        if (skin == null) throw new IllegalStateException("No Skin found for " + ownerId);

        return skin;
    }
}
