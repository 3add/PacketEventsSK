package threeadd.packetEventsSK.element.simple.api;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.event.UserDisconnectEvent;
import com.github.retrooper.packetevents.protocol.chat.RemoteChatSession;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoUpdate;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoUpdate.PlayerInfo;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChatSessionListener implements PacketListener {

    private static final Map<UUID, RemoteChatSession> sessions = new HashMap<>();

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (!event.getPacketType().equals(PacketType.Play.Server.PLAYER_INFO_UPDATE))
            return;

        WrapperPlayServerPlayerInfoUpdate packet = new WrapperPlayServerPlayerInfoUpdate(event);
        for (PlayerInfo entry : packet.getEntries()) {
            if (entry.getChatSession() == null)
                continue;

            sessions.put(entry.getGameProfile().getUUID(), entry.getChatSession());
        }
    }

    @Override
    public void onUserDisconnect(UserDisconnectEvent event) {
        sessions.remove(event.getUser().getUUID());
    }

    public static @Nullable RemoteChatSession getChatSession(UUID ownerId) {
        return sessions.getOrDefault(ownerId, null);
    }
}
