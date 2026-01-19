package threeadd.packetEventsSK.element.general.expressions.prop.server;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.expressions.base.PropertyExpression;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import me.tofaa.entitylib.EntityLib;
import me.tofaa.entitylib.meta.EntityMeta;
import me.tofaa.entitylib.wrapper.WrapperEntity;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Nullable;
import threeadd.packetEventsSK.element.general.api.PacketPropertyExpression;

// TODO docs
@SuppressWarnings("unused")
public class EntityMetaDataEntityMetadataProp extends PacketPropertyExpression<WrapperPlayServerEntityMetadata, EntityMeta> {

    static {
        PropertyExpression.register(EntityMetaDataEntityMetadataProp.class, EntityMeta.class,
                "packet[ ]entity[ ]meta[ ][data]", "packet");
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
