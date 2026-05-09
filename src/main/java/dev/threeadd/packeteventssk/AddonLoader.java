package dev.threeadd.packeteventssk;

import ch.njol.skript.Skript;
import com.github.shanebeee.skr.Registration;
import dev.threeadd.packeteventssk.api.general.SkBeePacketRegistrations;
import dev.threeadd.packeteventssk.api.util.registry.element.SkriptElementRegistration;
import dev.threeadd.packeteventssk.api.util.registry.element.SkriptElementRegistry;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AddonLoader {

    private static final Logger log = LoggerFactory.getLogger(AddonLoader.class);

    private final Plugin skriptPlugin;
    private final Registration registration;

    private boolean hasSkBeeNBT = false;

    protected AddonLoader() {
        this.skriptPlugin = Bukkit.getPluginManager().getPlugin("Skript");
        this.registration = new Registration("PacketEventsSK", false);
    }

    public Registration getRegistration() {
        return registration;
    }

    protected boolean canLoad() {
        if (skriptPlugin == null || !skriptPlugin.isEnabled()) {
            log.error("Skript plugin not found or is disabled, Skript elements cannot load,");
            return false;
        }

        if (!Skript.isAcceptRegistrations()) {
            log.error("Skript is no longer accepting registrations, PacketEventsSK can no longer load");
            return false;
        }

        if (isPlugmanReloaded()) {
            log.error("PacketEventsSK does not support reloading with PlugMan, stuff will break!");
            return false;
        }

        try {
            Class<?> nbtApiClass = Class.forName("com.shanebeestudios.skbee.api.nbt.NBTApi");
            boolean enabled = (boolean) nbtApiClass.getMethod("isEnabled").invoke(null);
            if (enabled) {
                log.info("Hooked into SkBee NBT using NBT-API");
                this.hasSkBeeNBT = true;
                SkBeePacketRegistrations.register();
            }
        } catch (ClassNotFoundException ignored) {
            // SkBee is not installed; NBT support will be unavailable
        } catch (Exception e) {
            log.warn("Failed to hook into SkBee NBT", e);
        }

        SkriptElementRegistry.INSTANCE.register(PacketEventsSK.getInstance().getPluginConfig());
        SkriptElementRegistry.INSTANCE.getRegisteredItems().forEach(this::loadElement);
        this.registration.finalizeRegistration();

        return true;
    }

    private void loadElement(SkriptElementRegistration element) {
        try {
            element.load(this.registration);
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

    public boolean hasSkBeeNBT() {
        return hasSkBeeNBT;
    }
}