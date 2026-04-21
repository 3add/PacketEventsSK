package dev.threeadd.packeteventssk.element.team;

import org.skriptlang.skript.addon.SkriptAddon;
import org.skriptlang.skript.registration.SyntaxRegistry;
import dev.threeadd.packeteventssk.util.registry.element.ElementCollection;

public class TeamElements implements ElementCollection {
    @Override
    public String identifier() {
        return "team";
    }

    @Override
    public String name() {
        return identifier();
    }

    @Override
    public void load(SkriptAddon addon) {
        SyntaxRegistry registry = addon.syntaxRegistry();
        TeamModule.registerAll(registry);
    }
}
