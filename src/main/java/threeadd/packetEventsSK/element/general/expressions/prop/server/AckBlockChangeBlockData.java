package threeadd.packetEventsSK.element.general.expressions.prop.server;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.expressions.base.PropertyExpression;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBlockChange;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.Nullable;
import threeadd.packetEventsSK.element.general.api.PacketPropertyExpression;

@SuppressWarnings("unused")
//TODO docs
public class AckBlockChangeBlockData extends PacketPropertyExpression<WrapperPlayServerBlockChange, BlockData> {

    static {
        PropertyExpression.register(AckBlockChangeBlockData.class, BlockData.class, "packet[ ]changed[ ]block[ ]data", "packet");
    }

    public AckBlockChangeBlockData() {
        super(BlockData.class, PacketType.Play.Server.BLOCK_CHANGE, true, true, null, Changer.ChangeMode.SET);
    }

    @Override
    protected @Nullable BlockData get(WrapperPlayServerBlockChange wrapper) {
        return SpigotConversionUtil.toBukkitBlockData(wrapper.getBlockState());
    }

    @Override
    protected void change(WrapperPlayServerBlockChange wrapper, Changer.ChangeMode mode, Object[] delta) {
        BlockData newData = getDeltaValue(delta, BlockData.class);
        wrapper.setBlockState(SpigotConversionUtil.fromBukkitBlockData(newData));
    }
}
