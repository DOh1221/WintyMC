package ru.armlix.winty.game.chunking.chunk;

import lombok.Getter;
import lombok.Setter;
import ru.armlix.winty.game.SizeInfo;
import ru.armlix.winty.network.netty.tcp.packet.game.world.Packet51MapChunk;

import java.util.Arrays;


public class Chunk {
    
    public static final int TOTAL_BLOCKS = SizeInfo.chunkSizeX * SizeInfo.chunkSizeZ * SizeInfo.chunkSizeY;

    @Getter
    private final int chunkX;
    @Getter
    private final int chunkZ;

    private static final boolean[] BLOCK_OPAQUE = new boolean[256];
    private static final byte[] BLOCK_EMISSION = new byte[256];
    private final NibbleArray metadata = new NibbleArray(TOTAL_BLOCKS);
    private final NibbleArray blockLight = new NibbleArray(TOTAL_BLOCKS);
    private final NibbleArray skyLight = new NibbleArray(TOTAL_BLOCKS);

    private final byte[] heightMap = new byte[SizeInfo.chunkSizeX * SizeInfo.chunkSizeZ];

    private final byte[] blocks;

    @Setter
    @Getter
    private long lastUpdated;

    @Getter
    private boolean isDirty;

    static {
        for (int i = 0; i < 256; i++) {
            BLOCK_OPAQUE[i] = true;
            BLOCK_EMISSION[i] = 0;
        }

        BLOCK_OPAQUE[0] = false;

        BLOCK_OPAQUE[50] = false;
        BLOCK_EMISSION[50] = 14;

        BLOCK_OPAQUE[10] = false;
        BLOCK_EMISSION[10] = 15;
        BLOCK_OPAQUE[11] = false;
        BLOCK_EMISSION[11] = 15;
    }

    public Chunk(int chunkX, int chunkZ) {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.blocks = new byte[TOTAL_BLOCKS];
    }

    public Chunk(int chunkX, int chunkZ, byte[] data) {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.blocks = data;
    }

    public static int index(int x, int y, int z) {
        return y + (z * SizeInfo.chunkSizeY) + (x * SizeInfo.chunkSizeY * SizeInfo.chunkSizeX);
    }

    public byte getBlock(int x, int y, int z) {
        return blocks[index(x, y, z)];
    }

    public void setBlock(int x, int y, int z, byte id) {
        blocks[index(x, y, z)] = id;
        updateHeightMap(x, y, z);
        markDirty();
    }

    public byte getBlockLight(int x, int y, int z) {
        return blockLight.get(x, y, z);
    }

    public void setBlockLight(int x, int y, int z, byte value) {
        blockLight.set(x, y, z, value);
        markDirty();
    }

    public byte getSkyLight(int x, int y, int z) {
        return skyLight.get(x, y, z);
    }

    public void setSkyLight(int x, int y, int z, byte value) {
        skyLight.set(x, y, z, value);
    }

    public byte[] getBlocksRaw() {
        return blocks;
    }

    public byte[] getHeightMapRaw() {
        return heightMap;
    }

    public void markDirty() {
        this.isDirty = true;
        setLastUpdated(System.currentTimeMillis());
    }

    private void updateHeightMap(int x, int y, int z) {
        int idx = z * SizeInfo.chunkSizeX + x;
        int prev = Byte.toUnsignedInt(heightMap[idx]);

        if (y > prev && getBlock(x, y, z) != 0) {
            heightMap[idx] = (byte) y;
        }
    }

    private void propagateBlockLight() {
        final int[] dx = {1, -1, 0, 0, 0, 0};
        final int[] dy = {0, 0, 1, -1, 0, 0};
        final int[] dz = {0, 0, 0, 0, 1, -1};

        boolean updated;
        do {
            updated = false;

            for (int x = 0; x < SizeInfo.chunkSizeX; x++) {
                for (int z = 0; z < SizeInfo.chunkSizeZ; z++) {
                    for (int y = 0; y < SizeInfo.chunkSizeY; y++) {
                        byte level = getBlockLight(x, y, z);
                        if (level <= 1) continue;

                        for (int i = 0; i < 6; i++) {
                            int nx = x + dx[i];
                            int ny = y + dy[i];
                            int nz = z + dz[i];

                            if (nx < 0 || ny < 0 || nz < 0 ||
                                    nx >= SizeInfo.chunkSizeX || ny >= SizeInfo.chunkSizeY || nz >= SizeInfo.chunkSizeZ)
                                continue;

                            if (BLOCK_OPAQUE[getBlock(nx, ny, nz)])
                                continue;

                            byte target = getBlockLight(nx, ny, nz);
                            if (target + 1 < level) {
                                blockLight.set(nx, ny, nz, (byte) (level - 1));
                                updated = true;
                            }
                        }
                    }
                }
            }
        } while (updated);
    }

    private void initBlockLight() {
        // clear
        for (int i = 0; i < TOTAL_BLOCKS; i++) {
            blockLight.raw()[i >> 1] = 0;
        }

        // sources
        for (int x = 0; x < SizeInfo.chunkSizeX; x++) {
            for (int z = 0; z < SizeInfo.chunkSizeZ; z++) {
                for (int y = 0; y < SizeInfo.chunkSizeY; y++) {
                    byte id = getBlock(x, y, z);
                    byte emission = BLOCK_EMISSION[id];
                    if (emission > 0) {
                        blockLight.set(x, y, z, emission);
                    }
                }
            }
        }

        propagateBlockLight();
    }

    private void initSkyLight() {
        for (int x = 0; x < SizeInfo.chunkSizeX; x++) {
            for (int z = 0; z < SizeInfo.chunkSizeZ; z++) {
                int surface = Byte.toUnsignedInt(heightMap[z * SizeInfo.chunkSizeX + x]);
                boolean blocked = false;

                for (int y = SizeInfo.chunkSizeY - 1; y >= 0; y--) {
                    if (!blocked && y >= surface) {
                        skyLight.set(x, y, z, (byte) 15);
                    } else {
                        byte id = getBlock(x, y, z);
                        if (BLOCK_OPAQUE[id]) {
                            blocked = true;
                            skyLight.set(x, y, z, (byte) 0);
                        } else {
                            skyLight.set(x, y, z, (byte) 15);
                        }
                    }
                }
            }
        }
    }

    private void computeHeightMap() {
        for (int x = 0; x < SizeInfo.chunkSizeX; x++) {
            for (int z = 0; z < SizeInfo.chunkSizeZ; z++) {
                int idx = z * SizeInfo.chunkSizeX + x;
                int y;

                for (y = SizeInfo.chunkSizeY - 1; y >= 0; y--) {
                    if (getBlock(x, y, z) != 0) {
                        break;
                    }
                }
                heightMap[idx] = (byte) Math.max(0, y);
            }
        }
    }

    public void initializeLighting() {
        computeHeightMap();
        initSkyLight();
        initBlockLight();
    }

    public static Packet51MapChunk toPacketData(Chunk chunk) {
        // total: TOTAL_BLOCKS + 3 * (TOTAL_BLOCKS / 2) = TOTAL_BLOCKS * 5 / 2
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

        if (cursor != size) {
            throw new IllegalStateException("PacketData size mismatch: expected=" + size + " wrote=" + cursor);
        }

        return new Packet51MapChunk(
                chunk.getChunkX(),
                0,
                chunk.getChunkZ(),
                16,
                128,
                16,
                out
        );
    }

}