package threeadd.packetEventsSK.element.entity.expressions.prop.item;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import me.tofaa.entitylib.meta.projectile.ItemEntityMeta;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import threeadd.packetEventsSK.element.entity.api.MetaPropertyExpression;

@SuppressWarnings("unused")
@Name("Fake Dropped Item Entity - Item Stack")
@Description("Represents the item stack held within a dropped item.")
@Example("""
        command droppeditem:
            trigger:
                create new fake dropped item entity at player for players:
                    set fake item stack of the fake entity to dirt
                    set fake gravity state of the fake entity to false
        
                    wait 10 seconds
                    kill fake entity the fake entity
        """)
@Since("1.0.0")
public class ItemProp
        extends MetaPropertyExpression<ItemEntityMeta, org.bukkit.inventory.ItemStack> {

    static {
        PropertyExpression.register(ItemProp.class, org.bukkit.inventory.ItemStack.class, "fake[ ]item[ ]stack", "fakeentity/fakeentitymeta");
    }

    @SuppressWarnings("unused")
    public ItemProp() {
        super(org.bukkit.inventory.ItemStack.class, ItemEntityMeta.class, Changer.ChangeMode.SET);
    }

    @Override
    protected @Nullable org.bukkit.inventory.ItemStack get(ItemEntityMeta meta) {
        return SpigotConversionUtil.toBukkitItemStack(meta.getItem());
    }

    @Override
    protected void change(ItemEntityMeta meta, Changer.ChangeMode mode, Object[] delta) {
        org.bukkit.inventory.ItemStack bukkitItem = getDeltaValue(delta, org.bukkit.inventory.ItemStack.class);
        ItemStack peItem = SpigotConversionUtil.fromBukkitItemStack(bukkitItem);

        meta.setItem(peItem);
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "dropped item of fake entity";
    }
}
