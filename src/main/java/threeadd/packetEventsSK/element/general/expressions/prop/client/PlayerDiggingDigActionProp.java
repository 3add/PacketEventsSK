package threeadd.packetEventsSK.element.general.expressions.prop.client;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.DiggingAction;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.DefaultSyntaxInfos;
import org.skriptlang.skript.registration.SyntaxRegistry;
import threeadd.packetEventsSK.element.general.api.PacketPropertyExpression;

@SuppressWarnings("unused")
@Name("General - Player Digging Packet - Digging Action")
@Description("Used to get the digging action involved in a player digging packet.")
@Since("1.0.0")
// TODO Example
public class PlayerDiggingDigActionProp extends PacketPropertyExpression<WrapperPlayClientPlayerDigging, DiggingAction> {

    public static void register(SyntaxRegistry registry) {
        registry.register(
                SyntaxRegistry.EXPRESSION,
                DefaultSyntaxInfos.Expression.builder(PlayerDiggingDigActionProp.class, DiggingAction.class)
                        .addPatterns(
                                "[the] packet[ ]dig[ging][ ]action of %packet%",
                                "%packet%'[s] packet[ ]dig[ging][ ]action"
                        )
                        .build()
        );
    }

    public PlayerDiggingDigActionProp() {
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
