package dev.threeadd.packeteventssk.element.entity.expressions.prop.meta.display;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.github.shanebeee.skr.Registration;
import dev.threeadd.packeteventssk.api.entity.EntityMetaPropertyExpression;
import me.tofaa.entitylib.meta.EntityMeta;
import me.tofaa.entitylib.meta.display.AbstractDisplayMeta;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class ExprFakeDisplayMetaHeightOrWidth extends EntityMetaPropertyExpression<AbstractDisplayMeta, Number> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprFakeDisplayMetaHeightOrWidth.class, Number.class, "fake display (:(height|width))", "fakeentitymeta")
                .name("Fake Display Entity - Display Height/Width")
                .description("""
                        Represents the height/width of a display entity.
                        See [Display Entity Data](https://minecraft.wiki/w/Display#Entity_data) on McWiki for more details.
                        """)
                .examples("""
                        command display <text>:
                            trigger:
                                set {_content} to "<rainbow>%arg-1%"
                        
                                create new fake text display entity at player for players:
                                    set fake display text of the fake entity to {_content}
                                    set fake display billboard of the fake entity to center
                                    set fake display height of the fake entity to 11
                                    set fake display width of the fake entity to 11
                        
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
    protected Number @Nullable [] getMetaProp(Event event, AbstractDisplayMeta meta) {
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
    protected void changeMeta(Event event, AbstractDisplayMeta meta, Object @Nullable [] delta, Changer.ChangeMode mode) {
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
    protected Class<AbstractDisplayMeta> getMetaClass() {
        return AbstractDisplayMeta.class;
    }

    @Override
    public Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    protected String getPropertyName() {
        return "fake display " + (isWidth ? "width" : "height");
    }
}
