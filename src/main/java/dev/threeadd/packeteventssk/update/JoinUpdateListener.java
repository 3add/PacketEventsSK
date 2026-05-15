package dev.threeadd.packeteventssk.update;

import dev.threeadd.packeteventssk.PacketEventsSK;
import dev.threeadd.packeteventssk.api.util.LogUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

// Highly inspired by SkBee's implementation https://github.com/ShaneBeee/SkBee/tree/master/src/main/java/com/shanebeestudios/skbee/api/util/update
public class JoinUpdateListener implements Listener {

    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPermission("packeteventssk.update.check")) return;

        Bukkit.getScheduler().runTaskLater(PacketEventsSK.getInstance(), () -> {
            UpdateChecker.getUpdateVersion(true).thenAccept(version -> {
                LogUtil.sendRichMessage(player, "Update available: <green>%s", version.getUpdateVersion());
                LogUtil.sendRichMessage(player, "Download at: <green>%s", version.getUpdateLink());
            });
        }, 30L);
    }
}
