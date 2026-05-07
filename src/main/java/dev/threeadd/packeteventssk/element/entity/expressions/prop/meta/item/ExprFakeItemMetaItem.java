package dev.threeadd.packeteventssk.element.entity.expressions.prop.meta.item;

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
import me.tofaa.entitylib.meta.projectile.ItemEntityMeta;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class ExprFakeItemMetaItem extends EntityMetaPropertyExpression<ItemEntityMeta, ItemType> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprFakeItemMetaItem.class, ItemType.class, "fake item[ ][type]", "fakeentitymeta")
                .name("Fake Dropped Item Entity - Item Type")
                .description("Represents the item stack held within a dropped item.")
                .examples("""
                        command droppeditem:
                            trigger:
                                create new fake dropped item entity at player for players:
                                    set fake item type of the fake entity to dirt
                                    set fake gravity state of the fake entity to false
                        
                                    wait 10 seconds
                                    kill fake entity the fake entity
                        """)
                .since("1.0.0", "1.1.0 (changed ItemStack to ItemType)")
                .docIdOverride("DroppedItemProp")
                .register();
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        setExpr((Expression<? extends EntityMeta>) expressions[0]);
        return true;
    }

    @Override
    protected ItemType @Nullable [] getMetaProp(Event event, ItemEntityMeta meta) {
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
    protected void changeMeta(Event event, ItemEntityMeta meta, Object @Nullable [] delta, Changer.ChangeMode mode) {
        if (delta == null || delta.length != 1 || !(delta[0] instanceof ItemType newItemType)) {
            throw new IllegalStateException("Unexpected delta value " + (delta != null ? Arrays.toString(delta) : "none"));
        }

        if (mode != Changer.ChangeMode.SET) { // should always be the case
            return;
        }

        meta.setItem(SpigotConversionUtil.fromBukkitItemStack(newItemType.getRandom()));
    }

    @Override
    protected Class<ItemEntityMeta> getMetaClass() {
        return ItemEntityMeta.class;
    }

    @Override
    public Class<? extends ItemType> getReturnType() {
        return ItemType.class;
    }

    @Override
    protected String getPropertyName() {
        return "fake item type";
    }
}
