package dev.threeadd.packeteventssk.element.entity;

import com.github.shanebeee.skr.Registration;
import dev.threeadd.packeteventssk.element.entity.field.FakeEntityFieldRegistry;
import dev.threeadd.packeteventssk.element.entity.section.SecExprNewFakeEntity;
import dev.threeadd.packeteventssk.api.util.registry.element.SkriptElementRegistration;
import dev.threeadd.packeteventssk.element.entity.effect.EffKillFakeEntity;
import dev.threeadd.packeteventssk.element.entity.effect.EffRideFakeEntity;
import dev.threeadd.packeteventssk.element.entity.effect.EffSpawnFakeEntity;
import dev.threeadd.packeteventssk.element.entity.effect.EffTeleportFakeEntity;
import dev.threeadd.packeteventssk.element.entity.expression.ExprFakeEntitiesAll;
import dev.threeadd.packeteventssk.element.entity.expression.ExprFakeEntityEventValue;
import dev.threeadd.packeteventssk.element.entity.expression.ExprFakeEntityFromId;
import dev.threeadd.packeteventssk.element.entity.expression.ExprFakeEntityFromUuid;
import dev.threeadd.packeteventssk.element.entity.expression.prop.*;
import dev.threeadd.packeteventssk.element.entity.expression.prop.living.ExprFakeLivingEntityAttribute;

public class EntityElementRegistration implements SkriptElementRegistration {

    @Override
    public String identifier() {
        return "entity";
    }

    @Override
    public void load(Registration reg) {

        // property registry (registered before the expr/sec using it)
        FakeEntityFieldRegistry.INSTANCE.registerAll();

        // start effects
        EffKillFakeEntity.register(reg);
        EffRideFakeEntity.register(reg);
        EffSpawnFakeEntity.register(reg);
        EffTeleportFakeEntity.register(reg);
        // end effects

        // start expressions
        ExprFakeLivingEntityAttribute.register(reg);

        ExprFakeEntityField.register(reg);
        ExprVisibleFakeEntities.register(reg);

        ExprFakeEntitiesAll.register(reg);
        ExprFakeEntityEventValue.register(reg);
        ExprFakeEntityFromId.register(reg);
        ExprFakeEntityFromUuid.register(reg);
        // end expressions

        // start sections
        SecExprNewFakeEntity.register(reg);
        // end sections

        // types
        Types.register(reg);
    }
}