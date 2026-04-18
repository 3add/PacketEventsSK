package threeadd.packetEventsSK.element.general.expressions.prop.server;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBlockChange;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import org.bukkit.block.data.BlockData;
import org.checkerframework.checker.index.qual.PolyUpperBound;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.DefaultSyntaxInfos;
import org.skriptlang.skript.registration.SyntaxRegistry;
import threeadd.packetEventsSK.element.general.api.PacketPropertyExpression;

@SuppressWarnings("unused")
@Name("General - Block Change Packet - Block Data")
@Description("Represents the block data of a block change packet.")
@Since("1.0.0")
// TODO Example

public class AckBlockChangeBlockDataProp extends PacketPropertyExpression<WrapperPlayServerBlockChange, BlockData> {

    public static void register(SyntaxRegistry registry) {
        registry.register(
                SyntaxRegistry.EXPRESSION,
                DefaultSyntaxInfos.Expression.builder(AckBlockChangeBlockDataProp.class, BlockData.class)
                        .addPatterns(
                                "[the] packet[ ]changed[ ]block[ ]data of %packet%",
                                "%packet%'[s] packet[ ]changed[ ]block[ ]data"
                        )
                        .build()
        );
    }

    public AckBlockChangeBlockDataProp() {
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
