package dev.threeadd.packeteventssk.element.entity.expressions.prop;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.github.shanebeee.skr.Registration;
import dev.threeadd.packeteventssk.api.entity.meta.MetaDefinitionRegistry;
import dev.threeadd.packeteventssk.api.util.properties.PropertyDefinition;
import dev.threeadd.packeteventssk.api.util.properties.PropertyField;
import me.tofaa.entitylib.meta.EntityMeta;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class ExprMetaField extends PropertyExpression<EntityMeta, Object> {

    public static void register(Registration reg) {
        StringBuilder description = new StringBuilder();
        description.append("Gets or sets a metadata property field value from an entity meta instance by its name.\n\n");
        description.append("### Available Meta Types and their fields\n");

        for (PropertyDefinition<Class<? extends EntityMeta>, EntityMeta> def : MetaDefinitionRegistry.INSTANCE.getAllDefinitions()) {
            String fieldsLine = def.getReadableFields();
            if (!fieldsLine.isEmpty()) {
                description.append("* **")
                        .append(def)
                        .append("** allowed fields:\n")
                        .append("  `")
                        .append(fieldsLine)
                        .append("`\n");
            }
        }

        reg.newPropertyExpression(ExprMetaField.class, Object.class, "[entity] [meta] [field] <[a-zA-Z0-9_ ]+>", "entitymetas")
                .name("Entity Meta Property Field")
                .description(description.toString())
                .examples("set air time of fake meta of {_entity} to 10 seconds")
                .since("1.1.2")
                .register();
    }

    private String fieldName;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.fieldName = parseResult.regexes.getFirst().group().trim();

        boolean isValidField = false;
        for (PropertyDefinition<Class<? extends EntityMeta>, EntityMeta> def : MetaDefinitionRegistry.INSTANCE.getAllDefinitions()) {
            if (def.getField(this.fieldName) != null) {
                isValidField = true;
                break;
            }
        }

        if (!isValidField) {
            Skript.error("The meta field '" + this.fieldName + "' is not registered or does not exist. Consider checking your spelling.");
            return false;
        }

        setExpr((Expression<? extends EntityMeta>) exprs[0]);
        return true;
    }

    @SuppressWarnings("unchecked")
    private PropertyField<EntityMeta, ?> getFieldHierarchical(@NotNull Class<? extends EntityMeta> metaClass, String fieldName) {
        Class<?> current = metaClass;
        while (current != null && EntityMeta.class.isAssignableFrom(current)) {
            PropertyDefinition<Class<? extends EntityMeta>, EntityMeta> def = MetaDefinitionRegistry.INSTANCE.getDefinition((Class<? extends EntityMeta>) current);
            if (def != null) {
                PropertyField<EntityMeta, ?> field = def.getField(fieldName);
                if (field != null) return field;
            }
            current = current.getSuperclass();
        }

        for (PropertyDefinition<Class<? extends EntityMeta>, EntityMeta> def : MetaDefinitionRegistry.INSTANCE.getAllDefinitions()) {
            if (def.type().isAssignableFrom(metaClass)) {
                PropertyField<EntityMeta, ?> field = def.getField(fieldName);
                if (field != null) return field;
            }
        }
        return null;
    }

    @Nullable
    @Override
    protected Object[] get(Event event, EntityMeta[] source) {
        if (this.fieldName == null) return null;

        List<Object> results = new ArrayList<>();

        for (EntityMeta meta : source) {
            if (meta == null) continue;

            PropertyField<EntityMeta, ?> targetField = getFieldHierarchical(meta.getClass(), this.fieldName);
            if (targetField == null || targetField.getter() == null) continue;

            Object value = ((Function<EntityMeta, ?>) targetField.getter()).apply(meta);
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

            for (PropertyDefinition<Class<? extends EntityMeta>, EntityMeta> def : MetaDefinitionRegistry.INSTANCE.getAllDefinitions()) {
                PropertyField<EntityMeta, ?> field = def.getField(this.fieldName);
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

        for (EntityMeta meta : getExpr().getArray(event)) {
            if (meta == null) continue;

            PropertyField<EntityMeta, ?> targetField = getFieldHierarchical(meta.getClass(), this.fieldName);
            if (targetField == null || targetField.setter() == null) continue;

            Class<?> expected = targetField.expectedType();
            boolean isCompatible = expected == Object.class || expected.isInstance(newValue);

            if (!isCompatible && expected.isArray() && newValue.getClass().isArray()) {
                isCompatible = true;
            }

            if (!isCompatible) {
                Skript.warning("Cannot set the meta field '" + this.fieldName + "' to a value of type " + newValue.getClass().getSimpleName() + ". Expected type: " + expected.getSimpleName());
                continue;
            }

            ((BiConsumer<EntityMeta, Object>) targetField.setter()).accept(meta, newValue);
        }
    }

    @Override
    public Class<?> getReturnType() {
        return Object.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        String meta = getExpr() != null ? getExpr().toString(event, debug) : "meta";
        return "meta field " + fieldName + " of " + meta;
    }
}