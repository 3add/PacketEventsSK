package dev.threeadd.packeteventssk.element.entity.expressions.prop.meta.display;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.github.shanebeee.skr.Registration;
import dev.threeadd.packeteventssk.api.entity.EntityMetaPropertyExpression;
import me.tofaa.entitylib.meta.EntityMeta;
import me.tofaa.entitylib.meta.display.AbstractDisplayMeta;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class ExprFakeDisplayMetaTransformationInterpolationDuration extends EntityMetaPropertyExpression<AbstractDisplayMeta, Timespan> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprFakeDisplayMetaTransformationInterpolationDuration.class, Timespan.class, "fake display transform interpolation duration", "fakeentitymeta")
                .name("Fake Display Entity - Transform Interpolation Duration")
                .description("""
                        Represents the transform interpolation duration of a Display Entity.
                        See [Display Entity Data](https://minecraft.wiki/w/Display#Entity_data) on McWiki for more details.
                        """)
                .examples("""
                        command display:
                            trigger:
                                create new fake item display entity at player for players:
                                    set fake display item of the fake entity to dirt
                                    set {_e} to the fake entity
                        
                                set fake display transform interpolation duration of {_e} to 0.5 seconds
                                set fake display interpolation delay of {_e} to 0 seconds
                                loop 5 times:
                                    set fake display translation of {_e} to vector(0, loop-value, 0)
                                    wait 1 seconds
                        
                                kill fake entity {_e}
                        """)
                .since("1.0.0")
                .register();
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        setExpr((Expression<? extends EntityMeta>) expressions[0]);
        return true;
    }

    @Override
    protected Timespan @Nullable [] getMetaProp(Event event, AbstractDisplayMeta meta) {
        return new Timespan[]{new Timespan(Timespan.TimePeriod.TICK, meta.getTransformationInterpolationDuration())};
    }

    @Override
    public Class<?> @Nullable [] acceptChange(Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.ADD
                || mode == Changer.ChangeMode.REMOVE
                || mode == Changer.ChangeMode.SET
                || mode == Changer.ChangeMode.RESET
                || mode == Changer.ChangeMode.REMOVE_ALL) {
            return CollectionUtils.array(Timespan.class);
        }
        return null;
    }

    @Override
    protected void changeMeta(Event event, AbstractDisplayMeta meta, Object @Nullable [] delta, Changer.ChangeMode mode) {
        short ticks;
        if (delta != null && delta.length == 1 && delta[0] instanceof Timespan timespan) {
            ticks = (short) timespan.getAs(Timespan.TimePeriod.TICK);
        } else {
            throw new IllegalStateException("Unexpected delta value " + (delta != null ? Arrays.toString(delta) : "none"));
        }

        switch (mode) {
            case SET -> meta.setTransformationInterpolationDuration(ticks);
            case ADD -> meta.setTransformationInterpolationDuration((short) (meta.getAirTicks() + ticks));
            case REMOVE -> meta.setTransformationInterpolationDuration((short) Math.max(0, meta.getAirTicks() - ticks));
            case RESET, REMOVE_ALL -> meta.setTransformationInterpolationDuration((short) 0);
        }
    }

    @Override
    protected Class<AbstractDisplayMeta> getMetaClass() {
        return AbstractDisplayMeta.class;
    }

    @Override
    public Class<? extends Timespan> getReturnType() {
        return Timespan.class;
    }

    @Override
    protected String getPropertyName() {
        return "fake display transform interpolation duration";
    }
}
