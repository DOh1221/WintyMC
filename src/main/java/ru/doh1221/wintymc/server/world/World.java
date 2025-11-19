package ru.doh1221.wintymc.server.world;

import it.unimi.dsi.fastutil.ints.IntIntImmutablePair;

import java.util.HashMap;

public class World {

    public BlockPos spawnPosition;
    public HashMap<IntIntImmutablePair, Chunk> chunks = new HashMap<>();

    public static World testWorld() {

        World world = new World();
        world.spawnPosition = new BlockPos(0, 81, 0);

        for (int chunkX = 0; chunkX < 10; chunkX++) {
            int chunkZ = 0; // только одна полоса чанков

            Chunk chunk = new Chunk(chunkX, chunkZ);

            // Генерация плоской поверхности
            generateFlatChunk(chunk);

            world.chunks.put(new IntIntImmutablePair(chunkX, chunkZ), chunk);
        }

        return world;
    }

    private static void generateFlatChunk(Chunk chunk) {

        final byte BLOCK_ID = 1; // Stone
        final int HEIGHT = 1;

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {

                // 1. Ставим один блок на Y=80
                chunk.setBlock(x, HEIGHT, z, BLOCK_ID);

                // 2. Heightmap
                chunk.getHeightMapRaw()[z * 16 + x] = (byte) HEIGHT;

                // 3. Metadata = 0
                chunk.setMetadata(x, HEIGHT, z, (byte) 0);

                // 4. Освещение:
                //    - blocklight = 0
                //    - skylight = 15 (макс)
                chunk.setBlockLight(x, HEIGHT, z, (byte) 0);

                for (int y = HEIGHT + 1; y < 128; y++) {
                    chunk.setSkyLight(x, y, z, (byte) 15);
                }

                // Воздух ниже тоже должен иметь skyLight=0 (как в ванильной генерации)
                for (int y = 0; y < HEIGHT; y++) {
                    chunk.setSkyLight(x, y, z, (byte) 0);
                }
            }
        }
    }


}
