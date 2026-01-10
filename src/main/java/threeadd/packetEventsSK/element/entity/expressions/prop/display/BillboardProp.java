package threeadd.packetEventsSK.element.entity.expressions.prop.display;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import me.tofaa.entitylib.meta.display.AbstractDisplayMeta;
import me.tofaa.entitylib.meta.display.AbstractDisplayMeta.BillboardConstraints;
import org.bukkit.entity.Display;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import threeadd.packetEventsSK.element.entity.api.MetaPropertyExpression;

@SuppressWarnings("unused")
@Name("Fake Display Entity - Display Billboard")
@Description("""
        Represents the billboard of a Display Entity.
        See [Display Entity Data](https://minecraft.wiki/w/Display#Entity_data) on McWiki for more details.
        """)
@Example("""
        command display <text>:
            trigger:
                set {_content} to minimessage from "<rainbow>%arg-1%"
        
                create new fake text display entity at player for players:
                    set fake display text of the fake entity to {_content}
                    set fake display billboard of the fake entity to center
                    wait 2 seconds
                    kill fake entity the fake entity
        """)
@Since("1.0.0")
public class BillboardProp extends MetaPropertyExpression<AbstractDisplayMeta, Display.Billboard> {

    static {
        PropertyExpression.register(BillboardProp.class, Display.Billboard.class,
                "fake[ ]display[ ]billboard", "fakeentity/fakeentitymeta");
    }

    @SuppressWarnings("unused")
    public BillboardProp() {
        super(Display.Billboard.class, AbstractDisplayMeta.class, Changer.ChangeMode.SET);
    }

    @Override
    protected @Nullable Display.Billboard get(AbstractDisplayMeta meta) {
        return fromPE(meta.getBillboardConstraints());
    }

    @Override
    protected void change(AbstractDisplayMeta meta, Changer.ChangeMode mode, Object[] delta) {
        Display.Billboard newBillboard = (Display.Billboard) delta[0];
        meta.setBillboardConstraints(fromBukkit(newBillboard));
    }

    private static Display.Billboard fromPE(BillboardConstraints constraints) {
        return Display.Billboard.valueOf(constraints.name());
    }

    private static BillboardConstraints fromBukkit(Display.Billboard billboard) {
        return BillboardConstraints.valueOf(billboard.name());
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "billboard of fake entity";
    }
}
