package ru.doh1221.wintymc.server.game.world.implement;

import lombok.AllArgsConstructor;
import ru.doh1221.wintymc.server.game.world.World;
import ru.doh1221.wintymc.server.game.world.chunk.Chunk;
import ru.doh1221.wintymc.server.game.world.chunk.IChunkGenerator;
import ru.doh1221.wintymc.server.game.world.chunk.IChunkProvider;

@AllArgsConstructor
public class ServerChunkProvider implements IChunkProvider {

    public IChunkGenerator generator;
    public World world;

    @Override
    public Chunk getOrCreate(int cx, int cz) {
        Chunk chunk = new Chunk(cx, cz);
        chunk.blocks = generator.generateChunk(world, cx, cz, world.random);
        return chunk;
    }
}
