package ru.doh1221.wintymc.server.game.world;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import ru.doh1221.wintymc.server.entity.Player;
import ru.doh1221.wintymc.server.WintyMC;
import ru.doh1221.wintymc.server.game.world.chunk.IChunkGenerator;
import ru.doh1221.wintymc.server.game.world.chunk.IChunkProvider;
import ru.doh1221.wintymc.server.game.world.implement.OverWorldGenerator;
import ru.doh1221.wintymc.server.game.world.implement.ServerChunkProvider;
import ru.doh1221.wintymc.server.game.world.implement.WorldChunkManager;
import ru.doh1221.wintymc.server.utils.location.View3D;

import java.util.Random;

@Getter
public class World {

    public final Random random;
    @Getter
    public final long seed;
    private final View3D spawnPosition;
    private final IChunkGenerator worldGenerator;
    private final ObjectArrayList<Player> players = new ObjectArrayList<>();
    private IChunkProvider chunkProvider;
    @Getter
    private WorldChunkManager worldChunkManager;

    public World(View3D view3D, IChunkGenerator worldGenerator, long seed) {
        this.spawnPosition = view3D;
        this.worldGenerator = worldGenerator;
        this.random = new Random(seed);
        this.seed = seed;
        this.worldChunkManager = new WorldChunkManager(this);
    }

    public void initialize() {
        this.chunkProvider = new OverWorldGenerator(this, seed);
        WintyMC.getInstance().timeTicker.registerWorld(this, 0, 50, 1, players);
    }

    public void startTicking() {
        //
    }

    public long getWorldTime() {
        return WintyMC.getInstance().timeTicker.getWorldTime(this);
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public void removePlayer(Player player) {
        players.remove(player);
    }

}
