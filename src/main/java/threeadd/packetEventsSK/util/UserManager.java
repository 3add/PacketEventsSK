package threeadd.packetEventsSK.util;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.User;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

// for some reason packetevents does not do this by default
public class UserManager implements Listener {

    private static final Map<UUID, User> connectedUsers = new ConcurrentHashMap<>();

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        connectedUsers.put(event.getPlayer().getUniqueId(),
                PacketEvents.getAPI().getPlayerManager().getUser(event.getPlayer()));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        connectedUsers.remove(event.getPlayer().getUniqueId());
    }

    public static User getUser(UUID uuid) {
        return connectedUsers.get(uuid);
    }
}
