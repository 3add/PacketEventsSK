package threeadd.packetEventsSK.util.effect;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class CustomEffect extends Effect {

    protected Expression<?>[] expressions;
    protected int patternIndex;

    protected abstract boolean initialize(SkriptParser.ParseResult parseResult);

    @Override
    public final boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.expressions = expressions;
        this.patternIndex = matchedPattern;

        return initialize(parseResult);
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
        if (expr == null) throw new IllegalStateException("No Expression at index " + index);

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
}