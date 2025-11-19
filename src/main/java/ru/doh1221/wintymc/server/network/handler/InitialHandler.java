package ru.doh1221.wintymc.server.network.handler;

import ru.doh1221.wintymc.server.entity.Player;
import ru.doh1221.wintymc.server.network.ChannelWrapper;
import ru.doh1221.wintymc.server.network.ConnectionHandler;
import ru.doh1221.wintymc.server.packet.auth.Packet1Login;
import ru.doh1221.wintymc.server.packet.auth.Packet2Handshake;
import ru.doh1221.wintymc.server.packet.game.chat.Packet3Chat;
import ru.doh1221.wintymc.server.packet.game.player.world.Packet13PlayerPositionLook;
import ru.doh1221.wintymc.server.packet.game.player.world.Packet6SpawnPosition;
import ru.doh1221.wintymc.server.packet.game.player.world.chunk.Packet50PreChunk;
import ru.doh1221.wintymc.server.packet.game.world.Packet4WorldTime;
import ru.doh1221.wintymc.server.packet.game.world.Packet51MapChunk;
import ru.doh1221.wintymc.server.packet.general.Packet255DisconnectKick;
import ru.doh1221.wintymc.server.packet.status.Packet254GetInfo;
import ru.doh1221.wintymc.server.world.Chunk;
import ru.doh1221.wintymc.server.world.World;

import java.util.UUID;

public class InitialHandler extends ConnectionHandler {

    private ChannelWrapper channelRef;
    private Player player;

    @Override
    public void connected(ChannelWrapper channel) throws Exception {
        this.channel = channel;
        this.channelRef = channel;
    }

    @Override
    public void disconnected(ChannelWrapper channel) throws Exception {

    }

    @Override
    public void handle(Packet254GetInfo packet254GetInfo) {
        channelRef.write(createServerListPing(51, "Beta 1.7.3", "A  Minecraft Server", 0, 10));
    }

    @Override
    public void handle(Packet3Chat packet3Chat) {
        channelRef.write(new Packet3Chat("<" + player.displayname + "> " + packet3Chat.message));
    }

    @Override
    public void handle(Packet1Login packet1Login) {
        if(packet1Login.protocolVersion != 14) {
            channelRef.write(new Packet255DisconnectKick("Server is out of date!"));
            channelRef.close();
        }
        System.out.println(player.displayname + " joined the server. " + player.uuid);

        channelRef.write(new Packet1Login(player.entityID, "CoolServer", 1851285L, (byte) 0));
        channelRef.write(new Packet6SpawnPosition(4, 128, 4));

        channelRef.write(new Packet4WorldTime(12000));



        channelRef.write(new Packet13PlayerPositionLook(4, 80, 1.0, 4, 1, 1));
        channelRef.write(new Packet4WorldTime(12000));

        World world = World.testWorld();
        //channelRef.write(new Packet50PreChunk(0, 0, true));
        for (var entry : world.chunks.entrySet()) {
            int cx = entry.getKey().leftInt();
            int cz = entry.getKey().rightInt();
            Chunk chunk = entry.getValue();

            // Сначала — объявляем чанк
            channelRef.write(new Packet50PreChunk(cx, cz, true));

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

            channelRef.write(mapPacket);
        }


        channelRef.write(new Packet3Chat("§e" + player.displayname + " joined the game"));
    }

    @Override
    public void handle(Packet2Handshake packet2Handshake) {
        System.out.println(packet2Handshake.username + " connecting to the server.");
        channelRef.write(new Packet2Handshake("-"));
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
