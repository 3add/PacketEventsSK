package dev.threeadd.packeteventssk.element.entity.expression.prop;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.shanebeee.skr.Registration;
import dev.threeadd.packeteventssk.api.util.field.FieldAccessor;
import dev.threeadd.packeteventssk.api.util.field.FieldSchema;
import dev.threeadd.packeteventssk.element.entity.field.FakeEntityFieldRegistry;
import me.tofaa.entitylib.wrapper.WrapperEntity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
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

        reg.newPropertyExpression(ExprFakeEntityField.class, Object.class, "[entity] (field|fake) <[a-zA-Z0-9_ ]+>", "fakeentity")
                .name("Fake Entity Property Field")
                .description(description.toString())
                // TODO example
                .since("1.1.2")
                .register();
    }

    private String fieldName;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.fieldName = parseResult.regexes.getFirst().group().trim();

        boolean isValidField = false;
        for (FieldSchema<EntityType, WrapperEntity> def : FakeEntityFieldRegistry.INSTANCE.getAllSchemas()) {
            if (def.getAccessor(this.fieldName) != null) {
                isValidField = true;
                break;
            }
        }

        if (!isValidField) {
            Skript.error("The fake entity field '" + this.fieldName + "' is not registered or does not exist. Consider checking your spelling.");
            return false;
        }

        setExpr((Expression<? extends WrapperEntity>) exprs[0]);
        return true;
    }

    @Nullable
    @Override
    protected Object[] get(Event event, WrapperEntity[] source) {
        if (this.fieldName == null) return null;

        List<Object> results = new ArrayList<>();

        for (WrapperEntity entity : source) {
            if (entity == null) continue;

            FieldAccessor<WrapperEntity, ?> targetField = FakeEntityFieldRegistry.INSTANCE.getAccessor(entity.getClass(), fieldName);
            if (targetField == null || targetField.getter() == null) continue;

            Object value = ((Function<WrapperEntity, ?>) targetField.getter()).apply(entity);
            if (value != null) {
                if (value.getClass().isArray()) {
                    int len = Array.getLength(value);
                    for (int i = 0; i < len; i++) {
                        results.add(Array.get(value, i));
                    }
                } else {
                    results.add(value);
                }
            }
        }
        return results.isEmpty() ? null : results.toArray();
    }

    @Nullable
    @Override
    public Class<?>[] acceptChange(Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET && this.fieldName != null) {
            List<Class<?>> acceptedTypes = new ArrayList<>();

            for (FieldSchema<EntityType, WrapperEntity> def : FakeEntityFieldRegistry.INSTANCE.getAllSchemas()) {
                FieldAccessor<WrapperEntity, ?> field = def.getAccessor(this.fieldName);
                if (field != null) {
                    Class<?> expected = field.expectedType();
                    if (!acceptedTypes.contains(expected)) {
                        acceptedTypes.add(expected);
                    }
                }
            }

            if (!acceptedTypes.isEmpty()) {
                return acceptedTypes.toArray(new Class<?>[0]);
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void change(Event event, Object[] delta, Changer.ChangeMode mode) {
        if (mode != Changer.ChangeMode.SET || delta == null || delta.length == 0 || this.fieldName == null) return;

        Object newValue = delta.length == 1 ? delta[0] : delta;

        for (WrapperEntity entity : getExpr().getArray(event)) {
            if (entity == null) continue;

            FieldAccessor<WrapperEntity, ?> targetField = FakeEntityFieldRegistry.INSTANCE.getAccessor(entity.getClass(), fieldName);
            if (targetField == null || targetField.setter() == null) continue;

            Class<?> expected = targetField.expectedType();
            boolean isCompatible = expected == Object.class || expected.isInstance(newValue);

            if (!isCompatible && expected.isArray() && newValue.getClass().isArray()) {
                isCompatible = true;
            }

            if (!isCompatible) {
                Skript.warning("Cannot set the fake entity field '" + this.fieldName + "' to a value of type " + newValue.getClass().getSimpleName() + ". Expected type: " + expected.getSimpleName());
                continue;
            }

            ((BiConsumer<WrapperEntity, Object>) targetField.setter()).accept(entity, newValue);
        }
    }

    @Override
    public Class<?> getReturnType() {
        return Object.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        String entity = getExpr() != null ? getExpr().toString(event, debug) : "fake entity";
        return "fake entity field " + fieldName + " of " + entity;
    }
}