package dev.threeadd.packeteventssk.element.entity.expressions.prop.meta.display.block;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.github.shanebeee.skr.Registration;
import dev.threeadd.packeteventssk.api.entity.EntityMetaPropertyExpression;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import me.tofaa.entitylib.meta.EntityMeta;
import me.tofaa.entitylib.meta.display.BlockDisplayMeta;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class ExprFakeBlockDisplayMetaBlock extends EntityMetaPropertyExpression<BlockDisplayMeta, BlockData> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprFakeBlockDisplayMetaBlock.class, BlockData.class, "fake display[ ]block[ ]data", "fakeentitymeta")
                .name("Fake Block Display Entity - Display Block Data")
                .description("""
                        Represents the block data of a Block Display Entity.
                        See [Display Entity Data](https://minecraft.wiki/w/Display#Entity_data) on McWiki for more details.
                        """)
                .examples("""
                        command test:
                            trigger:
                                create new fake block display entity at player for players:
                                    set fake display block data of the fake entity to dirt[]
                                    wait 2 seconds
                                    kill fake entity the fake entity
                        """)
                .since("1.0.0")
                .register();
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        setExpr((Expression<? extends EntityMeta>) expressions[0]);
        return true;
    }

    @Override
    protected BlockData @Nullable [] getMetaProp(Event event, BlockDisplayMeta meta) {
        return new BlockData[]{SpigotConversionUtil.toBukkitBlockData(meta.getBlockState())};
    }

    @Override
    public Class<?> @Nullable [] acceptChange(Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET) {
            return CollectionUtils.array(BlockData.class);
        }
        return null;
    }

    @Override
    protected void changeMeta(Event event, BlockDisplayMeta meta, Object @Nullable [] delta, Changer.ChangeMode mode) {
        if (delta == null || delta.length != 1 || !(delta[0] instanceof BlockData newBlockData)) {
            throw new IllegalStateException("Unexpected delta value " + (delta != null ? Arrays.toString(delta) : "none"));
        }

        if (mode != Changer.ChangeMode.SET) { // should always be the case
            return;
        }

        meta.setBlockState(SpigotConversionUtil.fromBukkitBlockData(newBlockData));
    }

    @Override
    protected Class<BlockDisplayMeta> getMetaClass() {
        return BlockDisplayMeta.class;
    }

    @Override
    public Class<? extends BlockData> getReturnType() {
        return BlockData.class;
    }

    @Override
    protected String getPropertyName() {
        return "fake block data";
    }
}
