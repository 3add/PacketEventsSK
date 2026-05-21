package dev.threeadd.packeteventssk.element.entity.field.entity;

import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.player.UserProfile;
import dev.threeadd.packeteventssk.api.entity.Skin;
import dev.threeadd.packeteventssk.api.util.field.FieldRegistrar;
import me.tofaa.entitylib.wrapper.WrapperPlayer;

public class FakePlayerEntityFieldRegistrar implements FieldRegistrar {

    @Override
    public void register() {

        FakeEntityFieldRegistry.INSTANCE.builder(EntityTypes.PLAYER, WrapperPlayer.class)
                .requiredField(Skin.class,
                        w -> new Skin(w.getTextures()),
                        (w, skin) -> w.setTextureProperties(skin.properties()),
                        "player skin", "skin")
                .requiredField(String.class,
                        WrapperPlayer::getUsername,
                        null,
                        "player username", "player name", "username", "name")
                .constructor(context -> {
                    FakeBaseEntityFieldRegistrar.CommonEntityData data = FakeBaseEntityFieldRegistrar.CommonEntityData.extract(context);

                    String name = context.getRequired("player username", String.class);
                    UserProfile profile = new UserProfile(data.uuid(), name);

                    WrapperPlayer player = new WrapperPlayer(profile, data.entityId());

                    // copy meta over
                    if (data.meta() != null) {
                        player.getEntityMeta().getMetadata().copyFrom(data.meta().getMetadata());
                    }

                    Skin skin = context.getRequired("player skin", Skin.class);
                    player.setTextureProperties(skin.properties());

                    FakeBaseEntityFieldRegistrar.finalizeEntitySetup(player, context);

                    return player;
                })
                .build();
    }
}
