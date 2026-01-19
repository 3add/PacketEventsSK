package threeadd.packetEventsSK.element.simple.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.SkriptParser;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import threeadd.packetEventsSK.element.entity.api.skin.Skin;
import threeadd.packetEventsSK.element.simple.api.PlayerSkinManager;
import threeadd.packetEventsSK.util.effect.CustomEffect;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
@Name("Simple Skin - Player Displayed SKin")
@Description("""
        Set the displayed skin of a player for a set of viewers.
        Internally everything is handled by the addon.
        """)
@Example("""
        command skinMeNotchForMe:
            trigger:
                fetch skin of player named "notch" and store it in {_skin}
                set displayed skin of player to {_skin} for player
        """)
@Since("1.0.0")
public class DisplayedSkinEff extends CustomEffect {

    static {
        Skript.registerEffect(DisplayedSkinEff.class,
                "set displayed skin of %players% to %skin% [for %-players%]");
    }

    @Override
    protected boolean initialize(SkriptParser.ParseResult parseResult) {
        return true;
    }

    @Override
    protected void execute(Event event) {
        List<Player> targets = getValuesOrNull(0, Player.class, event);
        Skin newSkin = getValueOrNull(1, Skin.class, event);
        List<Player> viewers = getValuesOrNull(2, Player.class, event);

        if (targets == null || newSkin == null) return;

        if (viewers == null || viewers.isEmpty()) {
            for (Player target : targets)
                PlayerSkinManager.setGlobalSkin(target.getUniqueId(), newSkin);

        } else {
            Set<UUID> viewerUuids = viewers.stream()
                    .map(Player::getUniqueId)
                    .collect(Collectors.toSet());

            for (Player target : targets)
                PlayerSkinManager.setSkinForViewers(target.getUniqueId(), viewerUuids, newSkin);
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "set displayed skin of players to skin for players";
    }
}

