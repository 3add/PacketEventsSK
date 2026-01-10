package threeadd.packetEventsSK.element.general.expressions.prop;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.SkriptParser;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import threeadd.packetEventsSK.element.entity.api.skin.Skin;
import threeadd.packetEventsSK.util.registry.PlayerSkinRegistry;
import threeadd.packetEventsSK.util.expressions.CustomPropertyExpression;

@SuppressWarnings("unused")
@Name("General - Player Skin of Player")
@Description("Gets the skin the player had when they joined the server")
@Example("""
        command revertToMySkin:
            trigger:
                set displayed skin of player to player's skin
        """)
public class PlayerSkinProp extends CustomPropertyExpression<Player, Skin> {
    static {
        PropertyExpression.register(PlayerSkinProp.class, Skin.class, "[player][ ]skin", "player");
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