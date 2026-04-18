package threeadd.packetEventsSK.element.simple;

import org.skriptlang.skript.registration.SyntaxRegistry;
import threeadd.packetEventsSK.element.simple.effects.EffDisplayedSkin;
import threeadd.packetEventsSK.element.simple.effects.EffGlow;
import threeadd.packetEventsSK.element.simple.expression.GlowingReceiversExpr;

public class GeneralModule {

    public static void registerAll(SyntaxRegistry registry) {
        EffDisplayedSkin.register(registry);
        EffGlow.register(registry);
        GlowingReceiversExpr.register(registry);
    }
}