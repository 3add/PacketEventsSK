package dev.threeadd.packeteventssk.element.general.expressions.prop.server;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import me.tofaa.entitylib.EntityLib;
import me.tofaa.entitylib.meta.EntityMeta;
import me.tofaa.entitylib.wrapper.WrapperEntity;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.DefaultSyntaxInfos;
import org.skriptlang.skript.registration.SyntaxRegistry;
import dev.threeadd.packeteventssk.element.general.api.PacketPropertyExpression;

@Name("General - Entity Metadata Packet - Entity Metadata")
@Description("Used to get the entity metadata of an entity metadata packet. This is represented as an EntityMeta object, which is a custom class that contains all the metadata of an entity. This is NOT thread safe, and should only be used in a synchronous context.")

@Since("1.0.0")
// TODO Example
@SuppressWarnings("unused")
public class EntityMetaDataEntityMetadataProp extends PacketPropertyExpression<WrapperPlayServerEntityMetadata, EntityMeta> {

    public static void register(SyntaxRegistry registry) {
        registry.register(
                SyntaxRegistry.EXPRESSION,
                DefaultSyntaxInfos.Expression.builder(EntityMetaDataEntityMetadataProp.class, EntityMeta.class)
                        .addPatterns(
                                "[the] packet entity[ ]meta[ ][data] of %packet%",
                                "%packet%'[s] packet entity[ ]meta[ ][data]"
                                )
                        .build()
        );
    }

    public EntityMetaDataEntityMetadataProp() {
        super(EntityMeta.class, PacketType.Play.Server.ENTITY_METADATA, true, true, null, Changer.ChangeMode.SET);
    }

    @Override
    protected @Nullable EntityMeta get(WrapperPlayServerEntityMetadata wrapper) {
        int entityId = wrapper.getEntityId();
        EntityType type;

        WrapperEntity entity = EntityLib.getApi().getEntity(entityId);
        if (entity != null)
            type = entity.getEntityType();
        else {
            Entity bukkitEntity = SpigotConversionUtil.getEntityById(null, entityId);
            if (bukkitEntity == null)
                return null;

            type = SpigotConversionUtil.fromBukkitEntityType(bukkitEntity.getType());
        }

        EntityMeta meta = EntityMeta.createMeta(wrapper.getEntityId(), type);
        meta.getMetadata().setMetaFromPacket(wrapper);
        
        return meta;
    }

    @Override
    protected void change(WrapperPlayServerEntityMetadata wrapper, Changer.ChangeMode mode, Object[] delta) {
        EntityMeta newMeta = getDeltaValue(delta, EntityMeta.class);
        wrapper.setEntityMetadata(newMeta);
    }
}
