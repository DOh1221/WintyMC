package ru.doh1221.wintymc.server.network.netty.tcp.handler;


import ru.doh1221.wintymc.server.entity.Player;
import ru.doh1221.wintymc.server.WintyMC;
import ru.doh1221.wintymc.server.game.world.World;
import ru.doh1221.wintymc.server.game.world.chunk.Chunk;
import ru.doh1221.wintymc.server.network.netty.tcp.ChannelWrapper;
import ru.doh1221.wintymc.server.network.netty.tcp.ConnectionHandler;
import ru.doh1221.wintymc.server.network.netty.tcp.packet.auth.Packet1Login;
import ru.doh1221.wintymc.server.network.netty.tcp.packet.auth.Packet2Handshake;
import ru.doh1221.wintymc.server.network.netty.tcp.packet.game.chat.Packet3Chat;
import ru.doh1221.wintymc.server.network.netty.tcp.packet.game.player.world.Packet11PlayerPosition;
import ru.doh1221.wintymc.server.network.netty.tcp.packet.game.player.world.Packet13PlayerPositionLook;
import ru.doh1221.wintymc.server.network.netty.tcp.packet.game.world.Packet4WorldTime;
import ru.doh1221.wintymc.server.network.netty.tcp.packet.game.world.Packet50PreChunk;
import ru.doh1221.wintymc.server.network.netty.tcp.packet.game.world.Packet51MapChunk;
import ru.doh1221.wintymc.server.network.netty.tcp.packet.game.world.Packet6SpawnPosition;
import ru.doh1221.wintymc.server.network.netty.tcp.packet.general.Packet255DisconnectKick;
import ru.doh1221.wintymc.server.network.netty.tcp.packet.status.Packet254GetInfo;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class InitialHandler extends ConnectionHandler {
    // TEST IMPLEMENTATION
    private Player player;

    public static Packet255DisconnectKick createServerListPing(
            int protocolVersion,
            String minecraftVersion,
            String motd,
            int currentPlayers,
            int maxPlayers
    ) {
        String payload = "§1\0"
                + protocolVersion + "\0"
                + minecraftVersion + "\0"
                + motd + "\0"
                + currentPlayers + "\0"
                + maxPlayers;

        return new Packet255DisconnectKick(payload);
    }

    @Override
    public void connected(ChannelWrapper channel) throws Exception {
        this.channel = channel;
    }

    @Override
    public void disconnected(ChannelWrapper channel) throws Exception {
        WintyMC.getInstance().world.removePlayer(this.player);
    }

    @Override
    public void handle(Packet254GetInfo packet254GetInfo) {
        channel.write(createServerListPing(51, "Beta 1.7.3", "A  Minecraft Server", 0, 10));
    }

    @Override
    public void handle(Packet3Chat packet3Chat) {
        channel.write(new Packet3Chat("<" + player.displayname + "> " + packet3Chat.message));
    }

    @Override
    public void handle(Packet13PlayerPositionLook pos) {

        if (this.player.position == null) {
            this.player.position = WintyMC.getInstance().world.getSpawnPosition();
            channel.write(new Packet13PlayerPositionLook(4, 90, 1.0, 4, 1, 1));
        }

        int previousCX = Math.floorDiv((int) pos.x, 16);
        int previousCZ = Math.floorDiv((int) pos.z, 16);

        this.player.position.setX(pos.x);
        this.player.position.setY(pos.y);
        this.player.position.setZ(pos.z);

        int cx = Math.floorDiv((int) pos.x, 16);
        int cz = Math.floorDiv((int) pos.z, 16);
        //if( cx == previousCX && cz == previousCZ) return;
        // Радиус в чанках
        int radius = 10; // = вокруг 4 чанка
        // radius = 2 → 25 чанков, как в реальном MC beta

        Set<Long> newVisible = new HashSet<>();

        // 1. Собираем чанки, которые должны быть доступны игроку
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                int ccx = cx + dx;
                int ccz = cz + dz;

                long key = Player.key(ccx, ccz);
                newVisible.add(key);

                // Если чанк ещё НЕ загружен — грузим и отправляем
                if (!player.loadedChunks.contains(key)) {
                    loadAndSendChunk(ccx, ccz);
                }
            }
        }

        // 2. Выгружаем старые чанки
        for (long old : player.loadedChunks) {
            if (!newVisible.contains(old)) {
                int ocx = (int) (old >> 32);
                int ocz = (int) old;

                // Клиенту говорим "этот чанк не нужен"
                channel.write(new Packet50PreChunk(ocx, ocz, false));
            }
        }

        // 3. Обновляем состояние игрока
        player.loadedChunks = newVisible;

    }

    @Override
    public void handle(Packet11PlayerPosition pos) {
        if (this.player.position == null) {
            this.player.position = WintyMC.getInstance().world.getSpawnPosition();
            channel.write(new Packet13PlayerPositionLook(4, 90, 1.0, 4, 1, 1));
        }

        int previousCX = Math.floorDiv((int) pos.x, 16);
        int previousCZ = Math.floorDiv((int) pos.z, 16);

        this.player.position.setX(pos.x);
        this.player.position.setY(pos.y);
        this.player.position.setZ(pos.z);

        int cx = Math.floorDiv((int) pos.x, 16);
        int cz = Math.floorDiv((int) pos.z, 16);
        //if( cx == previousCX && cz == previousCZ) return;
        // Радиус в чанках
        int radius = 10; // = вокруг 4 чанка
        // radius = 2 → 25 чанков, как в реальном MC beta

        Set<Long> newVisible = new HashSet<>();

        // 1. Собираем чанки, которые должны быть доступны игроку
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                int ccx = cx + dx;
                int ccz = cz + dz;

                long key = Player.key(ccx, ccz);
                newVisible.add(key);

                // Если чанк ещё НЕ загружен — грузим и отправляем
                if (!player.loadedChunks.contains(key)) {
                    loadAndSendChunk(ccx, ccz);
                }
            }
        }

        // 2. Выгружаем старые чанки
        for (long old : player.loadedChunks) {
            if (!newVisible.contains(old)) {
                int ocx = (int) (old >> 32);
                int ocz = (int) old;

                // Клиенту говорим "этот чанк не нужен"
                channel.write(new Packet50PreChunk(ocx, ocz, false));
            }
        }

        // 3. Обновляем состояние игрока
        player.loadedChunks = newVisible;
    }

    private void loadAndSendChunk(int cx, int cz) {
        World world = WintyMC.getInstance().world;

        Chunk chunk = world.getChunkProvider().getOrCreate(cx, cz);

        // 1. Packet50PreChunk (true = загружаем)
        channel.write(new Packet50PreChunk(cx, cz, true));

        // 2. Сам чанк
        byte[] data = Chunk.toPacketData(chunk).getRawData();

        Packet51MapChunk map = new Packet51MapChunk(
                cx * 16,
                0,
                cz * 16,
                16,
                128,
                16,
                data
        );

        Packet51MapChunk.compress(map);

        channel.write(map);
    }

    @Override
    public void handle(Packet1Login packet1Login) {
        if (packet1Login.protocolVersion > 14) {
            channel.write(new Packet255DisconnectKick(WintyMC.getInstance().langMap.getTranslation("kick.server.outofdate")));
            channel.close();
            return;
        } else if (packet1Login.protocolVersion < 14) {
            channel.write(new Packet255DisconnectKick(WintyMC.getInstance().langMap.getTranslation("kick.client.outofdate")));
            channel.close();
            return;
        }

        WintyMC.getInstance().getLogger().info(player.displayname + " joined the server. " + player.uuid);

        channel.write(new Packet1Login(player.entityID, "CoolServer", 1851285L, (byte) 0));
        channel.write(new Packet6SpawnPosition(4, 80, 4));

        channel.write(new Packet4WorldTime(WintyMC.getInstance().world.getWorldTime()));
        channel.write(new Packet13PlayerPositionLook(4, 90, 1.0, 4, 1, 1));
        channel.write(new Packet4WorldTime(WintyMC.getInstance().world.getWorldTime()));

        WintyMC.getInstance().world.addPlayer(player);

        channel.write(new Packet3Chat("§e" + player.displayname + " joined the game"));
    }

    @Override
    public void handle(Packet2Handshake packet2Handshake) {
        WintyMC.getInstance().getLogger().info(packet2Handshake.username + " connecting to the server: " + this.channel.getHandle().remoteAddress());
        channel.write(new Packet2Handshake("-"));
        player = new Player();
        player.connection = channel;
        player.username = packet2Handshake.username;
        player.displayname = packet2Handshake.username;
        player.uuid = UUID.randomUUID();
        player.health = 0.0;
    }

    @Override
    public String toString() {
        return "";
    }
}