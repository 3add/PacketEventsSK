package dev.threeadd.packeteventssk.element.entity.expressions.prop.meta.display.text;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.github.shanebeee.skr.Registration;
import dev.threeadd.packeteventssk.api.entity.EntityMetaPropertyExpression;
import me.tofaa.entitylib.meta.EntityMeta;
import me.tofaa.entitylib.meta.display.TextDisplayMeta;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class ExprFakeTextDisplayMetaTextShadowed extends EntityMetaPropertyExpression<TextDisplayMeta, Boolean> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprFakeTextDisplayMetaTextShadowed.class, Boolean.class, "fake display[ ](text|content)[ ]shadowed [state]", "entitymeta")
                .name("Fake Text Display Entity - Display Text Shadowed")
                .description("""
                        Represents the shadowed state of a Text Display Entity.
                        See [Display Entity Data](https://minecraft.wiki/w/Display#Entity_data) on McWiki for more details.
                        """)
                .examples("""
                        command display <color>:
                            trigger:
                                set {_color} to arg-1
                                create new fake text display entity at player for players:
                                    set fake display text of the fake entity to "<white>Hello World"
                                    set fake display background color of the fake entity to {_color}
                                    set fake display billboard of the fake entity to center
                                    wait 2 seconds
                                    kill fake entity the fake entity
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
    protected Boolean @Nullable [] getMetaProp(Event event, TextDisplayMeta meta) {
        return new Boolean[]{meta.isShadow()};
    }

    @Override
    public Class<?> @Nullable [] acceptChange(Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET) {
            return CollectionUtils.array(Boolean.class);
        }
        return null;
    }

    @Override
    protected void changeMeta(Event event, TextDisplayMeta meta, Object @Nullable [] delta, Changer.ChangeMode mode) {
        if (delta == null || delta.length != 1 || !(delta[0] instanceof Boolean newState)) {
            throw new IllegalStateException("Unexpected delta value " + (delta != null ? Arrays.toString(delta) : "none"));
        }

        if (mode != Changer.ChangeMode.SET) { // should always be the case
            return;
        }

        meta.setShadow(newState);
    }

    @Override
    protected Class<TextDisplayMeta> getMetaClass() {
        return TextDisplayMeta.class;
    }

    @Override
    public Class<? extends Boolean> getReturnType() {
        return Boolean.class;
    }

    @Override
    protected String getPropertyName() {
        return "fake display text shadowed state";
    }
}