package dev.threeadd.packeteventssk.element.simple.effects;

import ch.njol.skript.lang.SkriptParser;
import com.github.retrooper.packetevents.util.Vector3i;
import dev.threeadd.packeteventssk.element.simple.api.block.FakeBlockManager;
import dev.threeadd.packeteventssk.util.effect.CustomEffect;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO docs
public class EffMakeSee extends CustomEffect {

    public static void register(SyntaxRegistry syntaxRegistry) {
        syntaxRegistry.register(
                SyntaxRegistry.EFFECT,
                SyntaxInfo.builder(EffMakeSee.class)
                        .addPatterns("fake make %players% see %locations% as %blockdata%")
                        .build()
        );
    }

    @Override
    protected boolean initialize(SkriptParser.ParseResult parseResult) {
        return true;
    }

    @Override
    protected void execute(Event event) {
        List<Player> players = getValues(0, Player.class, event);
        List<Location> locations = getValues(1, Location.class, event);
        BlockData blockdata = getValue(2, BlockData.class, event);

        Map<Vector3i, BlockData> map = new HashMap<>();
        for (Location location : locations) {
            Vector3i vec = new Vector3i(location.getBlockX(), location.getBlockY(), location.getBlockZ());
            map.put(vec, blockdata);
        }

        for (Player player : players) {
            FakeBlockManager.setFakeBlocks(player.getUniqueId(), map);
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "force player to see blocks as blockdata";
    }
}
