package threeadd.packetEventsSK.element.entity.expressions.prop.display.text;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.PropertyExpression;
import com.shanebeestudios.skbee.api.wrapper.ComponentWrapper;
import me.tofaa.entitylib.meta.display.TextDisplayMeta;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import threeadd.packetEventsSK.PacketEventsSK;
import threeadd.packetEventsSK.element.entity.api.MetaPropertyExpression;

@SuppressWarnings("unused")
@Name("Fake Text Display Entity - Display Text")
@Description("""
        Represents the display text of a Text Display Entity.
        See [Display Entity Data](https://minecraft.wiki/w/Display#Entity_data) on McWiki for more details.
        """)
@Example("""
        command display <text>:
            trigger:
                set {_content} to minimessage from "<rainbow>%arg-1%"
        
                create new fake text display entity at player for players:
                    set fake display text of the fake entity to {_content}
                    set fake display billboard of the fake entity to center
                    wait 2 seconds
                    kill fake entity the fake entity
        """)
public class TextProp extends MetaPropertyExpression<TextDisplayMeta, ComponentWrapper> {

    static {
        if (PacketEventsSK.getLoader().HAS_SKBEE_COMPONENT)
            PropertyExpression.register(TextProp.class, ComponentWrapper.class,
                    "fake[ ]display[ ](text|content)", "fakeentity/fakeentitymeta");
    }

    @SuppressWarnings("unused")
    public TextProp() {
        super(ComponentWrapper.class, TextDisplayMeta.class, Changer.ChangeMode.SET);
    }

    @Override
    protected @Nullable ComponentWrapper get(TextDisplayMeta meta) {
        return ComponentWrapper.fromComponent(meta.getText());
    }

    @Override
    protected void change(TextDisplayMeta meta, Changer.ChangeMode mode, Object[] delta) {

        Object rawComponent = delta[0];
        ComponentWrapper wrapper;

        if (rawComponent instanceof ComponentWrapper componentWrapper) {
            wrapper = componentWrapper;
        } else if (rawComponent instanceof String text) {
            wrapper = ComponentWrapper.fromText(text);
        } else throw new IllegalStateException();

        meta.setText(wrapper.getComponent());
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "text of fake entity";
    }
}
