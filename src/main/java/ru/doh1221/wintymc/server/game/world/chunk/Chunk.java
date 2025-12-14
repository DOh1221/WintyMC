package ru.doh1221.wintymc.server.game.world.chunk;

import lombok.Getter;

/**
 * Clean, standalone chunk container.
 * Holds ALL data required by Packet51MapChunk:
 * - blocks (byte)
 * - metadata (nibble array)
 * - block light (nibble array)
 * - sky light (nibble array)
 * <p>
 * Also stores:
 * - heightmap
 * - tile entities
 * - entities
 * <p>
 * No NMS, no lighting engine, no generation logic.
 */
public class Chunk {

    public static final int SIZE = 16;
    public static final int HEIGHT = 128;
    public static final int TOTAL_BLOCKS = SIZE * SIZE * HEIGHT;
    // Metadata (0–15)
    private final NibbleArray metadata = new NibbleArray(TOTAL_BLOCKS);
    // Block light (0–15)
    private final NibbleArray blockLight = new NibbleArray(TOTAL_BLOCKS);
    // Sky light (0–15)
    private final NibbleArray skyLight = new NibbleArray(TOTAL_BLOCKS);
    // Heightmap (not in Packet51, but useful)
    private final byte[] heightMap = new byte[SIZE * SIZE];
    @Getter
    private final int chunkX;
    @Getter
    private final int chunkZ;
    // Block IDs
    public byte[] blocks = new byte[TOTAL_BLOCKS];

    public Chunk(int chunkX, int chunkZ) {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
    }

    public Chunk(int chunkX, int chunkZ, byte[] data) {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.blocks = data;
    }

    public static int index(int x, int y, int z) {
        // X (0–15), Y (0–127), Z (0–15)
        return y + (z * HEIGHT) + (x * HEIGHT * SIZE);
    }

    public byte getBlock(int x, int y, int z) {
        return blocks[index(x, y, z)];
    }

    public void setBlock(int x, int y, int z, byte id) {
        blocks[index(x, y, z)] = id;
        updateHeightMap(x, y, z);
    }

    public byte getMetadata(int x, int y, int z) {
        return metadata.get(x, y, z);
    }

    public void setMetadata(int x, int y, int z, byte value) {
        metadata.set(x, y, z, value);
    }

    public byte getBlockLight(int x, int y, int z) {
        return blockLight.get(x, y, z);
    }

    public void setBlockLight(int x, int y, int z, byte value) {
        blockLight.set(x, y, z, value);
    }

    public byte getSkyLight(int x, int y, int z) {
        return skyLight.get(x, y, z);
    }

    public void setSkyLight(int x, int y, int z, byte value) {
        skyLight.set(x, y, z, value);
    }

    private void updateHeightMap(int x, int y, int z) {
        int idx = z * SIZE + x;
        int prev = Byte.toUnsignedInt(heightMap[idx]);

        if (y > prev && getBlock(x, y, z) != 0) {
            heightMap[idx] = (byte) y;
        }
    }
    public byte[] toPacketData() {
        // total: TOTAL_BLOCKS + 3 * (TOTAL_BLOCKS / 2) = TOTAL_BLOCKS * 5 / 2
        int nibbleSize = TOTAL_BLOCKS / 2;
        int size = TOTAL_BLOCKS + 3 * nibbleSize;
        byte[] out = new byte[size];
        int cursor = 0;

        System.arraycopy(this.blocks, 0, out, cursor, TOTAL_BLOCKS);
        cursor += TOTAL_BLOCKS;

        System.arraycopy(this.metadata.raw(), 0, out, cursor, nibbleSize);
        cursor += nibbleSize;

        System.arraycopy(this.blockLight.raw(), 0, out, cursor, nibbleSize);
        cursor += nibbleSize;

        System.arraycopy(this.skyLight.raw(), 0, out, cursor, nibbleSize);
        cursor += nibbleSize;

        if (cursor != size) {
            throw new IllegalStateException("PacketData size mismatch: expected=" + size + " wrote=" + cursor);
        }

        return out;
    }

    public byte[] getBlocksRaw() {
        return blocks;
    }

    public byte[] getHeightMapRaw() {
        return heightMap;
    }
}
