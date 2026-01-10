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
@Name("Fake Entity - Teleport Fake Entity")
@Description("Teleport a spawned fake entity to a location.")
@Example("""
       command test:
            trigger:
                spawn a new fake text display entity at player for players and store it in {_entity}:
                    set fake display content of {_entity} to minimessage from "Hello"
                    set fake display billboard of {_entity} to center
                    set fake display teleport interpolation duration of {_entity} to 1 second

                    loop 5 times:
                        teleport fake entity {_entity} to player
                        wait 1 second

                    kill fake entity {_entity}
       """)
public class EffTeleport extends CustomEffect {

    static {
        Skript.registerEffect(EffTeleport.class,
                "teleport fake[ ]entit(y|ies) %fakeentities% to %location%");
    }

    @Override
    protected boolean initialize(SkriptParser.ParseResult parseResult) {
        return true;
    }

    @Override
    protected void execute(Event event) {
        List<WrapperEntity> entities = getValuesOrNull(0, WrapperEntity.class, event);
        org.bukkit.Location targetLoc = getValueOrNull(1, org.bukkit.Location.class, event);

        if (entities == null || targetLoc == null) return;

        Location peLoc = SpigotConversionUtil.fromBukkitLocation(targetLoc);
        entities.forEach(entity ->
                entity.teleport(peLoc));
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "spawn packet entity effect";
    }
}
