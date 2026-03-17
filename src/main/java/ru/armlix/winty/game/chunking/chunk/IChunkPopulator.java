package ru.armlix.winty.game.chunking.chunk;

import ru.armlix.winty.game.world.WorldInfo;

public interface IChunkPopulator {

    void populateChunk(WorldInfo info, Chunk chunk);

}
