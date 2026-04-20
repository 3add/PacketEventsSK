package threeadd.packetEventsSK.element.general.expressions.prop;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.SkriptParser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.DefaultSyntaxInfos;
import org.skriptlang.skript.registration.SyntaxRegistry;
import threeadd.packetEventsSK.element.entity.api.skin.Skin;
import threeadd.packetEventsSK.util.expressions.CustomPropertyExpression;
import threeadd.packetEventsSK.util.registry.PlayerSkinRegistry;

@SuppressWarnings("unused")
@Name("General - Player Skin of Player")
@Description("""
            Gets the skin the player had when they joined the server
            NOTE: This only works on servers in online mode
            """)
@Example("""
        command revertToMySkin:
            trigger:
                set displayed skin of player to player's skin
        """)
@Since("1.0.0")
public class PlayerSkinProp extends CustomPropertyExpression<Player, Skin> {

    private static final boolean ONLINE_MODE = Bukkit.getServer().getServerConfig().isProxyOnlineMode();

    public static void register(SyntaxRegistry registry) {
        registry.register(
                SyntaxRegistry.EXPRESSION,
                DefaultSyntaxInfos.Expression.builder(PlayerSkinProp.class, Skin.class)
                        .addPatterns("%player%'s skin", "skin of %player%")
                        .build()
        );
    }

    public PlayerSkinProp() {
        super(Skin.class, Player.class, true);
    }

    @Override
    protected boolean initialize(SkriptParser.ParseResult parseResult) {
        return true;
    }

    @Override
    protected @Nullable Skin getProperty(Player input) {
        return PlayerSkinRegistry.getSkin(input.getUniqueId());
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "skin of player";
    }
}