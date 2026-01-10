package threeadd.packetEventsSK.element.entity.api;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.lang.SkriptParser;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import me.tofaa.entitylib.meta.EntityMeta;
import me.tofaa.entitylib.wrapper.WrapperEntity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import threeadd.packetEventsSK.element.general.api.PacketTriggerEvent;
import threeadd.packetEventsSK.util.expressions.CustomPropertyExpression;

import java.util.Arrays;
import java.util.EnumSet;

// supports both WrapperEntity and EntityMeta
public abstract class MetaPropertyExpression<Meta extends EntityMeta, Return> extends CustomPropertyExpression<Object, Return> {

    private final EnumSet<ChangeMode> changeModes;
    private final Class<Meta> entityMetaClass;

    public MetaPropertyExpression(Class<Return> returnType, Class<Meta> entityMetaClass, ChangeMode... changeModes) {
        super(returnType, Object.class, true);
        this.entityMetaClass = entityMetaClass;
        this.changeModes = EnumSet.copyOf(Arrays.asList(changeModes));
    }

    @Override
    protected boolean initialize(SkriptParser.ParseResult parseResult) {
        return true;
    }

    @Nullable
    protected abstract Return get(Meta meta);

    @Override
    protected final Return getProperty(Object input) {
        Meta meta = resolveMeta(input);

        if (meta == null)
            return null;

        return get(meta);
    }

    @Override
    public Class<?>[] acceptChange(ChangeMode mode) {
        if (changeModes.contains(mode))
            return new Class[] { returnType };

        return null;
    }

    @Override
    public final void change(Event event, Object[] delta, ChangeMode mode) {
        Object input = getValueOrNull(0, Object.class, event);
        switch (input) {
            case WrapperEntity entity -> {
                entity.consumeEntityMeta(entityMetaClass, m -> change(m, mode, delta));
                entity.refresh();
            }
            case EntityMeta baseMeta -> {
                if (entityMetaClass.isAssignableFrom(baseMeta.getClass())) {
                    Meta meta = entityMetaClass.cast(baseMeta);
                    change(meta, mode, delta);

                    if (event instanceof PacketTriggerEvent triggerEvent) {
                        WrapperPlayServerEntityMetadata wrapper = (WrapperPlayServerEntityMetadata) triggerEvent.getWrapper();
                        wrapper.setEntityMetadata(meta); // update the metadata of the wrapper itself
                        triggerEvent.setModified(true); // set modified to be marked for re-encode
                    }
                }
            }
            case null, default -> {}
        }
    }

    @Nullable
    @SuppressWarnings("unchecked")
    private Meta resolveMeta(Object input) {
        EntityMeta baseMeta = null;

        if (input instanceof WrapperEntity entity) {
            baseMeta = entity.getEntityMeta();
        } else if (input instanceof EntityMeta meta) {
            baseMeta = meta;
        }

        if (baseMeta != null && entityMetaClass.isAssignableFrom(baseMeta.getClass())) {
            return (Meta) baseMeta;
        }

        return null;
    }

    protected abstract void change(Meta meta, ChangeMode mode, Object[] delta);
}