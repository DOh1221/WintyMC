package ru.doh1221.wintymc.server.game.world.test;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import ru.doh1221.wintymc.server.game.entity.Player;
import ru.doh1221.wintymc.server.network.netty.tcp.packet.game.world.Packet4WorldTime;
public class ThreadWorldTime extends Thread {

    private volatile boolean running = true;

    @Getter
    private long worldTime;
    public long TICK_DURATION_MS;

    private ObjectArrayList<Player> players;

    public ThreadWorldTime(long initialWorldTime, long msTickDuration, ObjectArrayList<Player> players) {
        this.worldTime = initialWorldTime;
        this.TICK_DURATION_MS = msTickDuration;
        this.players = players;

        setName("Ticking Time Thread");
        setDaemon(true);
    }

    @Override
    public void run() {
        long lastTick = System.currentTimeMillis();

        while (running) {
            long now = System.currentTimeMillis();

            if (now - lastTick >= TICK_DURATION_MS) {
                tick();
                lastTick += TICK_DURATION_MS;
            }

            try {
                Thread.sleep(1);
            } catch (InterruptedException ignored) {}
        }
    }

    private void tick() {
        worldTime += 1;

        if (worldTime >= 24000L) {
            worldTime = 0;
        }

        for(Player player : players) player.getConnection().write(new Packet4WorldTime(worldTime));
    }

    public void stopThread() {
        running = false;
    }
}
