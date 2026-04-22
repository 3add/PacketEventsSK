package dev.threeadd.packeteventssk.element.simple.api.block;

import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerMultiBlockChange;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerMultiBlockChange.EncodedBlock;
import dev.threeadd.packeteventssk.util.UserManager;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import org.bukkit.block.data.BlockData;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class FakeBlockManager {

    // UUID -> ChunkKey -> Position -> BlockState
    private final static Map<UUID, Map<Long, Map<Vector3i, WrappedBlockState>>> fakeBlocks = new ConcurrentHashMap<>();

    public static void setFakeBlocks(UUID uuid, Map<Vector3i, BlockData> blockDataMap) {
        if (blockDataMap == null || blockDataMap.isEmpty()) return;

        User user = UserManager.getUser(uuid);
        if (user == null) return;

        Map<Long, Map<Vector3i, WrappedBlockState>> playerMap = fakeBlocks.computeIfAbsent(uuid, k -> new ConcurrentHashMap<>());

        Map<Vector3i, List<EncodedBlock>> changesBySection = new HashMap<>();

        blockDataMap.forEach((pos, blockData) -> {
            WrappedBlockState state = SpigotConversionUtil.fromBukkitBlockData(blockData);

            int chunkX = pos.getX() >> 4;
            int chunkZ = pos.getZ() >> 4;
            long chunkKey = ((long) chunkX << 32) | (chunkZ & 0xFFFFFFFFL);

            Map<Vector3i, WrappedBlockState> chunkMap = playerMap.computeIfAbsent(chunkKey, k -> new ConcurrentHashMap<>());
            chunkMap.put(pos, state);

            int sectionY = pos.getY() >> 4;
            Vector3i sectionPos = new Vector3i(chunkX, sectionY, chunkZ);

            int localX = pos.getX() & 15;
            int localY = pos.getY() & 15;
            int localZ = pos.getZ() & 15;

            changesBySection.computeIfAbsent(sectionPos, k -> new ArrayList<>())
                    .add(new EncodedBlock(state.getGlobalId(), localX, localY, localZ));
        });

        for (Map.Entry<Vector3i, List<EncodedBlock>> entry : changesBySection.entrySet()) {
            EncodedBlock[] blockArray = entry.getValue().toArray(new EncodedBlock[0]);
            user.sendPacket(new WrapperPlayServerMultiBlockChange(entry.getKey(), true, blockArray));
        }
    }

    public static Map<Vector3i, WrappedBlockState> getFakeBlocksForChunk(UUID uuid, long chunkKey) {
        Map<Long, Map<Vector3i, WrappedBlockState>> playerMap = fakeBlocks.get(uuid);
        if (playerMap == null) return null;

        return playerMap.get(chunkKey);
    }

    public static WrappedBlockState getFakeBlock(UUID uuid, Vector3i pos) {
        Map<Long, Map<Vector3i, WrappedBlockState>> playerMap = fakeBlocks.get(uuid);
        if (playerMap == null) return null;

        int chunkX = pos.getX() >> 4;
        int chunkZ = pos.getZ() >> 4;
        long chunkKey = ((long) chunkX << 32) | (chunkZ & 0xFFFFFFFFL);

        Map<Vector3i, WrappedBlockState> chunkMap = playerMap.get(chunkKey);
        if (chunkMap == null) return null;

        return chunkMap.get(pos);
    }
}