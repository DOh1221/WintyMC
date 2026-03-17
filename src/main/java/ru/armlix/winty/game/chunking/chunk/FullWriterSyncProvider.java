package ru.armlix.winty.game.chunking.chunk;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import ru.armlix.winty.game.world.WorldInfo;
import ru.armlix.winty.utils.LongHash;

import java.util.Iterator;
import java.util.concurrent.CompletableFuture;

public final class FullWriterSyncProvider implements IChunkProvider {

    private final Long2ObjectMap<CompletableFuture<Chunk>> chunks = new Long2ObjectOpenHashMap<>();

    @Override
    public CompletableFuture<Chunk> getChunkAt(WorldInfo info, int cx, int cz) {
        long key = LongHash.toLong(cx, cz);
        synchronized (chunks) {
            CompletableFuture<Chunk> existing = chunks.get(key);
            if (existing != null) return existing;
            CompletableFuture<Chunk> promise = new CompletableFuture<>();
            chunks.put(key, promise);
            try {
                Chunk chunk = info.getChunkGenerator().generateChunk(info, cx, cz);
                info.getChunkPopulator().populateChunk(info, chunk);
                promise.complete(chunk);
            } catch (Throwable t) {
                promise.completeExceptionally(t);
                chunks.remove(key);
            }
            return promise;
        }
    }

    @Override
    public void tick() {
        synchronized (chunks) {
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

}
