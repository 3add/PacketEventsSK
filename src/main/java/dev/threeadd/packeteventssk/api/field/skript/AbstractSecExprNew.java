package dev.threeadd.packeteventssk.api.field.skript;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.expressions.base.SectionExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.util.Kleenean;
import com.github.shanebeee.skr.skript.SimpleEntryValidator;
import dev.threeadd.packeteventssk.api.field.BaseFieldRegistry;
import dev.threeadd.packeteventssk.api.field.ConstructionContext;
import dev.threeadd.packeteventssk.api.field.FieldAccessor;
import dev.threeadd.packeteventssk.api.field.FieldSchema;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;

import java.lang.reflect.Array;
import java.util.*;

/**
 * A util class for creating section expressions that construct new entities using fields.
 *
 * @param <K> type key (e.g. {@code PacketTypeCommon}, {@code EntityType})
 * @param <W> produced entity (e.g. {@code PacketWrapper<?>}, {@code WrapperEntity})
 */
public abstract class AbstractSecExprNew<K, W> extends SectionExpression<W> {

    private static final Map<Object, EntryValidator> VALIDATORS = new IdentityHashMap<>();

    protected abstract BaseFieldRegistry<K, W> getRegistry();

    /**
     * Extracts the type key from the type expression.
     *
     * @param typeExpr The expression representing the type.
     * @param event The runtime event, or {@code null} if called during parse-time validation.
     * @return The resolved type key, or {@code null} if it could not be resolved.
     */
    @Nullable
    protected abstract K resolveType(Expression<?> typeExpr, @Nullable Event event);

    /**
     * Human-readable name for {@code type} used in error/warning messages.
     */
    protected abstract String formatTypeName(@Nullable K type);

    /**
     * Category label used in messages when the type isn't yet known, e.g. {@code "packet"}.
     */
    protected abstract String categoryLabel();

    /**
     * Called after a schema is found but before section validation, lets subclasses
     * add extra rejection logic (e.g. abstract meta check in {@code SecExprNewMeta}).
     * Return {@code false} to abort parsing; emit your own {@link Skript#error} first.
     */
    protected boolean validateSchema(FieldSchema<K, W> schema, K type) {
        return true;
    }

    private final Map<String, Expression<?>> fieldExpressions = new HashMap<>();

    /**
     * Non-null only when the type was a literal (fast path).
     */
    private @Nullable K resolvedType;
    private @Nullable FieldSchema<K, W> resolvedSchema;
    protected Expression<K> typeExpr;

    private boolean isLiteral;

