package ru.doh1221.wintymc.server.network.netty.tcp.handler;


import ru.doh1221.wintymc.server.WintyMC;
import ru.doh1221.wintymc.server.game.entity.Player;
import ru.doh1221.wintymc.server.game.world.chunk.Chunk;
import ru.doh1221.wintymc.server.game.world.test.World;
import ru.doh1221.wintymc.server.network.netty.tcp.ChannelWrapper;
import ru.doh1221.wintymc.server.network.netty.tcp.ConnectionHandler;
import ru.doh1221.wintymc.server.network.netty.tcp.packet.auth.Packet1Login;
import ru.doh1221.wintymc.server.network.netty.tcp.packet.auth.Packet2Handshake;
import ru.doh1221.wintymc.server.network.netty.tcp.packet.game.chat.Packet3Chat;
import ru.doh1221.wintymc.server.network.netty.tcp.packet.game.player.world.Packet13PlayerPositionLook;
import ru.doh1221.wintymc.server.network.netty.tcp.packet.game.world.Packet4WorldTime;
import ru.doh1221.wintymc.server.network.netty.tcp.packet.game.world.Packet50PreChunk;
import ru.doh1221.wintymc.server.network.netty.tcp.packet.game.world.Packet51MapChunk;
import ru.doh1221.wintymc.server.network.netty.tcp.packet.game.world.Packet6SpawnPosition;
import ru.doh1221.wintymc.server.network.netty.tcp.packet.general.Packet255DisconnectKick;
import ru.doh1221.wintymc.server.network.netty.tcp.packet.status.Packet254GetInfo;

import java.util.UUID;

public class InitialHandler extends ConnectionHandler {

    private Player player;

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
    public void handle(Packet1Login packet1Login) {
        if(packet1Login.protocolVersion != 14) {
            channel.write(new Packet255DisconnectKick("Server is out of date!"));
            channel.close();
        }
        WintyMC.getInstance().getLogger().info(player.displayname + " joined the server. " + player.uuid);

        channel.write(new Packet1Login(player.entityID, "CoolServer", 1851285L, (byte) 0));
        channel.write(new Packet6SpawnPosition(4, 80, 4));

        channel.write(new Packet4WorldTime(12000));



        channel.write(new Packet13PlayerPositionLook(4, 90, 1.0, 4, 1, 1));
        channel.write(new Packet4WorldTime(12000));

        World world = World.testWorld();
        //channel.write(new Packet50PreChunk(0, 0, true));
        for (var entry : world.chunks.entrySet()) {
            int cx = entry.getKey().leftInt();
            int cz = entry.getKey().rightInt();
            Chunk chunk = entry.getValue();

            // Сначала — объявляем чанк
            channel.write(new Packet50PreChunk(cx, cz, true));

            // Потом — сами данные
            byte[] data = chunk.toPacketData();

            Packet51MapChunk mapPacket = new Packet51MapChunk(
                    cx * 16,   // real world X
                    0,
                    cz * 16,   // real world Z
                    16,        // width (0–15 = 16 blocks)
                    128,       // height
                    16,        // depth
                    data
            );

            Packet51MapChunk.compress(mapPacket);

            channel.write(mapPacket);
        }


        channel.write(new Packet3Chat("§e" + player.displayname + " joined the game"));
    }

    @Override
    public void handle(Packet2Handshake packet2Handshake) {
        WintyMC.getInstance().getLogger().info(packet2Handshake.username + " connecting to the server: " + this.channel.getHandle().remoteAddress());
        channel.write(new Packet2Handshake("-"));
        player = new Player();
        player.username = packet2Handshake.username;
        player.displayname = packet2Handshake.username;
        player.uuid = UUID.randomUUID();
        player.health = 0.0;
    }

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
    public String toString() {
        return "";
    }
}