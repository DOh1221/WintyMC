package ru.armlix.winty.game.chunking.chunk;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import ru.armlix.winty.game.world.WorldInfo;
import ru.armlix.winty.utils.LongHash;

import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public final class FullWriterAsyncProvider implements IChunkProvider {

    private final Long2ObjectMap<CompletableFuture<Chunk>> chunks = new Long2ObjectOpenHashMap<>();
    private final ExecutorService executor;

    public FullWriterAsyncProvider(ExecutorService executor) {
        this.executor = executor;
    }

    @Override
    public CompletableFuture<Chunk> getChunkAt(WorldInfo info, int cx, int cz) {
        long key = LongHash.toLong(cx, cz);
        CompletableFuture<Chunk> future = chunks.get(key);
        if (future != null) return future;

        CompletableFuture<Chunk> created = new CompletableFuture<>();
        chunks.put(key, created);

        executor.execute(() -> {
            try {
                Chunk chunk = info.getChunkGenerator().generateChunk(info, cx, cz);
                info.getChunkPopulator().populateChunk(info, chunk);
                created.complete(chunk);
            } catch (Throwable t) {
                created.completeExceptionally(t);
            }
        });

        return created;
    }

    @Override
    public void tick() {
        Iterator<Long2ObjectMap.Entry<CompletableFuture<Chunk>>> it = chunks.long2ObjectEntrySet().iterator();
        while (it.hasNext()) {
            Long2ObjectMap.Entry<CompletableFuture<Chunk>> e = it.next();
            CompletableFuture<Chunk> f = e.getValue();
            f.thenAccept(chunk -> {
                if(chunk.getLastUpdated() - System.currentTimeMillis() > 5_000) chunks.remove(f);
            });
        }
    }

}
