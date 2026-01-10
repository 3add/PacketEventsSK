package threeadd.packetEventsSK.element.entity.expressions.prop.display.text;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.PropertyExpression;
import me.tofaa.entitylib.meta.display.TextDisplayMeta;
import org.jetbrains.annotations.Nullable;
import threeadd.packetEventsSK.element.entity.api.MetaPropertyExpression;

@SuppressWarnings("unused")
@Name("Fake Text Display Entity - Display Text Shadowed")
@Description("""
        Represents the shadowed state of a Text Display Entity.
        See [Display Entity Data](https://minecraft.wiki/w/Display#Entity_data) on McWiki for more details.
        """)
@Example("""
        command display <text>:
            trigger:
                set {_content} to minimessage from "<rainbow>%arg-1%"

                create new fake text display entity at player for players:
                    set fake display text of the fake entity to {_content}
                    set fake display text shadowed of the fake entity to true
                    set fake display billboard of the fake entity to center
                    wait 10 seconds
                    kill fake entity the fake entity
        """)
public class TextShadowedProp extends MetaPropertyExpression<TextDisplayMeta, Boolean> {

    static {
        PropertyExpression.register(TextShadowedProp.class, Boolean.class,
                "fake[ ]display[ ](text|content)[ ]shadowed", "fakeentity/fakeentitymeta");
    }

    public TextShadowedProp() {
        super(Boolean.class, TextDisplayMeta.class, Changer.ChangeMode.SET);
    }

    @Override
    protected @Nullable Boolean get(TextDisplayMeta meta) {
        return meta.isShadow();
    }

    @Override
    protected void change(TextDisplayMeta meta, Changer.ChangeMode mode, Object[] delta) {
        boolean newValue = getDeltaValue(delta, Boolean.class);
        meta.setShadow(newValue);
    }
}
