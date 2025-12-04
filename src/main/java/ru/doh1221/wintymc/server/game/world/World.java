package ru.doh1221.wintymc.server.game.world;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import ru.doh1221.wintymc.server.WintyMC;
import ru.doh1221.wintymc.server.game.entity.Player;
import ru.doh1221.wintymc.server.game.world.chunk.IChunkGenerator;
import ru.doh1221.wintymc.server.game.world.chunk.IChunkProvider;
import ru.doh1221.wintymc.server.game.world.implement.ServerChunkProvider;
import ru.doh1221.wintymc.server.utils.location.View3D;

import java.util.Random;

@Getter
public class World {

    private View3D spawnPosition;
    private IChunkProvider chunkProvider;
    private IChunkGenerator worldGenerator;
    public final Random random;
    private ObjectArrayList<Player> players = new ObjectArrayList<>();


    public World(View3D view3D, IChunkGenerator worldGenerator, long seed) {
        this.spawnPosition = view3D;
        this.worldGenerator = worldGenerator;
        this.random = new Random(seed);
    }

    public void initialize() {
        this.chunkProvider = new ServerChunkProvider(this.worldGenerator, this);
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
