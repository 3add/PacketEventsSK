package dev.threeadd.packeteventssk.element.simple.effect;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.github.shanebeee.skr.Registration;
import dev.threeadd.packeteventssk.api.entity.Skin;
import dev.threeadd.packeteventssk.api.simple.PlayerSkinManager;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class EffDisplayedSkin extends Effect {

    public static void register(Registration reg) {
        reg.newEffect(EffDisplayedSkin.class, "set displayed skin of %players% to %skin% [for %-players%]")
                .name("Simple Skin - Player Displayed Skin")
                .description("""
                        Set the displayed skin of a player for a set of viewers.
                        Internally everything is handled by the addon (fully packet based).
                        """)
                .examples("""
                        command skinMeNotchForMe:
                            trigger:
                                fetch skin of player named "notch" and store it in {_skin}
                                set displayed skin of player to {_skin} for player
                        """)
                .since("1.0.0")
                .register();
    }

    private Expression<Player> targetExpr;
    private Expression<Skin> skinExpr;
    private @Nullable Expression<Player> viewersExpr;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.targetExpr = (Expression<Player>) expressions[0];
        this.skinExpr = (Expression<Skin>) expressions[1];
        this.viewersExpr = (Expression<Player>) expressions[2];

        return true;
    }

    @Override
    protected void execute(Event event) {
        Player[] targets = this.targetExpr.getAll(event);
        Skin newSkin = this.skinExpr.getSingle(event);

        if (targets == null || targets.length == 0 || newSkin == null) return;

        Player[] viewers = this.viewersExpr != null ? this.viewersExpr.getAll(event) : new Player[0];

        if (viewers == null || viewers.length == 0) {
            for (Player target : targets) {
                PlayerSkinManager.setGlobalSkin(target, newSkin);
            }
            return;
        }

        for (Player target : targets) {
            PlayerSkinManager.setSkinForViewers(target, Arrays.stream(viewers).toList(), newSkin);
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        String target = this.targetExpr.toString(event, debug);
        String skin = this.skinExpr.toString(event, debug);
        String viewersPart = this.viewersExpr != null ? " for " + this.viewersExpr.toString(event, debug) : "";
        return String.format("set displayed skin of %s to %s%s", target, skin, viewersPart);
    }
}