package dev.threeadd.packeteventssk.element.entity.expressions.prop.display.text;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import me.tofaa.entitylib.meta.display.TextDisplayMeta;
import net.kyori.adventure.text.Component;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.bukkit.text.TextComponentParser;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;
import dev.threeadd.packeteventssk.api.entity.MetaPropertyExpression;

@SuppressWarnings("unused")
@Name("Fake Text Display Entity - Display Text")
@Description("""
        Represents the display text of a Text Display Entity.
        Supports MiniMessage formatting.
        """)
@Example("""
        command display <text>:
            trigger:
                create new fake text display entity at player for players:
                    set fake display text of the fake entity to "<rainbow>%arg-1%"
                    set fake display billboard of the fake entity to center
                    wait 2 seconds
                    kill fake entity the fake entity
        """)
@Since("1.0.0")
public class TextProp extends MetaPropertyExpression<TextDisplayMeta, Component> {

    public static void register(SyntaxRegistry registry) {
        registry.register(
                SyntaxRegistry.EXPRESSION,
                SyntaxInfo.Expression.builder(TextProp.class, Component.class)
                        .addPatterns(
                                "[the] fake display (text|content) of %fakeentity%",
                                "%fakeentity%'s fake display (text|content)"
                        )
                        .build()
        );
    }

    public TextProp() {
        super(Component.class, TextDisplayMeta.class, Changer.ChangeMode.SET);
    }

    @Override
    protected @Nullable Component get(TextDisplayMeta meta) {
        return meta.getText();
    }

    @Override
    protected void change(TextDisplayMeta meta, Changer.ChangeMode mode, Object[] delta) {
        if (delta[0] instanceof Component component) {
            meta.setText(component);
        } else if (delta[0] instanceof String text) {
            meta.setText(TextComponentParser.instance().parse(text));
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "text of fake entity";
    }
}