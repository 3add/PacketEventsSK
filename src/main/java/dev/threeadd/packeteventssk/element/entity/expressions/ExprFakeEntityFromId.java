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

public class ExprFakeEntityFromId extends SimpleExpression<WrapperEntity> {

    public static void register(Registration reg) {
        reg.newSimpleExpression(ExprFakeEntityFromId.class, WrapperEntity.class, "fake[ ]entity (from|with) id %integer%")
                .name("Fake Entity - From Entity ID")
                .description("Retrieve a fake entity from it's entity id, this only works for spawned fake entities")
                .examples("""
                        command spawnfakeplayer:
                            trigger:
                                set {_p} to player
                                create new fake player entity at player for all players:
                                    set fake skin of the fake entity to {_p}'s skin
                                    add the fake entity id of the fake entity to {-id::*}
                        
                        command lookup <text>:
                            trigger:
                                loop {-id::*}:
                                    if {-id::*} contains arg-1 parsed as integer:
                                        send "Found %fake entity with id loop-value%"
                        """)
                .since("1.0.0", "1.1.0 (pattern altered)")
                .register();
    }

    private Expression<Integer> idExpr;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.idExpr = (Expression<Integer>) expressions[0];
        return true;
    }

    @Override
    protected WrapperEntity @Nullable [] get(Event event) {
        Integer id = idExpr.getSingle(event);
        if (id == null) return null;

        return new WrapperEntity[]{EntityLib.getApi().getEntity(id)};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends WrapperEntity> getReturnType() {
        return WrapperEntity.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        String id = this.idExpr.toString(event, debug);
        return String.format("fake entity with id %s", id);
    }
}
