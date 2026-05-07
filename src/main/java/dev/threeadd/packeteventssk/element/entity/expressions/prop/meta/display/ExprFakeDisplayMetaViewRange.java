package dev.threeadd.packeteventssk.element.entity.expressions.prop.meta.display;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.github.shanebeee.skr.Registration;
import dev.threeadd.packeteventssk.api.entity.EntityMetaPropertyExpression;
import me.tofaa.entitylib.meta.EntityMeta;
import me.tofaa.entitylib.meta.display.AbstractDisplayMeta;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class ExprFakeDisplayMetaViewRange extends EntityMetaPropertyExpression<AbstractDisplayMeta, Number> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprFakeDisplayMetaViewRange.class, Number.class, "fake display view[ ]range", "fakeentitymeta")
                .name("Fake Display Entity - View Range")
                .description("""
                        Represents the view range of a Display Entity.
                        See [Display Entity Data](https://minecraft.wiki/w/Display#Entity_data) on McWiki for more details.
                        """)
                .examples("""
                        command display:
                            trigger:
                                create new fake text display entity at player for players:
                                    set fake display text of the fake entity to "Hello <rainbow>World"
                                    set fake display billboard of the fake entity to center
                                    set fake display view range of the fake entity to 10
                                    wait 2 seconds
                                    kill fake entity the fake entity
                        
                                    broadcast fake display view range of the fake entity
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
    protected Number @Nullable [] getMetaProp(Event event, AbstractDisplayMeta meta) {
        return new Number[]{meta.getViewRange()};
    }

    @Override
    public Class<?> @Nullable [] acceptChange(Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.ADD
                || mode == Changer.ChangeMode.REMOVE
                || mode == Changer.ChangeMode.SET
                || mode == Changer.ChangeMode.RESET
                || mode == Changer.ChangeMode.REMOVE_ALL) {
            return CollectionUtils.array(Number.class);
        }
        return null;
    }

    @Override
    protected void changeMeta(Event event, AbstractDisplayMeta meta, Object @Nullable [] delta, Changer.ChangeMode mode) {
        if (delta == null || delta.length != 1 || !(delta[0] instanceof Number newValue)) {
            throw new IllegalStateException("Unexpected delta value " + (delta != null ? Arrays.toString(delta) : "none"));
        }

        switch (mode) {
            case SET -> meta.setViewRange(newValue.shortValue());
            case ADD -> meta.setViewRange((short) (meta.getViewRange() + newValue.shortValue()));
            case REMOVE -> meta.setViewRange((short) Math.max(0, meta.getViewRange() - newValue.shortValue()));
            case RESET, REMOVE_ALL -> meta.setViewRange((short) 0);
        }
    }

    @Override
    protected Class<AbstractDisplayMeta> getMetaClass() {
        return AbstractDisplayMeta.class;
    }

    @Override
    public Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    protected String getPropertyName() {
        return "fake display view range";
    }
}
