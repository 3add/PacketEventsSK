package dev.threeadd.packeteventssk.element.entity.expressions.prop.display.text;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import me.tofaa.entitylib.meta.display.TextDisplayMeta;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;
import dev.threeadd.packeteventssk.api.entity.MetaPropertyExpression;

@SuppressWarnings("unused")
@Name("Fake Text Display Entity - Display Text See Through")
@Description("""
        Represents the see through state of a Text Display Entity.
        See [Display Entity Data](https://minecraft.wiki/w/Display#Entity_data) on McWiki for more details.
        """)
@Example("""
        command display <text>:
            trigger:
                create new fake text display entity at player for players:
                    set fake display text of the fake entity to "Hello <rainbow>World"
                    set fake display text see through state of the fake entity to true
                    wait 2 seconds
                    kill fake entity the fake entity
        """)
@Since("1.0.0")
public class TextSeeThroughProp extends MetaPropertyExpression<TextDisplayMeta, Boolean> {

    public static void register(SyntaxRegistry registry) {
        registry.register(
                SyntaxRegistry.EXPRESSION,
                SyntaxInfo.Expression.builder(TextSeeThroughProp.class, Boolean.class)
                        .addPatterns(
                                "[the] fake display[ ](text|content)[ ]see through [state] of %fakeentity%",
                                "%fakeentity%'s fake display[ ](text|content)[ ]see through [state]"
                        )
                        .build()
        );
    }

    public TextSeeThroughProp() {
        super(Boolean.class, TextDisplayMeta.class, Changer.ChangeMode.SET);
    }

    @Override
    protected @Nullable Boolean get(TextDisplayMeta meta) {
        return meta.isSeeThrough();
    }

    @Override
    protected void change(TextDisplayMeta meta, Changer.ChangeMode mode, Object[] delta) {
        boolean newValue = getDeltaValue(delta, Boolean.class);
        meta.setSeeThrough(newValue);
    }
}
