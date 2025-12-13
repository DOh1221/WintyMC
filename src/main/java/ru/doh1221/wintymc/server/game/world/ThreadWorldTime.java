package ru.doh1221.wintymc.server.game.world;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import ru.doh1221.wintymc.server.entity.Player;
import ru.doh1221.wintymc.server.network.netty.tcp.packet.game.world.Packet4WorldTime;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ThreadWorldTime extends Thread {

    private final Map<World, WorldEntry> worlds = new ConcurrentHashMap<>();
    private final Map<Player, PlayerTimeState> personalTimes = new ConcurrentHashMap<>();
    private volatile boolean running = true;


    public ThreadWorldTime() {
        setName("World Timer Thread");
        setDaemon(true);
    }

    public void registerWorld(World world,
                              int initialTime,
                              long tickDurationMs,
                              long timeStep,
                              ObjectArrayList<Player> players) {
        worlds.put(world, new WorldEntry(initialTime, tickDurationMs, timeStep, players));
    }

    public int getWorldTime(World world) {
        return Math.toIntExact(worlds.get(world).time);
    }

    public void unregisterWorld(World world) {
        worlds.remove(world);
    }

    public void setPersonalTime(Player player,
                                Integer time,
                                Long tickDurationMs,
                                Long timeStep) {

        if (time == null) {
            personalTimes.remove(player);
            return;
        }

        if (tickDurationMs == null) tickDurationMs = 50L;
        if (timeStep == null) timeStep = 1L;

        personalTimes.put(player, new PlayerTimeState(time, tickDurationMs, timeStep));
    }

    @Override
    public void run() {

        while (running) {
            long now = System.currentTimeMillis();

            for (WorldEntry world : worlds.values()) {
                if (now - world.lastTick >= world.tickDurationMs) {
                    tickWorld(world);
                    world.lastTick += world.tickDurationMs;
                }
            }

            for (PlayerTimeState state : personalTimes.values()) {
                if (now - state.lastTick >= state.tickDurationMs) {
                    tickPlayerPersonal(state);
                    state.lastTick += state.tickDurationMs;
                }
            }

            try {
                Thread.sleep(1);
            } catch (InterruptedException ignored) {
            }
        }
    }

    private void tickWorld(WorldEntry world) {

        world.time += world.timeStep;
        if (world.time >= 24000) world.time = 0;

        for (Player player : world.players) {

            if (personalTimes.containsKey(player)) continue;

            player.getConnection().write(new Packet4WorldTime(world.time));
        }
    }

    private void tickPlayerPersonal(PlayerTimeState state) {

        state.time += state.timeStep;
        if (state.time >= 24000) state.time %= 24000;

        state.player.getConnection().write(new Packet4WorldTime(state.time));
    }

    public void stopThread() {
        running = false;
    }

    private static class WorldEntry {
        int time;
        long tickDurationMs;
        long timeStep;
        long lastTick;
        ObjectArrayList<Player> players;

        public WorldEntry(int time, long tickDurationMs, long timeStep, ObjectArrayList<Player> players) {
            this.time = time;
            this.tickDurationMs = tickDurationMs;
            this.timeStep = timeStep;
            this.players = players;
            this.lastTick = System.currentTimeMillis();
        }
    }

    private static class PlayerTimeState {
        Player player;

        int time;
        long tickDurationMs;
        long timeStep;
        long lastTick;

        public PlayerTimeState(int time, long tickDurationMs, long timeStep) {
            this.time = time;
            this.tickDurationMs = tickDurationMs;
            this.timeStep = timeStep;
            this.lastTick = System.currentTimeMillis();
            this.player = null; // позже будет подставлен
        }
    }
}
