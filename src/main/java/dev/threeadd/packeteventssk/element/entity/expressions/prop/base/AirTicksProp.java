package dev.threeadd.packeteventssk.element.entity.expressions.prop.base;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.util.Timespan;
import me.tofaa.entitylib.meta.EntityMeta;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import dev.threeadd.packeteventssk.api.entity.MetaPropertyExpression;

@SuppressWarnings("unused")
@Name("Fake Entity Property - Frozen Time")
@Description("The amount of time the entity has been frozen, **I experienced some weird issues with this expression, since it's basically useless anyways it's excluded from the project**")
@Example("""
        command fallify <integer>:
            trigger:
                set {_entity} to fake entity with id arg-1
                if {_entity} is not set:
                    send "Couldn't find that entity"
                    stop
        
                set fake air time of {_entity} to 100 years
        """)
@Since("1.0.0")
public class AirTicksProp extends MetaPropertyExpression<EntityMeta, Timespan> {

    //public static void register(SyntaxRegistry registry) {
    //    registry.register(
    //            SyntaxRegistry.EXPRESSION,
    //            SyntaxInfo.Expression.builder(AirTicksProp.class, Timespan.class)
    //                    .addPatterns(
    //                            "[the] fake air time of %fakeentity%",
    //                            "%fakeentity%'s fake air time"
    //                    )
    //                    .build()
    //    );
    //}
    // prolly uncomment if packetevents fixes the bug

    public AirTicksProp() {
        super(Timespan.class, EntityMeta.class, Changer.ChangeMode.SET);
    }

    @Override
    protected @Nullable Timespan get(EntityMeta meta) {
        return new Timespan(Timespan.TimePeriod.TICK, meta.getAirTicks());
    }

    @Override
    protected void change(EntityMeta meta, Changer.ChangeMode mode, Object[] delta) {
        Timespan newValue = getDeltaValue(delta, Timespan.class);
        meta.setAirTicks((short) newValue.getAs(Timespan.TimePeriod.TICK));
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "air time of fake entity";
    }
}

