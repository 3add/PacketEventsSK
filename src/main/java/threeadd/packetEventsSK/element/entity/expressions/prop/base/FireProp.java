package threeadd.packetEventsSK.element.entity.expressions.prop.base;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.PropertyExpression;
import me.tofaa.entitylib.meta.EntityMeta;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import threeadd.packetEventsSK.element.entity.api.MetaPropertyExpression;

@SuppressWarnings("unused")
@Name("Fake Entity Property - On Fire State")
@Description("If the entity is on fire. (actively burning)")
@Example("""
        command spawnVForVendetta:
            trigger:
                spawn new fake player at player for player:
                    set fake skin of the fake entity to player's skin
                    set fake fire state of the fake entity to true
        """)
public class FireProp extends MetaPropertyExpression<EntityMeta, Boolean> {

    static {
        PropertyExpression.register(FireProp.class, Boolean.class, "fake[ ][on][ ]fire[ ][state]", "fakeentity/fakeentitymeta");
    }

    @SuppressWarnings("unused")
    public FireProp() {
        super(Boolean.class, EntityMeta.class, Changer.ChangeMode.SET);
    }

    @Override
    protected @Nullable Boolean get(EntityMeta meta) {
        return meta.isOnFire();
    }

    @Override
    protected void change(EntityMeta meta, Changer.ChangeMode mode, Object[] delta) {
        boolean newState = (boolean) delta[0];
        meta.setOnFire(newState);
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "fire of fake entity";
    }
}
