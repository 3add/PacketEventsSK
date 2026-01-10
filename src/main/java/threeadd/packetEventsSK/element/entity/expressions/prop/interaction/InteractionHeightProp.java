package threeadd.packetEventsSK.element.entity.expressions.prop.interaction;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.SkriptParser;
import me.tofaa.entitylib.meta.other.InteractionMeta;
import org.jetbrains.annotations.Nullable;
import threeadd.packetEventsSK.element.entity.api.MetaPropertyExpression;

@SuppressWarnings("unused")
@Name("Fake Interaction Entity - Interaction Width/Height")
@Description("Represents the width or height of an interaction entity.")
@Example("""
        command interaction:
            trigger:
                create new fake interaction entity at player for players:
                    set fake interaction width of the fake entity to 2
                    set fake interaction height of the fake entity to 2
                    add fake entity id of the fake entity to {-id::*}
        
                    wait 10 seconds
                    kill fake entity the fake entity
        on interact entity receive netty processed:
            if {-id::*} doesn't contain packet entity id of event-packet:
                stop
        
            cancel packet
            send "You can't touch him!"
        """)
@Since("1.0.0")
public class InteractionHeightProp extends MetaPropertyExpression<InteractionMeta, Number> {

    static {
        PropertyExpression.register(InteractionHeightProp.class, Number.class, "fake[ ]interaction[ ]:(height|width)", "fakeentity");
    }

    public InteractionHeightProp() {
        super(Number.class, InteractionMeta.class, Changer.ChangeMode.SET);
    }

    private boolean isWidth = false;

    @Override
    protected boolean initialize(SkriptParser.ParseResult parseResult) {

        if (parseResult.hasTag("width"))
            isWidth = true;

        return true;
    }

    @Override
    protected @Nullable Number get(InteractionMeta meta) {
        if (isWidth)
            return meta.getWidth();

        return meta.getHeight();
    }

    @Override
    protected void change(InteractionMeta meta, Changer.ChangeMode mode, Object[] delta) {

        Number newValue = getDeltaValue(delta, 0, Number.class);

        if (isWidth) {
            meta.setWidth(newValue.floatValue());
            return;
        }

        meta.setHeight(newValue.floatValue());
    }
}
