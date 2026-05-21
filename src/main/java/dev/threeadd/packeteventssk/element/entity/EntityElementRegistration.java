package dev.threeadd.packeteventssk.element.entity;

import com.github.shanebeee.skr.Registration;
import dev.threeadd.packeteventssk.api.util.registry.element.SkriptElementRegistration;
import dev.threeadd.packeteventssk.element.entity.effect.EffKillFakeEntity;
import dev.threeadd.packeteventssk.element.entity.effect.EffRideFakeEntity;
import dev.threeadd.packeteventssk.element.entity.effect.EffSpawnFakeEntity;
import dev.threeadd.packeteventssk.element.entity.effect.EffTeleportFakeEntity;
import dev.threeadd.packeteventssk.element.entity.expression.ExprFakeEntitiesAll;
import dev.threeadd.packeteventssk.element.entity.expression.ExprFakeEntityFromId;
import dev.threeadd.packeteventssk.element.entity.expression.ExprFakeEntityFromUuid;
import dev.threeadd.packeteventssk.element.entity.expression.prop.ExprFakeEntityField;
import dev.threeadd.packeteventssk.element.entity.expression.prop.ExprMetaField;
import dev.threeadd.packeteventssk.element.entity.expression.prop.ExprVisibleFakeEntities;
import dev.threeadd.packeteventssk.element.entity.expression.prop.living.ExprFakeLivingEntityAttribute;
import dev.threeadd.packeteventssk.element.entity.field.entity.FakeEntityFieldRegistry;
import dev.threeadd.packeteventssk.element.entity.field.meta.MetaFieldRegistry;
import dev.threeadd.packeteventssk.element.entity.section.SecExprNewFakeEntity;
import dev.threeadd.packeteventssk.element.entity.section.SecExprNewMeta;

public class EntityElementRegistration implements SkriptElementRegistration {

    @Override
    public String identifier() {
        return "entity";
    }

    @Override
    public void load(Registration reg) {

        // property registry (registered before the expr/sec using it)
        FakeEntityFieldRegistry.INSTANCE.registerAll();
        MetaFieldRegistry.INSTANCE.registerAll();

        // start effects
        EffKillFakeEntity.register(reg);
        EffRideFakeEntity.register(reg);
        EffSpawnFakeEntity.register(reg);
        EffTeleportFakeEntity.register(reg);
        // end effects

        // start expressions
        ExprFakeLivingEntityAttribute.register(reg);

        ExprFakeEntityField.register(reg);
        ExprMetaField.register(reg);
        ExprVisibleFakeEntities.register(reg);

        ExprFakeEntitiesAll.register(reg);
        ExprFakeEntityFromId.register(reg);
        ExprFakeEntityFromUuid.register(reg);
        // end expressions

        // start sections
        SecExprNewFakeEntity.register(reg);
        SecExprNewMeta.register(reg);
        // end sections

        // types
        Types.register(reg);
    }
}