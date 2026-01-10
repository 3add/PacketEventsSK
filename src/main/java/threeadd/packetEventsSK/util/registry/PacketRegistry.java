package threeadd.packetEventsSK.util.registry;

import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.protocol.player.InteractionHand;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerGameTestHighlightPos;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTabComplete;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("DataFlowIssue")
public class PacketRegistry {

    static final Map<PacketTypeCommon, EmptyConstructor<?>> emptyWrappers = new ConcurrentHashMap<>();

    static {
        emptyWrappers.put(PacketType.Play.Server.DESTROY_ENTITIES, WrapperPlayServerDestroyEntities::new);
        emptyWrappers.put(PacketType.Play.Server.TAB_COMPLETE, () -> new WrapperPlayServerTabComplete(null, null, List.of()));
        emptyWrappers.put(PacketType.Play.Server.GAME_TEST_HIGHLIGHT_POS, () -> new WrapperPlayServerGameTestHighlightPos(null, Vector3i.zero()));
        emptyWrappers.put(PacketType.Play.Client.INTERACT_ENTITY, () -> new WrapperPlayClientInteractEntity(-1,
                WrapperPlayClientInteractEntity.InteractAction.INTERACT, InteractionHand.MAIN_HAND, Optional.empty(), Optional.empty()));
    }

    public static PacketWrapper<?> createEmpty(PacketTypeCommon type) {
        return emptyWrappers.get(type).create();
    }

    public static boolean hasEmptyWrapper(PacketTypeCommon type) {
        return emptyWrappers.containsKey(type);
    }

    @FunctionalInterface
    private interface EmptyConstructor<T extends PacketWrapper<?>> {
        T create();
    }
}