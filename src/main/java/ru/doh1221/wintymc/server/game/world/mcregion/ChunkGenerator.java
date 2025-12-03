package ru.doh1221.wintymc.server.game.world.mcregion;

import ru.doh1221.wintymc.server.game.nbt.NBTWriter;

import java.io.IOException;
import java.util.*;

/**
 * Fast-ish deterministic chunk generator that produces a simple terrain using per-column pseudo-noise.
 *
 * The generator produces chunk NBT bytes (uncompressed) with a minimal but compatible structure:
 * Root TAG_Compound -> "Level" TAG_Compound:
 *   - "Sections" TAG_List of TAG_Compound (each section contains Y, Blocks (4096), Data (2048), BlockLight(2048), SkyLight(2048))
 *   - "Biomes" byte[256]
 *   - "xPos", "zPos", "LastUpdate", "TerrainPopulated", "LightPopulated"
 *
 * Blocks use old-style numeric IDs (byte). We use:
 *   0 = air, 1 = stone, 2 = grass
 *
 * Generator is deterministic based on chunk coordinates and has an option to run in parallel.
 */
public final class ChunkGenerator {

    private ChunkGenerator() {}

    /**
     * Generate chunk NBT (uncompressed bytes) for given chunk coordinates.
     *
     * @param chunkX absolute chunk X
     * @param chunkZ absolute chunk Z
     */
    public static byte[] generateChunkNBT(int chunkX, int chunkZ) throws IOException {
        // height per column using a cheap deterministic noise (xorshift + sine) - fast
        int[][] height = new int[16][16];
        Random rand = new Random(Objects.hash(chunkX, chunkZ));
        double base = 64.0;
        for (int dx = 0; dx < 16; dx++) {
            for (int dz = 0; dz < 16; dz++) {
                // combine hashed pseudo-random with smooth offset
                long seed = ((chunkX * 16L + dx) * 73428767L) ^ ((chunkZ * 16L + dz) * 91236721L);
                seed ^= 0x9E3779B97F4A7C15L;
                double noise = fractalNoise(seed);
                int h = (int) Math.round(base + noise * 12.0);
                height[dx][dz] = Math.max(1, Math.min(250, h));
            }
        }

        // determine which sections are needed
        int maxH = 0;
        for (int dx = 0; dx < 16; dx++) for (int dz = 0; dz < 16; dz++) maxH = Math.max(maxH, height[dx][dz]);
        int maxSection = (maxH >> 4); // section Y (0..15) that contains top blocks

        List<Map<String, Object>> sections = new ArrayList<>(Math.max(1, maxSection + 1));
        // For each section y create arrays
        for (int sectionY = 0; sectionY <= maxSection; sectionY++) {
            byte[] blocks = new byte[4096]; // ids
            byte[] data = new byte[2048]; // nibble-packed block data, zeros
            byte[] blockLight = new byte[2048];
            byte[] skyLight = new byte[2048];
            Arrays.fill(blocks, (byte) 0);
            Arrays.fill(data, (byte) 0);
            Arrays.fill(blockLight, (byte) 0);
            Arrays.fill(skyLight, (byte) (sectionY >= 15 ? 0 : (byte) 0xFF)); // full skylight for demo (not exact)

            // fill blocks for each (x,z,y) inside the section
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    int columnTop = height[x][z];
                    for (int y = 0; y < 16; y++) {
                        int worldY = (sectionY << 4) | y;
                        int idx = (y << 8) | (z << 4) | x; // y*256 + z*16 + x
                        byte id = 0;
                        if (worldY <= columnTop - 2) {
                            id = 1; // stone
                        } else if (worldY == columnTop - 1) {
                            id = 2; // grass (top)
                        } else {
                            id = 0; // air
                        }
                        blocks[idx] = id;
                    }
                }
            }

            Map<String, Object> sec = new LinkedHashMap<>();
            sec.put("Y", (byte) sectionY);
            sec.put("Blocks", blocks);
            sec.put("Data", data);
            sec.put("BlockLight", blockLight);
            sec.put("SkyLight", skyLight);
            sections.add(sec);
        }

        // Biomes (256 bytes)
        byte[] biomes = new byte[256];
        Arrays.fill(biomes, (byte) 1); // plains-ish

        Map<String, Object> level = new LinkedHashMap<>();
        level.put("Sections", sections);
        level.put("Biomes", biomes);
        level.put("xPos", chunkX);
        level.put("zPos", chunkZ);
        level.put("LastUpdate", System.currentTimeMillis());
        level.put("TerrainPopulated", (byte) 1);
        level.put("LightPopulated", (byte) 1);
        // HeightMap: int[256] representing column heights (optional but useful)
        int[] heightMap = new int[256];
        for (int x = 0; x < 16; x++) for (int z = 0; z < 16; z++) heightMap[z << 4 | x] = height[x][z];
        level.put("HeightMap", heightMap);

        Map<String, Object> root = new LinkedHashMap<>();
        root.put("Level", level);

        return NBTWriter.writeNamedCompound("", root);
    }

    // cheap smooth periodic noise based on seed
    private static double fractalNoise(long seed) {
        // three octaves of deterministic noise
        double n = 0.0;
        double amp = 1.0;
        double freq = 1.0 / 27.0;
        long s = seed;
        for (int o = 0; o < 3; o++) {
            s = xorshift64(s);
            double val = ((s & 0x7FFFFFFFFFFFFFFFL) / (double) Long.MAX_VALUE) * 2.0 - 1.0;
            n += val * amp;
            amp *= 0.5;
            freq *= 2.0;
        }
        // clamp
        return Math.max(-1.0, Math.min(1.0, n));
    }

    private static long xorshift64(long x) {
        x ^= (x << 13);
        x ^= (x >>> 7);
        x ^= (x << 17);
        return x;
    }
}