package ru.doh1221.wintymc.server.game.world.chunk;

import ru.doh1221.wintymc.server.game.world.test.Entity;
import ru.doh1221.wintymc.server.game.world.test.TileEntity;
import ru.doh1221.wintymc.server.utils.location.Loc3D;

import java.util.*;

/**
 * Clean, standalone chunk container.
 * Holds ALL data required by Packet51MapChunk:
 * - blocks (byte)
 * - metadata (nibble array)
 * - block light (nibble array)
 * - sky light (nibble array)
 *
 * Also stores:
 * - heightmap
 * - tile entities
 * - entities
 *
 * No NMS, no lighting engine, no generation logic.
 */
public class Chunk {

    public static final int SIZE = 16;
    public static final int HEIGHT = 128;
    public static final int TOTAL_BLOCKS = SIZE * SIZE * HEIGHT;

    // Block IDs
    public byte[] blocks = new byte[TOTAL_BLOCKS];

    // Metadata (0–15)
    private final NibbleArray metadata = new NibbleArray(TOTAL_BLOCKS);

    // Block light (0–15)
    private final NibbleArray blockLight = new NibbleArray(TOTAL_BLOCKS);

    // Sky light (0–15)
    private final NibbleArray skyLight = new NibbleArray(TOTAL_BLOCKS);

    // Heightmap (not in Packet51, but useful)
    private final byte[] heightMap = new byte[SIZE * SIZE];

    private final int chunkX;
    private final int chunkZ;

    private final List<List<Entity>> entitySlices = new ArrayList<>(HEIGHT / 16);
    private final Map<Loc3D, TileEntity> tileEntities = new HashMap<>();

    public Chunk(int chunkX, int chunkZ) {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;

        for (int i = 0; i < HEIGHT / 16; i++) {
            entitySlices.add(new ArrayList<>());
        }
    }

    public Chunk(int chunkX, int chunkZ, byte[] data) {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;

        for (int i = 0; i < HEIGHT / 16; i++) {
            entitySlices.add(new ArrayList<>());
        }
    }

    // --------------------------------
    // Index helper
    // --------------------------------
    public static int index(int x, int y, int z) {
        // X (0–15), Y (0–127), Z (0–15)
        return y + (z * HEIGHT) + (x * HEIGHT * SIZE);
    }


    // --------------------------------
    // Block access
    // --------------------------------
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

    // --------------------------------
    // Heightmap
    // --------------------------------
    private void updateHeightMap(int x, int y, int z) {
        int idx = z * SIZE + x;
        int prev = Byte.toUnsignedInt(heightMap[idx]);

        if (y > prev && getBlock(x, y, z) != 0) {
            heightMap[idx] = (byte) y;
        }
    }

    // --------------------------------
    // Tile entities
    // --------------------------------
    public TileEntity getTileEntity(int x, int y, int z) {
        return tileEntities.get(new Loc3D(x, y, z));
    }

    public void setTileEntity(int x, int y, int z, TileEntity te) {
        Loc3D pos = new Loc3D(x, y, z);
        te.setPosition(pos);
        tileEntities.put(pos, te);
    }

    public void removeTileEntity(int x, int y, int z) {
        tileEntities.remove(new Loc3D(x, y, z));
    }

    // --------------------------------
    // Packet51MapChunk encoder
    // --------------------------------
    // Внутри класса Chunk — замените существующие методы на эти

    /**
     * Формирует payload exactly как в оригинальном Packet51 для полного чанка 16x128x16.
     * Формат: [blocks (TOTAL_BLOCKS bytes)] [metadata (TOTAL_BLOCKS/2 bytes)]
     *         [blockLight (TOTAL_BLOCKS/2 bytes)] [skyLight (TOTAL_BLOCKS/2 bytes)]
     */
    public byte[] toPacketData() {
        // total: TOTAL_BLOCKS + 3 * (TOTAL_BLOCKS / 2) = TOTAL_BLOCKS * 5 / 2
        int nibbleSize = TOTAL_BLOCKS / 2;
        int size = TOTAL_BLOCKS + 3 * nibbleSize;
        byte[] out = new byte[size];
        int cursor = 0;

        // 1) Blocks — копируем весь массив блоков
        System.arraycopy(this.blocks, 0, out, cursor, TOTAL_BLOCKS);
        cursor += TOTAL_BLOCKS;

        // 2) Metadata — NibbleArray уже хранит упакованные байты (по 2 nibbles в байт)
        System.arraycopy(this.metadata.raw(), 0, out, cursor, nibbleSize);
        cursor += nibbleSize;

        // 3) Block light
        System.arraycopy(this.blockLight.raw(), 0, out, cursor, nibbleSize);
        cursor += nibbleSize;

        // 4) Sky light
        System.arraycopy(this.skyLight.raw(), 0, out, cursor, nibbleSize);
        cursor += nibbleSize;

        // на всякий случай:
        if (cursor != size) {
            throw new IllegalStateException("PacketData size mismatch: expected=" + size + " wrote=" + cursor);
        }

        return out;
    }

    // --------------------------------
    // Getters
    // --------------------------------

    public int getChunkX() { return chunkX; }
    public int getChunkZ() { return chunkZ; }

    public byte[] getBlocksRaw() { return blocks; }
    public byte[] getHeightMapRaw() { return heightMap; }
    public Map<Loc3D, TileEntity> getTileEntities() { return tileEntities; }
}
