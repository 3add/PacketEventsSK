package dev.threeadd.packeteventssk.element.entity.expressions.prop.meta.display.item;

import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.github.shanebeee.skr.Registration;
import dev.threeadd.packeteventssk.api.entity.EntityMetaPropertyExpression;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import me.tofaa.entitylib.meta.EntityMeta;
import me.tofaa.entitylib.meta.display.ItemDisplayMeta;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class ExprFakeItemDisplayMetaItem extends EntityMetaPropertyExpression<ItemDisplayMeta, ItemType> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprFakeItemDisplayMetaItem.class, ItemType.class, "fake display[ ]item[ ][type]", "entitymeta")
                .name("Fake Item Display Entity - Display Item Type")
                .description("""
                        Represents the display ItemType of an Item/Block Display Entity
                        See [Display Entity Data](https://minecraft.wiki/w/Display#Entity_data) on McWiki for more details.
                        """)
                .examples("""
                        command test:
                            trigger:
                                create new fake item display entity at player for players:
                                    set fake display item of the fake entity to dirt
                                    wait 2 seconds
                                    kill fake entity the fake entity
                        """)
                .since("1.0.0")
                .docIdOverride("ExprFakeItemDisplayMetaItem")
                .register();
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        setExpr((Expression<? extends EntityMeta>) expressions[0]);
        return true;
    }

    @Override
    protected ItemType @Nullable [] getMetaProp(Event event, ItemDisplayMeta meta) {
        return new ItemType[]{new ItemType(SpigotConversionUtil.toBukkitItemStack(meta.getItem()))};
    }

    @Override
    public Class<?> @Nullable [] acceptChange(Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET) {
            return CollectionUtils.array(ItemType.class);
        }
        return null;
    }

    @Override
    protected void changeMeta(Event event, ItemDisplayMeta meta, Object @Nullable [] delta, Changer.ChangeMode mode) {
        if (delta == null || delta.length != 1 || !(delta[0] instanceof ItemType newItemType)) {
            throw new IllegalStateException("Unexpected delta value " + (delta != null ? Arrays.toString(delta) : "none"));
        }

        if (mode != Changer.ChangeMode.SET) { // should always be the case
            return;
        }

        meta.setItem(SpigotConversionUtil.fromBukkitItemStack(newItemType.getRandom()));
    }

    @Override
    protected Class<ItemDisplayMeta> getMetaClass() {
        return ItemDisplayMeta.class;
    }

    @Override
    public Class<? extends ItemType> getReturnType() {
        return ItemType.class;
    }

    @Override
    protected String getPropertyName() {
        return "fake display item type";
    }
}
