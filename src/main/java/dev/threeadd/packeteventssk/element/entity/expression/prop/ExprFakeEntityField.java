package dev.threeadd.packeteventssk.element.entity.expression.prop;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.shanebeee.skr.Registration;
import com.google.common.primitives.Primitives;
import dev.threeadd.packeteventssk.api.util.field.FieldAccessor;
import dev.threeadd.packeteventssk.api.util.field.FieldSchema;
import dev.threeadd.packeteventssk.element.entity.field.entity.FakeEntityFieldRegistry;
import me.tofaa.entitylib.wrapper.WrapperEntity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class ExprFakeEntityField extends PropertyExpression<WrapperEntity, Object> {

    public static void register(Registration reg) {
        StringBuilder description = new StringBuilder();
        description.append("Gets or sets a fake entity property field value from a fake entity instance by its name.\nNote that some entities inherit properties (for example all entities inherit \"entity\" fields\n\n");
        description.append("### Available Fake Entity Fields by Category\n");

        List<FieldSchema<EntityType, WrapperEntity>> schemas = new ArrayList<>(FakeEntityFieldRegistry.INSTANCE.getAllSchemas());
        schemas.sort(Comparator.comparingInt(schema -> schema.accessors().size()));
        for (FieldSchema<EntityType, WrapperEntity> schema : schemas) {

            Set<String> myFields = schema.accessors().stream()
                    .map(FieldAccessor::name)
                    .collect(Collectors.toSet());

            FieldSchema<EntityType, WrapperEntity> parentSchema = null;
            int maxSubsetSize = -1;

            for (FieldSchema<EntityType, WrapperEntity> other : schemas) {
                if (other == schema) continue;
                Set<String> otherFields = other.accessors().stream()
                        .map(FieldAccessor::name)
                        .collect(Collectors.toSet());

                if (myFields.containsAll(otherFields) && myFields.size() > otherFields.size()) {
                    if (otherFields.size() > maxSubsetSize) {
                        maxSubsetSize = otherFields.size();
                        parentSchema = other;
                    }
                }
            }

            Set<String> parentFieldNames = parentSchema != null
                    ? parentSchema.accessors().stream().map(FieldAccessor::name).collect(Collectors.toSet())
                    : Collections.emptySet();

            StringBuilder fieldLines = new StringBuilder();
            for (FieldAccessor<WrapperEntity, ?> field : schema.accessors()) {
                if (!parentFieldNames.contains(field.name())) {
                    fieldLines.append("  - `").append(field.name());
                    if (field.aliases().length > 0) {
                        fieldLines.append(" (").append(String.join(", ", field.aliases())).append(")");
                    }

                    fieldLines.append("`\n");
                }
            }

            if (!fieldLines.isEmpty()) {
                String typeName = schema.type().getName().getKey().toLowerCase(Locale.ENGLISH).replace("_", " ");
                description.append("* **").append(typeName).append("** fields:\n");

                if (parentSchema != null) {
                    String parentName = parentSchema.type().getName().getKey().toLowerCase(Locale.ENGLISH).replace("_", " ");
                    description.append("  - *(Inherits all fields from **").append(parentName).append("**)*\n");
                }

                description.append(fieldLines).append("\n");
            }
        }

        reg.newPropertyExpression(ExprFakeEntityField.class, Object.class, "[fake] fake entity [field] <[a-zA-Z0-9_ ]+>", "fakeentity")
                .name("Fake Entity Property Field")
                .description(description.toString())
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

    private FieldAccessor <WrapperEntity, ?> fieldAccessor;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        String fieldName = parseResult.regexes.getFirst().group().trim();

        boolean isValidField = false;
        for (FieldSchema<EntityType, WrapperEntity> def : FakeEntityFieldRegistry.INSTANCE.getAllSchemas()) {
            this.fieldAccessor = def.getAccessor(fieldName);
            if (this.fieldAccessor != null) {
                isValidField = true;
                break;
            }
        }

        if (!isValidField) {
            Skript.error("The fake entity field '" + fieldName + "' is not registered or does not exist. Consider checking your spelling.");
            return false;
        }

        setExpr((Expression<? extends WrapperEntity>) exprs[0]);
        return true;
    }

    @Nullable
    @Override
    protected Object[] get(Event event, WrapperEntity[] source) {
        if (this.fieldAccessor == null) return null;

        List<Object> elements = new ArrayList<>();

        for (WrapperEntity entity : source) {
            if (entity == null) continue;

            Object value = this.fieldAccessor.getter().apply(entity);
            if (value == null) continue;

            if (value.getClass().isArray()) {
                int len = Array.getLength(value);
                int i = 0;
                while (i < len) {
                    elements.add(Array.get(value, i++));
                }
            } else {
                elements.add(value);
            }
        }

        if (elements.isEmpty()) return null;

        Class<?> returnType = Primitives.wrap(getReturnType()); // wrap primitives to avoid java.lang.ClassCastException on arrays of primitives
        return elements.toArray((Object[]) Array.newInstance(returnType, 0));
    }

    @Nullable
    @Override
    public Class<?>[] acceptChange(Changer.ChangeMode mode) {
        if (this.fieldAccessor.setter() == null) {
            Skript.error("Cannot set " + this.fieldAccessor.name() + " because it is a read-only field.");
            return null;
        }

        if (mode != Changer.ChangeMode.SET) return null;

        Class<?> expected = this.fieldAccessor.expectedType();
        Class<?> typeToAccept = expected.isArray() ? expected.getComponentType() : expected;

        return new Class<?>[]{Primitives.wrap(typeToAccept)};
    }

    @SuppressWarnings("unchecked")
    @Override
    public void change(Event event, Object[] delta, Changer.ChangeMode mode) {
        if (mode != Changer.ChangeMode.SET || delta == null || delta.length == 0 || this.fieldAccessor.setter() == null) return;

        for (WrapperEntity entity : getExpr().getArray(event)) {
            if (entity == null) continue;

            Object newValue;
            Class<?> expected = this.fieldAccessor.expectedType();

            if (expected.isArray()) {
                Class<?> componentType = expected.getComponentType();
                Object typedArray = Array.newInstance(componentType, delta.length);
                for (int i = 0; i < delta.length; i++) {
                    Array.set(typedArray, i, delta[i]);
                }
                newValue = typedArray;
            } else {
                if (delta.length == 1) {
                    newValue = delta[0];
                } else {
                    newValue = delta;
                }
            }

            boolean isCompatible = expected == Object.class || expected.isInstance(newValue);
            if (!isCompatible && expected.isArray() && newValue.getClass().isArray()) {
                isCompatible = true;
            }

            if (!isCompatible) {
                Skript.warning("Cannot set the fake entity field '" + this.fieldAccessor.name() + "' to a value of type " + newValue.getClass().getSimpleName() + ". Expected type: " + expected.getSimpleName());
                continue;
            }

            ((BiConsumer<WrapperEntity, Object>) this.fieldAccessor.setter()).accept(entity, newValue);
        }
    }

    @Override
    public boolean isSingle() {
        return !this.fieldAccessor.expectedType().isArray() && getExpr().isSingle();
    }

    @Override
    public Class<?> getReturnType() {
        Class<?> expected = this.fieldAccessor.expectedType();
        return expected.isArray() ? expected.getComponentType() : expected;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        String entity = getExpr() != null ? getExpr().toString(event, debug) : "fake entity";
        return "fake entity field " + this.fieldAccessor.name() + " of " + entity;
    }
}