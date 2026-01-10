package threeadd.packetEventsSK;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import me.tofaa.entitylib.APIConfig;
import me.tofaa.entitylib.EntityLib;
import me.tofaa.entitylib.spigot.SpigotEntityLibPlatform;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import threeadd.packetEventsSK.config.Config;
import threeadd.packetEventsSK.config.Configurable;
import threeadd.packetEventsSK.element.simple.api.ChatSessionListener;
import threeadd.packetEventsSK.element.simple.api.PlayerSkinListener;
import threeadd.packetEventsSK.util.UserManager;
import threeadd.packetEventsSK.util.registry.PlayerSkinRegistry;
import threeadd.packetEventsSK.element.simple.api.GlowingEntityListener;
import threeadd.packetEventsSK.element.general.api.PacketEventListener;

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
        PacketEventsSK.loader = new AddonLoader(this);
        if (!PacketEventsSK.loader.canLoad()) return;

        MetricsLoader.loadMetrics(this);

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
