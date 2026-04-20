package threeadd.packetEventsSK.element.entity.expressions.prop.display;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import me.tofaa.entitylib.meta.display.AbstractDisplayMeta;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;
import threeadd.packetEventsSK.element.entity.api.MetaPropertyExpression;

@Name("Fake Display Entity - View Range")
@Description("""
        Represents the view range of a Display Entity.
        See [Display Entity Data](https://minecraft.wiki/w/Display#Entity_data) on McWiki for more details.
        """)
@Example("""
        command display <text>:
            trigger:
                create new fake text display entity at player for players:
                    set fake display text of the fake entity to "Hello <rainbow>World"
                    set fake display view range of the fake entity to 10
                    wait 2 seconds
                    kill fake entity the fake entity
        """)
@SuppressWarnings("unused")
public class ViewRangeProp extends MetaPropertyExpression<AbstractDisplayMeta, Number> {

    public static void register(SyntaxRegistry registry) {
        registry.register(
                SyntaxRegistry.EXPRESSION,
                SyntaxInfo.Expression.builder(ViewRangeProp.class, Number.class)
                        .addPatterns(
                                "[the] fake display view[ ]range of %fakeentity%",
                                "%fakeentity%'s fake display view[ ]range"
                        )
                        .build()
        );
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
