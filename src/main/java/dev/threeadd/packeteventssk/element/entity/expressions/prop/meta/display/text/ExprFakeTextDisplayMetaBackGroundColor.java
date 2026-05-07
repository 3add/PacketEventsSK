package dev.threeadd.packeteventssk.element.entity.expressions.prop.meta.display.text;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.Color;
import ch.njol.skript.util.ColorRGB;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.github.shanebeee.skr.Registration;
import dev.threeadd.packeteventssk.api.entity.EntityMetaPropertyExpression;
import me.tofaa.entitylib.meta.EntityMeta;
import me.tofaa.entitylib.meta.display.TextDisplayMeta;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class ExprFakeTextDisplayMetaBackGroundColor extends EntityMetaPropertyExpression<TextDisplayMeta, Color> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprFakeTextDisplayMetaBackGroundColor.class, Color.class, "fake display[ ]background[ ]color", "fakeentitymeta")
                .name("Fake Text Display Entity - Background Color")
                .description("""
                        Represents the background color of a Text Display Entity.
                        See [Display Entity Data](https://minecraft.wiki/w/Display#Entity_data ) on McWiki for more details.
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
    protected Color @Nullable [] getMetaProp(Event event, TextDisplayMeta meta) {
        return new ColorRGB[]{ColorRGB.fromBukkitColor(org.bukkit.Color.fromARGB(meta.getBackgroundColor()))};
    }

    @Override
    public Class<?> @Nullable [] acceptChange(Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET) {
            return CollectionUtils.array(Color.class);
        }
        return null;
    }

    @Override
    protected void changeMeta(Event event, TextDisplayMeta meta, Object @Nullable [] delta, Changer.ChangeMode mode) {
        if (delta == null || delta.length != 1 || !(delta[0] instanceof Color newColor)) {
            throw new IllegalStateException("Unexpected delta value " + (delta != null ? Arrays.toString(delta) : "none"));
        }

        if (mode != Changer.ChangeMode.SET) { // should always be the case
            return;
        }

        meta.setBackgroundColor(newColor.asARGB());
    }

    @Override
    protected Class<TextDisplayMeta> getMetaClass() {
        return TextDisplayMeta.class;
    }

    @Override
    public Class<? extends Color> getReturnType() {
        return Color.class;
    }

    @Override
    protected String getPropertyName() {
        return "fake display background color";
    }
}