package threeadd.packetEventsSK;

// Highly "inspired" by https://github.com/ShaneBeee/SkBee/blob/838805d592a090b60b9ce25ae1497380747fbb81/src/main/java/com/shanebeestudios/skbee/AddonLoader.java

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.config.Config;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import threeadd.packetEventsSK.util.registry.element.ElementCollection;
import threeadd.packetEventsSK.util.registry.element.ElementRegistry;

import java.io.IOException;

public class AddonLoader {

    private static final Logger log = LoggerFactory.getLogger(AddonLoader.class);

    private final PacketEventsSK plugin;
    private final Plugin skriptPlugin;
    private SkriptAddon skriptAddon;

    public Boolean HAS_SKBEE_COMPONENT = false;

    protected AddonLoader(PacketEventsSK plugin) {
        this.plugin = plugin;
        this.skriptPlugin = Bukkit.getPluginManager().getPlugin("Skript");
    }

    protected boolean canLoad() {
        if (skriptPlugin == null || !skriptPlugin.isEnabled()) {
            log.error("Skript plugin not found or is disabled, Skript elements cannot load,");
            return false;
        }

        this.skriptAddon = Skript.registerAddon(plugin);

        Plugin skBeePlugin = Bukkit.getPluginManager().getPlugin("SkBee");
        if (skBeePlugin != null && skBeePlugin.isEnabled() && skBeePlugin instanceof SkBee skBee) {
            Config skBeeConfig = skBee.getPluginConfig();
            if (skBeeConfig.ELEMENTS_TEXT_COMPONENT) {
                HAS_SKBEE_COMPONENT = true;
                PacketEventsSK.getInstance().getComponentLogger()
                        .info(Component.text("SkBee Text Components loaded")
                                .appendSpace()
                                .append(Component.text("successfully")
                                        .decorate(TextDecoration.UNDERLINED)
                                        .color(TextColor.color(0x89F53B))));
            }
        } else {
            PacketEventsSK.getInstance().getComponentLogger()
                    .info(Component.text("SkBee Text Components loading")
                            .appendSpace()
                            .append(Component.text("failed")
                                    .decorate(TextDecoration.UNDERLINED)
                                    .color(TextColor.color(0xF52A0D))));
        }

        if (!Skript.isAcceptRegistrations()) {
            log.error("Skript is no longer accepting registrations, PacketEventsSK can no longer load");
            return false;
        }

        if (isPlugmanReloaded()) {
            log.error("PacketEventsSK does not support reloading with PlugMan, stuff will break!");
            return false;
        }

        ElementRegistry.INSTANCE.register(PacketEventsSK.getConfiguration());
        ElementRegistry.INSTANCE.getRegisteredItems().forEach(this::loadElement);

        return true;
    }

    private void loadElement(ElementCollection element) {
        log.info("Loaded {} elements", element.identifier());

        try {
            skriptAddon.loadClasses(element.getClass().getPackageName());
        } catch (IOException e) {
            log.error("Something went wrong loading {} ", element, e);
        }
    }

    private boolean isPlugmanReloaded() {
        for (StackTraceElement stackTraceElement : Thread.currentThread().getStackTrace()) {
            if (stackTraceElement.toString().contains("rylinaux.plugman.command."))
                return true;
        }
        return false;
    }

    public SkriptAddon getAddon() {
        return skriptAddon;
    }
}
