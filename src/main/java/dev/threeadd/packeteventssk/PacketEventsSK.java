package dev.threeadd.packeteventssk;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import dev.threeadd.packeteventssk.api.general.EntityTracker;
import dev.threeadd.packeteventssk.api.general.PlayerSkinRegistry;
import dev.threeadd.packeteventssk.api.general.UserManager;
import dev.threeadd.packeteventssk.api.simple.ChatSessionListener;
import dev.threeadd.packeteventssk.api.simple.GlowingEntityListener;
import dev.threeadd.packeteventssk.api.simple.PlayerSkinListener;
import dev.threeadd.packeteventssk.api.util.LogUtil;
import dev.threeadd.packeteventssk.config.Config;
import dev.threeadd.packeteventssk.config.Configurable;
import me.tofaa.entitylib.APIConfig;
import me.tofaa.entitylib.EntityLib;
import me.tofaa.entitylib.spigot.SpigotEntityLibPlatform;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class PacketEventsSK extends JavaPlugin {

    private static PacketEventsSK instance;
    private Config config;
    private AddonLoader loader;

    @Override
    public void onLoad() {
        long start = System.nanoTime();
        LogUtil.info("Loading PacketEventsSK");

        instance = this;
        this.config = new Config(this);

        // PE listener
        PacketEvents.getAPI().getEventManager().registerListener(new EntityTracker(), PacketListenerPriority.MONITOR);

        if (getPluginConfig().getConfigValue(Configurable.ELEMENTS_SIMPLE)) {
            PacketEvents.getAPI().getEventManager().registerListener(new GlowingEntityListener(), PacketListenerPriority.NORMAL);
            PacketEvents.getAPI().getEventManager().registerListener(new ChatSessionListener(), PacketListenerPriority.MONITOR);
            PacketEvents.getAPI().getEventManager().registerListener(new PlayerSkinListener(), PacketListenerPriority.NORMAL);
        }

        PacketEvents.getAPI().load();

        SpigotEntityLibPlatform platform = new SpigotEntityLibPlatform(this);
        APIConfig config = new APIConfig(PacketEvents.getAPI())
                .tickTickables()
                .usePlatformLogger();

        EntityLib.init(platform, config);

        long end = System.nanoTime();
        LogUtil.info("Finished loading PacketEventsSK v%s in %sms", getPluginMeta().getVersion(), (end - start) / 1_000_000F);
    }

    @Override
    public void onEnable() {
        long start = System.nanoTime();
        LogUtil.info("Starting PacketEventsSK");

        this.loader = new AddonLoader();
        if (!this.loader.canLoad()) return;

        MetricsLoader.loadMetrics(this);

        // Exclusive to online-mode servers
        if (Bukkit.getServerConfig().isProxyOnlineMode()) {
            getServer().getPluginManager().registerEvents(new PlayerSkinRegistry(), this);
        }

        getServer().getPluginManager().registerEvents(new UserManager(), this);

        long end = System.nanoTime();
        LogUtil.info("Finished loading PacketEventsSK v%s in %sms", getPluginMeta().getVersion(), (end - start) / 1_000_000F);
    }

    @Override
    public void onDisable() {
        LogUtil.info("Disabling PacketEventsSK");
    }

    public static PacketEventsSK getInstance() {
        return instance;
    }

    public Config getPluginConfig() {
        return this.config;
    }

    public AddonLoader getLoader() {
        return this.loader;
    }
}
