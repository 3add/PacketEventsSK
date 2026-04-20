package dev.threeadd.packeteventssk;

import ch.njol.skript.Skript;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.skriptlang.skript.addon.SkriptAddon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import dev.threeadd.packeteventssk.util.registry.element.ElementCollection;
import dev.threeadd.packeteventssk.util.registry.element.ElementRegistry;

public class AddonLoader {

    private static final Logger log = LoggerFactory.getLogger(AddonLoader.class);

  private final Plugin skriptPlugin;
    private SkriptAddon skriptAddon;

    protected AddonLoader() {
      this.skriptPlugin = Bukkit.getPluginManager().getPlugin("Skript");
    }

    protected boolean canLoad() {
        if (skriptPlugin == null || !skriptPlugin.isEnabled()) {
            log.error("Skript plugin not found or is disabled, Skript elements cannot load,");
            return false;
        }

        this.skriptAddon = Skript.instance().registerAddon(PacketEventsSK.class, "PacketEventsSK");

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
        try {
            skriptAddon.loadModules(element);
            logElementStatus(element.identifier(), true);
        } catch (Exception e) {
            logElementStatus(element.identifier(), false);
            log.error("Something went wrong loading {}", element.identifier(), e);
        }
    }

    private void logElementStatus(String elementName, boolean success) {
        String statusText = success ? "successfully" : "failed";
        int color = success ? 0x89F53B : 0xF52A0D;

        PacketEventsSK.getInstance().getComponentLogger()
                .info(Component.text(elementName + " loaded")
                        .appendSpace()
                        .append(Component.text(statusText)
                                .decorate(TextDecoration.UNDERLINED)
                                .color(TextColor.color(color))));
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