package threeadd.packetEventsSK.element.entity;

import org.skriptlang.skript.registration.SyntaxRegistry;
import threeadd.packetEventsSK.element.entity.effect.EffKill;
import threeadd.packetEventsSK.element.entity.effect.EffRide;
import threeadd.packetEventsSK.element.entity.effect.EffSpawn;
import threeadd.packetEventsSK.element.entity.effect.EffTeleport;
import threeadd.packetEventsSK.element.entity.expressions.ExprFakeEntityWithId;
import threeadd.packetEventsSK.element.entity.expressions.ExprFakeEntityWithUuid;
import threeadd.packetEventsSK.element.entity.expressions.ExprLastFakeEntity;
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
import threeadd.packetEventsSK.element.entity.sections.CreateFakeEntitySec;

public class EntityModule {

    public static void registerAll(SyntaxRegistry registry) {

        CreateFakeEntitySec.register(registry);

        ExprLastFakeEntity.register(registry);
        ExprFakeEntityWithId.register(registry);
        ExprFakeEntityWithUuid.register(registry);

        FakeEntityViewersProp.register(registry);
        IdProp.register(registry);
        LocationProp.register(registry);
        UuidProp.register(registry);

        InteractionHeightProp.register(registry);
        ItemProp.register(registry);
        BlockProp.register(registry);

        EffKill.register(registry);
        EffRide.register(registry);
        EffSpawn.register(registry);
        EffTeleport.register(registry);

        //AirTicksProp.register(registry);
        AttributeProp.register(registry);
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

        Types.register();
    }

}