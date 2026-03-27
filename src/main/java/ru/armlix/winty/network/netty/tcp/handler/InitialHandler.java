package ru.armlix.winty.network.netty.tcp.handler;

import ru.armlix.winty.TestingWorld;
import ru.armlix.winty.game.chunking.chunk.Chunk;
import ru.armlix.winty.game.world.World;
import ru.armlix.winty.network.netty.Player;
import ru.armlix.winty.network.netty.tcp.ChannelWrapper;
import ru.armlix.winty.network.netty.tcp.ConnectionHandler;
import ru.armlix.winty.network.netty.tcp.packet.auth.Packet1Login;
import ru.armlix.winty.network.netty.tcp.packet.auth.Packet2Handshake;
import ru.armlix.winty.network.netty.tcp.packet.game.chat.Packet3Chat;
import ru.armlix.winty.network.netty.tcp.packet.game.player.world.Packet11PlayerPosition;
import ru.armlix.winty.network.netty.tcp.packet.game.player.world.Packet13PlayerPositionLook;
import ru.armlix.winty.network.netty.tcp.packet.game.world.Packet4WorldTime;
import ru.armlix.winty.network.netty.tcp.packet.game.world.Packet50PreChunk;
import ru.armlix.winty.network.netty.tcp.packet.game.world.Packet51MapChunk;
import ru.armlix.winty.network.netty.tcp.packet.game.world.Packet6SpawnPosition;
import ru.armlix.winty.network.netty.tcp.packet.general.Packet255DisconnectKick;
import ru.armlix.winty.network.netty.tcp.packet.status.Packet254GetInfo;
import ru.armlix.winty.utils.location.View3D;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

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
            this.player.position = new View3D(0, 100, 0, 0, 0);
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

        player.damage(0.1);

    }

    @Override
    public void handle(Packet11PlayerPosition pos) {
        if (this.player.position == null) {
            this.player.position = new View3D(0, 100, 0, 0, 0);
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
        World world = TestingWorld.world;

        CompletableFuture<Chunk> chunk = world.getChunkAt(cx, cz);

        chunk.thenAccept(ch -> {
            // 1. Packet50PreChunk (true = загружаем)
            channel.write(new Packet50PreChunk(cx, cz, true));

            // 2. Сам чанк
            byte[] data = Chunk.toPacketData(ch).getRawData();

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
        });


    }

    @Override
    public void handle(Packet1Login packet1Login) {
        channel.write(new Packet1Login(player.getEntityID(), "CoolServer", 1851285L, (byte) 0));
        channel.write(new Packet6SpawnPosition(4, 80, 4));

        channel.write(new Packet4WorldTime(TestingWorld.world.getTime()));
        channel.write(new Packet13PlayerPositionLook(4, 90, 1.0, 4, 1, 1));
        channel.write(new Packet4WorldTime(TestingWorld.world.getTime()));

        TestingWorld.world.spawnEntity(player);

        channel.write(new Packet3Chat("§e" + player.displayname + " joined the game"));

    }

    @Override
    public void handle(Packet2Handshake packet2Handshake) {
        channel.write(new Packet2Handshake("-"));
        player = new Player(this.channel);
        player.connection = channel;
        player.username = packet2Handshake.username;
        player.displayname = packet2Handshake.username;
        player.uuid = UUID.randomUUID();
    }

    @Override
    public String toString() {
        return "";
    }
}