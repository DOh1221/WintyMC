package ru.doh1221.wintymc.server.game.world.test;

import it.unimi.dsi.fastutil.ints.IntIntImmutablePair;
import ru.doh1221.wintymc.server.game.world.chunk.Chunk;
import ru.doh1221.wintymc.server.utils.location.Loc3D;

import java.util.HashMap;

public class World {

    public Loc3D spawnPosition;
    public HashMap<IntIntImmutablePair, Chunk> chunks = new HashMap<>();

    public static World testWorld() {
        World world = new World();
        world.spawnPosition = new Loc3D(0, 81, 0);
        int sizeBlocks = 200;
        int sizeChunks = (int) Math.ceil(sizeBlocks / 16.0); // 13

        for (int chunkX = 0; chunkX < sizeChunks; chunkX++) {
            for (int chunkZ = 0; chunkZ < sizeChunks; chunkZ++) {

                Chunk chunk = new Chunk(chunkX, chunkZ);
                generateFlatChunk(chunk); // твоя функция высотой, например, на уровне 64

                world.chunks.put(new IntIntImmutablePair(chunkX, chunkZ), chunk);
            }
        }
        return world;
    }

    private static void generateFlatChunk(Chunk chunk) {
        final byte BLOCK_ID = 1;
        final int HEIGHT = 80;
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                chunk.setBlock(x, HEIGHT, z, BLOCK_ID);
                chunk.getHeightMapRaw()[z * 16 + x] = (byte) HEIGHT;
                chunk.setMetadata(x, HEIGHT, z, (byte) 0);
                // - blocklight = 0 // - skylight = 15 (макс)
                chunk.setBlockLight(x, HEIGHT + 1, z, (byte) 14);
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
