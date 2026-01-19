package threeadd.packetEventsSK.element.general.expressions.prop.client;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import threeadd.packetEventsSK.element.general.api.PacketPropertyExpression;

@SuppressWarnings("unused")
@Name("General - Interact Entity Packet - Entity Id")
@Description("Used to get the entity id involved in an entity interact packet.")
@Example("""
        command addNoTouchable:
            trigger:
                if target entity of player is not set:
                    send "You're not looking at any entity"
                    stop
                add entity id of target entity of player to {-ids::*}

        on interact entity receive netty processed:
            if {-ids::*} doesn't contain packet entity id of event-packet:
                stop
        
            cancel packet
            send "You can't touch him!"
        """)
@Since("1.0.0")
public class InteractEntityEntityIdProp extends PacketPropertyExpression<WrapperPlayClientInteractEntity, Integer> {

    static {
        PropertyExpression.register(InteractEntityEntityIdProp.class, Integer.class,
                "packet[ ]entity[ ]id", "packet");
    }

    public InteractEntityEntityIdProp() {
        super(Integer.class, PacketType.Play.Client.INTERACT_ENTITY, true, true, null, Changer.ChangeMode.SET);
    }


    @Override
    protected @Nullable Integer get(WrapperPlayClientInteractEntity wrapper) {
        return wrapper.getEntityId();
    }

    @Override
    protected void change(WrapperPlayClientInteractEntity wrapper, Changer.ChangeMode mode, Object[] delta) {
        int newId = getDeltaValue(delta, 0, Integer.class);
        wrapper.setEntityId(newId);
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "entity of interact entity id packet";
    }
}
