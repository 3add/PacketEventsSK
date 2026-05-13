package dev.threeadd.packeteventssk.element.entity.expressions.prop.meta.living;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.github.shanebeee.skr.Registration;
import dev.threeadd.packeteventssk.api.entity.EntityMetaPropertyExpression;
import me.tofaa.entitylib.meta.types.LivingEntityMeta;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class ExprFakeLivingEntityMetaHealth extends EntityMetaPropertyExpression<LivingEntityMeta, Number> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprFakeLivingEntityMetaHealth.class, Number.class, "fake (health|hp)", "entitymeta")
                .name("Fake Living Entity - Health")
                .description("Represents the health of a fake living entity.")
                // TODO example
                .since("1.1.0")
                .register();
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        setExpr((Expression<? extends LivingEntityMeta>) expressions[0]);
        return true;
    }

    @Override
    protected Number @Nullable [] getMetaProp(Event event, LivingEntityMeta meta) {
        return new Number[]{meta.getHealth()};
    }

    @Override
    public Class<?> @Nullable [] acceptChange(Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET) {
            return CollectionUtils.array(Number.class);
        }
        return null;
    }

    @Override
    protected void changeMeta(Event event, LivingEntityMeta meta, Object @Nullable [] delta, Changer.ChangeMode mode) {
        if (delta == null || delta.length != 1 || !(delta[0] instanceof Number newNumber)) {
            throw new IllegalStateException("Unexpected delta value " + (delta != null ? Arrays.toString(delta) : "none"));
        }

        if (mode != Changer.ChangeMode.SET) { // should always be the case
            return;
        }

        meta.setHealth(newNumber.floatValue());
    }

    @Override
    protected Class<LivingEntityMeta> getMetaClass() {
        return LivingEntityMeta.class;
    }

    @Override
    public Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    protected String getPropertyName() {
        return "fake health";
    }
}
