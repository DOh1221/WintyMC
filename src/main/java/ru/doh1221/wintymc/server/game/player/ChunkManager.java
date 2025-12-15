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
    private final Player player;
    private final ExecutorService chunkIO;
    private final BiConsumer<Integer, Integer> onChunkReady;
    @Getter
    @Setter
    private double lastChunkUpdateX;
    @Getter
    @Setter
    private double lastChunkUpdateZ;
    @Getter
    private volatile int radius;

    public ChunkManager(Player player) {
        this(player, WintyMC.getInstance().getChunkIO(), 3, null);
    }

    public ChunkManager(Player player, ExecutorService chunkIO) {
        this(player, chunkIO, 3, null);
    }

    public ChunkManager(Player player, ExecutorService chunkIO, int radius, BiConsumer<Integer, Integer> onChunkReady) {
        this.player = Objects.requireNonNull(player, "player");
        this.chunkIO = Objects.requireNonNull(chunkIO, "chunkIO");
        this.radius = Math.max(3, radius);
        this.onChunkReady = onChunkReady;
        this.loadedChunks = ConcurrentHashMap.newKeySet();
        this.pendingChunks = ConcurrentHashMap.newKeySet();

        this.sendExecutor = Executors.newSingleThreadExecutor(r -> new Thread(r, "ChunkSend-" + player.getUsername()));
    }

    public void requestChunkAsync(World world, int cx, int cz) {
        long key = LongHash.toLong(cx, cz);

        if (loadedChunks.contains(key)) return;
        if (pendingChunks.contains(key)) return;
        if (!isChunkInView(cx, cz)) return;

        pendingChunks.add(key);
        sendExecutor.execute(() -> {
            player.connection.write(new Packet50PreChunk(cx, cz, true));
            sendChunkInternal(Chunk.empty(cx, cz));
        });

        CompletableFuture<Chunk> future = CompletableFuture.supplyAsync(() -> world.getChunkProvider().getOrCreate(cx, cz), chunkIO);

        pendingFutures.put(key, future);

        future.whenComplete((chunk, ex) -> {
            pendingFutures.remove(key);

            if (ex != null) {
                pendingChunks.remove(key);
                ex.printStackTrace();
                return;
            }

            if (!isChunkInView(cx, cz)) {
                pendingChunks.remove(key);
                sendExecutor.execute(() -> unloadChunkInternal(cx, cz));
                return;
            }

            loadedChunks.add(key);
            pendingChunks.remove(key);

            sendExecutor.execute(() -> {
                if (!loadedChunks.contains(key)) return;

                sendChunkInternal(chunk);

                if (onChunkReady != null) {
                    try {
                        onChunkReady.accept(cx, cz);
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            });
        });
    }

    public void requestChunkSync(World world, int cx, int cz) {
        long key = LongHash.toLong(cx, cz);

        if (loadedChunks.contains(key)) return;
        if (!isChunkInView(cx, cz)) return;

        try {
            Chunk chunk = world.getChunkProvider().getOrCreate(cx, cz);
            if (chunk != null) chunk.recalculateHeightMap();

            loadedChunks.add(key);
            pendingChunks.remove(key);

            sendExecutor.execute(() -> {
                if (!player.isOnline()) return;
                player.connection.write(new Packet50PreChunk(cx, cz, true));
                sendChunkInternal(chunk);
                if (onChunkReady != null) onChunkReady.accept(cx, cz);
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            sendExecutor.execute(() -> unloadChunkInternal(cx, cz));
        }
    }

    private void sendChunkInternal(Chunk chunk) {
        if (chunk == null) return;
        chunk.initLighting();

        Packet51MapChunk pkt = Chunk.toPacketData(chunk);
        Packet51MapChunk.compress(pkt);

        player.connection.write(pkt);
    }

    private void unloadChunkInternal(int cx, int cz) {
        long key = LongHash.toLong(cx, cz);

        CompletableFuture<Chunk> fut = pendingFutures.remove(key);
        if (fut != null) fut.cancel(true);

        pendingChunks.remove(key);
        loadedChunks.remove(key);

        player.connection.write(new Packet50PreChunk(cx, cz, false));
    }


    public void unloadChunk(int cx, int cz) {
        sendExecutor.execute(() -> unloadChunkInternal(cx, cz));
    }

    public void clearAll() {
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

    public void setRadius(int radius) {
        this.radius = Math.max(3, radius);
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

}
