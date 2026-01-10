package threeadd.packetEventsSK.element.simple.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.SkriptParser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import threeadd.packetEventsSK.element.simple.api.GlowingEntityManager;
import threeadd.packetEventsSK.util.effect.CustomEffect;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
@Name("Simple Glow - Entity Glow State")
@Description("""
        Set the glow state of an entity for a set of viewers.
        Internally everything is handled by the addon.
        To change the color of the glowing you can look into team packets.
        """)
@Example("""
        command glowMeForMe:
            trigger:
                set glow state of player to true for player
        """)
@Since("1.0.0")
public class EffGlow extends CustomEffect {

    static {
        Skript.registerEffect(EffGlow.class,
                "set glow[ing] [state] of %entities% [to] %boolean% [for %-players%]");
    }

    @Override
    protected boolean initialize(SkriptParser.ParseResult parseResult) {
        return true;
    }

    @Override
    protected void execute(Event event) {
        List<Entity> targets = getValuesOrNull(0, Entity.class, event);
        Boolean newState = getValueOrNull(1, Boolean.class, event);
        List<Player> viewers = getValuesOrNull(2, Player.class, event);

        if (targets == null || newState == null) return;

        if (viewers == null || viewers.isEmpty()) {
            viewers = new ArrayList<>(Bukkit.getOnlinePlayers());
        }

        Set<UUID> viewerUuids = viewers.stream()
                .map(Player::getUniqueId)
                .collect(Collectors.toSet());

        for (Entity target : targets) {
            int entityId = target.getEntityId();

            if (newState) {
                GlowingEntityManager.addGlowingReceivers(entityId, viewerUuids);
            } else {
                GlowingEntityManager.removeGlowingReceivers(entityId, viewerUuids);
            }
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "set glowing state of entities to boolean for players";
    }
}
