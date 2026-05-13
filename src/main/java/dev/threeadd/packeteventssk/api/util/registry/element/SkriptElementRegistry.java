package dev.threeadd.packeteventssk.api.util.registry.element;

import dev.threeadd.packeteventssk.api.util.registry.Registry;
import dev.threeadd.packeteventssk.config.Config;
import dev.threeadd.packeteventssk.config.Configurable;
import dev.threeadd.packeteventssk.element.entity.EntityElementRegistration;
import dev.threeadd.packeteventssk.element.general.GeneralElementRegistration;
import dev.threeadd.packeteventssk.element.simple.SimpleElementRegistration;

public class SkriptElementRegistry extends Registry<SkriptElementRegistration> {

    public final static SkriptElementRegistry INSTANCE = new SkriptElementRegistry();

    private SkriptElementRegistry() {
    }

    public void register(Config config) {

        // Always registered
        register(new GeneralElementRegistration());

        if (config.getConfigValue(Configurable.ELEMENTS_SIMPLE)) {
            register(new SimpleElementRegistration());
        }

        if (config.getConfigValue(Configurable.ELEMENTS_ENTITY)) {
            register(new EntityElementRegistration());
        }
    }
}
