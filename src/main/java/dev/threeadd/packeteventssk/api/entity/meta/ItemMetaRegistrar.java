package dev.threeadd.packeteventssk.api.entity.meta;

import ch.njol.skript.aliases.ItemType;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import dev.threeadd.packeteventssk.api.util.field.FieldRegistrar;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import me.tofaa.entitylib.meta.projectile.ItemEntityMeta;

public class ItemMetaRegistrar implements FieldRegistrar {

    @Override
    public boolean register() {
        MetaFieldRegistry.INSTANCE.builder(EntityTypes.ITEM, ItemEntityMeta.class)
                .optionalField(ItemType.class,
                        meta -> new ItemType(SpigotConversionUtil.toBukkitItemStack(meta.getItem())),
                        (meta, newItem) -> meta.setItem(SpigotConversionUtil.fromBukkitItemStack(newItem.getRandom())),
                        "item")
                .build(this);

        return true;
    }
}
