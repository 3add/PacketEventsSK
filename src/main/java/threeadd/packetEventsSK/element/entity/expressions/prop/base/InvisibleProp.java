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
@Name("Fake Entity Property - Invisible State")
@Description("If a fake entity experiencing gravity. (If they are visible by others)")
@Example("""
        command dadify <integer>:
            trigger:
                set {_entity} to fake entity with id arg-1
                if {_entity} is not set:
                    send "Couldn't find that entity"
                    stop

                set fake invisible state of {_entity} to true
        """)
@Since("1.0.0")
public class InvisibleProp extends MetaPropertyExpression<EntityMeta, Boolean> {

    static {
        PropertyExpression.register(InvisibleProp.class, Boolean.class, "fake[ ]invisible[ ][state]", "fakeentity/fakeentitymeta");
    }

    @SuppressWarnings("unused")
    public InvisibleProp() {
        super(Boolean.class, EntityMeta.class, Changer.ChangeMode.SET);
    }

    @Override
    protected @Nullable Boolean get(EntityMeta meta) {
        return meta.isInvisible();
    }

    @Override
    protected void change(EntityMeta meta, Changer.ChangeMode mode, Object[] delta) {
        boolean newState = (boolean) delta[0];
        meta.setInvisible(newState);
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "invisible of fake entity";
    }
}
