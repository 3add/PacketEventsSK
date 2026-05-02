package dev.threeadd.packeteventssk.element.entity.expressions.prop.display.item;

import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.*;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import me.tofaa.entitylib.meta.display.ItemDisplayMeta;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.DefaultSyntaxInfos;
import org.skriptlang.skript.registration.SyntaxRegistry;
import dev.threeadd.packeteventssk.api.entity.MetaPropertyExpression;

@SuppressWarnings("unused")
@DocumentationId("DisplayItemProp")
@Name("Fake Item Display Entity - Display Item Type")
@Description("""
        Represents the display ItemType of an Item/Block Display Entity
        See [Display Entity Data](https://minecraft.wiki/w/Display#Entity_data) on McWiki for more details.
        """)
@Example("""
        command test:
            trigger:
                create new fake item display entity at player for players:
                    set fake display item of the fake entity to dirt
                    wait 2 seconds
                    kill fake entity the fake entity
        """)
@Since("1.0.0")
public class ItemProp extends MetaPropertyExpression<ItemDisplayMeta, ItemType> {

    public static void register(SyntaxRegistry registry) {
        registry.register(
                SyntaxRegistry.EXPRESSION,
                DefaultSyntaxInfos.Expression.builder(ItemProp.class, ItemType.class)
                        .addPatterns(
                                "[the] fake display[ ]item[ ][type] of %fakeentity%",
                                "%fakeentity%'s fake display[ ]item[ ][type]"
                        )
                        .build()
        );
    }

    @SuppressWarnings("unused")
    public ItemProp() {
        super(ItemType.class, ItemDisplayMeta.class, Changer.ChangeMode.SET);
    }

    @Override
    protected @Nullable ItemType get(ItemDisplayMeta meta) {
        return new ItemType(SpigotConversionUtil.toBukkitItemStack(meta.getItem()));
    }

    @Override
    protected void change(ItemDisplayMeta meta, Changer.ChangeMode mode, Object[] delta) {
        ItemType item = getDeltaValue(delta, ItemType.class);
        meta.setItem(SpigotConversionUtil.fromBukkitItemStack(item.getRandom()));
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "item type of fake entity";
    }
}
