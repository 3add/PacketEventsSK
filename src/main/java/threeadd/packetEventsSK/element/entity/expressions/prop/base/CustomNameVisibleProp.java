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
        command spawnRichCow <text>:
            trigger:
                set {_name} to mini message from arg-1

                spawn new fake cow at player for player:
                    set fake custom name of the fake entity to {_name}
                    set fake custom name visible state of the fake entity to true
        """)
public class CustomNameVisibleProp extends MetaPropertyExpression<EntityMeta, Boolean> {

    static {
        PropertyExpression.register(CustomNameVisibleProp.class, Boolean.class, "fake[ ]custom[ ]name[ ]visible[ ][state]", "fakeentity/fakeentitymeta");
    }

    @SuppressWarnings("unused")
    public CustomNameVisibleProp() {
        super(Boolean.class, EntityMeta.class, Changer.ChangeMode.SET);
    }

    @Override
    protected @Nullable Boolean get(EntityMeta meta) {
        return meta.isCustomNameVisible();
    }

    @Override
    protected void change(EntityMeta meta, Changer.ChangeMode mode, Object[] delta) {
        boolean newState = (boolean) delta[0];
        meta.setCustomNameVisible(newState);
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "custom name visible of fake entity";
    }
}
