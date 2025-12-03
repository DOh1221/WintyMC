package ru.doh1221.wintymc.server.game.world.mcregion;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.*;

/**
 * Example: fast generation of one region (32x32 chunks), write to r.0.0.mca and then read back to verify.
 *
 * This example uses a fixed thread pool and parallel generation of chunks for speed.
 */
public class Main {

    public static void main(String[] args) throws Exception {
        Path out = Path.of("r.0.0.mca");
        int regionChunkX = 0;
        int regionChunkZ = 0;

        int threads = Math.max(1, Runtime.getRuntime().availableProcessors() - 1);
        ExecutorService genPool = Executors.newFixedThreadPool(threads);

        try (RegionFile region = new RegionFile(out)) {
            // generate all 32x32 chunks in parallel and write sequentially
            CompletableFuture<Void>[] futures = new CompletableFuture[32 * 32];
            int idx = 0;
            for (int cz = 0; cz < 32; cz++) {
                for (int cx = 0; cx < 32; cx++) {
                    final int localX = cx;
                    final int localZ = cz;
                    futures[idx++] = CompletableFuture.supplyAsync(() -> {
                        try {
                            // absolute chunk coords; for example region at 0,0 -> chunk coords 0..31
                            int absX = regionChunkX * 32 + localX;
                            int absZ = regionChunkZ * 32 + localZ;
                            byte[] nbt = ChunkGenerator.generateChunkNBT(absX, absZ);
                            // write chunk into region
                            region.writeChunk(localX, localZ, nbt);
                            return null;
                        } catch (IOException e) {
                            throw new CompletionException(e);
                        }
                    }, genPool);
                }
            }
            // wait for all generation+writes
            CompletableFuture.allOf(futures).join();
            System.out.println("Region generation finished and written to " + out);

            // read back and verify first few chunks
            for (int cz = 0; cz < 4; cz++) {
                for (int cx = 0; cx < 4; cx++) {
                    byte[] uncompressed = region.readChunk(cx, cz);
                    if (uncompressed == null) {
                        System.out.println("Chunk " + cx + "," + cz + " not found");
                    } else {
                        System.out.println("Chunk " + cx + "," + cz + " uncompressed NBT size: " + uncompressed.length);
                    }
                }
            }
        } finally {
            genPool.shutdown();
            genPool.awaitTermination(10, TimeUnit.SECONDS);
        }
    }
}