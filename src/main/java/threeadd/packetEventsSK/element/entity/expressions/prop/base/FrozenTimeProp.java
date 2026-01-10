package threeadd.packetEventsSK.element.entity.expressions.prop.base;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.util.Timespan;
import me.tofaa.entitylib.meta.EntityMeta;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import threeadd.packetEventsSK.element.entity.api.MetaPropertyExpression;

@SuppressWarnings("unused")
@Name("Fake Entity Property - Frozen Time")
@Description("The amount of time the entity has been frozen")
@Example("""
        command spawnOlaf:
            trigger:
                spawn new fake player at player for player:
                    set fake skin of the fake entity to player's skin
        
                    # actual age of olaf according to https://en.fmyly.com/article/how-old-is-olaf-in-frozen-two/
                    set fake frozen time of the fake entity to 3 years
        """)
@Since("1.0.0")
public class FrozenTimeProp extends MetaPropertyExpression<EntityMeta, Timespan> {

    static {
        PropertyExpression.register(FrozenTimeProp.class, Timespan.class, "fake[ ]frozen[ ]time", "fakeentity/fakeentitymeta");
    }

    public FrozenTimeProp() {
        super(Timespan.class, EntityMeta.class, Changer.ChangeMode.SET);
    }

    @Override
    protected @Nullable Timespan get(EntityMeta meta) {
        return new Timespan(Timespan.TimePeriod.TICK, meta.getTicksFrozenInPowderedSnow());
    }

    @Override
    protected void change(EntityMeta meta, Changer.ChangeMode mode, Object[] delta) {
        Timespan newValue = getDeltaValue(delta, Timespan.class);
        meta.setTicksFrozenInPowderedSnow((int) newValue.getAs(Timespan.TimePeriod.TICK));
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "frozen time of fake entity";
    }
}

