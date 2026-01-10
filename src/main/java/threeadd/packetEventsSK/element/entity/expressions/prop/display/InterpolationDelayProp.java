package threeadd.packetEventsSK.element.entity.expressions.prop.display;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.util.Timespan;
import me.tofaa.entitylib.meta.display.AbstractDisplayMeta;
import org.jetbrains.annotations.Nullable;
import threeadd.packetEventsSK.element.entity.api.MetaPropertyExpression;

@SuppressWarnings("unused")
@Name("Fake Display Entity - Display Interpolation Delay")
@Description("""
        Represents the interpolation delay of a display entity.
        See [Display Entity Data](https://minecraft.wiki/w/Display#Entity_data) on McWiki for more details.
        """)
@Example("""
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
@Since("1.0.0")
public class InterpolationDelayProp extends MetaPropertyExpression<AbstractDisplayMeta, Timespan> {

    static {
        PropertyExpression.register(InterpolationDelayProp.class, Timespan.class,
                "fake[ ]display[ ]interpolation[ ]delay", "fakeentity/fakeentitymeta");
    }

    public InterpolationDelayProp() {
        super(Timespan.class, AbstractDisplayMeta.class, Changer.ChangeMode.SET);
    }

    @Override
    protected @Nullable Timespan get(AbstractDisplayMeta meta) {
        return new Timespan(Timespan.TimePeriod.TICK, meta.getInterpolationDelay());
    }

    @Override
    protected void change(AbstractDisplayMeta meta, Changer.ChangeMode mode, Object[] delta) {

        Timespan newValue = getDeltaValue(delta, Timespan.class);
        int ticks = Math.toIntExact(newValue.get(Timespan.TimePeriod.TICK));

        meta.setInterpolationDelay(ticks);
    }
}
