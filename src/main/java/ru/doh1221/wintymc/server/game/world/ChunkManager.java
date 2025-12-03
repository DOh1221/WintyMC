package ru.doh1221.wintymc.server.game.world;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import ru.doh1221.wintymc.server.game.world.chunk.Chunk;

import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChunkManager {

    private final Long2ObjectOpenHashMap<Chunk> chunks = new Long2ObjectOpenHashMap<>();

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private final ExecutorService ioExecutor = Executors.newSingleThreadExecutor();

    public void putChunk(int x, int z, Chunk chunk) {
        long key = getChunkKey(x, z);
        lock.writeLock().lock();
        try {
            chunks.put(key, chunk);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Chunk getChunk(int x, int z) {
        long key = getChunkKey(x, z);
        lock.readLock().lock();
        try {
            return chunks.get(key);
        } finally {
            lock.readLock().unlock();
        }
    }

    public void removeChunk(int x, int z) {
        long key = getChunkKey(x, z);
        Chunk chunk;
        lock.writeLock().lock();
        try {
            chunk = chunks.remove(key);
        } finally {
            lock.writeLock().unlock();
        }
    }

    private long getChunkKey(int x, int z) {
        return (((long) x) << 32) | (z & 0xFFFFFFFFL);
    }

    public void shutdown() {
        ioExecutor.shutdown();
    }
}
