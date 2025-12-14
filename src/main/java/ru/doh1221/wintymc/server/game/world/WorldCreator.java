package ru.doh1221.wintymc.server.game.world;

import ru.doh1221.wintymc.server.game.world.chunk.IChunkGenerator;

public interface WorldCreator {

    World createWorld(String worldName, IChunkGenerator generator, long seed);

    void removeWorld(String worldName);

    void saveWorld(World world);

}
