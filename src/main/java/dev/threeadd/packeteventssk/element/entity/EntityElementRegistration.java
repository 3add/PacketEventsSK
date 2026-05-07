package dev.threeadd.packeteventssk.element.entity;

import com.github.shanebeee.skr.Registration;
import dev.threeadd.packeteventssk.element.entity.effect.EffKillFakeEntity;
import dev.threeadd.packeteventssk.element.entity.effect.EffRideFakeEntity;
import dev.threeadd.packeteventssk.element.entity.effect.EffSpawnFakeEntity;
import dev.threeadd.packeteventssk.element.entity.effect.EffTeleportFakeEntity;
import dev.threeadd.packeteventssk.element.entity.expressions.ExprFakeEntitiesAll;
import dev.threeadd.packeteventssk.element.entity.expressions.ExprFakeEntityEventValue;
import dev.threeadd.packeteventssk.element.entity.expressions.ExprFakeEntityFromId;
import dev.threeadd.packeteventssk.element.entity.expressions.ExprFakeEntityFromUuid;
import dev.threeadd.packeteventssk.element.entity.expressions.prop.*;
import dev.threeadd.packeteventssk.element.entity.expressions.prop.meta.base.*;
import dev.threeadd.packeteventssk.element.entity.expressions.prop.meta.display.*;
import dev.threeadd.packeteventssk.element.entity.expressions.prop.meta.display.block.ExprFakeBlockDisplayMetaBlock;
import dev.threeadd.packeteventssk.element.entity.expressions.prop.meta.display.item.ExprFakeItemDisplayMetaItem;
import dev.threeadd.packeteventssk.element.entity.expressions.prop.meta.display.text.ExprFakeTextDisplayMetaBackGroundColor;
import dev.threeadd.packeteventssk.element.entity.expressions.prop.meta.display.text.ExprFakeTextDisplayMetaText;
import dev.threeadd.packeteventssk.element.entity.expressions.prop.meta.display.text.ExprFakeTextDisplayMetaTextShadowed;
import dev.threeadd.packeteventssk.element.entity.expressions.prop.meta.interaction.ExprFakeInterationMetaHeightOrWidth;
import dev.threeadd.packeteventssk.element.entity.expressions.prop.living.ExprFakeLivingEntityAttribute;
import dev.threeadd.packeteventssk.element.entity.expressions.prop.meta.item.ExprFakeItemMetaItem;
import dev.threeadd.packeteventssk.element.entity.expressions.prop.player.ExprFakePlayerEntitySkin;
import dev.threeadd.packeteventssk.element.entity.sections.EffSecCreateFakeEntity;
import dev.threeadd.packeteventssk.api.util.registry.element.SkriptElementRegistration;

public class EntityElementRegistration implements SkriptElementRegistration {

    @Override
    public String identifier() {
        return "entity";
    }

    @Override
    public void load(Registration reg) {

        // start effects
        EffKillFakeEntity.register(reg);
        EffRideFakeEntity.register(reg);
        EffSpawnFakeEntity.register(reg);
        EffTeleportFakeEntity.register(reg);
        // end effects

        // start expressions
        ExprFakeLivingEntityAttribute.register(reg);

        ExprFakeMetaAirTime.register(reg);
        ExprFakeMetaCustomName.register(reg);
        ExprFakeMetaCustomNameVisible.register(reg);
        ExprFakeMetaElytra.register(reg);
        ExprFakeMetaFire.register(reg);
        ExprFakeMetaFrozenTime.register(reg);
        ExprFakeMetaGlowing.register(reg);
        ExprFakeMetaGravity.register(reg);
        ExprFakeMetaInvisible.register(reg);
        ExprFakeMetaPose.register(reg);
        ExprFakeMetaSilent.register(reg);
        ExprFakeMetaSneaking.register(reg);
        ExprFakeMetaSprinting.register(reg);
        ExprFakeMetaSwimming.register(reg);

        ExprFakeBlockDisplayMetaBlock.register(reg);

        ExprFakeItemDisplayMetaItem.register(reg);

        ExprFakeTextDisplayMetaBackGroundColor.register(reg);
        ExprFakeTextDisplayMetaText.register(reg);
        ExprFakeTextDisplayMetaTextShadowed.register(reg);

        ExprFakeDisplayMetaBillboard.register(reg);
        ExprFakeDisplayMetaHeightOrWidth.register(reg);
        ExprFakeDisplayMetaInterpolationDelay.register(reg);
        ExprFakeDisplayMetaLeftOrRightRotation.register(reg);
        ExprFakeDisplayMetaScale.register(reg);
        ExprFakeDisplayMetaTeleportInterpolationDuration.register(reg);
        ExprFakeDisplayMetaTransformationInterpolationDuration.register(reg);
        ExprFakeDisplayMetaTranslation.register(reg);
        ExprFakeDisplayMetaViewRange.register(reg);

        ExprFakeInterationMetaHeightOrWidth.register(reg);

        ExprFakeItemMetaItem.register(reg);

        ExprFakePlayerEntitySkin.register(reg);

        ExprFakeEntityId.register(reg);
        ExprFakeEntityLocation.register(reg);
        ExprFakeEntityMeta.register(reg);
        ExprFakeEntityType.register(reg);
        ExprFakeEntityUuid.register(reg);
        ExprFakeEntityViewers.register(reg);
        ExprVisibleFakeEntities.register(reg);

        ExprFakeEntitiesAll.register(reg);
        ExprFakeEntityEventValue.register(reg);
        ExprFakeEntityFromId.register(reg);
        ExprFakeEntityFromUuid.register(reg);
        // end expressions

        // start sections
        EffSecCreateFakeEntity.register(reg);
        // end sections

        // types
        Types.register(reg);
    }
}