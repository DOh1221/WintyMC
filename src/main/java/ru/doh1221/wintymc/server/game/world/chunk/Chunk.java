package ru.doh1221.wintymc.server.game.world.chunk;

import lombok.Getter;
import ru.doh1221.wintymc.server.network.netty.tcp.packet.game.world.Packet51MapChunk;

import java.util.Arrays;

public class Chunk {

    public static final int SIZE = 16;
    public static final int HEIGHT = 128;
    public static final int TOTAL_BLOCKS = SIZE * SIZE * HEIGHT;
    private static final byte[] EMPTY_BLOCKS;

    static {
        EMPTY_BLOCKS = new byte[TOTAL_BLOCKS];
        Arrays.fill(EMPTY_BLOCKS, (byte) 0xFF);
    }

    @Getter
    private final int chunkX;
    @Getter
    private final int chunkZ;

    private byte[] blocks;
    private NibbleArray metadata;
    private NibbleArray blockLight;
    private NibbleArray skyLight;
    private final byte[] heightMap = new byte[SIZE * SIZE];

    public Chunk(int chunkX, int chunkZ, byte[] blocks) {
        this(chunkX, chunkZ);
        this.blocks = blocks;

        this.metadata = new NibbleArray(TOTAL_BLOCKS);
        this.blockLight = new NibbleArray(TOTAL_BLOCKS);
        this.skyLight = new NibbleArray(TOTAL_BLOCKS);

        //recalculateHeightMap();
    }

    public Chunk(int chunkX, int chunkZ) {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
    }

    public static Chunk empty(int cx, int cz) {
        return new Chunk(cx, cz, EMPTY_BLOCKS);
    }

    public static int index(int x, int y, int z) {
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

    public void recalculateHeightMap() {
        for (int x = 0; x < SIZE; x++) {
            for (int z = 0; z < SIZE; z++) {

                int height = 0;
                int baseIndex = (x * HEIGHT * SIZE) + (z * HEIGHT);

                for (int y = HEIGHT - 1; y >= 0; y--) {
                    if (blocks[baseIndex + y] != 0) {
                        height = y;
                        break;
                    }
                }

                heightMap[z * SIZE + x] = (byte) height;
            }
        }
    }

    public static Chunk fromPacketData(Packet51MapChunk packet) {
        if (packet == null) {
            throw new IllegalArgumentException("packet == null");
        }

        byte[] data = packet.getRawData();
        int expectedSize = TOTAL_BLOCKS * 5 / 2;

        if (data.length != expectedSize) {
            throw new IllegalStateException(
                    "Invalid Packet51 payload size: expected=" + expectedSize + " got=" + data.length
            );
        }

        Chunk chunk = new Chunk(packet.getChunkX(), packet.getChunkZ());

        int cursor = 0;
        int nibbleSize = TOTAL_BLOCKS / 2;

        System.arraycopy(data, cursor, chunk.blocks, 0, TOTAL_BLOCKS);
        cursor += TOTAL_BLOCKS;

        System.arraycopy(data, cursor, chunk.metadata.raw(), 0, nibbleSize);
        cursor += nibbleSize;

        System.arraycopy(data, cursor, chunk.blockLight.raw(), 0, nibbleSize);
        cursor += nibbleSize;

        System.arraycopy(data, cursor, chunk.skyLight.raw(), 0, nibbleSize);
        cursor += nibbleSize;

        for (int x = 0; x < SIZE; x++) {
            for (int z = 0; z < SIZE; z++) {
                int idx = z * SIZE + x;
                int height = 0;

                for (int y = HEIGHT - 1; y >= 0; y--) {
                    if (chunk.getBlock(x, y, z) != 0) {
                        height = y;
                        break;
                    }
                }
                chunk.heightMap[idx] = (byte) height;
            }
        }

        return chunk;
    }

    public static Packet51MapChunk toPacketData(Chunk chunk) {
        int nibbleSize = TOTAL_BLOCKS / 2;
        int size = TOTAL_BLOCKS + 3 * nibbleSize;
        byte[] out = new byte[size];
        int cursor = 0;

        System.arraycopy(chunk.blocks, 0, out, cursor, TOTAL_BLOCKS);
        cursor += TOTAL_BLOCKS;

        System.arraycopy(chunk.metadata.raw(), 0, out, cursor, nibbleSize);
        cursor += nibbleSize;

        System.arraycopy(chunk.blockLight.raw(), 0, out, cursor, nibbleSize);
        cursor += nibbleSize;

        System.arraycopy(chunk.skyLight.raw(), 0, out, cursor, nibbleSize);
        cursor += nibbleSize;

        return new Packet51MapChunk(
                chunk.getChunkX() * 16,
                0,
                chunk.getChunkZ() * 16,
                16,
                128,
                16,
                out
        );
    }

    public byte[] getBlocksRaw() {
        return blocks;
    }

    public byte[] getHeightMapRaw() {
        return heightMap;
    }
}
