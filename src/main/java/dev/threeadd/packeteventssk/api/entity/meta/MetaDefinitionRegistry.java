package dev.threeadd.packeteventssk.api.entity.meta;

import dev.threeadd.packeteventssk.api.util.properties.AbstractPropertyRegistry;
import dev.threeadd.packeteventssk.api.util.properties.PropertyDefinition;
import dev.threeadd.packeteventssk.api.util.properties.PropertyField;
import me.tofaa.entitylib.meta.EntityMeta;

public class MetaDefinitionRegistry extends AbstractPropertyRegistry<Class<? extends EntityMeta>, EntityMeta> {
    public static final MetaDefinitionRegistry INSTANCE = new MetaDefinitionRegistry();

    public <MetaClass extends EntityMeta> Builder<MetaClass> builder(Class<MetaClass> metaClass) {
        return INSTANCE.createBuilder(metaClass);
    }

    @Override
    protected <T extends EntityMeta> Builder<T> createBuilder(Class<? extends EntityMeta> key) {
        return new MetaBuilder<>(key);
    }

    public class MetaBuilder<T extends EntityMeta> extends Builder<T> {
        public MetaBuilder(Class<? extends EntityMeta> type) {
            super(type);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void build() {
            Class<?> current = type.getSuperclass();
            while (current != null && EntityMeta.class.isAssignableFrom(current)) {
                PropertyDefinition<Class<? extends EntityMeta>, EntityMeta> parentDef = INSTANCE.getDefinition((Class<? extends EntityMeta>) current);
                if (parentDef != null) {
                    for (PropertyField<EntityMeta, ?> parentField : parentDef.fields()) {
                        boolean duplicate = false;
                        for (PropertyField<EntityMeta, ?> localField : fields) {
                            if (localField.name().equalsIgnoreCase(parentField.name())) {
                                duplicate = true;
                                break;
                            }
                        }

                        if (!duplicate) {
                            fields.add(parentField);
                        }
                    }
                }
                current = current.getSuperclass();
            }
            super.build();
        }
    }
}