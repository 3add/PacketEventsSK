package threeadd.packetEventsSK.element.entity.expressions.prop.display.text;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.util.Color;
import ch.njol.skript.util.ColorRGB;
import me.tofaa.entitylib.meta.display.TextDisplayMeta;
import org.jetbrains.annotations.Nullable;
import threeadd.packetEventsSK.element.entity.api.MetaPropertyExpression;

// TODO docs
@SuppressWarnings("unused")
public class BackgroundColorProp extends MetaPropertyExpression<TextDisplayMeta, Color> {

    static {
        PropertyExpression.register(BackgroundColorProp.class, Color.class,
                "fake[ ]display[ ]background[ ]color", "fakeentity/fakeentitymeta");
    }

    public BackgroundColorProp() {
        super(Color.class, TextDisplayMeta.class, Changer.ChangeMode.SET);
    }

    @Override
    protected @Nullable Color get(TextDisplayMeta meta) {
        return ColorRGB.fromBukkitColor(org.bukkit.Color.fromARGB(meta.getBackgroundColor()));
    }

    @Override
    protected void change(TextDisplayMeta meta, Changer.ChangeMode mode, Object[] delta) {
        Color newColor = getDeltaValue(delta, Color.class);
        meta.setBackgroundColor(newColor.asARGB());
    }
}
