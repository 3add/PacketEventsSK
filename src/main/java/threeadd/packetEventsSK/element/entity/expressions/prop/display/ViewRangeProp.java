package threeadd.packetEventsSK.element.entity.expressions.prop.display;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.expressions.base.PropertyExpression;
import me.tofaa.entitylib.meta.display.AbstractDisplayMeta;
import org.jetbrains.annotations.Nullable;
import threeadd.packetEventsSK.element.entity.api.MetaPropertyExpression;

// TODO docs
@SuppressWarnings("unused")
public class ViewRangeProp extends MetaPropertyExpression<AbstractDisplayMeta, Number> {

    static {
        PropertyExpression.register(ViewRangeProp.class, Number.class,
                "fake[ ]display[ ]view[ ]range", "fakeentity/fakeentitymeta");
    }

    public ViewRangeProp() {
        super(Number.class, AbstractDisplayMeta.class, Changer.ChangeMode.SET);
    }

    @Override
    protected @Nullable Number get(AbstractDisplayMeta meta) {
        return meta.getViewRange();
    }

    @Override
    protected void change(AbstractDisplayMeta meta, Changer.ChangeMode mode, Object[] delta) {
        float newRange = getDeltaValue(delta, Number.class).floatValue();
        meta.setViewRange(newRange);
    }
}
