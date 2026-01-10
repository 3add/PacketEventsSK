package threeadd.packetEventsSK.element.simple.api;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.protocol.player.UserProfile;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoUpdate;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoUpdate.PlayerInfo;
import threeadd.packetEventsSK.element.entity.api.skin.Skin;

import static com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoUpdate.Action.ADD_PLAYER;

public class PlayerSkinListener implements PacketListener {

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (!event.getPacketType().equals(PacketType.Play.Server.PLAYER_INFO_UPDATE))
            return;

        User viewer = event.getUser();

        WrapperPlayServerPlayerInfoUpdate packet = new WrapperPlayServerPlayerInfoUpdate(event);
        if (!packet.getActions().contains(ADD_PLAYER))
            return;

        for (PlayerInfo entry : packet.getEntries()) {
            UserProfile profile = entry.getGameProfile();
            Skin skin = PlayerSkinManager.getSkinForViewer(profile.getUUID(), viewer.getUUID());

            if (skin == null)
                continue;

            profile.setTextureProperties(skin.getProperties());
        }
    }
}
