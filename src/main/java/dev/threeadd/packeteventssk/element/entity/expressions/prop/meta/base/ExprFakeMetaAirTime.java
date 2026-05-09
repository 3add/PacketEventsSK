package dev.threeadd.packeteventssk.element.entity.expressions.prop.meta.base;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.github.shanebeee.skr.Registration;
import dev.threeadd.packeteventssk.api.entity.EntityMetaPropertyExpression;
import me.tofaa.entitylib.meta.EntityMeta;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

// TODO causes "java.lang.IllegalStateException: java.io.IOException: Unknown nbt type id 88" on clients
public class ExprFakeMetaAirTime extends EntityMetaPropertyExpression<EntityMeta, Timespan> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprFakeMetaAirTime.class, Timespan.class, "fake air time", "entitymeta")
                .name("Fake Entity Property - Air Time")
                .description("The amount of time the entity has been in the air")
                .examples("""
                        command fallify <integer>:
                            trigger:
                                set {_entity} to fake entity with id arg-1
                                if {_entity} is not set:
                                    send "Couldn't find that entity"
                                    stop
                        
                                set fake air time of {_entity} to 100 years
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
    protected Timespan @Nullable [] getMetaProp(Event event, EntityMeta meta) {
        return new Timespan[]{ new Timespan(Timespan.TimePeriod.TICK, meta.getAirTicks()) };
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
    protected void changeMeta(Event event, EntityMeta entityMeta, Object @Nullable [] delta, Changer.ChangeMode mode) {
        short ticks = 0;
        if (mode == Changer.ChangeMode.SET || mode == Changer.ChangeMode.ADD || mode == Changer.ChangeMode.REMOVE) {
            if (delta != null && delta.length == 1 && delta[0] instanceof Timespan timespan) {
                ticks = (short) timespan.getAs(Timespan.TimePeriod.TICK);
            } else {
                throw new IllegalStateException("Unexpected delta value");
            }
        }

        switch (mode) {
            case SET -> entityMeta.setAirTicks(ticks);
            case ADD -> entityMeta.setAirTicks((short) (entityMeta.getAirTicks() + ticks));
            case REMOVE -> entityMeta.setAirTicks((short) Math.max(0, entityMeta.getAirTicks() - ticks));
            case RESET, REMOVE_ALL -> entityMeta.setAirTicks((short) 0);
        }
    }

    @Override
    protected Class<EntityMeta> getMetaClass() {
        return EntityMeta.class;
    }

    @Override
    public Class<? extends Timespan> getReturnType() {
        return Timespan.class;
    }

    @Override
    protected String getPropertyName() {
        return "fake air time";
    }
}