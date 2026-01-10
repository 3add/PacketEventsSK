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
@Name("Fake Entity Property - Custom Name Visible State")
@Description("If the custom name of a fake entity is visible by other entities.")
@Example("""
        command spawnFlyingDutchMan:
            trigger:
                spawn new fake player at player for player:
                    set fake skin of the fake entity to player's skin
                    set fake elytra flying state of the fake entity to true
        """)
public class ElytraProp extends MetaPropertyExpression<EntityMeta, Boolean> {

    static {
        PropertyExpression.register(ElytraProp.class, Boolean.class, "fake[ ]elytra[ ][flying][ ][state]", "fakeentity/fakeentitymeta");
    }

    @SuppressWarnings("unused")
    public ElytraProp() {
        super(Boolean.class, EntityMeta.class, Changer.ChangeMode.SET);
    }

    @Override
    protected @Nullable Boolean get(EntityMeta meta) {
        return meta.isFlyingWithElytra();
    }

    @Override
    protected void change(EntityMeta meta, Changer.ChangeMode mode, Object[] delta) {
        boolean newState = (boolean) delta[0];
        meta.setFlyingWithElytra(newState);
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "elytra of fake entity";
    }
}
