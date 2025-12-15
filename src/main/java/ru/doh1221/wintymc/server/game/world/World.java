package ru.doh1221.wintymc.server.game.world;

import lombok.Getter;
import lombok.Setter;
import ru.doh1221.wintymc.server.entity.Player;
import ru.doh1221.wintymc.server.game.world.chunk.IChunkGenerator;
import ru.doh1221.wintymc.server.game.world.chunk.IChunkProvider;
import ru.doh1221.wintymc.server.game.world.implement.OverWorldGenerator;
import ru.doh1221.wintymc.server.game.world.implement.ServerChunkProvider;
import ru.doh1221.wintymc.server.network.netty.tcp.packet.game.world.Packet6SpawnPosition;

import java.util.Random;
@Getter
@Setter
public class World {

    private String worldName;
    private Random rng;
    private long seed;

    private IChunkProvider chunkProvider;
    private IChunkGenerator chunkGenerator;

    //test
    @Getter
    public WorldChunkManager worldChunkManager;

    public World(String worldName, long seed, IChunkGenerator generator) {
        this.worldName = worldName;
        this.rng = new Random(seed);
        this.seed = seed;
        this.chunkGenerator = generator;
        //test
        this.worldChunkManager = new WorldChunkManager(this);
        this.chunkProvider = new OverWorldGenerator(this, seed);
    }

    public void playerJoinedWorld(Player player) {
        player.connection.write(new Packet6SpawnPosition(chunkGenerator.getSpawnPosition()));
    }

}
