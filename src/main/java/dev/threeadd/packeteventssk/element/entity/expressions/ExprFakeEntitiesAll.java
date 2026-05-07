package dev.threeadd.packeteventssk.element.entity.expressions;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.github.shanebeee.skr.Registration;
import me.tofaa.entitylib.EntityLib;
import me.tofaa.entitylib.wrapper.WrapperEntity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

public class ExprFakeEntitiesAll extends SimpleExpression<WrapperEntity> {

    public static void register(Registration reg) {
        reg.newSimpleExpression(ExprFakeEntitiesAll.class, WrapperEntity.class, "[all] fake[ ]entities")
                // TODO docs
                .register();
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        return true;
    }

    @Override
    protected WrapperEntity @Nullable [] get(Event event) {
        return EntityLib.getApi().getAllEntities().toArray(new WrapperEntity[0]);
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class<? extends WrapperEntity> getReturnType() {
        return WrapperEntity.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "fake entities";
    }
}
