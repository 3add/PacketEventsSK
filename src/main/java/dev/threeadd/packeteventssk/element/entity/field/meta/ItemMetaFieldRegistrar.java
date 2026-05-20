package dev.threeadd.packeteventssk.element.entity.field.meta;

import ch.njol.skript.aliases.ItemType;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import dev.threeadd.packeteventssk.api.util.field.ConstructionContext;
import dev.threeadd.packeteventssk.api.util.field.FieldRegistrar;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import me.tofaa.entitylib.meta.projectile.ItemEntityMeta;

// Should fully match: https://minecraft.wiki/w/Java_Edition_protocol/Entity_metadata#Item
// including order of fields
public class ItemMetaFieldRegistrar implements FieldRegistrar {

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void register() {

        // complete
        MetaFieldRegistry.INSTANCE.builder(EntityTypes.ITEM, ItemEntityMeta.class)
                .optionalField(ItemType.class,
                        meta -> new ItemType(SpigotConversionUtil.toBukkitItemStack(meta.getItem())),
                        (meta, newItem) -> meta.setItem(SpigotConversionUtil.fromBukkitItemStack(newItem.getRandom())),
                        "item")
                .constructor(context -> {

                    ItemEntityMeta meta = (ItemEntityMeta) BaseMetaFieldRegistrar.BASE_ENTITY_META_CONSTRUCTOR.apply((ConstructionContext) context);

                    ItemType type = context.getOptional("item", ItemType.class);
                    if (type != null) {
                        meta.setItem(SpigotConversionUtil.fromBukkitItemStack(type.getRandom()));
                    }

                    return meta;
                })
                .build();
    }
}
