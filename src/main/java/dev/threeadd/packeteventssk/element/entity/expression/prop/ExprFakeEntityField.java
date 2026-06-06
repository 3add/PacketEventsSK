package dev.threeadd.packeteventssk.element.entity.expression.prop;

import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.shanebeee.skr.Registration;
import dev.threeadd.packeteventssk.api.field.BaseFieldRegistry;
import dev.threeadd.packeteventssk.api.field.FieldAccessor;
import dev.threeadd.packeteventssk.api.field.FieldSchema;
import dev.threeadd.packeteventssk.api.field.doc.FieldDescriptionBuilder;
import dev.threeadd.packeteventssk.api.field.skript.AbstractExprField;
import dev.threeadd.packeteventssk.element.entity.field.entity.FakeEntityFieldRegistry;
import me.tofaa.entitylib.wrapper.WrapperEntity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ExprFakeEntityField extends AbstractExprField<EntityType, WrapperEntity> {

    public static void register(Registration reg) {
        List<FieldSchema<EntityType, WrapperEntity>> schemas =
                new ArrayList<>(FakeEntityFieldRegistry.INSTANCE.getAllSchemas());
        schemas.sort(java.util.Comparator.comparingInt(s -> s.accessors().size()));

        String description = "Gets or sets a fake entity property field by name.\n"
                + "Note that some entity types inherit properties from base types.\n\n"
                + "### Available Fake Entity Fields by Category\n"
                + FieldDescriptionBuilder.buildHierarchical(
                schemas,
                schema -> "fake " + schema.type().getName().getKey()
                        .toLowerCase(Locale.ENGLISH).replace("_", " ") + " entity",
                true);

        reg.newPropertyExpression(ExprFakeEntityField.class, Object.class,
                        "[fake] fake entity [field] <[a-zA-Z0-9_ ]+>", "fakeentity")
                .name("Fake Entity Property Field")
                .description(description)
                .examples("""
                        on load:
                            set {-notchSkin} to skin of player named "notch"

                        command test5:
                            trigger:
                                set {_player} to a new fake player entity:
                                    name: "test"
                                    skin: skin of player
                                    location: location of player
                                    viewers: players

                                wait 1 second
                                set fake entity skin of {_player} to {-notchSkin}
                        """)
                .since("1.1.2")
                .register();
    }

    /**
     * Resolves the accessor via the entity's runtime type, so subtype-specific field
     * overrides (e.g. player-only fields) are found correctly.
     */
    @Override
    @Nullable
    protected FieldAccessor<WrapperEntity, ?> resolveAccessor(WrapperEntity entity) {
        EntityType type = entity.getEntityType();
        if (type == null) return null;
        FieldSchema<EntityType, WrapperEntity> schema = FakeEntityFieldRegistry.INSTANCE.getSchema(type);
        if (schema == null) return null;
        return schema.getAccessor(this.fieldName);
    }

    @Override
    protected BaseFieldRegistry<EntityType, WrapperEntity> getRegistry() {
        return FakeEntityFieldRegistry.INSTANCE;
    }

    @Override
    protected String categoryLabel() {
        return "fake entity";
    }
}