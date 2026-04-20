package threeadd.packetEventsSK.element.simple;

import org.skriptlang.skript.addon.SkriptAddon;
import org.skriptlang.skript.registration.SyntaxRegistry;
import threeadd.packetEventsSK.util.registry.element.ElementCollection;

public class GeneralElements implements ElementCollection {

    @Override
    public String identifier() {
        return "general";
    }

    @Override
    public String name() {
        return identifier();
    }

    @Override
    public void load(SkriptAddon addon) {
        SyntaxRegistry registry = addon.syntaxRegistry();
        GeneralModule.registerAll(registry);
    }
}
