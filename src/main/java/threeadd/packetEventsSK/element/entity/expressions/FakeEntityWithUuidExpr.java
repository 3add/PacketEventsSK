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

import java.util.UUID;

@SuppressWarnings("unused")
@Name("Fake Entity - From Entity UUID")
@Description("Retrieve a fake entity from it's entity uuid, this only works for spawned fake entities")
@Example("""
        command spawnfakeplayer:
            trigger:
                create new fake player entity at player for all players:
                    set fake skin of the fake entity to player's skin
                    add the packet entity uuid of the fake entity to {-uuid::*}

        command lookup <text>:
            trigger:
                loop {-uuid::*}:
                    if {-uuid::*} contains arg-1 parsed as uuid:
                        send "Found %fake entity with uuid loop-value%"
        """)
@Since("1.0.0")
public class FakeEntityWithUuidExpr extends CustomExpression<WrapperEntity> {

    static {
        Skript.registerExpression(FakeEntityWithUuidExpr.class, WrapperEntity.class, ExpressionType.SIMPLE,
                "fake[ ]entity with uuid %uuid%");
    }

    @SuppressWarnings("unused")
    public FakeEntityWithUuidExpr() {
        super(WrapperEntity.class, true);
    }

    @Override
    protected boolean initialize(SkriptParser.ParseResult parseResult) {
        return true;
    }

    @Override
    protected @Nullable WrapperEntity getOne(Event currentEvent) {

        UUID uuid = getValueOrNull(0, UUID.class, currentEvent);
        if (uuid == null) return null;

        return EntityLib.getApi().getEntity(uuid);
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "fake entity from uuid";
    }
}
