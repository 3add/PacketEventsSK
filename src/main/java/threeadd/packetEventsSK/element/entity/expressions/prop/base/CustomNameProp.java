package threeadd.packetEventsSK.element.entity.expressions.prop.base;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import me.tofaa.entitylib.meta.EntityMeta;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;
import threeadd.packetEventsSK.element.entity.api.MetaPropertyExpression;

@SuppressWarnings("unused")
@Name("Fake Entity Property - Custom Name")
@Description("A custom name of a fake entity, this does not support colors due to minecraft limitations.")
@Example("""
        command spawnRichCow:
            trigger:
                set {_name} to mini message from arg-1

                spawn new fake cow at player for player:
                    set fake custom name of the fake entity to {_name}
                    set fake custom name visible state of the fake entity to true
        """)
@Since("1.0.0")

public class CustomNameProp extends MetaPropertyExpression<EntityMeta, String> {

    public static void register(SyntaxRegistry registry) {
         registry.register(
                 SyntaxRegistry.EXPRESSION,
                 SyntaxInfo.Expression.builder(CustomNameProp.class, String.class)
                         .addPatterns(
                                 "[the] fake custom name of %fakeentity%",
                                 "%fakeentity%'s fake custom name"
                         )
                         .build()
         );
    }

    @SuppressWarnings("unused")
    public CustomNameProp() {
        super(String.class, EntityMeta.class, Changer.ChangeMode.SET);
    }

    @Override
    protected @Nullable String get(EntityMeta meta) {
        return PlainTextComponentSerializer.plainText().serialize(meta.getCustomName());
    }

    @Override
    protected void change(EntityMeta meta, Changer.ChangeMode mode, Object[] delta) {
        String newName = getDeltaValue(delta, String.class);
        meta.setCustomName(Component.text(newName));
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "custom name of fake entity";
    }
}
