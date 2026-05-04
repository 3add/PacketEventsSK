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
@Name("Fake Text Display Entity - Display Text Opacity")
@Description("""
        Represents the opacity state of a Text Display Entity.
        See [Display Entity Data](https://minecraft.wiki/w/Display#Entity_data) on McWiki for more details.
        """)
@Example("""
        command display <text>:
            trigger:
                create new fake text display entity at player for players:
                    set fake display text of the fake entity to "Hello <rainbow>World"
                    set fake display text opacity of the fake entity to 128
                    wait 2 seconds
                    kill fake entity the fake entity
        """)
@Since("Insert Version")
public class TextOpacityProp extends MetaPropertyExpression<TextDisplayMeta, Byte> {

    public static void register(SyntaxRegistry registry) {
        registry.register(
                SyntaxRegistry.EXPRESSION,
                SyntaxInfo.Expression.builder(TextOpacityProp.class, Byte.class)
                        .addPatterns(
                                "[the] fake display[ ](text|content)[ ]opacity of %fakeentity%",
                                "%fakeentity%'s fake display[ ](text|content)[ ]opacity"
                        )
                        .build()
        );
    }

    @SuppressWarnings("unused")
    public TextOpacityProp() {
        super(Byte.class, TextDisplayMeta.class, Changer.ChangeMode.SET);
    }

    @Override
    protected @Nullable Byte get(TextDisplayMeta meta) {
        return meta.getTextOpacity();
    }

    @Override
    protected void change(TextDisplayMeta meta, Changer.ChangeMode mode, Object[] delta) {
        byte newValue = getDeltaValue(delta, Byte.class);
        meta.setTextOpacity(newValue);
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "text opacity of fake entity";
    }
}

