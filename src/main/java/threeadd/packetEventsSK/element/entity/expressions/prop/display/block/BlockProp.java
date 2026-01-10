package threeadd.packetEventsSK.element.entity.expressions.prop.display.block;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.PropertyExpression;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import me.tofaa.entitylib.meta.display.BlockDisplayMeta;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import threeadd.packetEventsSK.element.entity.api.MetaPropertyExpression;

@SuppressWarnings("unused")
@Name("Fake Block Display Entity - Display Block Data")
@Description("""
        Represents the block data of a Block Display Entity.
        See [Display Entity Data](https://minecraft.wiki/w/Display#Entity_data) on McWiki for more details.
        """)
@Example("""
        command test:
            trigger:
                create new fake block display entity at player for players:
                    set fake display block data of the fake entity to dirt[]
                    wait 2 seconds
                    kill fake entity the fake entity
        """)
public class BlockProp extends MetaPropertyExpression<BlockDisplayMeta, BlockData> {

    static {
        PropertyExpression.register(BlockProp.class, BlockData.class,
                "fake[ ]display[ ]block[ ]data", "fakeentity/fakeentitymeta");
    }

    @SuppressWarnings("unused")
    public BlockProp() {
        super(BlockData.class, BlockDisplayMeta.class, Changer.ChangeMode.SET);
    }

    @Override
    protected @Nullable BlockData get(BlockDisplayMeta meta) {
        return SpigotConversionUtil.toBukkitBlockData(meta.getBlockState());
    }

    @Override
    protected void change(BlockDisplayMeta meta, Changer.ChangeMode mode, Object[] delta) {
        BlockData newData = (BlockData) delta[0];
        meta.setBlockState(SpigotConversionUtil.fromBukkitBlockData(newData));
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "block of fake entity";
    }
}
