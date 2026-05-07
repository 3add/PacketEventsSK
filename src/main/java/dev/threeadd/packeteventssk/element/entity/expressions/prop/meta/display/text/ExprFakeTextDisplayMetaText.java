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
import net.kyori.adventure.text.Component;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class ExprFakeTextDisplayMetaText extends EntityMetaPropertyExpression<TextDisplayMeta, Component> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprFakeTextDisplayMetaText.class, Component.class, "fake display (text|content)", "fakeentitymeta")
                .name("Fake Text Display Entity - Display Text")
                .description("""
                        Represents the display text of a Text Display Entity.
                        Supports MiniMessage formatting.
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
    protected Component @Nullable [] getMetaProp(Event event, TextDisplayMeta meta) {
        return new Component[]{meta.getText()};
    }

    @Override
    public Class<?> @Nullable [] acceptChange(Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET) {
            return CollectionUtils.array(Component.class);
        }
        return null;
    }

    @Override
    protected void changeMeta(Event event, TextDisplayMeta meta, Object @Nullable [] delta, Changer.ChangeMode mode) {
        if (delta == null || delta.length != 1 || !(delta[0] instanceof Component newContent)) {
            throw new IllegalStateException("Unexpected delta value " + (delta != null ? Arrays.toString(delta) : "none"));
        }

        if (mode != Changer.ChangeMode.SET) { // should always be the case
            return;
        }

        meta.setText(newContent);
    }

    @Override
    protected Class<TextDisplayMeta> getMetaClass() {
        return TextDisplayMeta.class;
    }

    @Override
    public Class<? extends Component> getReturnType() {
        return Component.class;
    }

    @Override
    protected String getPropertyName() {
        return "fake display text";
    }
}