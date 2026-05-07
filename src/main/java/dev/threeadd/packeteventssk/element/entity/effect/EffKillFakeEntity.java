package dev.threeadd.packeteventssk.element.entity.effect;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.github.shanebeee.skr.Registration;
import me.tofaa.entitylib.wrapper.WrapperEntity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

public class EffKillFakeEntity extends Effect {

    public static void register(Registration reg) {
        reg.newEffect(EffKillFakeEntity.class, "kill fake[ ]entit(y|ies) %fakeentities%")
                .name("Fake Entity - Kill Fake Entity")
                .description("Used to kill a spawned fake entity")
                .examples("""
                        command spawn:
                            trigger:
                                create new fake dropped item entity at player for players and store it in {_e}:
                                    set fake item type of the fake entity to dirt
                                wait 5 seconds
                                kill fake entity {_e}
                        """)
                .since("1.0.0")
                .register();
    }

    Expression<WrapperEntity> entityExpr;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.entityExpr = (Expression<WrapperEntity>) expressions[0];
        return true;
    }

    @Override
    protected void execute(Event event) {
        WrapperEntity[] entities = this.entityExpr.getAll(event);
        if (entities == null) return;

        for (WrapperEntity entity : entities) {
            entity.remove();
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "kill fake " + this.entityExpr.toString(event, debug);
    }
}