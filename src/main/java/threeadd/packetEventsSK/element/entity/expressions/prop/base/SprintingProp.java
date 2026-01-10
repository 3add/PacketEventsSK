package threeadd.packetEventsSK.element.entity.expressions.prop.base;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.PropertyExpression;
import me.tofaa.entitylib.meta.EntityMeta;
import me.tofaa.entitylib.wrapper.WrapperEntity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import threeadd.packetEventsSK.element.entity.api.MetaPropertyExpression;

@SuppressWarnings("unused")
@Name("Fake Entity Property - Sprinting State")
@Description("If the entity is sprinting.")
@Example("""
        command sprintify <integer>:
            trigger:
                set {_entity} to fake entity with id arg-1
                if {_entity} is not set:
                    send "Couldn't find that entity"
                    stop
        
                set fake sprinting state of {_entity} to true
        """)
public class SprintingProp extends MetaPropertyExpression<EntityMeta, Boolean> {

    static {
        PropertyExpression.register(SprintingProp.class, Boolean.class, "fake[ ]sprinting[ ][state]", "fakeentity/fakeentitymeta");
    }

    @SuppressWarnings("unused")
    public SprintingProp() {
        super(Boolean.class, EntityMeta.class, Changer.ChangeMode.SET);
    }

    @Override
    protected @Nullable Boolean get(EntityMeta meta) {
        return meta.isSprinting();
    }

    @Override
    protected void change(EntityMeta meta, Changer.ChangeMode mode, Object[] delta) {
        boolean newState = (boolean) delta[0];
        meta.setSprinting(newState);
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "sprinting of fake entity";
    }
}
