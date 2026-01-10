package threeadd.packetEventsSK.element.entity.expressions.prop;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.SkriptParser;
import me.tofaa.entitylib.wrapper.WrapperEntity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import threeadd.packetEventsSK.util.expressions.CustomPropertyExpression;

@SuppressWarnings("unused")
@Name("Fake Entity - Entity Id")
@Description("Get the entity id used to represent this fake entity (integer)")
@Example("""
        command spawnfakeplayer:
            trigger:
                create new fake player entity at player for all players:
                    set packet skin of the fake entity to player's skin
                    add the packet entity id of the fake entity to {-id::*}

        command lookup <text>:
            trigger:
                loop {-id::*}:
                    if {-id::*} contains arg-1 parsed as integer:
                        send "Found %fake entity with id loop-value%"
        """)
public class IdProp extends CustomPropertyExpression<WrapperEntity, Integer> {

    static {
        PropertyExpression.register(IdProp.class, Integer.class, "fake[ ]entity[ ]id", "fakeentity");
    }

    @SuppressWarnings("unused")
    public IdProp() {
        super(Integer.class, WrapperEntity.class, true);
    }

    @Override
    protected boolean initialize(SkriptParser.ParseResult parseResult) {
        return true;
    }

    @Override
    protected @Nullable Integer getProperty(WrapperEntity input) {
        return input.getEntityId();
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "entity id of fake entity";
    }
}
