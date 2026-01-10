package threeadd.packetEventsSK.element.entity.expressions.prop.base;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import me.tofaa.entitylib.meta.EntityMeta;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import threeadd.packetEventsSK.element.entity.api.MetaPropertyExpression;

@SuppressWarnings("unused")
@Name("Fake Entity Property - Gravity State")
@Description("If a fake entity experiencing gravity. (If they are capable of falling)")
@Example("""
        command flyingItem <itemtype>:
            trigger:
                spawn new fake dropped item at player for players:
                    set fake item stack of fake entity to 1 of arg-1
                    set fake gravity state of fake entity to false
        """)
@Since("1.0.0")
public class GravityProp extends MetaPropertyExpression<EntityMeta, Boolean> {

    static {
        PropertyExpression.register(GravityProp.class, Boolean.class, "fake[ ]gravity[ ][state]", "fakeentity/fakeentitymeta");
    }

    @SuppressWarnings("unused")
    public GravityProp() {
        super(Boolean.class, EntityMeta.class, Changer.ChangeMode.SET);
    }

    @Override
    protected @Nullable Boolean get(EntityMeta meta) {
        return !meta.hasNoGravity();
    }

    @Override
    protected void change(EntityMeta meta, Changer.ChangeMode mode, Object[] delta) {
        boolean newState = (boolean) delta[0];
        meta.setHasNoGravity(!newState);
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "gravity of fake entity";
    }
}
