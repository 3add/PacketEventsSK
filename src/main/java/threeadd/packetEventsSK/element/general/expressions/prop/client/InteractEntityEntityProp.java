package threeadd.packetEventsSK.element.general.expressions.prop.client;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import threeadd.packetEventsSK.element.general.api.PacketPropertyExpression;
import threeadd.packetEventsSK.util.ConversionUtil;

@SuppressWarnings("unused")
@Name("General - Interact Entity Packet - Entity")
@Description("""
        Used to get the entity involved in an entity interact packet.
        **This property is NOT thread safe.**
        """)
@Example("""
        command addNoTouchable:
            trigger:
                if target entity of player is not set:
                    send "You're not looking at any entity"
                    stop
                add target entity of player to {-entities::*}

        on interact entity receive sync processed:
            if {-entities::*} doesn't contain packet entity of event-packet:
                stop
        
            send "You can't touch him! But I can't do much cause it wouldn't be thread safe..."
        """)
@Since("1.0.0")
public class InteractEntityEntityProp extends PacketPropertyExpression<WrapperPlayClientInteractEntity, Entity> {

    static {
        PropertyExpression.register(InteractEntityEntityProp.class, Entity.class,
                "packet[ ]entity", "packet");
    }

    public InteractEntityEntityProp() {
        super(Entity.class, PacketType.Play.Client.INTERACT_ENTITY, true, false, InteractEntityEntityIdProp.class, Changer.ChangeMode.SET);
    }

    @Override
    protected @Nullable Entity get(WrapperPlayClientInteractEntity wrapper) {
        return ConversionUtil.getEntityById(wrapper.getEntityId());
    }

    @Override
    protected void change(WrapperPlayClientInteractEntity wrapper, Changer.ChangeMode mode, Object[] delta) {
        Entity newEntity = getDeltaValue(delta, 0, Entity.class);
        wrapper.setEntityId(newEntity.getEntityId());
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "entity of interact entity packet";
    }
}
