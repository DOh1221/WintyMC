package ru.doh1221.wintymc.server.game.world;

import lombok.SneakyThrows;
import ru.doh1221.wintymc.server.game.world.chunk.IChunkGenerator;
import ru.doh1221.wintymc.server.utils.world.LevelInfo;
import ru.doh1221.wintymc.server.utils.world.impl.LocalWorldLoader;

import java.io.File;

public class LocalWorldCreator implements WorldCreator {
    @SneakyThrows
    @Override
    public World createWorld(String worldName, IChunkGenerator generator, long seed) {
        LocalWorldLoader loader = new LocalWorldLoader(new File(worldName));
        loader.load();
        if(!loader.worldExists()) {

            loader.info = new LevelInfo(
                    seed,
                    (int) Math.round(generator.getSpawnPosition().getX()),
                    (int) Math.round(generator.getSpawnPosition().getY()),
                    (int) Math.round(generator.getSpawnPosition().getZ()),
                    generator.getSpawnPosition().getYaw(),
                    generator.getSpawnPosition().getPitch(),
                    12000,
                    System.currentTimeMillis(),
                    0,
                    worldName,
                    LevelInfo.saveVersion,
                    0,
                    false,
                    0,
                    false,
                    null
            );
            loader.save();
        }

        return
    }

    @Override
    public void removeWorld(String worldName) {

    }

    @SneakyThrows
    @Override
    public void saveWorld(World world) {
        LocalWorldLoader loader = new LocalWorldLoader(new File("world"));
        LevelInfo info = loader.load();
        info.setLastPlayed(System.currentTimeMillis());
        loader.info = info;
        loader.save();
    }
}
