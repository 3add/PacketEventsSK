package dev.threeadd.packeteventssk.element.simple.api.block;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.world.chunk.BaseChunk;
import com.github.retrooper.packetevents.protocol.world.chunk.Column;
import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBlockChange;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChunkData;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerMultiBlockChange;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerMultiBlockChange.EncodedBlock;
import org.jspecify.annotations.NonNull;

import java.util.Map;
import java.util.UUID;

public class FakeBlockListener implements PacketListener {

    @Override
    public void onPacketSend(@NonNull PacketSendEvent event) {
        onChunkDataSend(event);
        onBlockChangeSend(event);
        onMultiBlockChangeSend(event);
    }

    private void onChunkDataSend(PacketSendEvent event) {
        if (event.getPacketType() != PacketType.Play.Server.CHUNK_DATA) return;

        WrapperPlayServerChunkData wrapper = new WrapperPlayServerChunkData(event);
        int chunkX = wrapper.getColumn().getX();
        int chunkZ = wrapper.getColumn().getZ();

        // Pack chunk X and Z into a single long key
        long chunkKey = ((long) chunkX << 32) | (chunkZ & 0xFFFFFFFFL);

        // Get ONLY the fake blocks in this specific chunk
        Map<Vector3i, WrappedBlockState> chunkFakes = FakeBlockManager.getFakeBlocksForChunk(event.getUser().getUUID(), chunkKey);
        if (chunkFakes == null || chunkFakes.isEmpty()) return;

        Column column = wrapper.getColumn();
        BaseChunk[] chunks = column.getChunks();

        // Infer min section Y based on chunk array length
        int minSectionY = (chunks.length >= 24) ? -4 : 0;

        boolean modified = false;

        // Iterate ONLY over the fake blocks we need to set
        for (Map.Entry<Vector3i, WrappedBlockState> entry : chunkFakes.entrySet()) {
            Vector3i globalPos = entry.getKey();
            WrappedBlockState state = entry.getValue();

            int sectionY = globalPos.getY() >> 4;
            int chunkArrayIndex = sectionY - minSectionY;

            if (chunkArrayIndex >= 0 && chunkArrayIndex < chunks.length) {
                BaseChunk section = chunks[chunkArrayIndex];
                if (section != null) {
                    int localX = globalPos.getX() & 15;
                    int localY = globalPos.getY() & 15;
                    int localZ = globalPos.getZ() & 15;

                    section.set(localX, localY, localZ, state);
                    modified = true;
                }
            }
        }

        if (modified) {
            wrapper.setColumn(column);
            event.markForReEncode(true);
        }
    }

    private void onBlockChangeSend(PacketSendEvent event) {
        if (event.getPacketType() != PacketType.Play.Server.BLOCK_CHANGE)
            return;

        WrapperPlayServerBlockChange wrapper = new WrapperPlayServerBlockChange(event);
        Vector3i pos = wrapper.getBlockPosition();

        WrappedBlockState fakeState = FakeBlockManager.getFakeBlock(event.getUser().getUUID(), pos);
        if (fakeState == null) return;

        wrapper.setBlockState(fakeState);
        event.markForReEncode(true);
    }

    private void onMultiBlockChangeSend(PacketSendEvent event) {
        if (event.getPacketType() != PacketType.Play.Server.MULTI_BLOCK_CHANGE)
            return;

        WrapperPlayServerMultiBlockChange wrapper = new WrapperPlayServerMultiBlockChange(event);
        UUID playerUUID = event.getUser().getUUID();

        EncodedBlock[] blocks = wrapper.getBlocks();
        boolean modified = false;

        for (int i = 0; i < blocks.length; i++) {
            EncodedBlock encodedBlock = blocks[i];

            // Calculate global position from section position and local coordinates
            Vector3i sectionPos = wrapper.getChunkPosition();
            int globalX = (sectionPos.getX() << 4) | (encodedBlock.getX() & 15);
            int globalY = (sectionPos.getY() << 4) | (encodedBlock.getY() & 15);
            int globalZ = (sectionPos.getZ() << 4) | (encodedBlock.getZ() & 15);

            Vector3i globalPos = new Vector3i(globalX, globalY, globalZ);

            WrappedBlockState fakeState = FakeBlockManager.getFakeBlock(playerUUID, globalPos);
            if (fakeState != null) {
                blocks[i] = new EncodedBlock(fakeState.getGlobalId(), encodedBlock.getX(), encodedBlock.getY(), encodedBlock.getZ());
                modified = true;
            }
        }

        if (modified) {
            wrapper.setBlocks(blocks);
            event.markForReEncode(true);
        }
    }
}