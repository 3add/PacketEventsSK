package threeadd.packetEventsSK.element.entity.expressions.prop.display.text;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.util.Color;
import ch.njol.skript.util.ColorRGB;
import me.tofaa.entitylib.meta.display.TextDisplayMeta;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;
import threeadd.packetEventsSK.element.entity.api.MetaPropertyExpression;

@Name("Fake Text Display Entity - Background Color")
@Description("""
        Represents the background color of a Text Display Entity.
        See [Display Entity Data](https://minecraft.wiki/w/Display#Entity_data ) on McWiki for more details.
        """)
@Example("""
        command display <color>:
            trigger:
                create new fake text display entity at player for players:
                    set fake display text of the fake entity to "<white>Hello World"
                    set fake display background color of the fake entity to arg-1
                    set fake display billboard of the fake entity to center
                    wait 2 seconds
                    kill fake entity the fake entity
        """)
@Since("1.0.0")
public class BackgroundColorProp extends MetaPropertyExpression<TextDisplayMeta, Color> {

    public static void register(SyntaxRegistry registry) {
        registry.register(
                SyntaxRegistry.EXPRESSION,
                SyntaxInfo.Expression.builder(BackgroundColorProp.class, Color.class)
                        .addPatterns(
                                "[the] fake display[ ]background[ ]color of %fakeentity%",
                                "%fakeentity%'s fake display[ ]background[ ]color"
                        )
                        .build()
        );
    }

    public BackgroundColorProp() {
        super(Color.class, TextDisplayMeta.class, Changer.ChangeMode.SET);
    }

    @Override
    protected @Nullable Color get(TextDisplayMeta meta) {
        return ColorRGB.fromBukkitColor(org.bukkit.Color.fromARGB(meta.getBackgroundColor()));
    }

    @Override
    protected void change(TextDisplayMeta meta, Changer.ChangeMode mode, Object[] delta) {
        Color newColor = getDeltaValue(delta, Color.class);
        meta.setBackgroundColor(newColor.asARGB());
    }
}