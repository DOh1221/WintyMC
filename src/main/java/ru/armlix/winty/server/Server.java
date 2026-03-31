package ru.armlix.winty.server;

import ru.armlix.winty.game.chunking.chunk.*;
import ru.armlix.winty.game.world.World;
import ru.armlix.winty.game.world.WorldInfo;
import ru.armlix.winty.utils.location.Location;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class Server {

    Logger logger = Logger.getLogger(Server.class.getName());

    protected ServerDefinition serverDefinition;
    boolean isRunning = false;

    ArrayList<World> worlds = new ArrayList<>();

    public Server(ServerDefinition serverDefinition) {
        this.serverDefinition = serverDefinition;
    }

    public void start() {
        logger.info("Starting Minecraft Server " + serverDefinition.getVersion());

        WorldInfo demoWorldInfo = new WorldInfo("demoWorld", UUID.randomUUID(), (short) 10, (short) 12, 12000L, 1859812L, true, 12, new FlatMapGenerator(), new IChunkPopulator() {
            @Override
            public void populateChunk(WorldInfo info, Chunk chunk) {

            }
        }, new Location(null, 0, 0, 0, 0, 0));

        IChunkProvider demoProvider = new FullWriterAsyncProvider(Executors.newFixedThreadPool(2));

        // ******************* Demo World ***********************
        World demoWorld = new World(demoWorldInfo, demoProvider);
        demoWorld.init();
        // ******************************************************

        worlds.add(demoWorld);

        this.isRunning = true;

        startTickingLoop();

    }

    public void startTickingLoop() {
        while (isRunning) {
            long tickStart = System.nanoTime();

            for (World world : worlds) {
                world.tick();
            }

            long elapsedMs = (System.nanoTime() - tickStart) / 1_000_000L;
            long sleepMs = Math.max(0L, serverDefinition.getTickDuration() - elapsedMs);

            try {
                Thread.sleep(sleepMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

}
