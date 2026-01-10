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
@Name("Fake Display Entity - Translation")
@Description("""
        Represents the translation of a Display Entity.
        See [Display Entity Data](https://minecraft.wiki/w/Display#Entity_data) on McWiki for more details.
        """)
@Example("""
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
@Since("1.0.0")
public class TranslationProp extends MetaPropertyExpression<AbstractDisplayMeta, Vector> {

    static {
        PropertyExpression.register(TranslationProp.class, Vector.class,
                "fake[ ]display[ ]translation", "fakeentity/fakeentitymeta");
    }

    public TranslationProp() {
        super(Vector.class, AbstractDisplayMeta.class, Changer.ChangeMode.SET);
    }

    @Override
    protected @Nullable Vector get(AbstractDisplayMeta meta) {
        return ConversionUtil.toBukkitVector(meta.getTranslation());
    }

    @Override
    protected void change(AbstractDisplayMeta meta, Changer.ChangeMode mode, Object[] delta) {

        Vector newValue = getDeltaValue(delta, Vector.class);
        meta.setTranslation(ConversionUtil.toPeVectorF(newValue));
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "display translation of fake entity";
    }
}
