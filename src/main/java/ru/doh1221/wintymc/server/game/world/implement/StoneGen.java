package ru.doh1221.wintymc.server.game.world.implement;

import ru.doh1221.wintymc.server.game.objects.blocks.BlockRegistry;
import ru.doh1221.wintymc.server.game.world.World;
import ru.doh1221.wintymc.server.game.world.chunk.Chunk;
import ru.doh1221.wintymc.server.game.world.chunk.IChunkGenerator;
import ru.doh1221.wintymc.server.utils.location.View3D;

import java.util.Objects;
import java.util.Random;

public class StoneGen implements IChunkGenerator {

    @Override
    public byte[] generateChunk(World world, int cx, int cz, Random random) {
        byte[] test = new byte[Chunk.TOTAL_BLOCKS];

        int y = 1;
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                test[Chunk.index(x, y, z)] = (byte) Objects.requireNonNull(BlockRegistry.getByBlockName("stone")).getID();
            }
        }

        return test;
    }

    @Override
    public View3D getSpawnPosition() {
        return new View3D(0, 400, 0, 0, 0);
    }

}
