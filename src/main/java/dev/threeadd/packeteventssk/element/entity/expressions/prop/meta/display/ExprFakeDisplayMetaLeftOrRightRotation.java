package dev.threeadd.packeteventssk.element.entity.expressions.prop.meta.display;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.github.retrooper.packetevents.util.Quaternion4f;
import com.github.shanebeee.skr.Registration;
import dev.threeadd.packeteventssk.api.entity.EntityMetaPropertyExpression;
import dev.threeadd.packeteventssk.api.util.ConversionUtil;
import me.tofaa.entitylib.meta.EntityMeta;
import me.tofaa.entitylib.meta.display.AbstractDisplayMeta;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

import java.util.Arrays;

public class ExprFakeDisplayMetaLeftOrRightRotation extends EntityMetaPropertyExpression<AbstractDisplayMeta, Quaternionf> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprFakeDisplayMetaLeftOrRightRotation.class, Quaternionf.class, "fake display (:(left|right))[ ]rotation", "entitymeta")
                .name("Fake Display Entity - Display Rotation")
                .description("""
                        Represents the transformation left/right rotation of a Display Entity.
                        See [Display Entity Data](https://minecraft.wiki/w/Display#Entity_data) on McWiki for more details.
                        """)
                .examples("""
                        command display:
                            trigger:
                                create new fake item display entity at player for players:
                                    set fake display item of the fake entity to dirt
                                    set {_e} to the fake entity
                        
                                set fake display transform interpolation duration of {_e} to 1 seconds
                                set fake display interpolation delay of {_e} to 0 seconds
                                loop 5 times:
                                    set fake display left rotation of {_e} to quaternion(loop-value / 5, 1, 1, 1)
                                    wait 1 seconds
                        
                                kill fake entity {_e}
                        """)
                .since("1.0.0")
                .register();
    }

    private boolean isRight = false;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        setExpr((Expression<? extends EntityMeta>) expressions[0]);
        if (parseResult.hasTag("right")) {
            isRight = true;
        }
        return true;
    }

    @Override
    protected Quaternionf @Nullable [] getMetaProp(Event event, AbstractDisplayMeta meta) {
        Quaternion4f rotation = isRight ? meta.getRightRotation() : meta.getLeftRotation();
        return new Quaternionf[]{ConversionUtil.toBukkitQuaternionf(rotation)};
    }

    @Override
    public Class<?> @Nullable [] acceptChange(Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET) {
            return CollectionUtils.array(Quaternionf.class);
        }
        return null;
    }

    @Override
    protected void changeMeta(Event event, AbstractDisplayMeta meta, Object @Nullable [] delta, Changer.ChangeMode mode) {
        if (delta == null || delta.length != 1 || !(delta[0] instanceof Quaternionf newValue)) {
            throw new IllegalStateException("Unexpected delta value " + (delta != null ? Arrays.toString(delta) : "none"));
        }

        if (mode != Changer.ChangeMode.SET) {
            return;
        }

        if (isRight) {
            meta.setRightRotation(ConversionUtil.toPeQuaternion4f(newValue));
        } else {
            meta.setLeftRotation(ConversionUtil.toPeQuaternion4f(newValue));
        }
    }

    @Override
    protected Class<AbstractDisplayMeta> getMetaClass() {
        return AbstractDisplayMeta.class;
    }

    @Override
    public Class<? extends Quaternionf> getReturnType() {
        return Quaternionf.class;
    }

    @Override
    protected String getPropertyName() {
        return "fake display " + (isRight ? "right" : "left") + " rotation";
    }
}