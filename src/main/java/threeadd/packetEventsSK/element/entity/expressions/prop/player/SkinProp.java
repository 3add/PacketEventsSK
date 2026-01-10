package threeadd.packetEventsSK.element.entity.expressions.prop.player;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.coll.CollectionUtils;
import me.tofaa.entitylib.wrapper.WrapperEntity;
import me.tofaa.entitylib.wrapper.WrapperPlayer;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import threeadd.packetEventsSK.element.entity.api.skin.Skin;
import threeadd.packetEventsSK.util.expressions.CustomPropertyExpression;

@Name("Fake Player Entity - Skin")
@Description("Represents the skin held within a player entity.")
@Example("""
        command fakeme:
            trigger:
                create new fake player entity at player for players:
                    set fake skin of the fake entity to player's skin
        
                    wait 10 seconds
                    kill fake entity the fake entity
        """)
@SuppressWarnings("unused")
public class SkinProp extends CustomPropertyExpression<WrapperEntity, Skin> {

    static {
        PropertyExpression.register(SkinProp.class, Skin.class, "fake[ ]skin", "fakeentity");
    }

    @SuppressWarnings("unused")
    public SkinProp() {
        super(Skin.class, WrapperEntity.class, true);
    }

    @Override
    protected boolean initialize(SkriptParser.ParseResult parseResult) {
        return true;
    }

    @Override
    protected @Nullable Skin getProperty(WrapperEntity input) {
        if (!(input instanceof WrapperPlayer player)) {
            Skript.warning("A packet entity that isn't a player doesn't have a skin");
            return null;
        }

        if (player.getTextureProperties().isEmpty()) return null;
        return new Skin(player.getTextureProperties());
    }

    @Override
    public Class<?> @Nullable [] acceptChange(Changer.ChangeMode mode) {
        if (mode.equals(Changer.ChangeMode.SET))
            return CollectionUtils.array(Skin.class);

        return null;
    }

    @Override
    public void change(Event event, Object @Nullable [] delta, Changer.ChangeMode mode) {
        WrapperEntity wrapper = getValueOrNull(0, WrapperEntity.class, event);
        Skin skin = getDeltaValueOrNull(delta, 0, Skin.class);
        if (wrapper == null || skin == null) return;

        if (!(wrapper instanceof WrapperPlayer wrapperPlayer)) {
            Skript.warning("A packet entity that isn't a player doesn't have a skin");
            return;
        }

        wrapperPlayer.setTextureProperties(skin.getProperties());
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "skin of fake player entity";
    }
}
