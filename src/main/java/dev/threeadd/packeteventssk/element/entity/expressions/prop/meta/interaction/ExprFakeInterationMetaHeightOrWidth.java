package dev.threeadd.packeteventssk.element.entity.expressions.prop.meta.interaction;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.github.shanebeee.skr.Registration;
import dev.threeadd.packeteventssk.api.entity.EntityMetaPropertyExpression;
import me.tofaa.entitylib.meta.EntityMeta;
import me.tofaa.entitylib.meta.other.InteractionMeta;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class ExprFakeInterationMetaHeightOrWidth extends EntityMetaPropertyExpression<InteractionMeta, Number> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprFakeInterationMetaHeightOrWidth.class, Number.class, "fake interaction (:(height|width))", "entitymeta")
                .name("Fake Interaction Entity - Interaction Width/Height")
                .description("Represents the width or height of an interaction entity.")
                .examples("""
                        command interaction:
                            trigger:
                                create new fake interaction entity at player for players:
                                    set fake interaction width of the fake entity to 2
                                    set fake interaction height of the fake entity to 2
                                    add fake entity id of the fake entity to {-id::*}
                        
                                    wait 10 seconds
                                    kill fake entity the fake entity
                        """)
                .since("1.0.0")
                .register();
    }

    private boolean isWidth = false;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        setExpr((Expression<? extends EntityMeta>) expressions[0]);

        if (parseResult.hasTag("width")) {
            isWidth = true;
        }
        return true;
    }

    @Override
    protected Number @Nullable [] getMetaProp(Event event, InteractionMeta meta) {
        return new Number[]{isWidth ? meta.getWidth() : meta.getHeight()};
    }

    @Override
    public Class<?> @Nullable [] acceptChange(Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET) {
            return CollectionUtils.array(Number.class);
        }
        return null;
    }

    @Override
    protected void changeMeta(Event event, InteractionMeta meta, Object @Nullable [] delta, Changer.ChangeMode mode) {
        if (delta == null || delta.length != 1 || !(delta[0] instanceof Number newValue)) {
            throw new IllegalStateException("Unexpected delta value " + (delta != null ? Arrays.toString(delta) : "none"));
        }

        if (mode != Changer.ChangeMode.SET) { // should always be the case
            return;
        }

        if (isWidth) {
            meta.setWidth(newValue.floatValue());
        } else {
            meta.setHeight(newValue.floatValue());
        }
    }

    @Override
    protected Class<InteractionMeta> getMetaClass() {
        return InteractionMeta.class;
    }

    @Override
    public Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    protected String getPropertyName() {
        return "fake interaction " + (isWidth ? "width" : "height");
    }
}
