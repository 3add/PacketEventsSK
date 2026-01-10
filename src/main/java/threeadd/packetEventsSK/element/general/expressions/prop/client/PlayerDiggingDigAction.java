package threeadd.packetEventsSK.element.general.expressions.prop.client;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.expressions.base.PropertyExpression;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.DiggingAction;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging;
import org.jetbrains.annotations.Nullable;
import threeadd.packetEventsSK.element.general.api.PacketPropertyExpression;

@SuppressWarnings("unused")
// TODO docs
public class PlayerDiggingDigAction extends PacketPropertyExpression<WrapperPlayClientPlayerDigging, DiggingAction> {

    static {
        PropertyExpression.register(PlayerDiggingDigAction.class, DiggingAction.class,
                "packet[ ]dig[ging][ ]action", "packet");
    }

    public PlayerDiggingDigAction() {
        super(DiggingAction.class, PacketType.Play.Client.PLAYER_DIGGING, true, true, null, Changer.ChangeMode.SET);
    }

    @Override
    protected @Nullable DiggingAction get(WrapperPlayClientPlayerDigging wrapper) {
        return wrapper.getAction();
    }

    @Override
    protected void change(WrapperPlayClientPlayerDigging wrapper, Changer.ChangeMode mode, Object[] delta) {

        DiggingAction newAction = getDeltaValue(delta, DiggingAction.class);
        wrapper.setAction(newAction);
    }
}
