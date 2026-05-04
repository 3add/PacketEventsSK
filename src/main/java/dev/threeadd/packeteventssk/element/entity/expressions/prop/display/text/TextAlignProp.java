package dev.threeadd.packeteventssk.element.entity.expressions.prop.display.text;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.SkriptParser;
import me.tofaa.entitylib.meta.display.TextDisplayMeta;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;
import dev.threeadd.packeteventssk.api.entity.MetaPropertyExpression;

@SuppressWarnings("unused")
@Name("Fake Text Display Entity - Text Alignment")
@Description("""
        Represents the text alignment (left or right) state of a Text Display Entity.
        See [Display Entity Data](https://minecraft.wiki/w/Display#Entity_data) on McWiki for more details.
        """)
@Example("""
        command display <text>:
            trigger:
                create new fake text display entity at player for players:
                    set fake display text of the fake entity to "Hello <rainbow>World"
                    set fake display text align right of the fake entity to true
                    wait 2 seconds
                    kill fake entity the fake entity
        """)
@Since("Insert Version")
public class TextAlignProp extends MetaPropertyExpression<TextDisplayMeta, Boolean> {

    public static void register(SyntaxRegistry registry) {
        registry.register(
                SyntaxRegistry.EXPRESSION,
                SyntaxInfo.Expression.builder(TextAlignProp.class, Boolean.class)
                        .addPatterns(
                                "[the] fake display[ ](text|content)[ ]align[ ](:(right|left)) of %fakeentity%",
                                "%fakeentity%'s fake display[ ](text|content)[ ]align[ ](:(right|left))"
                        )
                        .build()
        );
    }

    private boolean isRight = false;

    @Override
    protected boolean initialize(SkriptParser.ParseResult parseResult) {
        if (parseResult.hasTag("right"))
            isRight = true;

        return true;
    }

    @SuppressWarnings("unused")
    public TextAlignProp() {
        super(Boolean.class, TextDisplayMeta.class, Changer.ChangeMode.SET);
    }

    @Override
    protected @Nullable Boolean get(TextDisplayMeta meta) {
        if (isRight)
            return meta.isAlignRight();

        return meta.isAlignLeft();
    }

    @Override
    protected void change(TextDisplayMeta meta, Changer.ChangeMode mode, Object[] delta) {
        boolean newValue = getDeltaValue(delta, Boolean.class);
        if (isRight) {
            meta.setAlignRight(newValue);
            return;
        }

        meta.setAlignLeft(newValue);
    }
}
