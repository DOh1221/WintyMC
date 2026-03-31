package ru.armlix.winty.game.chunking.chunk;

import ru.armlix.winty.game.world.WorldInfo;
import ru.armlix.winty.utils.LongHash;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

public final class FullWriterAsyncProvider implements IChunkProvider {

    private final ConcurrentHashMap<Long, CompletableFuture<Chunk>> chunks = new ConcurrentHashMap<>();
    private final ExecutorService executor;

    public FullWriterAsyncProvider(ExecutorService executor) {
        this.executor = executor;
    }

    @Override
    public CompletableFuture<Chunk> getChunkAt(WorldInfo info, int cx, int cz) {
        long key = LongHash.toLong(cx, cz);

        return chunks.computeIfAbsent(key, k -> {
            CompletableFuture<Chunk> future = new CompletableFuture<>();

            executor.execute(() -> {
                try {
                    Chunk chunk = info.getChunkGenerator().generateChunk(info, cx, cz);
                    info.getChunkPopulator().populateChunk(info, chunk);
                    chunk.markDirty();
                    future.complete(chunk);
                } catch (Throwable t) {
                    chunks.remove(key, future);
                    future.completeExceptionally(t);
                }
            });

            return future;
        });
    }

    @Override
    public void tick() {
        long now = System.currentTimeMillis();

        chunks.entrySet().removeIf(entry -> {
            CompletableFuture<Chunk> future = entry.getValue();

            if (!future.isDone()) return false;

            Chunk chunk = future.getNow(null);
            if (chunk == null) return true;

            return now - chunk.getLastUpdated() > 5_000;
        });
    }
}