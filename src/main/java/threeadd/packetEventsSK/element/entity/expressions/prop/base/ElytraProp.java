package threeadd.packetEventsSK.element.entity.expressions.prop.base;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import me.tofaa.entitylib.meta.EntityMeta;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;
import threeadd.packetEventsSK.element.entity.api.MetaPropertyExpression;

@SuppressWarnings("unused")
@Name("Fake Entity - Elytra Flying State")
@Description("Represents the fake elytra flying state of a fake entity, this only changes the client side metadata, it doesn't actually make the entity fly")
@Example("""
        command toggleElytra:
            trigger:
                set the fake elytra flying state of player's target entity to true
        """)
@Since("1.0.0")

public class ElytraProp extends MetaPropertyExpression<EntityMeta, Boolean> {

    public static void register(SyntaxRegistry registry) {
       registry.register(
               SyntaxRegistry.EXPRESSION,
               SyntaxInfo.Expression.builder(ElytraProp.class, Boolean.class)
                       .addPatterns(
                               "[the] fake elytra flying state of %fakeentity%",
                               "%fakeentity%'s fake elytra flying state"
                       )
                       .build()
       );
    }

    @SuppressWarnings("unused")
    public ElytraProp() {
        super(Boolean.class, EntityMeta.class, Changer.ChangeMode.SET);
    }

    @Override
    protected @Nullable Boolean get(EntityMeta meta) {
        return meta.isFlyingWithElytra();
    }

    @Override
    protected void change(EntityMeta meta, Changer.ChangeMode mode, Object[] delta) {
        boolean newState = (boolean) delta[0];
        meta.setFlyingWithElytra(newState);
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "elytra of fake entity";
    }
}
