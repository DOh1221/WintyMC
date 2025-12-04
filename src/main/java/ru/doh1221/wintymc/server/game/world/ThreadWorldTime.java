package ru.doh1221.wintymc.server.game.world;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import ru.doh1221.wintymc.server.game.entity.Player;
import ru.doh1221.wintymc.server.network.netty.tcp.packet.game.world.Packet4WorldTime;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ThreadWorldTime extends Thread {

    private volatile boolean running = true;

    /** Все миры */
    private final Map<World, WorldEntry> worlds = new ConcurrentHashMap<>();

    /** Персональные таймеры игроков */
    private final Map<Player, PlayerTimeState> personalTimes = new ConcurrentHashMap<>();


    public ThreadWorldTime() {
        setName("World Timer Thread");
        setDaemon(true);
    }

    // =========================================================================
    //  РЕГИСТРАЦИЯ МИРОВ
    // =========================================================================

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


    // =========================================================================
    //  ПЕРСОНАЛЬНОЕ ВРЕМЯ
    // =========================================================================

    /**
     * @param player игрок
     * @param time начальное время или null чтобы убрать персональное время
     * @param tickDurationMs длительность тика персонального времени
     * @param timeStep величина прибавления за тик
     */
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



    // =========================================================================
    //  ПОТОК
    // =========================================================================

    @Override
    public void run() {

        while (running) {
            long now = System.currentTimeMillis();

            // ------- Тикаем все миры -------
            for (WorldEntry world : worlds.values()) {
                if (now - world.lastTick >= world.tickDurationMs) {
                    tickWorld(world);
                    world.lastTick += world.tickDurationMs;
                }
            }

            // ------- Тикаем персональное время игроков -------
            for (PlayerTimeState state : personalTimes.values()) {
                if (now - state.lastTick >= state.tickDurationMs) {
                    tickPlayerPersonal(state);
                    state.lastTick += state.tickDurationMs;
                }
            }

            try {
                Thread.sleep(1);
            } catch (InterruptedException ignored) {}
        }
    }



    // =========================================================================
    //    ЛОГИКА ТИКА МИРА
    // =========================================================================

    private void tickWorld(WorldEntry world) {

        // обновляем время мира
        world.time += world.timeStep;
        if (world.time >= 24000) world.time = 0;

        // отправляем игрокам
        for (Player player : world.players) {

            // если у игрока персональный режим — не отправляем мировое время
            if (personalTimes.containsKey(player)) continue;

            player.getConnection().write(new Packet4WorldTime(world.time));
        }
    }


    // =========================================================================
    //    ЛОГИКА ТИКА ПЕРСОНАЛЬНОГО ВРЕМЕНИ ИГРОКА
    // =========================================================================

    private void tickPlayerPersonal(PlayerTimeState state) {

        state.time += state.timeStep;
        if (state.time >= 24000) state.time %= 24000;

        state.player.getConnection().write(new Packet4WorldTime(state.time));
    }



    // =========================================================================
    //   ОСТАНОВКА ПОТОКА
    // =========================================================================

    public void stopThread() {
        running = false;
    }

    // =========================================================================
    //   ВНУТРЕННИЕ КЛАССЫ
    // =========================================================================

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
