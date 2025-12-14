package ru.doh1221.wintymc.server.game.world.chunk;

import ru.doh1221.wintymc.server.game.world.World;
import ru.doh1221.wintymc.server.utils.location.View3D;

import java.util.Random;

public interface IChunkGenerator {

    byte[] generateChunk(World world, int cx, int cz, Random random);

    View3D getSpawnPosition();

}
