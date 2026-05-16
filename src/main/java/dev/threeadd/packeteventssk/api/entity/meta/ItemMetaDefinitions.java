package dev.threeadd.packeteventssk.api.entity.meta;

import ch.njol.skript.aliases.ItemType;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import me.tofaa.entitylib.meta.projectile.ItemEntityMeta;

public class ItemMetaDefinitions {

    public static void register() {
        MetaDefinitionRegistry.INSTANCE.builder(ItemEntityMeta.class)
                .optionalField(ItemType.class,
                        meta -> new ItemType(SpigotConversionUtil.toBukkitItemStack(meta.getItem())),
                        (meta, newItem) -> meta.setItem(SpigotConversionUtil.fromBukkitItemStack(newItem.getRandom())),
                        "item")
                .build();
    }
}
