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
@Name("Fake Entity Property - Sneaking State")
@Description("If the entity is sneaking. (Sneaking is mainly just it's nametag becoming grayer)")
@Example("""
        command sneakify <integer>:
            trigger:
                set {_entity} to fake entity with id arg-1
                if {_entity} is not set:
                    send "Couldn't find that entity"
                    stop
        
                set fake sneaking state of {_entity} to true
        """)
@Since("1.0.0")
public class SneakingProp extends MetaPropertyExpression<EntityMeta, Boolean> {

    static {
        PropertyExpression.register(SneakingProp.class, Boolean.class, "fake[ ]sneaking[ ][state]", "fakeentity/fakeentitymeta");
    }

    @SuppressWarnings("unused")
    public SneakingProp() {
        super(Boolean.class, EntityMeta.class, Changer.ChangeMode.SET);
    }

    @Override
    protected @Nullable Boolean get(EntityMeta meta) {
        return meta.isSneaking();
    }

    @Override
    protected void change(EntityMeta meta, Changer.ChangeMode mode, Object[] delta) {
        boolean newState = getDeltaValue(delta, Boolean.class);
        meta.setSneaking(newState);
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "sneaking of fake entity";
    }
}
