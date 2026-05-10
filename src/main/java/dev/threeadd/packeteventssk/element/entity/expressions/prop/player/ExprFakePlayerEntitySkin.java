package dev.threeadd.packeteventssk.element.entity.expressions.prop.player;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import com.github.retrooper.packetevents.protocol.player.TextureProperty;
import com.github.shanebeee.skr.Registration;
import dev.threeadd.packeteventssk.api.entity.Skin;
import me.tofaa.entitylib.wrapper.WrapperEntity;
import me.tofaa.entitylib.wrapper.WrapperPlayer;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ExprFakePlayerEntitySkin extends SimplePropertyExpression<WrapperEntity, Skin> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprFakePlayerEntitySkin.class, Skin.class, "fake skin", "fakeentity")
                .name("Fake Player Entity - Skin")
                .description("Represents the skin held within a player entity.")
                .examples("""
                        command fakeme:
                            trigger:
                                set {_p} to player
                                create new fake player entity at player for players:
                                    set fake skin of the fake entity to {_p}'s skin
                        
                                    wait 10 seconds
                                    kill fake entity the fake entity
                        """)
                .since("1.0.0")
                .register();
    }

    @Override
    public @Nullable Skin convert(WrapperEntity entity) {
        if (entity instanceof WrapperPlayer player) {
            if (!player.getTextureProperties().isEmpty()) {
                return new Skin(player.getTextureProperties());
            }
        } else {
            Skript.warning("A packet entity that isn't a player doesn't have a skin.");
        }
        return null;
    }

    @Override
    public Class<?> @Nullable [] acceptChange(Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET) {
            return CollectionUtils.array(Skin.class);
        }
        return null;
    }

    @Override
    public void change(Event event, Object @Nullable [] delta, Changer.ChangeMode mode) {
        if (mode != Changer.ChangeMode.SET || delta == null || delta.length == 0 || !(delta[0] instanceof Skin(List<TextureProperty> properties))) return;

        WrapperEntity[] entities = getExpr().getAll(event);
        if (entities == null) return;

        for (WrapperEntity entity : entities) {
            if (entity instanceof WrapperPlayer player) {
                player.setTextureProperties(properties);
            } else {
                Skript.warning("A packet entity that isn't a player doesn't have a skin.");
            }
        }
    }

    @Override
    public Class<? extends Skin> getReturnType() {
        return Skin.class;
    }

    @Override
    protected String getPropertyName() {
        return "fake skin";
    }
}