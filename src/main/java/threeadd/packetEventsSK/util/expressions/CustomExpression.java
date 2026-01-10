package threeadd.packetEventsSK.util.expressions;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.ArrayList;

import java.util.List;

public abstract class CustomExpression<Return> extends SimpleExpression<Return> {

    protected final Class<? extends Return> returnType;
    protected final boolean isSingle;

    protected Expression<?>[] expressions;
    protected int patternIndex;
    protected SkriptParser.ParseResult parseResult;

    public CustomExpression(Class<? extends Return> returnType, boolean isSingle) {
        this.returnType = returnType;
        this.isSingle = isSingle;
    }

    protected abstract boolean initialize(SkriptParser.ParseResult parseResult);

    @Override
    public final boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.expressions = expressions;
        this.patternIndex = matchedPattern;
        this.parseResult = parseResult;

        return initialize(parseResult);
    }

    @Override
    @Nullable
    public final Return[] get(@NotNull Event event) {

        if (isSingle) {
            @SuppressWarnings("unchecked")
            Return[] array = (Return[]) Array.newInstance(getReturnType(), 1);
            array[0] = getOne(event);

            return array;
        } else {
            List<Return> list = getMany(event);
            if (list == null) return null;

            @SuppressWarnings("unchecked")
            Return[] array = (Return[]) Array.newInstance(getReturnType(), list.size());

            for (int i = 0; i < list.size(); i++) {
                array[i] = list.get(i);
            }

            return array;
        }
    }

    @Nullable
    protected Return getOne(Event currentEvent) {
        return null;
    }

    @Nullable
    protected List<Return> getMany(Event currentEvent) {
        return null;
    }

    @Override
    public final boolean isSingle() {
        return this.isSingle;
    }

    @Override
    public final Class<? extends Return> getReturnType() {
        return this.returnType;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return this.getClass().getSimpleName() + (event != null ? " (Event: " + event.getEventName() + ")" : "");
    }

    // Getting Expressions

    @NotNull
    protected final <E extends Expression<?>> E getExpression(int index, Class<E> expressionClass) {
        E expression = getExpressionOrNull(index, expressionClass);

        if (expression == null)
            throw new IllegalStateException("Expression at index " + index + " of type " + expressionClass.getName() + " was not found");

        return expression;
    }

    @NotNull
    protected final <E extends Expression<?>> E getExpressionOrDefault(int index, Class<E> requiredReturnType, @NotNull E defaultValue) {
        E expression = getExpressionOrNull(index, requiredReturnType);

        if (expression == null)
            return defaultValue;

        return expression;
    }

    @Nullable
    protected final <E extends Expression<?>> E getExpressionOrNull(int index, Class<E> expressionClass) {
        if (expressions == null || index < 0 || index >= expressions.length)
            return null;

        Expression<?> expression = expressions[index];
        if (expression == null) return null;

        if (expressionClass.isAssignableFrom(expression.getClass())) {
            return expressionClass.cast(expression);
        }

        return null;
    }

    // Getting values from Expressions

    @Nullable
    protected final <V> V getValueOrNull(int index, Class<V> returnType, Event event) {
        Expression<?> expr = getExpressionOrNull(index, Expression.class);
        if (expr == null) return null;

        if (!returnType.isAssignableFrom(expr.getReturnType()))
            throw new IllegalStateException("Wrong type Expression at index " + index + " (target " + returnType + ", found " + expr.getReturnType() + ")");

        if (event == null)
            throw new IllegalStateException("Event is null");

        Object raw = expr.getSingle(event);
        if (raw == null) return null;

        return returnType.cast(raw);
    }

    @NotNull
    protected final <V> V getValueOrDefault(int index, Class<V> returnType, @NotNull V defaultValue, Event event) {
        V value = getValueOrNull(index, returnType, event);

        if (value == null) {
            return defaultValue;
        }

        return value;
    }

    @NotNull
    protected final <V> V getValue(int index, Class<V> returnType, Event event) {
        V value = getValueOrNull(index, returnType, event);

        if (value == null)
            throw new IllegalStateException("No value found of type " + returnType.getName() + " at index " + index);

        return value;
    }

    @Nullable
    protected final <V> List<V> getValuesOrNull(int index, Class<V> returnType, Event event) {
        Expression<?> expr = getExpressionOrNull(index, Expression.class);
        if (expr == null) return null;

        if (!returnType.isAssignableFrom(expr.getReturnType()))
            throw new IllegalStateException("Expression at index " + index + " isn't of type " + returnType.getName());

        if (event == null)
            throw new IllegalStateException("No currently stored parse event");

        Object raw = expr.getArray(event);
        if (raw == null) return null;

        List<V> list = new ArrayList<>();
        for (Object o : expr.getArray(event))
            list.add(returnType.cast(o));

        return list;
    }

    @NotNull
    protected final <V> List<V> getValuesOrDefault(int index, Class<V> returnType, @NotNull List<V> defaultValues, Event event) {
        List<V> values = getValuesOrNull(index, returnType, event);

        if (values == null)
            return defaultValues;

        return values;
    }

    @NotNull
    protected final <V> List<V> getValues(int index, Class<V> returnType, Event event) {
        List<V> values = getValuesOrNull(index, returnType, event);

        if (values == null)
            throw new IllegalStateException("No value found of type " + returnType.getName() + " at index " + index);

        return values;
    }

    // Getting values from Change Deltas

    @Nullable
    protected final <V> V getDeltaValueOrNull(Object[] delta, int index, Class<V> returnType) {
        if (delta == null || index < 0 || index >= delta.length)
            return null;

        Object raw = delta[index];
        if (raw == null) return null;

        if (!returnType.isInstance(raw))
            throw new IllegalStateException("Delta value at index " + index + " is not of type " + returnType.getName() + " (found " + raw.getClass().getName() + ")");

        return returnType.cast(raw);
    }

    @NotNull
    protected final <V> V getDeltaValue(Object[] delta, int index, Class<V> returnType) {
        V value = getDeltaValueOrNull(delta, index, returnType);

        if (value == null)
            throw new IllegalStateException("No delta value found of type " + returnType.getName() + " at index " + index);

        return value;
    }

    @NotNull
    protected final <V> V getDeltaValue(Object[] delta, Class<V> returnType) {
        return getDeltaValue(delta, 0, returnType);
    }

    @Nullable
    protected final <V> List<V> getDeltaValuesOrNull(Object[] delta, Class<V> returnType) {
        if (delta == null) return null;

        List<V> list = new ArrayList<>();
        for (Object o : delta) {
            if (!returnType.isAssignableFrom(o.getClass()))
                continue;

            list.add(returnType.cast(o));
        }

        if (list.isEmpty()) return null;

        return list;
    }

    @NotNull
    protected final <V> List<V> getDeltaValuesOrDefault(Object[] delta, Class<V> returnType, @NotNull List<V> defaultValues) {
        List<V> values = getDeltaValuesOrNull(delta, returnType);

        if (values == null)
            return defaultValues;

        return values;
    }

    @NotNull
    protected final <V> List<V> getDeltaValues(Object[] delta, Class<V> returnType) {
        List<V> values = getDeltaValuesOrNull(delta, returnType);

        if (values == null)
            throw new IllegalStateException("Delta array is null. Cannot retrieve plural delta values of type " + returnType.getName());

        return values;
    }
}