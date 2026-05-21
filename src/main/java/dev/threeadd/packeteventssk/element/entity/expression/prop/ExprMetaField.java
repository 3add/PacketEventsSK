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
import dev.threeadd.packeteventssk.element.entity.field.meta.MetaFieldRegistry;
import me.tofaa.entitylib.meta.EntityMeta;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.BiConsumer;
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

        reg.newPropertyExpression(ExprMetaField.class, Object.class, "[fake] [entity] meta [field] <[a-zA-Z0-9_ ]+>", "entitymeta")
                .name("Entity Meta Property Field")
                .description(description.toString())
                .examples("""
                        on clientbound entity metadata:
                            # note that {_meta} is a copy of the packet's meta
                            set {_meta} to packet meta of event-packet
                            set meta glowing state of {_meta} to true
                        
                            # so we set it again here
                            set packet meta of event-packet to {_meta}
                        """)
                .since("1.1.2")
                .register();
    }

    private FieldAccessor <EntityMeta, ?> fieldAccessor;

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        String fieldName = parseResult.regexes.getFirst().group().trim();

        boolean isValidField = false;
        for (FieldSchema<EntityType, EntityMeta> def : MetaFieldRegistry.INSTANCE.getAllSchemas()) {
            this.fieldAccessor = def.getAccessor(fieldName);
            if (this.fieldAccessor != null) {
                isValidField = true;
                break;
            }
        }

        if (!isValidField) {
            Skript.error("The meta field '" + fieldName + "' is not registered or does not exist. Consider checking your spelling.");
            return false;
        }

        setExpr((Expression) exprs[0]);
        return true;
    }

    @Nullable
    @Override
    protected Object[] get(Event event, EntityMeta[] source) {
        if (this.fieldAccessor == null) return null;

        List<Object> elements = new ArrayList<>();

        for (EntityMeta entity : source) {
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

        for (Object obj : getExpr().getArray(event)) {
            if (!(obj instanceof EntityMeta meta)) continue;

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
                Skript.warning("Cannot set the meta field '" + this.fieldAccessor.name() + "' to a value of type " + newValue.getClass().getSimpleName() + ". Expected type: " + expected.getSimpleName());
                continue;
            }

            ((BiConsumer<EntityMeta, Object>) this.fieldAccessor.setter()).accept(meta, newValue);
        }
    }

    @Override
    public boolean isSingle() {
        return !this.fieldAccessor.expectedType().isArray();
    }

    @Override
    public Class<?> getReturnType() {
        Class<?> expected = this.fieldAccessor.expectedType();
        return expected.isArray() ? expected.getComponentType() : expected;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        String meta = getExpr() != null ? getExpr().toString(event, debug) : "meta";
        return "meta field " + this.fieldAccessor.name() + " of " + meta;
    }
}