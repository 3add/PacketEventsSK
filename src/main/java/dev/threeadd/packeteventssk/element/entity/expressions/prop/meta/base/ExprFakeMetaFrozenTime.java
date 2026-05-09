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

import java.util.Arrays;

public class ExprFakeMetaFrozenTime extends EntityMetaPropertyExpression<EntityMeta, Timespan> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprFakeMetaFrozenTime.class, Timespan.class, "fake frozen time", "entitymeta")
                .name("Fake Entity Property - Frozen Time")
                .description("The amount of time the entity has been frozen (in powdered snow, makes them shake)")
                .examples("""
                        command spawnOlaf:
                            trigger:
                                set {_p} to player
                                spawn new fake player at player for player:
                                    set fake skin of the fake entity to {_p}'s skin
                        
                                    # actual age of olaf according to https://en.fmyly.com/article/how-old-is-olaf-in-frozen-two/
                                    set fake frozen time of the fake entity to 3 years
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
        return new Timespan[]{new Timespan(Timespan.TimePeriod.TICK, meta.getAirTicks())};
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
    protected void changeMeta(Event event, EntityMeta meta, Object @Nullable [] delta, Changer.ChangeMode mode) {
        short ticks;
        if (delta != null && delta.length == 1 && delta[0] instanceof Timespan timespan) {
            ticks = (short) timespan.getAs(Timespan.TimePeriod.TICK);
        } else {
            throw new IllegalStateException("Unexpected delta value " + (delta != null ? Arrays.toString(delta) : "none"));
        }

        switch (mode) {
            case SET -> meta.setTicksFrozenInPowderedSnow(ticks);
            case ADD -> meta.setTicksFrozenInPowderedSnow((short) (meta.getAirTicks() + ticks));
            case REMOVE -> meta.setTicksFrozenInPowderedSnow((short) Math.max(0, meta.getAirTicks() - ticks));
            case RESET, REMOVE_ALL -> meta.setTicksFrozenInPowderedSnow((short) 0);
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
        return "fake frozen time";
    }
}

