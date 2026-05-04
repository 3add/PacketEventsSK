package dev.threeadd.packeteventssk.element.entity.expressions.prop.display.text;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import me.tofaa.entitylib.meta.display.TextDisplayMeta;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;
import dev.threeadd.packeteventssk.api.entity.MetaPropertyExpression;

@SuppressWarnings("unused")
@Name("Fake Text Display Entity - Display Text Line Width")
@Description("""
        Represents the line width state of a Text Display Entity.
        See [Display Entity Data](https://minecraft.wiki/w/Display#Entity_data) on McWiki for more details.
        """)
@Example("""
        command display <text>:
            trigger:
                create new fake text display entity at player for players:
                    set fake display text of the fake entity to "Hello <rainbow>World"
                    set fake display text line width of the fake entity to 5
                    wait 2 seconds
                    kill fake entity the fake entity
        """)
@Since("Insert Version")
public class TextLineWidthProp extends MetaPropertyExpression<TextDisplayMeta, Integer> {

    public static void register(SyntaxRegistry registry) {
        registry.register(
                SyntaxRegistry.EXPRESSION,
                SyntaxInfo.Expression.builder(TextLineWidthProp.class, Integer.class)
                        .addPatterns(
                                "[the] fake display[ ](text|content)[ ]line width of %fakeentity%",
                                "%fakeentity%'s fake display[ ](text|content)[ ]line width"
                        )
                        .build()
        );
    }

    @SuppressWarnings("unused")
    public TextLineWidthProp() {
        super(Integer.class, TextDisplayMeta.class, Changer.ChangeMode.SET);
    }

    @Override
    protected @Nullable Integer get(TextDisplayMeta meta) {
        return meta.getLineWidth();
    }

    @Override
    protected void change(TextDisplayMeta meta, Changer.ChangeMode mode, Object[] delta) {
        int newValue = getDeltaValue(delta, Integer.class);
        meta.setLineWidth(newValue);
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "text line width of fake entity";
    }
}
