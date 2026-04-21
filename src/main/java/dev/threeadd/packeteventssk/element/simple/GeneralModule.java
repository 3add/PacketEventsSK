package dev.threeadd.packeteventssk.element.simple;

import org.skriptlang.skript.registration.SyntaxRegistry;
import dev.threeadd.packeteventssk.element.simple.effects.EffDisplayedSkin;
import dev.threeadd.packeteventssk.element.simple.effects.EffGlow;
import dev.threeadd.packeteventssk.element.simple.expression.GlowingReceiversExpr;

public class GeneralModule {

    public static void registerAll(SyntaxRegistry registry) {
        EffDisplayedSkin.register(registry);
        EffGlow.register(registry);
        GlowingReceiversExpr.register(registry);
    }
}