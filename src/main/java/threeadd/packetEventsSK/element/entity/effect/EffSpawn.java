package threeadd.packetEventsSK.element.entity.effect;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.SkriptParser;
import com.github.retrooper.packetevents.protocol.world.Location;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import me.tofaa.entitylib.wrapper.WrapperEntity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import threeadd.packetEventsSK.util.effect.CustomEffect;

import java.util.List;

@SuppressWarnings("unused")
@Name("Fake Entity - Spawn Fake Entity")
@Description("Spawn a fake entity at a location, it can only be spawned once. To move use teleport.")
@Example("""
        command test:
            trigger:
                spawn a new fake text display entity and store it in {_entity}:
                    # you can also access "the fake entity" here instead of {_entity}
                    add players to fake entity viewers of {_entity}
                    spawn fake entity {_entity} at the player
                    set fake display content of {_entity} to minimessage from "hello world"
                    set fake display billboard of {_entity} to center
                    wait 2 seconds
                    kill fake entity {_entity}
        """)
public class EffSpawn extends CustomEffect {

    static {
        Skript.registerEffect(EffSpawn.class,
                "spawn fake[ ]entit(y|ies) %fakeentitys% at %location%");
    }

    @Override
    protected boolean initialize(SkriptParser.ParseResult parseResult) {
        return true;
    }

    @Override
    protected void execute(Event event) {
        List<WrapperEntity> entities = getValuesOrNull(0, WrapperEntity.class, event);
        org.bukkit.Location spawnLoc = getValueOrNull(1, org.bukkit.Location.class, event);

        if (entities == null || spawnLoc == null) return;

        Location peLoc = SpigotConversionUtil.fromBukkitLocation(spawnLoc);
        entities.forEach(entity ->
                entity.spawn(peLoc));
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "spawn packet entity effect";
    }
}
