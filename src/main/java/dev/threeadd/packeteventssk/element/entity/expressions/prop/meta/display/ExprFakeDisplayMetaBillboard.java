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
import me.tofaa.entitylib.meta.display.AbstractDisplayMeta.BillboardConstraints;
import org.bukkit.entity.Display;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class ExprFakeDisplayMetaBillboard extends EntityMetaPropertyExpression<AbstractDisplayMeta, Display.Billboard> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprFakeDisplayMetaBillboard.class, Display.Billboard.class, "fake display billboard", "entitymeta")
                .name("Fake Display Entity - Display Billboard")
                .description("""
                        Represents the billboard of a Display Entity.
                        See [Display Entity Data](https://minecraft.wiki/w/Display#Entity_data) on McWiki for more details.
                        """)
                .examples("""
                        command display <text>:
                            trigger:
                                set {_content} to "<rainbow>%arg-1%"
                        
                                create new fake text display entity at player for players:
                                    set fake display text of the fake entity to {_content}
                                    set fake display billboard of the fake entity to center
                                    wait 2 seconds
                                    kill fake entity the fake entity
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
    protected Display.Billboard @Nullable [] getMetaProp(Event event, AbstractDisplayMeta meta) {
        return new Display.Billboard[]{Display.Billboard.valueOf(meta.getBillboardConstraints().name())};
    }

    @Override
    public Class<?> @Nullable [] acceptChange(Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET) {
            return CollectionUtils.array(Display.Billboard.class);
        }
        return null;
    }

    @Override
    protected void changeMeta(Event event, AbstractDisplayMeta meta, Object @Nullable [] delta, Changer.ChangeMode mode) {
        if (delta == null || delta.length != 1 || !(delta[0] instanceof Display.Billboard newBillboard)) {
            throw new IllegalStateException("Unexpected delta value " + (delta != null ? Arrays.toString(delta) : "none"));
        }

        if (mode != Changer.ChangeMode.SET) { // should always be the case
            return;
        }

        meta.setBillboardConstraints(BillboardConstraints.valueOf(newBillboard.name()));
    }

    @Override
    protected Class<AbstractDisplayMeta> getMetaClass() {
        return AbstractDisplayMeta.class;
    }

    @Override
    public Class<? extends Display.Billboard> getReturnType() {
        return Display.Billboard.class;
    }

    @Override
    protected String getPropertyName() {
        return "fake display billboard";
    }
}
