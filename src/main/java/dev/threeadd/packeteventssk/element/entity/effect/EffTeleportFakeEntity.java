package dev.threeadd.packeteventssk.element.entity.effect;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.github.shanebeee.skr.Registration;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import me.tofaa.entitylib.wrapper.WrapperEntity;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

public class EffTeleportFakeEntity extends Effect {

    public static void register(Registration reg) {
        reg.newEffect(EffTeleportFakeEntity.class, "teleport fake[ ]entit(y|ies) %fakeentities% to %location%")
                .name("Fake Entity - Teleport Fake Entity")
                .description("Teleport a spawned fake entity to a location.")
                .examples("""
                        command test:
                            trigger:
                                set {_p} to player
                                create a new fake text display entity at player for players and store it in {_entity}:
                                    set fake display content of {_entity} to minimessage from "Hello"
                                    set fake display billboard of {_entity} to center
                                    set fake display teleport interpolation duration of {_entity} to 1 second
                        
                                    loop 5 times:
                                        teleport fake entity {_entity} to {_p}
                                        wait 1 second
                        
                                    kill fake entity {_entity}
                        """)
                .since("1.0.0")
                .register();
    }

    Expression<WrapperEntity> entityExpr;
    Expression<Location> locationExpr;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.entityExpr = (Expression<WrapperEntity>) expressions[0];
        this.locationExpr = (Expression<Location>) expressions[1];
        return true;
    }

    @Override
    protected void execute(Event event) {
        WrapperEntity[] entities = this.entityExpr.getAll(event);
        Location location = this.locationExpr.getSingle(event);
        if (location == null) return;

        com.github.retrooper.packetevents.protocol.world.Location peLoc = SpigotConversionUtil.fromBukkitLocation(location);
        for (WrapperEntity entity : entities) {
            entity.teleport(peLoc);
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        String entity = entityExpr.toString(event, debug);
        String location = locationExpr.toString(event, debug);
        return String.format("teleport fake %s to %s", entity, location);
    }
}
