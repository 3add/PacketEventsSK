package dev.threeadd.packeteventssk.element.entity;

import dev.threeadd.packeteventssk.element.entity.expressions.prop.base.*;
import dev.threeadd.packeteventssk.element.entity.expressions.prop.display.*;
import org.skriptlang.skript.registration.SyntaxRegistry;
import dev.threeadd.packeteventssk.element.entity.effect.EffKill;
import dev.threeadd.packeteventssk.element.entity.effect.EffSpawn;
import dev.threeadd.packeteventssk.element.entity.effect.EffTeleport;
import dev.threeadd.packeteventssk.element.entity.expressions.ExprFakeEntityWithId;
import dev.threeadd.packeteventssk.element.entity.expressions.ExprFakeEntityWithUuid;
import dev.threeadd.packeteventssk.element.entity.expressions.ExprLastFakeEntity;
import dev.threeadd.packeteventssk.element.entity.expressions.prop.FakeEntityViewersProp;
import dev.threeadd.packeteventssk.element.entity.expressions.prop.IdProp;
import dev.threeadd.packeteventssk.element.entity.expressions.prop.LocationProp;
import dev.threeadd.packeteventssk.element.entity.expressions.prop.UuidProp;
import dev.threeadd.packeteventssk.element.entity.expressions.prop.display.block.BlockProp;
import dev.threeadd.packeteventssk.element.entity.expressions.prop.display.item.ItemProp;
import dev.threeadd.packeteventssk.element.entity.expressions.prop.display.text.BackgroundColorProp;
import dev.threeadd.packeteventssk.element.entity.expressions.prop.display.text.TextProp;
import dev.threeadd.packeteventssk.element.entity.expressions.prop.display.text.TextShadowedProp;
import dev.threeadd.packeteventssk.element.entity.expressions.prop.interaction.InteractionHeightProp;
import dev.threeadd.packeteventssk.element.entity.expressions.prop.player.SkinProp;
import dev.threeadd.packeteventssk.element.entity.sections.CreateFakeEntitySec;

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

        dev.threeadd.packeteventssk.element.entity.expressions.prop.item.ItemProp.register(registry);
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