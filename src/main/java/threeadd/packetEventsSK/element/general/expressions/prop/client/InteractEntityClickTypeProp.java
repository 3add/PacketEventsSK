package threeadd.packetEventsSK.element.general.expressions.prop.client;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.Nullable;
import threeadd.packetEventsSK.element.general.api.PacketPropertyExpression;
import threeadd.packetEventsSK.util.ConversionUtil;

@SuppressWarnings("unused")
@Name("General - Interact Entity Packet - Click Type")
@Description("""
        Used to get the click type involved in an entity interact packet.
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
        
            if packet click type of event-packet isn't left mouse button:
                stop
        
            send "You can't hit him! But I can't do much cause it wouldn't be thread safe..."
        """)
@Since("1.0.0")
public class InteractEntityClickTypeProp extends PacketPropertyExpression<WrapperPlayClientInteractEntity, ClickType> {

    static {
        PropertyExpression.register(InteractEntityClickTypeProp.class, ClickType.class,
                "packet[ ]click[ ]type", "packet");
    }

    public InteractEntityClickTypeProp() {
        super(ClickType.class, PacketType.Play.Client.INTERACT_ENTITY, true, true, null, Changer.ChangeMode.SET);
    }

    @Override
    protected @Nullable ClickType get(WrapperPlayClientInteractEntity wrapper) {
        return ConversionUtil.toBukkitClick(wrapper.getAction());
    }

    @Override
    protected void change(WrapperPlayClientInteractEntity wrapper, Changer.ChangeMode mode, Object[] delta) {
        ClickType clickType = getDeltaValue(delta, 0, ClickType.class);
        if (!(clickType.equals(ClickType.LEFT) || clickType.equals(ClickType.RIGHT)))
            return;

        WrapperPlayClientInteractEntity.InteractAction peAction = ConversionUtil.toPeAction(clickType);
        wrapper.setAction(peAction);
    }
}
