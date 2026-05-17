package dev.threeadd.packeteventssk.element.entity;

import com.github.shanebeee.skr.Registration;
import dev.threeadd.packeteventssk.api.entity.meta.*;
import dev.threeadd.packeteventssk.api.util.registry.element.SkriptElementRegistration;
import dev.threeadd.packeteventssk.element.entity.effect.EffKillFakeEntity;
import dev.threeadd.packeteventssk.element.entity.effect.EffRideFakeEntity;
import dev.threeadd.packeteventssk.element.entity.effect.EffSpawnFakeEntity;
import dev.threeadd.packeteventssk.element.entity.effect.EffTeleportFakeEntity;
import dev.threeadd.packeteventssk.element.entity.expressions.ExprFakeEntitiesAll;
import dev.threeadd.packeteventssk.element.entity.expressions.ExprFakeEntityEventValue;
import dev.threeadd.packeteventssk.element.entity.expressions.ExprFakeEntityFromId;
import dev.threeadd.packeteventssk.element.entity.expressions.ExprFakeEntityFromUuid;
import dev.threeadd.packeteventssk.element.entity.expressions.prop.*;
import dev.threeadd.packeteventssk.element.entity.expressions.prop.living.ExprFakeLivingEntityAttribute;
import dev.threeadd.packeteventssk.element.entity.expressions.prop.player.ExprFakePlayerEntitySkin;
import dev.threeadd.packeteventssk.element.entity.sections.EffSecCreateFakeEntity;

public class EntityElementRegistration implements SkriptElementRegistration {

    @Override
    public String identifier() {
        return "entity";
    }

    @Override
    public void load(Registration reg) {

        // property registry (registered before the expr/sec using it)
        MetaFieldRegistry.INSTANCE.registerAll();

        // start effects
        EffKillFakeEntity.register(reg);
        EffRideFakeEntity.register(reg);
        EffSpawnFakeEntity.register(reg);
        EffTeleportFakeEntity.register(reg);
        // end effects

        // start expressions
        ExprFakeLivingEntityAttribute.register(reg);

        ExprFakePlayerEntitySkin.register(reg);

        ExprFakeEntityId.register(reg);
        ExprFakeEntityLocation.register(reg);
        ExprFakeEntityMeta.register(reg);
        ExprFakeEntityType.register(reg);
        ExprFakeEntityUuid.register(reg);
        ExprFakeEntityViewers.register(reg);
        ExprMetaField.register(reg);
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