package ru.armlix.winty.game.world;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.armlix.winty.game.chunking.chunk.IChunkGenerator;
import ru.armlix.winty.game.chunking.chunk.IChunkPopulator;

import java.util.UUID;

@AllArgsConstructor
public class WorldInfo {

    // NBT DATA
    @Getter
    private String worldName;
    @Getter
    private UUID worldUUID;

    // INGAME LOGIC
    @Getter
    private int maxViewDistance;
    @Getter
    private int tickableDistance;

    // GAMEPLAY DATA
    @Getter
    private long savedTime;
    @Getter
    private long seed;

    @Getter
    @Setter
    private boolean preloadSpawn;
    @Getter
    @Setter
    private int preloadSpawnRadius;

    @Getter
    @Setter
    private IChunkGenerator chunkGenerator;
    @Getter
    @Setter
    private IChunkPopulator chunkPopulator;

}
