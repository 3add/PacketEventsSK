package dev.threeadd.packeteventssk.element.simple.effect;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.github.shanebeee.skr.Registration;
import dev.threeadd.packeteventssk.api.simple.GlowingEntityManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class EffGlow extends Effect {

    public static void register(Registration reg) {
        reg.newEffect(EffGlow.class, "set glow[ing] [state] of %entities% [to] %boolean% [for %-players%]")
                .name("Simple Glow - Entity Glow State")
                .description("""
                        Set the glow state of an entity for a set of viewers.
                        Internally everything is handled by the addon. (fully packet based)
                        """)
                .examples("""
                        command glowMeForMe:
                            trigger:
                                set glow state of player to true for player
                        """)
                .since("1.0.0")
                .register();
    }

    private Expression<Entity> targetExpr;
    private Expression<Boolean> stateExpr;
    private @Nullable Expression<Player> viewersExpr;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.targetExpr = (Expression<Entity>) expressions[0];
        this.stateExpr = (Expression<Boolean>) expressions[1];
        this.viewersExpr = (Expression<Player>) expressions[2];

        return true;
    }

    @Override
    protected void execute(Event event) {
        Entity[] targets = this.targetExpr.getAll(event);
        Boolean newState = this.stateExpr.getSingle(event);

        if (targets == null || targets.length == 0 || newState == null) return;

        Player[] viewers = this.viewersExpr != null ? this.viewersExpr.getAll(event) : null;

        Set<UUID> viewerUuids;

        if (viewers == null || viewers.length == 0) {
            viewerUuids = Bukkit.getOnlinePlayers().stream()
                    .map(Player::getUniqueId)
                    .collect(Collectors.toSet());
        } else {
            viewerUuids = Arrays.stream(viewers)
                    .map(Player::getUniqueId)
                    .collect(Collectors.toSet());
        }

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
        String target = this.targetExpr.toString(event, debug);
        String glowingState = this.stateExpr.toString(event, debug);
        String viewersPart = this.viewersExpr != null ? " for " + this.viewersExpr.toString(event, debug) : "";
        return String.format("set glowing state of %s to %s%s", target, glowingState, viewersPart);
    }
}