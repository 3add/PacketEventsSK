package dev.threeadd.packeteventssk.element.general.expression.prop;

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
import dev.threeadd.packeteventssk.element.general.field.meta.MetaFieldRegistry;
import me.tofaa.entitylib.meta.EntityMeta;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ExprMetaField extends PropertyExpression<EntityMeta, Object> {

    public static void register(Registration reg) {
        StringBuilder description = new StringBuilder();
        description.append("Gets or sets a metadata property field value from an entity meta instance by its name.\nNote that some entities inherit properties (for example all entities inherit \"entity\" fields\n\n");
        description.append("### Available Meta Fields by Category\n");

        List<FieldSchema<EntityType, EntityMeta>> schemas = new ArrayList<>(MetaFieldRegistry.INSTANCE.getAllSchemas());
        schemas.sort(Comparator.comparingInt(schema -> schema.accessors().size()));
        for (FieldSchema<EntityType, EntityMeta> schema : schemas) {

            Set<String> myFields = schema.accessors().stream()
                    .map(FieldAccessor::name)
                    .collect(Collectors.toSet());

            FieldSchema<EntityType, EntityMeta> parentSchema = null;
            int maxSubsetSize = -1;

            for (FieldSchema<EntityType, EntityMeta> other : schemas) {
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
            for (FieldAccessor<EntityMeta, ?> field : schema.accessors()) {
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

        reg.newPropertyExpression(ExprMetaField.class, Object.class, "[meta] (field|fake) <[a-zA-Z0-9_ ]+>", "entitymeta")
                .name("Entity Meta Property Field")
                .description(description.toString())
                .examples("""
                        on clientbound entity metadata:
                            # note that {_meta} is a copy of the packet's meta
                            set {_meta} to meta of event-packet
                            set glowing state of {_meta} to true
                        
                            # so we set it again here
                            set meta of event-packet to {_meta}
                        """)
                .since("1.1.2")
                .register();
    }

    private String fieldName;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.fieldName = parseResult.regexes.getFirst().group().trim();

        boolean isValidField = false;
        for (FieldSchema<EntityType, EntityMeta> def : MetaFieldRegistry.INSTANCE.getAllSchemas()) {
            if (def.getAccessor(this.fieldName) != null) {
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

    @Nullable
    @Override
    protected Object[] get(Event event, EntityMeta[] source) {
        if (this.fieldName == null) return null;

        List<Object> results = new ArrayList<>();

        for (EntityMeta meta : source) {
            if (meta == null) continue;

            FieldAccessor<EntityMeta, ?> targetField = MetaFieldRegistry.INSTANCE.getAccessor(meta.getClass(), fieldName);
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

            for (FieldSchema<EntityType, EntityMeta> def : MetaFieldRegistry.INSTANCE.getAllSchemas()) {
                FieldAccessor<EntityMeta, ?> field = def.getAccessor(this.fieldName);
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

            FieldAccessor<EntityMeta, ?> targetField = MetaFieldRegistry.INSTANCE.getAccessor(meta.getClass(), fieldName);
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