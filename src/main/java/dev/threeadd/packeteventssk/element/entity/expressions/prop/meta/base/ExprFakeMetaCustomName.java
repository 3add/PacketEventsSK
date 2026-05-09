package dev.threeadd.packeteventssk.element.entity.expressions.prop.meta.base;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.github.shanebeee.skr.Registration;
import dev.threeadd.packeteventssk.api.entity.EntityMetaPropertyExpression;
import me.tofaa.entitylib.meta.EntityMeta;
import net.kyori.adventure.text.Component;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class ExprFakeMetaCustomName extends EntityMetaPropertyExpression<EntityMeta, Component> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprFakeMetaCustomName.class, Component.class, "fake custom[ ]name", "entitymeta")
                .name("Fake Entity Property - Custom Name")
                .description("A custom name of a fake entity, this does not support colors due to minecraft limitations.")
                .examples("""
                        command spawnRichCow <text>:
                            trigger:
                                set {_name} to arg-1
                
                                spawn new fake cow at player for player:
                                    set fake custom name of the fake entity to {_name}
                                    set fake custom name visible state of the fake entity to true
                        """)
                .since("1.0.0")
                .register();
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        setExpr((Expression<? extends EntityMeta>) expressions[0]);
        return true;
    }

    @Override
    protected Component @Nullable [] getMetaProp(Event event, EntityMeta meta) {
        return new Component[]{meta.getCustomName()};
    }

    @Override
    public Class<?> @Nullable [] acceptChange(Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET || mode == Changer.ChangeMode.RESET) {
            return CollectionUtils.array(Component.class);
        }
        return null;
    }

    @Override
    protected void changeMeta(Event event, EntityMeta meta, Object @Nullable [] delta, Changer.ChangeMode mode) {
        if (delta == null || delta.length != 1 || !(delta[0] instanceof Component customName)) {
            throw new IllegalStateException("Unexpected delta value " + (delta != null ? Arrays.toString(delta) : "none"));
        }

        switch (mode) {
            case SET -> meta.setCustomName(customName);
            case RESET -> meta.setCustomName(null);
        }
    }

    @Override
    protected Class<EntityMeta> getMetaClass() {
        return EntityMeta.class;
    }

    @Override
    public Class<? extends Component> getReturnType() {
        return Component.class;
    }

    @Override
    protected String getPropertyName() {
        return "fake custom name";
    }
}
