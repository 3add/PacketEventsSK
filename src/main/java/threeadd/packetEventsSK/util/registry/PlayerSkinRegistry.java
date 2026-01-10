package threeadd.packetEventsSK.util.registry;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.User;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import threeadd.packetEventsSK.element.entity.api.skin.Skin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerSkinRegistry implements Listener {

    private static final Map<UUID, Skin> skins = new HashMap<>();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        User user = PacketEvents.getAPI().getPlayerManager().getUser(event.getPlayer());
        skins.put(user.getUUID(), new Skin(user.getProfile().getTextureProperties()));
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
