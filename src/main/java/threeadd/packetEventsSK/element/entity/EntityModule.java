package threeadd.packetEventsSK.element.entity;

import org.bukkit.block.Block;
import org.bukkit.block.data.type.Fire;
import org.skriptlang.skript.registration.SyntaxRegistry;
import threeadd.packetEventsSK.element.entity.effect.EffKill;
import threeadd.packetEventsSK.element.entity.effect.EffSpawn;
import threeadd.packetEventsSK.element.entity.effect.EffTeleport;
import threeadd.packetEventsSK.element.entity.expressions.FakeEntityWithIdExpr;
import threeadd.packetEventsSK.element.entity.expressions.FakeEntityWithUuidExpr;
import threeadd.packetEventsSK.element.entity.expressions.prop.FakeEntityViewersProp;
import threeadd.packetEventsSK.element.entity.expressions.prop.IdProp;
import threeadd.packetEventsSK.element.entity.expressions.prop.LocationProp;
import threeadd.packetEventsSK.element.entity.expressions.prop.UuidProp;
import threeadd.packetEventsSK.element.entity.expressions.prop.base.*;
import threeadd.packetEventsSK.element.entity.expressions.prop.display.*;
import threeadd.packetEventsSK.element.entity.expressions.prop.display.block.BlockProp;
import threeadd.packetEventsSK.element.entity.expressions.prop.display.item.ItemProp;
import threeadd.packetEventsSK.element.entity.expressions.prop.display.text.BackgroundColorProp;
import threeadd.packetEventsSK.element.entity.expressions.prop.display.text.TextProp;
import threeadd.packetEventsSK.element.entity.expressions.prop.display.text.TextShadowedProp;
import threeadd.packetEventsSK.element.entity.expressions.prop.interaction.InteractionHeightProp;
import threeadd.packetEventsSK.element.entity.expressions.prop.player.SkinProp;

public class EntityModule {

    private static final String BASE_PACKAGE = "threeadd.packetEventsSK.element.entity";

    private static final String[] STATIC_REGISTRATION_CLASSES = {
            BASE_PACKAGE + ".Types",
            BASE_PACKAGE + ".sections.CreateFakeEntitySec",
    };

    public static void registerAll(SyntaxRegistry registry) {

        FakeEntityWithIdExpr.register(registry);
        FakeEntityWithUuidExpr.register(registry);

        FakeEntityViewersProp.register(registry);
        IdProp.register(registry);
        LocationProp.register(registry);
        UuidProp.register(registry);

        InteractionHeightProp.register(registry);
        ItemProp.register(registry);
        BlockProp.register(registry);

        EffKill.register(registry);
        EffSpawn.register(registry);
        EffTeleport.register(registry);

        //AirTicksProp.register(registry);
        CustomNameProp.register(registry);
        CustomNameVisibleProp.register(registry);
        ElytraProp.register(registry);
        FireProp.register(registry);
        FrozenTimeProp.register(registry);
        GlowingProp.register(registry);
        GravityProp.register(registry);
        InvisibleProp.register(registry);
        PoseProp.register(registry);
        SilentProp.register(registry);
        SneakingProp.register(registry);
        SprintingProp.register(registry);
        SwimmingProp.register(registry);

        threeadd.packetEventsSK.element.entity.expressions.prop.item.ItemProp.register(registry);
        BackgroundColorProp.register(registry);
        TextProp.register(registry);
        TextShadowedProp.register(registry);
        BillboardProp.register(registry);
        HeightOrHeightProp.register(registry);
        InterpolationDelayProp.register(registry);
        RotationProp.register(registry);
        ScaleProp.register(registry);
        TeleportInterpolationDurationProp.register(registry);
        TransformInterpolationDurationProp.register(registry);
        TranslationProp.register(registry);
        ViewRangeProp.register(registry);

        SkinProp.register(registry);

        for (String className : STATIC_REGISTRATION_CLASSES) {
            forceInitialize(className);
        }
    }

    private static void forceInitialize(String className) {
        try {
            Class.forName(className, true, EntityModule.class.getClassLoader());
        } catch (ClassNotFoundException exception) {
            throw new IllegalStateException("Unable to initialize entity syntax class: " + className, exception);
        }
    }
}