package dev.threeadd.packeteventssk.element.entity.expression;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.github.shanebeee.skr.Registration;
import me.tofaa.entitylib.EntityLib;
import me.tofaa.entitylib.wrapper.WrapperEntity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class ExprFakeEntityFromUuid extends SimpleExpression<WrapperEntity> {

    public static void register(Registration reg) {
        reg.newSimpleExpression(ExprFakeEntityFromUuid.class, WrapperEntity.class, "fake[ ]entity (from|with) uuid %uuid%")
                .name("Fake Entity - From Entity UUID")
                .description("Retrieve a fake entity from it's entity uuid, this only works for spawned fake entities")
                .examples("""
                        command spawnfakeplayer:
                            trigger:
                                set {_p} to player
                                create new fake player entity at player for all players:
                                    set fake skin of the fake entity to {_p}'s skin
                                    add fake uuid of the fake entity to {-uuid::*}
                        
                        command lookup <text>:
                            trigger:
                                loop {-uuid::*}:
                                    if {-uuid::*} contains arg-1 parsed as uuid:
                                        send "Found %fake entity with uuid loop-value%"
                        """)
                .since("1.0.0", "1.1.0 (pattern altered)")
                .register();
    }

    private Expression<UUID> uuidExpr;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.uuidExpr = (Expression<UUID>) expressions[0];
        return true;
    }

    @Override
    protected WrapperEntity @Nullable [] get(Event event) {
        UUID uuid = this.uuidExpr.getSingle(event);
        if (uuid == null) return null;

        WrapperEntity entity = EntityLib.getApi().getEntity(uuid);
        if (entity == null) return null;

        return new WrapperEntity[]{entity};
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
        String uuid = this.uuidExpr.toString(event, debug);
        return String.format("fake entity with uuid %s", uuid);
    }
}
