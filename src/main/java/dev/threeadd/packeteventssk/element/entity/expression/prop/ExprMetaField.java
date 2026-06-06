package dev.threeadd.packeteventssk.element.entity.expression.prop;

import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.shanebeee.skr.Registration;
import dev.threeadd.packeteventssk.api.field.BaseFieldRegistry;
import dev.threeadd.packeteventssk.api.field.FieldAccessor;
import dev.threeadd.packeteventssk.api.field.FieldSchema;
import dev.threeadd.packeteventssk.api.field.doc.FieldDescriptionBuilder;
import dev.threeadd.packeteventssk.api.field.skript.AbstractExprField;
import dev.threeadd.packeteventssk.element.entity.field.meta.MetaFieldRegistry;
import me.tofaa.entitylib.meta.EntityMeta;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ExprMetaField extends AbstractExprField<EntityType, EntityMeta> {

    public static void register(Registration reg) {
        List<FieldSchema<EntityType, EntityMeta>> schemas = new ArrayList<>(MetaFieldRegistry.INSTANCE.getAllSchemas());
        schemas.sort(java.util.Comparator.comparingInt(s -> s.accessors().size()));

        String description = "Gets or sets a metadata property field from an entity meta instance.\n"
                + "Note that some entities inherit properties (e.g. all entities inherit \"entity\" fields).\n\n"
                + "### Available Meta Fields by Category\n"
                + FieldDescriptionBuilder.buildHierarchical(
                schemas,
                schema -> "fake " + schema.type().getName().getKey()
                        .toLowerCase(Locale.ENGLISH).replace("_", " ") + " entity",
                false);

        reg.newPropertyExpression(ExprMetaField.class, Object.class,
                        "[fake] [entity] meta [field] <[a-zA-Z0-9_ ]+>", "entitymeta")
                .name("Entity Meta Property Field")
                .description(description)
                .examples("""
                        on clientbound entity metadata:
                            set {_meta} to packet meta of event-packet
                            set meta glowing state of {_meta} to true
                            set packet meta of event-packet to {_meta}
                        """)
                .since("1.1.2")
                .register();
    }

    /**
     * Uses {@link MetaFieldRegistry#getAccessor(Class, String)} to walk the Java class
     * hierarchy of the live {@link EntityMeta} instance, correctly resolving overridden
     * fields in subtype schemas.
     */
    @Override
    @Nullable
    protected FieldAccessor<EntityMeta, ?> resolveAccessor(EntityMeta instance) {
        return MetaFieldRegistry.INSTANCE.getAccessor(instance.getClass(), this.fieldName);
    }

    @Override
    protected BaseFieldRegistry<EntityType, EntityMeta> getRegistry() {
        return MetaFieldRegistry.INSTANCE;
    }

    @Override
    protected String categoryLabel() {
        return "meta";
    }
}