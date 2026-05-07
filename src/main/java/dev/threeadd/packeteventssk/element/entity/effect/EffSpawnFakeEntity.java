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

public class EffSpawnFakeEntity extends Effect {

    public static void register(Registration reg) {
        reg.newEffect(EffSpawnFakeEntity.class, "spawn fake[ ]entit(y|ies) %fakeentities% at %location%")
                .name("Fake Entity - Spawn Fake Entity")
                .description("Spawn a fake entity at a location, it can only be spawned once. To move use teleport.")
                .examples("""
                        command test:
                            trigger:
                                set {_p} to player
                                create a new fake text display entity and store it in {_entity}:
                                    add players to fake entity viewers of {_entity}
                                    spawn fake entity {_entity} at {_p}
                                    set fake display content of {_entity} to "hello world"
                                    set fake display billboard of {_entity} to center
                                    wait 2 seconds
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
            entity.spawn(peLoc);
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        String entity = this.entityExpr.toString(event, debug);
        String location = this.locationExpr.toString(event, debug);
        return String.format("spawn fake %s at %s", entity, location);
    }
}