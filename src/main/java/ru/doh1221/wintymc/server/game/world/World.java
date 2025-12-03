package ru.doh1221.wintymc.server.game.world;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import ru.doh1221.wintymc.server.game.entity.Player;
import ru.doh1221.wintymc.server.game.world.chunk.IChunkGenerator;
import ru.doh1221.wintymc.server.game.world.chunk.IChunkProvider;
import ru.doh1221.wintymc.server.game.world.implement.ServerChunkProvider;
import ru.doh1221.wintymc.server.game.world.test.ThreadWorldTime;
import ru.doh1221.wintymc.server.utils.location.View3D;

import java.util.Random;

@Getter
public class World {

    private View3D spawnPosition;
    private IChunkProvider chunkProvider;
    private IChunkGenerator worldGenerator;
    public final Random random;
    private ObjectArrayList<Player> players = new ObjectArrayList<>();
    public ThreadWorldTime threadWorldTime;

    public World(View3D view3D, IChunkGenerator worldGenerator, long seed) {
        this.spawnPosition = view3D;
        this.worldGenerator = worldGenerator;
        this.random = new Random(seed);
    }

    public void initialize() {
        this.chunkProvider = new ServerChunkProvider(this.worldGenerator, this);
        this.threadWorldTime = new ThreadWorldTime(0, 50, players);
    }

    public void startTicking() {
        this.threadWorldTime.start();
    }

    public void stopTicking() {
        this.threadWorldTime.stopThread();
    }

    public long getWorldTime() {
        return this.threadWorldTime.getWorldTime();
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public void removePlayer(Player player) {
        players.remove(player);
    }

}
