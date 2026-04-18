package threeadd.packetEventsSK.util;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import threeadd.packetEventsSK.PacketEventsSK;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

// for some reason packetevents does not do this by default
public class UserManager implements Listener {

    private static final Map<UUID, User> connectedUsers = new ConcurrentHashMap<>();

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        cacheUser(player);

        // PacketEvents user can be unavailable in the same tick as PlayerJoinEvent.
        if (!connectedUsers.containsKey(player.getUniqueId())) {
            Bukkit.getScheduler().runTaskLater(PacketEventsSK.getInstance(), () -> cacheUser(player), 1L);
        }
    }

    private void cacheUser(Player player) {
        User user = PacketEvents.getAPI().getPlayerManager().getUser(player);
        if (user != null) {
            connectedUsers.put(player.getUniqueId(), user);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        connectedUsers.remove(event.getPlayer().getUniqueId());
    }

    public static User getUser(UUID uuid) {
        return connectedUsers.get(uuid);
    }
}
