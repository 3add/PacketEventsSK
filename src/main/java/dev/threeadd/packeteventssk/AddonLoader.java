package dev.threeadd.packeteventssk;

import ch.njol.skript.Skript;
import com.github.shanebeee.skr.Registration;
import dev.threeadd.packeteventssk.api.util.LogUtil;
import dev.threeadd.packeteventssk.api.util.registry.element.SkriptElementRegistration;
import dev.threeadd.packeteventssk.api.util.registry.element.SkriptElementRegistry;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class AddonLoader {

    private final Plugin skriptPlugin;
    private final Registration registration;

    protected AddonLoader() {
        this.skriptPlugin = Bukkit.getPluginManager().getPlugin("Skript");
        this.registration = new Registration("PacketEventsSK", true);
    }

    public Registration getRegistration() {
        return this.registration;
    }

    protected boolean canLoad() {
        if (this.skriptPlugin == null || !this.skriptPlugin.isEnabled()) {
            LogUtil.error("Skript plugin not found or is disabled, Skript elements cannot load");
            return false;
        }

        if (!Skript.isAcceptRegistrations()) {
            LogUtil.error("Skript is no longer accepting registrations, PacketEventsSK can no longer load");
            return false;
        }

        if (isPlugmanReloaded()) {
            LogUtil.error("PacketEventsSK does not support reloading with PlugMan, stuff will break!");
            return false;
        }

        SkriptElementRegistry.INSTANCE.register(PacketEventsSK.getInstance().getPluginConfig());
        SkriptElementRegistry.INSTANCE.getRegisteredItems().forEach(this::loadElement);
        this.registration.finalizeRegistration();

        // ELEMENT COUNT
        int typeCount = this.registration.getTypes().size();
        int structureCount = this.registration.getStructures().size();
        int eventCount = this.registration.getEvents().size();
        int sectionCount = this.registration.getSections().size();
        int effectCount = this.registration.getEffects().size();
        int expressionCount = this.registration.getExpressions().size();
        int conditionCount = this.registration.getConditions().size();
        int total = eventCount + effectCount + expressionCount + conditionCount + sectionCount + typeCount + structureCount;

        LogUtil.info("Loaded %s PacketEventsSK elements", total);
        LogUtil.info(" - %s types", typeCount);
        LogUtil.info(" - %s structures", structureCount);
        LogUtil.info(" - %s events", eventCount);
        LogUtil.info(" - %s sections", sectionCount);
        LogUtil.info(" - %s effects", effectCount);
        LogUtil.info(" - %s expressions", expressionCount);
        LogUtil.info(" - %s conditions", conditionCount);

        return true;
    }

    private void loadElement(SkriptElementRegistration element) {
        try {
            element.load(this.registration);
            logElementStatus(element.identifier(), true);
        } catch (Exception e) {
            logElementStatus(element.identifier(), false);
            throw new IllegalStateException("Failed to load element " + element.identifier(), e);
        }
    }

    private void logElementStatus(String elementName, boolean success) {
        String statusText = success ? "successfully" : "failed";
        String color = success ? "89F53B" : "F52A0D";

        LogUtil.mini("loaded %s <#%s><u>%s</u>", elementName, color, statusText);
    }

    private boolean isPlugmanReloaded() {
        for (StackTraceElement stackTraceElement : Thread.currentThread().getStackTrace()) {
            if (stackTraceElement.toString().contains("rylinaux.plugman.command."))
                return true;
        }
        return false;
    }
}