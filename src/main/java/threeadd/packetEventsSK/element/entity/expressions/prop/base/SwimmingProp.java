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
@Name("Fake Entity Property - Swimming State")
@Description("If the entity is swimming.")
@Example("""
        command swimOrDie <integer>:
            trigger:
                set {_entity} to fake entity with id arg-1
                if {_entity} is not set:
                    send "Couldn't find that entity"
                    stop
        
                set fake swimming state of {_entity} to true
        """)
@Since("1.0.0")
public class SwimmingProp extends MetaPropertyExpression<EntityMeta, Boolean> {

    static {
        PropertyExpression.register(SwimmingProp.class, Boolean.class, "fake[ ]swimming[ ][state]", "fakeentity/fakeentitymeta");
    }

    @SuppressWarnings("unused")
    public SwimmingProp() {
        super(Boolean.class, EntityMeta.class, Changer.ChangeMode.SET);
    }

    @Override
    protected @Nullable Boolean get(EntityMeta meta) {
        return meta.isSwimming();
    }

    @Override
    protected void change(EntityMeta meta, Changer.ChangeMode mode, Object[] delta) {
        boolean newState = (boolean) delta[0];
        meta.setSwimming(newState);
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "swimming of fake entity";
    }
}
