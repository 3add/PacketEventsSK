package threeadd.packetEventsSK.element.entity.expressions.prop.display;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.SkriptParser;
import com.github.retrooper.packetevents.util.Quaternion4f;
import me.tofaa.entitylib.meta.display.AbstractDisplayMeta;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import threeadd.packetEventsSK.element.entity.api.MetaPropertyExpression;
import threeadd.packetEventsSK.util.ConversionUtil;

@SuppressWarnings("unused")
@Name("Fake Display Entity - Display Rotation")
@Description("""
        Represents the transformation left/right rotation of a Display Entity.
        See [Display Entity Data](https://minecraft.wiki/w/Display#Entity_data) on McWiki for more details.
        """)
@Example("""
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
@Since("1.0.0")
public class RotationProp extends MetaPropertyExpression<AbstractDisplayMeta, Quaternionf> {

    static {
        PropertyExpression.register(RotationProp.class, Quaternionf.class,
                "fake[ ]display[ ]:(left|right)[ ]rotation", "fakeentity/fakeentitymeta");
    }

    public RotationProp() {
        super(Quaternionf.class, AbstractDisplayMeta.class, Changer.ChangeMode.SET);
    }

    private boolean isRight = false;

    @Override
    protected boolean initialize(SkriptParser.ParseResult parseResult) {
        if (parseResult.hasTag("right"))
            isRight = true;

        return true;
    }

    @Override
    protected @Nullable Quaternionf get(AbstractDisplayMeta meta) {
        Quaternion4f rotation;

        if (isRight) {
            rotation = meta.getRightRotation();
        } else {
            rotation = meta.getLeftRotation();
        }

        return ConversionUtil.toBukkitQuaternionf(rotation);
    }

    @Override
    protected void change(AbstractDisplayMeta meta, Changer.ChangeMode mode, Object[] delta) {

        Quaternionf newValue = getDeltaValue(delta, Quaternionf.class);

        // The helper methods were not correct
        if (isRight)
            meta.setRightRotation(ConversionUtil.toPeQuaternion4f(newValue));
        else
            meta.setLeftRotation(ConversionUtil.toPeQuaternion4f(newValue));
    }
}
