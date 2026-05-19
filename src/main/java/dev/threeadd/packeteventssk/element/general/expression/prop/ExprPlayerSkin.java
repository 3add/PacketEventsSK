package dev.threeadd.packeteventssk.element.general.expression.prop;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import com.github.shanebeee.skr.Registration;
import dev.threeadd.packeteventssk.api.entity.Skin;
import dev.threeadd.packeteventssk.api.general.PlayerSkinRegistry;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class ExprPlayerSkin extends SimplePropertyExpression<Player, Skin> {

    private static final boolean ONLINE_MODE = Bukkit.getServer().getServerConfig().isProxyOnlineMode();

    public static void register(Registration reg) {
        if (ONLINE_MODE) {
            reg.newPropertyExpression(ExprPlayerSkin.class, Skin.class, "skin", "player")
                    .name("General - Player Skin of Player")
                    .description("""
                            Gets the skin the player had when they joined the server
                            NOTE: This only works on servers in online mode
                            """)
                    .examples("""
                            command revertToMySkin:
                                trigger:
                                    set displayed skin of player to player's skin
                            """)
                    .since("1.0.0")
                    .register();
        }
    }

    @Override
    public @Nullable Skin convert(Player player) {
        if (!ONLINE_MODE) return null;
        return PlayerSkinRegistry.getSkin(player.getUniqueId());
    }

    @Override
    public Class<? extends Skin> getReturnType() {
        return Skin.class;
    }

    @Override
    protected String getPropertyName() {
        return "skin";
    }
}