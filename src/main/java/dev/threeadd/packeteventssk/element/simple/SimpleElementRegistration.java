package dev.threeadd.packeteventssk.element.simple;

import com.github.shanebeee.skr.Registration;
import dev.threeadd.packeteventssk.api.util.registry.element.SkriptElementRegistration;
import dev.threeadd.packeteventssk.element.simple.effects.EffDisplayedSkin;
import dev.threeadd.packeteventssk.element.simple.effects.EffGlow;
import dev.threeadd.packeteventssk.element.simple.expression.ExprGlowReceivers;

public class SimpleElementRegistration implements SkriptElementRegistration {

    @Override
    public String identifier() {
        return "simple";
    }

    @Override
    public void load(Registration reg) {

        // start effects
        EffDisplayedSkin.register(reg);
        EffGlow.register(reg);
        // end effects

        // start expressions
        ExprGlowReceivers.register(reg);
        // end expressions
    }
}
