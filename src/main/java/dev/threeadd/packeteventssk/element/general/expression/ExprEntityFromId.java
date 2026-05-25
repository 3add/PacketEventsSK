package dev.threeadd.packeteventssk.element.general.expression;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.github.shanebeee.skr.Registration;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

public class ExprEntityFromId extends SimpleExpression<Entity> {

    public static void register(Registration reg) {
        reg.newSimpleExpression(ExprEntityFromId.class, Entity.class, "entity (from|with) [protocol] id %integer%")
                .name("General - Entity From Entity ID")
                .description("Retrieve a bukkit entity from it's entity id, this is not thread safe.")
                .examples("""
                        on clientbound entity velocity sync processed:
                            set {_entity} to entity with id (packet entity id of event-packet)
                            broadcast {_entity}
                        """)
                .since("1.1.4")
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
    protected Entity @Nullable [] get(Event event) {
        Integer id = idExpr.getSingle(event);
        if (id == null) return null;

        Entity entity = SpigotConversionUtil.getEntityById(null, id);
        if (entity == null) return null;

        return new Entity[]{entity};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends Entity> getReturnType() {
        return Entity.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        String id = this.idExpr.toString(event, debug);
        return String.format("fake entity with id %s", id);
    }
}
