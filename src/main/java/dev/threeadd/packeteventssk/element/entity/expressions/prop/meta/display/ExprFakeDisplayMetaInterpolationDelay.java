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

public class ExprFakeDisplayMetaInterpolationDelay extends EntityMetaPropertyExpression<AbstractDisplayMeta, Timespan> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprFakeDisplayMetaInterpolationDelay.class, Timespan.class, "fake display interpolation delay", "entitymeta")
                .name("Fake Display Entity - Display Interpolation Delay")
                .description("""
                        Represents the interpolation delay of a display entity.
                        See [Display Entity Data](https://minecraft.wiki/w/Display#Entity_data) on McWiki for more details.
                        """)
                .examples("""
                        command display:
                            trigger:
                                create new fake item display entity at player for players:
                                    set fake display item of the fake entity to dirt
                                    set {_e} to the fake entity
                        
                                set fake display transform interpolation duration of {_e} to 1 seconds
                                set fake display interpolation delay of {_e} to 0 seconds
                                loop 5 times:
                                    set fake display left rotation of {_e} to quaternion(loop-value / 5, 1, 1, 1)
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
        return new Timespan[]{new Timespan(Timespan.TimePeriod.TICK, meta.getInterpolationDelay())};
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
            case SET -> meta.setInterpolationDelay(ticks);
            case ADD -> meta.setInterpolationDelay((short) (meta.getAirTicks() + ticks));
            case REMOVE -> meta.setInterpolationDelay((short) Math.max(0, meta.getAirTicks() - ticks));
            case RESET, REMOVE_ALL -> meta.setInterpolationDelay((short) 0);
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
        return "fake display interpolation delay";
    }
}
