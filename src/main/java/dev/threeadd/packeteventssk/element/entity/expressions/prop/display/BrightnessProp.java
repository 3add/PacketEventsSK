package dev.threeadd.packeteventssk.element.entity.expressions.prop.display;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import me.tofaa.entitylib.meta.display.AbstractDisplayMeta;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;
import dev.threeadd.packeteventssk.api.entity.MetaPropertyExpression;

@SuppressWarnings("unused")
@Name("Fake Display Entity - Brightness")
@Description("""
        Represents the brightness override of a Display Entity.
        See [Display Entity Data](https://minecraft.wiki/w/Display#Entity_data) on McWiki for more details.
        """)
@Example("""
        command display <text>:
            trigger:
                create new fake text display entity at player for players:
                    set fake display text of the fake entity to "Hello <rainbow>World"
                    set fake display brightness of the fake entity to 15
                    wait 2 seconds
                    kill fake entity the fake entity
        """)
@Since("1.0.0")
public class BrightnessProp extends MetaPropertyExpression<AbstractDisplayMeta, Number> {

    public static void register(SyntaxRegistry registry) {
        registry.register(
                SyntaxRegistry.EXPRESSION,
                SyntaxInfo.Expression.builder(BrightnessProp.class, Number.class)
                        .supplier(BrightnessProp::new)
                        .addPatterns(
                                "[the] fake display brightness [override] of %fakeentity%",
                                "%fakeentity%'s fake display brightness [override]"
                        )
                        .build()
        );
    }

    public BrightnessProp() {
        super(Number.class, AbstractDisplayMeta.class, Changer.ChangeMode.SET);
    }

    @Override
    protected @Nullable Number get(AbstractDisplayMeta meta) {
        return meta.getBrightnessOverride();
    }

    @Override
    protected void change(AbstractDisplayMeta meta, Changer.ChangeMode mode, Object[] delta) {
        int newBrightness = getDeltaValue(delta, Number.class).intValue();
        meta.setBrightnessOverride(newBrightness);
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "brightness of fake entity";
    }
}
