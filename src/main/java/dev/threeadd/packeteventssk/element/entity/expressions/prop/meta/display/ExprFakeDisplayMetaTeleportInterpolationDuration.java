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

public class ExprFakeDisplayMetaTeleportInterpolationDuration extends EntityMetaPropertyExpression<AbstractDisplayMeta, Timespan> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprFakeDisplayMetaTeleportInterpolationDuration.class, Timespan.class, "fake display teleport interpolation duration", "entitymeta")
                .name("Fake Display Entity - Teleport Interpolation Duration")
                .description("""
                        Represents the teleport interpolation duration of a Display Entity.
                        See [Display Entity Data](https://minecraft.wiki/w/Display#Entity_data) on McWiki for more details.
                        """)
                .examples("""
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
        return new Timespan[]{new Timespan(Timespan.TimePeriod.TICK, meta.getPositionRotationInterpolationDuration())};
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
        if (mode == Changer.ChangeMode.RESET || mode == Changer.ChangeMode.REMOVE_ALL) {
            meta.setPositionRotationInterpolationDuration((short) 0);
            return;
        }

        if (delta == null || delta.length != 1 || !(delta[0] instanceof Timespan timespan)) {
            throw new IllegalStateException("Unexpected delta value " + (delta != null ? Arrays.toString(delta) : "none"));
        }

        short ticks = (short) timespan.getAs(Timespan.TimePeriod.TICK);

        switch (mode) {
            case SET -> meta.setPositionRotationInterpolationDuration(ticks);
            case ADD -> meta.setPositionRotationInterpolationDuration((short) (meta.getPositionRotationInterpolationDuration() + ticks));
            case REMOVE -> meta.setPositionRotationInterpolationDuration((short) Math.max(0, meta.getPositionRotationInterpolationDuration() - ticks));
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
        return "fake display teleport interpolation duration";
    }
}
