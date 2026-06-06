package dev.threeadd.packeteventssk.element.entity.field.entity;

import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import dev.threeadd.packeteventssk.api.field.FieldRegistrar;
import dev.threeadd.packeteventssk.api.field.InheritingFieldRegistry;
import me.tofaa.entitylib.wrapper.WrapperEntity;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FakeEntityFieldRegistry extends InheritingFieldRegistry<EntityType, WrapperEntity> {

    public static final FakeEntityFieldRegistry INSTANCE = new FakeEntityFieldRegistry();

    @Override
    public List<FieldRegistrar> getRegistrars() {
        return List.of(
                new FakeBaseEntityFieldRegistrar(),
                new FakePlayerEntityFieldRegistrar()
        );
    }

    @Override
    public EntityType getBaseKey() {
        return EntityTypes.ENTITY;
    }

    @Override
    protected @Nullable EntityType getParentKey(EntityType key) {
        if (key.equals(EntityTypes.ENTITY)) {
            return null;
        }
        return EntityTypes.ENTITY;
    }
}