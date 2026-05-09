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

public class ExprFakeMetaCustomNameVisible extends EntityMetaPropertyExpression<EntityMeta, Boolean> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprFakeMetaCustomNameVisible.class, Boolean.class, "fake custom[ ]name visible [state]", "entitymeta")
                .name("Fake Entity Property - Custom Name Visible State")
                .description("If the custom name of a fake entity is visible by other entities.")
                .examples("""
                        command spawnRichCow <text>:
                            trigger:
                                set {_name} to arg-1
                        
                                spawn new fake cow at player for player:
                                    set fake custom name of the fake entity to {_name}
                                    set fake custom name visible state of the fake entity to true
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
        return new Boolean[]{meta.isCustomNameVisible()};
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

        meta.setCustomNameVisible(newState);
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
        return "fake custom name visible state";
    }
}
