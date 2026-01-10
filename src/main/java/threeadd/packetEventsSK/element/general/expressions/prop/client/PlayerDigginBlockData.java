package threeadd.packetEventsSK.element.general.expressions.prop.client;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.expressions.base.PropertyExpression;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;
import threeadd.packetEventsSK.element.general.api.PacketPropertyExpression;
import threeadd.packetEventsSK.util.ConversionUtil;

// TODO Example
public class PlayerDigginBlockData extends PacketPropertyExpression<WrapperPlayClientPlayerDigging, Vector> {

    static {
        PropertyExpression.register(PlayerDigginBlockData.class, Vector.class,
                "packet[ ]dug[ ]block[ ]pos[ition]", "packet");
    }

    public PlayerDigginBlockData() {
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
