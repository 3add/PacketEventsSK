package threeadd.packetEventsSK.element.entity.expressions.prop.display;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.util.Timespan;
import ch.njol.skript.util.Timespan.TimePeriod;
import me.tofaa.entitylib.meta.display.AbstractDisplayMeta;
import org.jetbrains.annotations.Nullable;
import threeadd.packetEventsSK.element.entity.api.MetaPropertyExpression;

@SuppressWarnings("unused")
@Name("Fake Display Entity - Transform Interpolation Duration")
@Description("""
        Represents the transform interpolation duration of a Display Entity.
        See [Display Entity Data](https://minecraft.wiki/w/Display#Entity_data) on McWiki for more details.
        """)
@Example("""
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
public class TransformInterpolationDurationProp extends MetaPropertyExpression<AbstractDisplayMeta, Timespan> {

    static {
        PropertyExpression.register(TransformInterpolationDurationProp.class, Timespan.class,
                "fake[ ]display[ ]transform[ation][ ]interpolation[ ]duration", "fakeentity/fakeentitymeta");
    }

    public TransformInterpolationDurationProp() {
        super(Timespan.class, AbstractDisplayMeta.class, Changer.ChangeMode.SET);
    }

    @Override
    protected @Nullable Timespan get(AbstractDisplayMeta meta) {
        return new Timespan(TimePeriod.TICK, meta.getTransformationInterpolationDuration() );
    }

    @Override
    protected void change(AbstractDisplayMeta meta, Changer.ChangeMode mode, Object[] delta) {
        Timespan newValue = getDeltaValue(delta, Timespan.class);

        int ticks = Math.toIntExact(newValue.get(TimePeriod.TICK));

        meta.setTransformationInterpolationDuration(ticks);
    }
}
