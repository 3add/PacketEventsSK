package dev.threeadd.packeteventssk.element.entity;

import org.skriptlang.skript.addon.SkriptAddon;
import org.skriptlang.skript.registration.SyntaxRegistry;
import dev.threeadd.packeteventssk.util.registry.element.ElementCollection;

public class EntityElements implements ElementCollection {

    @Override
    public String identifier() {
        return "entity";
    }

    @Override
    public String name() {
        return identifier();
    }

    @Override
    public void load(SkriptAddon addon) {
        SyntaxRegistry registry = addon.syntaxRegistry();
        EntityModule.registerAll(registry);
    }
}