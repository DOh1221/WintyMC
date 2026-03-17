package ru.armlix.winty.game.world;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import lombok.Setter;
import ru.armlix.winty.game.Tickable;
import ru.armlix.winty.game.chunking.chunk.Chunk;
import ru.armlix.winty.game.chunking.chunk.IChunkProvider;
import ru.armlix.winty.game.counters.TimeCounter;
import ru.armlix.winty.game.entiy.Entity;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class World implements Tickable {

    @Setter
    private TimeCounter timeCounter;
    private long timeSpeedModifier = 1L;

    @Getter
    private WorldInfo worldInfo;
    @Getter
    private IChunkProvider chunkProvider;

    @Getter
    private List<Entity> entities = new ObjectArrayList<>();

    public World(WorldInfo worldInfo, IChunkProvider chunkProvider) {
        this.worldInfo = worldInfo;
        this.timeCounter = new TimeCounter(worldInfo.getSavedTime());
        this.chunkProvider = chunkProvider;
    }

    public void init() {
        if(worldInfo.isPreloadSpawn()) {
            // TODO preload spawn chunks
        }

    }

    @Override
    public void tick() {
        // world time ticking
        timeCounter.increment(timeSpeedModifier);

        for (Entity entity : entities) {
            entity.tick(timeCounter.getTime());
        }

        // TODO block ticking

        // Chunk GC
        chunkProvider.tick();
    }

    public CompletableFuture<Chunk> getChunkAt(int cx, int cz) {
        return getChunkProvider().getChunkAt(this.worldInfo, cx, cz);
    }

    public boolean spawnEntity(Entity entity) {
        entities.add(entity);
        return true;
    }

    public void setBlock(int x, int y, int z, byte id) {
        getChunkProvider().getChunkAt(this.worldInfo, x << 4, z << 4).thenAccept(
                chunk -> {
                    chunk.setBlock(x, y, z, id);
                }
        );
    }

    public long getTime() {
        return timeCounter.getTime();
    }
    public void setTime(long time) {
        timeCounter.setTime(time);
    }

    public void setTimeModifier(long timeSpeedModifier) {
        this.timeSpeedModifier = timeSpeedModifier;
    }

}
