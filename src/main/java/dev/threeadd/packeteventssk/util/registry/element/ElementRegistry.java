package dev.threeadd.packeteventssk.util.registry.element;

import dev.threeadd.packeteventssk.config.Config;
import dev.threeadd.packeteventssk.config.Configurable;
import dev.threeadd.packeteventssk.element.entity.EntityElements;
import dev.threeadd.packeteventssk.element.general.GeneralElements;
import dev.threeadd.packeteventssk.element.team.TeamElements;
import dev.threeadd.packeteventssk.util.registry.Registry;

public class ElementRegistry extends Registry<ElementCollection> {

    public final static ElementRegistry INSTANCE = new ElementRegistry();

    private ElementRegistry() {
    }

    public void register(Config config) {

        // Always registered
        register(new GeneralElements());

        if (config.getConfigValue(Configurable.ELEMENTS_SIMPLE)) {
            register(new dev.threeadd.packeteventssk.element.simple.GeneralElements());
        }

        if (config.getConfigValue(Configurable.ELEMENTS_ENTITY)) {
            register(new EntityElements());
        }

        if (config.getConfigValue(Configurable.ELEMENTS_TEAM)) {
            register(new TeamElements());
        }
    }
}
