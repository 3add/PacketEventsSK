package dev.threeadd.packeteventssk.element.entity.expressions.prop.meta.base;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.github.shanebeee.skr.Registration;
import dev.threeadd.packeteventssk.api.entity.EntityMetaPropertyExpression;
import me.tofaa.entitylib.meta.EntityMeta;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

// TODO doesn't seem to update on the client (does server side, checked)
public class ExprFakeMetaInvisible extends EntityMetaPropertyExpression<EntityMeta, Boolean> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprFakeMetaInvisible.class, Boolean.class, "fake invisible [state]", "entitymeta")
                .name("Fake Entity Property - Invisible State")
                .description("If a fake entity is invisible. (If they are visible by other entities)")
                .examples("""
                        command dadify <integer>:
                            trigger:
                                set {_entity} to fake entity with id arg-1
                                if {_entity} is not set:
                                    send "Couldn't find that entity"
                                    stop
                        
                                set fake invisible state of {_entity} to true
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
    protected Boolean @Nullable [] getMetaProp(Event event, EntityMeta meta) {
        return new Boolean[]{meta.isInvisible()};
    }

    @Override
    public Class<?> @Nullable [] acceptChange(Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET) {
            return CollectionUtils.array(Boolean.class);
        }
        return null;
    }

    @Override
    protected void changeMeta(Event event, EntityMeta meta, Object @Nullable [] delta, Changer.ChangeMode mode) {
        if (delta == null || delta.length != 1 || !(delta[0] instanceof Boolean newState)) {
            throw new IllegalStateException("Unexpected delta value " + (delta != null ? Arrays.toString(delta) : "none"));
        }

        if (mode != Changer.ChangeMode.SET) { // should always be the case
            return;
        }

        meta.setInvisible(newState);
    }

    @Override
    protected Class<EntityMeta> getMetaClass() {
        return EntityMeta.class;
    }

    @Override
    public Class<? extends Boolean> getReturnType() {
        return Boolean.class;
    }

    @Override
    protected String getPropertyName() {
        return "fake invisible state";
    }
}
