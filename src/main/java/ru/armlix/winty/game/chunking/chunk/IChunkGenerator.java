package ru.armlix.winty.game.chunking.chunk;

import ru.armlix.winty.game.world.WorldInfo;

public interface IChunkGenerator {

    Chunk generateChunk(WorldInfo worldInfo, int x, int z);

}
