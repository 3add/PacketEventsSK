package threeadd.packetEventsSK.element.entity.expressions.prop.display;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.util.Timespan;
import ch.njol.skript.util.Timespan.TimePeriod;
import me.tofaa.entitylib.meta.display.AbstractDisplayMeta;
import org.jetbrains.annotations.Nullable;
import threeadd.packetEventsSK.element.entity.api.MetaPropertyExpression;

@SuppressWarnings("unused")
@Name("Fake Display Entity - Teleport Interpolation Duration")
@Description("""
        Represents the teleport interpolation duration of a Display Entity.
        See [Display Entity Data](https://minecraft.wiki/w/Display#Entity_data) on McWiki for more details.
        """)
@Example("""
        command display:
            trigger:
                create new fake item display entity at player for players:
                    set fake display item of the fake entity to dirt
                    set {_e} to the fake entity
        
                set fake display teleport interpolation duration of {_e} to 0.5 seconds
                loop 5 times:
                    set {_locVector} to fake entity location of {_e}
                    set {_loc} to {_locVector} to location in world of player
        
                    set {_newLoc} to {_loc} ~ vector(0, 1, 0)
                    teleport fake entity {_e} to {_newLoc}
                    wait 1 seconds
        
                kill fake entity {_e}
        """)
@Since("1.0.0")
public class TeleportInterpolationDurationProp extends MetaPropertyExpression<AbstractDisplayMeta, Timespan> {

    static {
        PropertyExpression.register(TeleportInterpolationDurationProp.class, Timespan.class,
                "fake[ ]display[ ]teleport[ation][ ]interpolation[ ]duration", "fakeentity/fakeentitymeta");
    }

    public TeleportInterpolationDurationProp() {
        super(Timespan.class, AbstractDisplayMeta.class, Changer.ChangeMode.SET);
    }

    @Override
    protected @Nullable Timespan get(AbstractDisplayMeta meta) {
        return new Timespan(TimePeriod.TICK, meta.getPositionRotationInterpolationDuration());
    }

    @Override
    protected void change(AbstractDisplayMeta meta, Changer.ChangeMode mode, Object[] delta) {
        Timespan newValue = getDeltaValue(delta, Timespan.class);

        int ticks = Math.toIntExact(newValue.get(TimePeriod.TICK));

        meta.setPositionRotationInterpolationDuration(ticks);
    }
}
