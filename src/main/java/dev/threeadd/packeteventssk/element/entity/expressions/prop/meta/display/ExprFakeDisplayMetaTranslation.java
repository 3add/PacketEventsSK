package dev.threeadd.packeteventssk.element.entity.expressions.prop.meta.display;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.github.shanebeee.skr.Registration;
import dev.threeadd.packeteventssk.api.entity.EntityMetaPropertyExpression;
import dev.threeadd.packeteventssk.api.util.ConversionUtil;
import me.tofaa.entitylib.meta.EntityMeta;
import me.tofaa.entitylib.meta.display.AbstractDisplayMeta;
import org.bukkit.event.Event;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class ExprFakeDisplayMetaTranslation extends EntityMetaPropertyExpression<AbstractDisplayMeta, Vector> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprFakeDisplayMetaTranslation.class, Vector.class, "fake display translation", "fakeentitymeta")
                .name("Fake Display Entity - Translation")
                .description("""
                        Represents the translation of a Display Entity.
                        See [Display Entity Data](https://minecraft.wiki/w/Display#Entity_data) on McWiki for more details.
                        """)
                .examples("""
                        command display:
                            trigger:
                                create new fake item display entity at player for players:
                                    set fake display item of the fake entity to dirt
                                    set {_e} to the fake entity
                        
                                set fake display transform interpolation duration of {_e} to 0.5 seconds
                                set fake display interpolation delay of {_e} to 0 seconds
                                loop 5 times:
                                    set fake display translation of {_e} to vector(0, loop-value, 0)
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
    protected Vector @Nullable [] getMetaProp(Event event, AbstractDisplayMeta meta) {
        return new Vector[]{ConversionUtil.toBukkitVector(meta.getTranslation())};
    }

    @Override
    public Class<?> @Nullable [] acceptChange(Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET) {
            return CollectionUtils.array(Vector.class);
        }
        return null;
    }

    @Override
    protected void changeMeta(Event event, AbstractDisplayMeta meta, Object @Nullable [] delta, Changer.ChangeMode mode) {
        if (delta == null || delta.length != 1 || !(delta[0] instanceof Vector newValue)) {
            throw new IllegalStateException("Unexpected delta value " + (delta != null ? Arrays.toString(delta) : "none"));
        }

        if (mode != Changer.ChangeMode.SET) {
            return;
        }

        meta.setTranslation(ConversionUtil.toPeVectorF(newValue));
    }

    @Override
    protected Class<AbstractDisplayMeta> getMetaClass() {
        return AbstractDisplayMeta.class;
    }

    @Override
    public Class<? extends Vector> getReturnType() {
        return Vector.class;
    }

    @Override
    protected String getPropertyName() {
        return "fake display translation";
    }
}
