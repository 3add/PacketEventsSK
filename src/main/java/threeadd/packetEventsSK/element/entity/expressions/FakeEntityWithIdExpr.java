package threeadd.packetEventsSK.element.entity.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import me.tofaa.entitylib.EntityLib;
import me.tofaa.entitylib.wrapper.WrapperEntity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import threeadd.packetEventsSK.util.expressions.CustomExpression;

@SuppressWarnings("unused")
@Name("Fake Entity - From Entity ID")
@Description("Retrieve a fake entity from it's entity id, this only works for spawned fake entities")
@Example("""
        command spawnfakeplayer:
            trigger:
                create new fake player entity at player for all players:
                    set fake skin of the fake entity to player's skin
                    add the packet entity id of the fake entity to {-id::*}

        command lookup <text>:
            trigger:
                loop {-id::*}:
                    if {-id::*} contains arg-1 parsed as integer:
                        send "Found %fake entity with id loop-value%"
        """)
@Since("1.0.0")
public class FakeEntityWithIdExpr extends CustomExpression<WrapperEntity> {

    static {
        Skript.registerExpression(FakeEntityWithIdExpr.class, WrapperEntity.class, ExpressionType.SIMPLE,
                "fake[ ]entity with id %integer%");
    }

    @SuppressWarnings("unused")
    public FakeEntityWithIdExpr() {
        super(WrapperEntity.class, true);
    }

    @Override
    protected boolean initialize(SkriptParser.ParseResult parseResult) {
        return true;
    }

    @Override
    protected @Nullable WrapperEntity getOne(Event currentEvent) {

        Integer id = getValueOrNull(0, Integer.class, currentEvent);
        if (id == null) return null;

        return EntityLib.getApi().getEntity(id);
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "fake entity from id";
    }
}
