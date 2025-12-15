package ru.doh1221.wintymc.server.game.player;

import lombok.Getter;
import lombok.Setter;
import ru.doh1221.wintymc.server.WintyMC;
import ru.doh1221.wintymc.server.entity.Player;
import ru.doh1221.wintymc.server.game.world.World;
import ru.doh1221.wintymc.server.game.world.chunk.Chunk;
import ru.doh1221.wintymc.server.network.netty.tcp.packet.game.world.Packet50PreChunk;
import ru.doh1221.wintymc.server.network.netty.tcp.packet.game.world.Packet51MapChunk;
import ru.doh1221.wintymc.server.utils.ChunkUtils;
import ru.doh1221.wintymc.server.utils.LongHash;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.BiConsumer;

public class ChunkManager {

    @Getter
    private final Set<Long> loadedChunks;
    @Getter
    private final Set<Long> pendingChunks;

    private final ConcurrentMap<Long, CompletableFuture<Chunk>> pendingFutures = new ConcurrentHashMap<>();
    private final Executor sendExecutor;
    @Getter
    @Setter
    private double lastChunkUpdateX;
    @Getter
    @Setter
    private double lastChunkUpdateZ;

    @Getter
    private volatile int radius;
    private final Player player;

    private final ExecutorService chunkIO;

    private final BiConsumer<Integer, Integer> onChunkReady;

    public ChunkManager(Player player) {
        this(player, WintyMC.getInstance().getChunkIO(), 3, null);
    }

    public ChunkManager(Player player, ExecutorService chunkIO) {
        this(player, chunkIO, 3, null);
    }

    public ChunkManager(Player player, ExecutorService chunkIO, int radius, BiConsumer<Integer, Integer> onChunkReady) {
        this.player = Objects.requireNonNull(player);
        this.chunkIO = Objects.requireNonNull(chunkIO);
        this.radius = Math.max(3, radius);
        this.onChunkReady = onChunkReady;
        this.loadedChunks = ConcurrentHashMap.newKeySet();
        this.pendingChunks = ConcurrentHashMap.newKeySet();
        this.sendExecutor = Executors.newSingleThreadExecutor();
    }

    public void requestChunkAsync(World world, int cx, int cz) {
        long key = LongHash.toLong(cx, cz);

        if (loadedChunks.contains(key)) return;
        if (pendingChunks.contains(key)) return;
        if (!isChunkInView(cx, cz)) return;

        pendingChunks.add(key);

        sendExecutor.execute(() -> {
            if (!player.isOnline()) return;

            player.connection.write(new Packet50PreChunk(cx, cz, true));
            sendChunk(Chunk.empty(cx, cz));
        });

        CompletableFuture<Chunk> future =
                CompletableFuture.supplyAsync(
                        () -> world.getChunkProvider().getOrCreate(cx, cz),
                        chunkIO
                );

        pendingFutures.put(key, future);

        future.whenComplete((chunk, ex) -> {
            pendingFutures.remove(key);

            if (ex != null || !player.isOnline()) {
                pendingChunks.remove(key);
                return;
            }

            if (!isChunkInView(cx, cz)) {
                pendingChunks.remove(key);
                unloadChunk(cx, cz);
                return;
            }

            loadedChunks.add(key);
            pendingChunks.remove(key);

            sendExecutor.execute(() -> {
                if (!player.isOnline()) return;
                if (!loadedChunks.contains(key)) return;

                sendChunk(chunk);

                if (onChunkReady != null)
                    onChunkReady.accept(cx, cz);
            });
        });
    }


    public void requestChunkSync(World world, int cx, int cz) {
        long key = LongHash.toLong(cx, cz);

        if (loadedChunks.contains(key)) return;
        if (!isChunkInView(cx, cz)) return;

        Packet50PreChunk pre = new Packet50PreChunk(cx, cz, true);
        player.connection.write(pre);

        try {
            Chunk chunk = world.getChunkProvider().getOrCreate(cx, cz);
            chunk.recalculateHeightMap();
            loadedChunks.add(key);
            sendChunk(chunk);
            if (onChunkReady != null) onChunkReady.accept(cx, cz);
        } catch (Exception ex) {
            ex.printStackTrace();
            unloadChunk(cx, cz);
        }
    }

    public void sendChunk(Chunk chunk) {
        Packet51MapChunk pkt = Chunk.toPacketData(chunk);
        Packet51MapChunk.compress(pkt);
        player.connection.write(pkt);
    }

    public void unloadChunk(int cx, int cz) {
        long key = LongHash.toLong(cx, cz);

        CompletableFuture<Chunk> fut = pendingFutures.remove(key);
        if (fut != null) {
            fut.cancel(true);
        }
        pendingChunks.remove(key);
        loadedChunks.remove(key);

        Packet50PreChunk pre = new Packet50PreChunk(cx, cz, false);
        player.connection.write(pre);
    }

    public void clearAll() {
        // cancel all
        for (Map.Entry<Long, CompletableFuture<Chunk>> e : pendingFutures.entrySet()) {
            e.getValue().cancel(true);
        }
        pendingFutures.clear();
        pendingChunks.clear();
        loadedChunks.clear();
    }

    public boolean isChunkInView(int cx, int cz) {
        int pcx = ChunkUtils.toChunk(player.position.getX());
        int pcz = ChunkUtils.toChunk(player.position.getZ());
        return Math.abs(cx - pcx) <= radius && Math.abs(cz - pcz) <= radius;
    }

    public List<int[]> spiralChunks(int centerX, int centerZ, int radius) {
        List<int[]> out = new ArrayList<>();
        int x = 0, z = 0;
        int dx = 0, dz = -1;
        int max = (radius * 2 + 1) * (radius * 2 + 1);
        for (int i = 0; i < max; i++) {
            int cx = centerX + x;
            int cz = centerZ + z;
            if (Math.abs(x) <= radius && Math.abs(z) <= radius) {
                out.add(new int[]{cx, cz});
            }
            if (x == z || (x < 0 && x == -z) || (x > 0 && x == 1 - z)) {
                int tmp = dx;
                dx = -dz;
                dz = tmp;
            }
            x += dx;
            z += dz;
        }
        return out;
    }

    public void setRadius(int radius) {
        this.radius = Math.max(3, radius);
    }
}
