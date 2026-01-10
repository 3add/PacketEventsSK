package threeadd.packetEventsSK.element.entity.expressions.prop.display;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import me.tofaa.entitylib.meta.display.AbstractDisplayMeta;
import org.bukkit.event.Event;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;
import threeadd.packetEventsSK.element.entity.api.MetaPropertyExpression;
import threeadd.packetEventsSK.util.ConversionUtil;

@SuppressWarnings("unused")
@Name("Fake Display Entity - Display Scale")
@Description("""
        Represents the transformation scale of a Display Entity.
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
                    set fake display scale of {_e} to vector(1, loop-value, 1)
                    wait 1 seconds
        
                kill fake entity {_e}
        """)
@Since("1.0.0")
public class ScaleProp extends MetaPropertyExpression<AbstractDisplayMeta, Vector> {

    static {
        PropertyExpression.register(ScaleProp.class, Vector.class,
                "fake[ ]display[ ]scale", "fakeentity/fakeentitymeta");
    }

    @SuppressWarnings("unused")
    public ScaleProp() {
        super(Vector.class, AbstractDisplayMeta.class, Changer.ChangeMode.SET);
    }

    @Override
    protected @Nullable Vector get(AbstractDisplayMeta meta) {
        return ConversionUtil.toBukkitVector(meta.getScale());
    }

    @Override
    protected void change(AbstractDisplayMeta meta, Changer.ChangeMode mode, Object[] delta) {
        Vector vector = getDeltaValue(delta, Vector.class);

        meta.setScale(ConversionUtil.toPeVectorF(vector));
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "scale of fake entity";
    }
}
