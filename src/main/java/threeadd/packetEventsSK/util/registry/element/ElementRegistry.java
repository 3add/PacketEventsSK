package threeadd.packetEventsSK.util.registry.element;

import threeadd.packetEventsSK.config.Config;
import threeadd.packetEventsSK.config.Configurable;
import threeadd.packetEventsSK.element.team.TeamElements;
import threeadd.packetEventsSK.util.registry.Registry;
import threeadd.packetEventsSK.element.entity.EntityElements;
import threeadd.packetEventsSK.element.general.GeneralElements;

public class ElementRegistry extends Registry<ElementCollection> {

    public final static ElementRegistry INSTANCE = new ElementRegistry();

    private ElementRegistry() {
    }

    public void register(Config config) {

        // Always registered
        register(new GeneralElements());

        if (config.getConfigValue(Configurable.ELEMENTS_SIMPLE)) {
            register(new threeadd.packetEventsSK.element.simple.GeneralElements());
        }

        if (config.getConfigValue(Configurable.ELEMENTS_ENTITY)) {
            register(new EntityElements());
        }

        if (config.getConfigValue(Configurable.ELEMENTS_TEAM)) {
            register(new TeamElements());
        }
    }
}
