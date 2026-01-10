package threeadd.packetEventsSK.element.entity.expressions.prop.display;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.SkriptParser;
import me.tofaa.entitylib.meta.display.AbstractDisplayMeta;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import threeadd.packetEventsSK.element.entity.api.MetaPropertyExpression;

@SuppressWarnings("unused")
@Name("Fake Display Entity - Display Width/Height")
@Description("""
        Represents the height/width of a display entity.
        See [Display Entity Data](https://minecraft.wiki/w/Display#Entity_data) on McWiki for more details.
        """)
@Example("""
        command display <text>:
            trigger:
                set {_content} to minimessage from "<rainbow>%arg-1%"
        
                create new fake item display entity at player for players:
                    set fake display item of the fake entity to dirt
                    set fake display height of the fake entity to 11
                    set fake display width of the fake entity to 11
                    wait 2 seconds
                    kill fake entity the fake entity
        """)
@Since("1.0.0")
public class HeightOrHeightProp extends MetaPropertyExpression<AbstractDisplayMeta, Number> {

    private static final Logger log = LoggerFactory.getLogger(HeightOrHeightProp.class);

    static {
        PropertyExpression.register(HeightOrHeightProp.class, Number.class,
                "fake[ ]display[ ]:(height|width)", "fakeentity/fakeentitymeta");
    }

    @SuppressWarnings("unused")
    public HeightOrHeightProp() {
        super(Number.class, AbstractDisplayMeta.class, Changer.ChangeMode.SET);
    }

    private boolean isWidth = false;

    @Override
    protected boolean initialize(SkriptParser.ParseResult parseResult) {
        if (parseResult.hasTag("width"))
            isWidth = true;

        return true;
    }

    @Override
    protected @Nullable Number get(AbstractDisplayMeta meta) {
        if (isWidth)
            return meta.getWidth();

        return meta.getHeight();
    }

    @Override
    protected void change(AbstractDisplayMeta meta, Changer.ChangeMode mode, Object[] delta) {
        float newValue = getDeltaValue(delta, Number.class).floatValue();
        if (isWidth) {
            meta.setWidth(newValue);
            return;
        }

        meta.setHeight(newValue);
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "height of fake entity";
    }
}
