package threeadd.packetEventsSK.util.section;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.*;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

// This one was taken from the disky GitHub
// https://github.com/DiSkyOrg/DiSky/blob/581fa28839f9f8050b92f2e8bed52fc72a1743a0/src/main/java/net/itsthesky/disky/api/skript/ReturningSection.java
// as of 10/01/2026
// I just added warning suppressions and some stuff

/**
 * A section that return a value once it's entirely run.
 * @author ItsTheSky
 */
public abstract class ReturningSection<T> extends EffectSection {

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <T, S extends ReturningSection<T>> void register(Class<S> sectionClass,
                                                                   Class<T> returnType,
                                                                   Class expression,
                                                                   String[] builderPatterns,
                                                                   String... patterns) {
        for (int i = 0; i < patterns.length; i++)
            patterns[i] += " [and store (it|the result) in %-~objects%]";

        Skript.registerSection(sectionClass, patterns);
        Skript.registerExpression(expression, returnType, ExpressionType.SIMPLE, builderPatterns);
    }

    public abstract T createNewValue(@NotNull Event event);

    protected ParseResult parseResult;
    protected Expression<?>[] exprs;
    protected Class<T> returnType;

    private T currentValue;
    private Expression<T> expression;

    protected ReturningSection(Class<T> returnType) {
        this.returnType = returnType;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult, @Nullable SectionNode sectionNode, @Nullable List<TriggerItem> triggerItems) {
        if (sectionNode != null)
            loadOptionalCode(sectionNode);

        this.parseResult = parseResult;
        this.exprs = expressions;

        this.expression = (Expression<T>) exprs[exprs.length - 1];

        if (this.expression != null && !Changer.ChangerUtils.acceptsChange(this.expression, Changer.ChangeMode.SET, returnType)) {
            Skript.error(this.expression.toString(null, Skript.debug()) + " cannot be set to store a " + returnType.getSimpleName() + " value");
            return false;
        }

        return initialize();
    }

    protected abstract boolean initialize();

    @Override
    protected @Nullable TriggerItem walk(@NotNull Event e) {

        currentValue = createNewValue(e);
        if (expression != null)
            expression.change(e, new Object[] {currentValue}, Changer.ChangeMode.SET);

        return walk(e, true);
    }

    public T getCurrentValue() {
        return currentValue;
    }

    public abstract static class LastBuilderExpression<T, S extends ReturningSection<T>> extends SimpleExpression<T> {
        private S section;

        @Override
        public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
            section = getParser().getCurrentSection(getSectionClass());
            return getParser().isCurrentSection(getSectionClass());
        }

        @SuppressWarnings("unchecked")
        @Override
        protected T @NotNull [] get(@NotNull Event e) {
            return (T[]) new Object[] {section.getCurrentValue()};
        }

        @Override
        public boolean isSingle() {
            return true;
        }

        @SuppressWarnings("unchecked")
        public Class<S> getSectionClass() {
            Type type = ((ParameterizedType) getClass()
                    .getGenericSuperclass()).getActualTypeArguments()[1];

            if (type instanceof ParameterizedType) {
                type = ((ParameterizedType) type).getRawType();
            }
            return (Class<S>) type;
        }

        @SuppressWarnings("unchecked")
        @Override
        public @NotNull Class<? extends T> getReturnType() {
            Type type = ((ParameterizedType) getClass()
                    .getGenericSuperclass()).getActualTypeArguments()[0];

            if (type instanceof ParameterizedType) {
                type = ((ParameterizedType) type).getRawType();
            }
            return (Class<T>) type;
        }

        @Override
        public @NotNull String toString(@Nullable Event e, boolean debug) {
            return "the last builder value";
        }
    }
}