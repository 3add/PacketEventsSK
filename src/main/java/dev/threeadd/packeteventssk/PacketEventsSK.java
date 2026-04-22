package dev.threeadd.packeteventssk;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import dev.threeadd.packeteventssk.element.simple.api.block.FakeBlockListener;
import me.tofaa.entitylib.APIConfig;
import me.tofaa.entitylib.EntityLib;
import me.tofaa.entitylib.spigot.SpigotEntityLibPlatform;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import dev.threeadd.packeteventssk.config.Config;
import dev.threeadd.packeteventssk.config.Configurable;
import dev.threeadd.packeteventssk.element.general.api.PacketEventListener;
import dev.threeadd.packeteventssk.element.simple.api.skin.ChatSessionListener;
import dev.threeadd.packeteventssk.element.simple.api.glow.GlowingEntityListener;
import dev.threeadd.packeteventssk.element.simple.api.skin.PlayerSkinListener;
import dev.threeadd.packeteventssk.util.UserManager;
import dev.threeadd.packeteventssk.util.registry.PlayerSkinRegistry;

@SuppressWarnings("unused")
public final class PacketEventsSK extends JavaPlugin {

    private static final Logger log = LoggerFactory.getLogger(PacketEventsSK.class);
    private static PacketEventsSK instance;
    private static Config config;
    private static AddonLoader loader;

    private long start;

    @Override
    public void onLoad() {
        start = System.currentTimeMillis();
        log.info("Starting PacketEventsSK");

        PacketEventsSK.instance = this;
        PacketEventsSK.config = new Config(this);

        // PE listener
        PacketEvents.getAPI().getEventManager().registerListener(new PacketEventListener(), PacketListenerPriority.NORMAL);

        if (getConfiguration().getConfigValue(Configurable.ELEMENTS_SIMPLE)) {
            PacketEvents.getAPI().getEventManager().registerListener(new FakeBlockListener(), PacketListenerPriority.NORMAL);
            PacketEvents.getAPI().getEventManager().registerListener(new GlowingEntityListener(), PacketListenerPriority.NORMAL);
            PacketEvents.getAPI().getEventManager().registerListener(new ChatSessionListener(), PacketListenerPriority.NORMAL);
            PacketEvents.getAPI().getEventManager().registerListener(new PlayerSkinListener(), PacketListenerPriority.NORMAL);
        }

        PacketEvents.getAPI().load();

        SpigotEntityLibPlatform platform = new SpigotEntityLibPlatform(this);
        APIConfig config = new APIConfig(PacketEvents.getAPI())
                .tickTickables()
                .usePlatformLogger();

        EntityLib.init(platform, config);
    }

    @Override
    public void onEnable() {
        PacketEventsSK.loader = new AddonLoader();
        if (!PacketEventsSK.loader.canLoad()) return;

        MetricsLoader.loadMetrics(this);

        // Exclusive to online-mode servers
        if (Bukkit.getServerConfig().isProxyOnlineMode())
            getServer().getPluginManager().registerEvents(new PlayerSkinRegistry(), this);

        getServer().getPluginManager().registerEvents(new UserManager(), this);

        float secondsPassed = (float) (System.currentTimeMillis() - start) / 1000;
        log.info("Finished startup of PacketEventsSK v{} in {}s", getPluginMeta().getVersion(), secondsPassed);
    }

    @Override
    public void onDisable() {
        log.info("Disabling PacketEventsSK");
    }

    public static PacketEventsSK getInstance() {
        return instance;
    }

    public static Config getConfiguration() {
        return config;
    }

    public static AddonLoader getLoader() {
        return loader;
    }
}
