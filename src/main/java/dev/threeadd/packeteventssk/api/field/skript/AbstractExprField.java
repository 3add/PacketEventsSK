package dev.threeadd.packeteventssk.api.field.skript;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.google.common.primitives.Primitives;
import dev.threeadd.packeteventssk.api.field.BaseFieldRegistry;
import dev.threeadd.packeteventssk.api.field.FieldAccessor;
import dev.threeadd.packeteventssk.api.field.FieldSchema;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * @param <K> type key
 * @param <W> wrapper class
 */
public abstract class AbstractExprField<K, W> extends PropertyExpression<W, Object> {

    protected abstract BaseFieldRegistry<K, W> getRegistry();

    /**
     * Returns the correct {@link FieldAccessor} for a live wrapper instance, or
     * {@code null} if this field does not apply to the instance's concrete type.
     */
    @Nullable
    protected abstract FieldAccessor<W, ?> resolveAccessor(W instance);

    /**
     * Category label for error messages, e.g. {@code "packet"} or {@code "meta field"}.
     */
    protected abstract String categoryLabel();

    @Nullable
    protected FieldAccessor<W, ?> hintAccessor;
    protected String fieldName;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.fieldName = parseResult.regexes.getFirst().group().trim();

        // walk every schema to find at least one accessor with this name
        // we only need a type-hint for getReturnType() runtime resolution is
        // done per-instance via resolveAccessor()
        for (FieldSchema<K, W> schema : getRegistry().getAllSchemas()) {
            FieldAccessor<W, ?> candidate = schema.getAccessor(this.fieldName);
            if (candidate != null) {
                this.hintAccessor = candidate;
                break;
            }
        }

        if (this.hintAccessor == null) {
            Skript.error("The " + categoryLabel() + " field '" + this.fieldName
                    + "' is not registered or does not exist. Consider checking your spelling.");
            return false;
        }

        setExpr((Expression<? extends W>) exprs[0]);
        return true;
    }

    @Override
    protected Object[] get(Event event, W[] source) {
        List<Object> elements = new ArrayList<>();

        for (W wrapper : source) {
            if (wrapper == null) continue;

            FieldAccessor<W, ?> accessor = resolveAccessor(wrapper);
            if (accessor == null) continue;

            Object value = accessor.getter().apply(wrapper);
            if (value == null) continue;

            if (value.getClass().isArray()) {
                int len = Array.getLength(value);
                for (int i = 0; i < len; i++) elements.add(Array.get(value, i));
            } else {
                elements.add(value);
            }
        }

        if (elements.isEmpty()) return null;

        // wrap primitives to avoid ClassCastException on typed array creation
        Class<?> returnType = Primitives.wrap(getReturnType());
        return elements.toArray((Object[]) Array.newInstance(returnType, 0));
    }

    @Nullable
    @Override
    public Class<?>[] acceptChange(Changer.ChangeMode mode) {
        if (this.hintAccessor == null || this.hintAccessor.setter() == null) {
            Skript.error("Cannot set '" + this.fieldName + "' because it is a read-only field.");
            return null;
        }
        if (mode != Changer.ChangeMode.SET) return null;

        Class<?> expected = this.hintAccessor.expectedType();
        Class<?> toAccept = expected.isArray() ? expected.getComponentType() : expected;
        return new Class<?>[]{Primitives.wrap(toAccept)};
    }

    @SuppressWarnings("unchecked")
    @Override
    public void change(Event event, Object[] delta, Changer.ChangeMode mode) {
        if (mode != Changer.ChangeMode.SET || delta == null || delta.length == 0) return;

        for (W wrapper : getExpr().getArray(event)) {
            if (wrapper == null) continue;

            FieldAccessor<W, ?> accessor = resolveAccessor(wrapper);
            if (accessor == null || accessor.setter() == null) continue;

            Object newValue = buildNewValue(accessor, delta);
            if (newValue == null) continue;

            if (!isCompatible(accessor.expectedType(), newValue)) {
                Skript.warning("Cannot set the " + categoryLabel() + " field '" + accessor.name()
                        + "' to a value of type " + newValue.getClass().getSimpleName()
                        + ". Expected: " + accessor.expectedType().getSimpleName());
                continue;
            }

            ((BiConsumer<W, Object>) accessor.setter()).accept(wrapper, newValue);
        }
    }

    private @Nullable Object buildNewValue(FieldAccessor<W, ?> accessor, Object[] delta) {
        Class<?> expected = accessor.expectedType();
        if (expected.isArray()) {
            Class<?> component = expected.getComponentType();
            Object arr = Array.newInstance(component, delta.length);
            for (int i = 0; i < delta.length; i++) Array.set(arr, i, delta[i]);
            return arr;
        }
        return delta.length == 1 ? delta[0] : delta;
    }

    private boolean isCompatible(Class<?> expected, Object value) {
        if (expected == Object.class) return true;
        if (expected.isInstance(value)) return true;
        return expected.isArray() && value.getClass().isArray();
    }

    @Override
    public boolean isSingle() {
        if (this.hintAccessor == null) return true;
        return getExpr().isSingle() && !this.hintAccessor.expectedType().isArray();
    }

    @Override
    public Class<?> getReturnType() {
        if (this.hintAccessor == null) return Object.class;
        Class<?> expected = this.hintAccessor.expectedType();
        return expected.isArray() ? expected.getComponentType() : expected;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        String source = getExpr() != null ? getExpr().toString(event, debug) : categoryLabel();
        return categoryLabel() + " field " + this.fieldName + " of " + source;
    }
}