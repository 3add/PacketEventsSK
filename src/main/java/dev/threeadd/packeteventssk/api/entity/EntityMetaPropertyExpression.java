package dev.threeadd.packeteventssk.api.entity;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.expressions.base.PropertyExpression;
import me.tofaa.entitylib.meta.EntityMeta;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public abstract class EntityMetaPropertyExpression<Meta extends EntityMeta, Property> extends PropertyExpression<EntityMeta, Property> {

    /**
     * The metaclass of type {@link Meta}
     *
     * @return The metaclass
     */
    protected abstract Class<Meta> getMetaClass();

    /**
     * Override to handle the conversion of the meta to property
     */
    protected abstract Property @Nullable [] getMetaProp(Event event, Meta meta);

    /**
     * Override to specify a name for this prop (used in {@link EntityMetaPropertyExpression#toString()})
     *
     * @return The specified name for this prop
     */
    protected abstract String getPropertyName();

    /**
     * Override to handle the changing of meta of type {@link Meta}
     */
    protected void changeMeta(Event event, Meta meta, Object @Nullable [] delta, Changer.ChangeMode mode) {
    }

    @SuppressWarnings("unchecked")
    @Override
    protected final Property[] get(Event event, EntityMeta[] source) {
        List<Property> results = new ArrayList<>();
        for (EntityMeta meta : source) {
            if (!getMetaClass().isInstance(meta)) {
                continue;
            }

            Property[] props = getMetaProp(event, getMetaClass().cast(meta));
            if (props != null) {
                for (Property prop : props) {
                    if (prop != null) results.add(prop);
                }
            }
        }
        return results.toArray((Property[]) Array.newInstance(getReturnType(), results.size()));
    }

    @Override
    public final void change(Event event, Object @Nullable [] delta, Changer.ChangeMode mode) {
        EntityMeta[] metas = getExpr().getAll(event);
        if (metas == null) return;

        for (EntityMeta meta : metas) {
            if (getMetaClass().isInstance(meta)) {
                changeMeta(event, getMetaClass().cast(meta), delta, mode);
            }
        }
    }

    @Override
    public final String toString(@Nullable Event event, boolean debug) {
        return getPropertyName() + " of " + getExpr().toString(event, debug);
    }
}