package threeadd.packetEventsSK.element.general.expressions.prop;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.SkriptParser;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import threeadd.packetEventsSK.util.expressions.CustomPropertyExpression;

@SuppressWarnings("unused")
@Name("General - Entity Id of Entity")
@Description("Gets the entity id of an entity on the server")
@Example("""
        command killTargetForMe:
            trigger:
                create a new destroy entities send packet:
                    add (entity id of target entity of player) to packet entity ids of the packet
                    send packet the packet to the player
        """)
public class EntityIdProp extends CustomPropertyExpression<Entity, Integer> {

    static {
        PropertyExpression.register(EntityIdProp.class, Integer.class, "entity[ ]id",
                "entity");
    }

    public EntityIdProp() {
        super(Integer.class, Entity.class, true);
    }

    @Override
    protected boolean initialize(SkriptParser.ParseResult parseResult) {
        return true;
    }

    @Override
    protected Integer getOne(Event event) {
        return getValue(0, Entity.class, event).getEntityId();
    }
}
