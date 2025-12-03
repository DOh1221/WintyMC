package ru.doh1221.wintymc.server.game.world.chunk;

public interface IChunkProvider {

    Chunk getOrCreate(int cx, int cz);

}