    /**
     * Builds and caches {@link EntryValidator} instances for every schema in
     * {@code registry}. Must be called inside the {@code register(Registration)} method
     * of each concrete subclass before the expression is registered with Skript.
     */
    protected static <K, W> void buildValidators(BaseFieldRegistry<K, W> registry) {
        for (FieldSchema<K, W> schema : registry.getAllSchemas()) {
            if (VALIDATORS.containsKey(schema)) continue;
            SimpleEntryValidator builder = SimpleEntryValidator.builder();
            for (FieldAccessor<W, ?> field : schema.accessors()) {
                builder.addOptionalEntry(field.name(), Object.class);
                for (String alias : field.aliases()) {
                    builder.addOptionalEntry(alias, Object.class);
                }
            }
            VALIDATORS.put(schema, builder.build());
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult, @Nullable SectionNode sectionNode, @Nullable List<TriggerItem> triggerItems) {
        this.typeExpr = (Expression<K>) expressions[0];
        this.isLiteral = this.typeExpr instanceof Literal<?>;

        if (this.isLiteral) {
            return initLiteral(sectionNode);
        } else {
            return initDynamic(sectionNode);
        }
    }

    private boolean initLiteral(@Nullable SectionNode sectionNode) {
        K type = resolveType(this.typeExpr, null);
        if (type == null) return false; // subclass already logged an error

        this.resolvedType = type;
        FieldSchema<K, W> schema = getRegistry().getSchema(type);

        if (schema == null) {
            Skript.warning("Creation of " + formatTypeName(type) + " " + categoryLabel() + " is not currently supported. Consider handling it through reflection.");
            // some registries (FakeEntity) emit a warning and return true to allow
            // the expression to parse the get() will just return null at runtime
            return onMissingSchema(type);
        }

        if (!validateSchema(schema, type)) return false;

        this.resolvedSchema = schema;
        return initSection(sectionNode, schema, type);
    }

    private boolean initDynamic(@Nullable SectionNode sectionNode) {
        FieldSchema<K, W> baseSchema = getRegistry().getBaseSchema();
        if (baseSchema == null) {
            Skript.error("The " + categoryLabel() + " type must be a parse-time literal because this create section has no base schema for dynamic inputs.");
            return false;
        }

        // parse-time validation only covers the base schema fields
        return initSection(sectionNode, baseSchema, null);
    }

    /**
     * Validates the section node against {@code schema} and populates
     * {@link #fieldExpressions}.  {@code type} is null in dynamic mode.
     */
    @SuppressWarnings("unchecked")
    private boolean initSection(@Nullable SectionNode sectionNode, FieldSchema<K, W> schema, @Nullable K type) {
        boolean hasRequired = schema.accessors().stream().anyMatch(f -> !f.isOptional());

        if (sectionNode == null) {
            if (hasRequired && type != null) { // only hard-error for literal mode
                Skript.error("You must provide a section with the required fields to create a " + formatTypeName(type) + " " + categoryLabel() + ".");
                return false;
            }
            return true;
        }

        EntryValidator validator = VALIDATORS.get(schema);
        if (validator == null) {
            Skript.error("No validator found for " + (type != null ? formatTypeName(type) : categoryLabel()) + ".");
            return false;
        }

        EntryContainer container = validator.validate(sectionNode);
        if (container == null) return false;

        List<String> missingKeys = new ArrayList<>();

        for (FieldAccessor<W, ?> field : schema.accessors()) {
            String key = field.name();
            Expression<?> expr = resolveEntryExpression(container, field);

            if (expr == null) {
                if (!field.isOptional()) missingKeys.add(key);
                continue;
            }

            Class<?> baseType = field.expectedType().isArray() ? field.expectedType().getComponentType() : field.expectedType();

            Expression<?> converted = expr.getConvertedExpression(baseType);
            if (converted == null) {
                Skript.error("The value for '" + key + "' must be of type " + field.expectedType().getSimpleName() + ".");
                return false;
            }

            this.fieldExpressions.put(key, converted);
        }

        if (!missingKeys.isEmpty()) {
            String name = type != null ? formatTypeName(type) : categoryLabel();
            Skript.error("Missing required entries for " + name + " " + categoryLabel() + ": "
                    + String.join(", ", missingKeys));
            return false;
        }

        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected W @Nullable [] get(Event event) {
        W result = construct(event);
        W[] arr = (W[]) Array.newInstance(getReturnType(), 1);
        arr[0] = result;
        return arr;
    }

    private @Nullable W construct(@NotNull Event event) {
        FieldSchema<K, W> schema;
        K type;

        if (this.isLiteral) {
            schema = this.resolvedSchema;
            type = this.resolvedType;
        } else {
            type = resolveType(this.typeExpr, event);
            if (type == null) return null;
            schema = getRegistry().getSchema(type);
        }

        if (schema == null || schema.constructor() == null) return null;

        Map<String, Object> values = new HashMap<>();
        for (FieldAccessor<W, ?> field : schema.accessors()) {
            Expression<?> expr = this.fieldExpressions.get(field.name());
            if (expr == null) continue;

            Object value;
            if (field.expectedType().isArray() || !expr.isSingle()) {
                Object[] array = expr.getArray(event);
                value = (array == null || array.length == 0) ? null : array;
            } else {
                value = expr.getSingle(event);
            }

            if (value == null) {
                if (!field.isOptional()) return null;
                continue;
            }
            values.put(field.name(), value);
        }

        return schema.constructor().apply(new ConstructionContext<>(type, schema.accessors(), values));
    }

    private @Nullable Expression<?> resolveEntryExpression(EntryContainer container, FieldAccessor<W, ?> field) {
        Expression<?> expr = container.getOptional(field.name(), Object.class, false);
        if (expr != null) return expr;
        for (String alias : field.aliases()) {
            expr = container.getOptional(alias, Object.class, false);
            if (expr != null) return expr;
        }
        return null;
    }

    /**
     * Called when no schema could be found, the return value is used on {@code init}.
     * Use this to decide weather to accept empty schemas or not.
     * For example {@code FakeEntity}'s work without a schema and will use the default {@code EntityTypes.ENTITY} fields.
     */
    protected boolean onMissingSchema(K type) {
        return true;
    }

    @Override
    public boolean isSingle() {
        return true;
    }
}

