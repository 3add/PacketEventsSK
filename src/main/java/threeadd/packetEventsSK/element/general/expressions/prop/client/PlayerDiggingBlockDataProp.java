package threeadd.packetEventsSK.element.general.expressions.prop.client;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.DefaultSyntaxInfos;
import org.skriptlang.skript.registration.SyntaxRegistry;
import threeadd.packetEventsSK.element.general.api.PacketPropertyExpression;
import threeadd.packetEventsSK.util.ConversionUtil;

@Name("General - Player Digging Packet - Block Position")
@Description("Used to get the block position of a player digging packet. This block position is represented as a 3d vector.")
@Since("1.0.0")
// TODO Example
@SuppressWarnings("unused")
public class PlayerDiggingBlockDataProp extends PacketPropertyExpression<WrapperPlayClientPlayerDigging, Vector> {

    public static void register(SyntaxRegistry registry) {
        registry.register(
                SyntaxRegistry.EXPRESSION,
                DefaultSyntaxInfos.Expression.builder(PlayerDiggingBlockDataProp.class, Vector.class)
                        .addPatterns(
                                "[the] packet[ ]dug[ ]block[ ]pos[ition] of %packet%",
                                "%packet%'[s] packet[ ]dug[ ]block[ ]pos[ition]"
                        )
                        .build()
        );
    }

    public PlayerDiggingBlockDataProp() {
        super(Vector.class, PacketType.Play.Client.PLAYER_DIGGING, true, true, null, Changer.ChangeMode.SET);
    }

    @Override
    protected @Nullable Vector get(WrapperPlayClientPlayerDigging wrapper) {
        return ConversionUtil.toBukkitVector(wrapper.getBlockPosition());
    }

    @Override
    protected void change(WrapperPlayClientPlayerDigging wrapper, Changer.ChangeMode mode, Object[] delta) {
        Vector newPosition = getDeltaValue(delta, Vector.class);
        wrapper.setBlockPosition(ConversionUtil.toPeVectorI(newPosition));
    }
}
