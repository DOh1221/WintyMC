package ru.doh1221.wintymc.server.network.netty.tcp.handler;

import ru.doh1221.wintymc.server.WintyMC;
import ru.doh1221.wintymc.server.entity.Player;
import ru.doh1221.wintymc.server.network.netty.tcp.ChannelWrapper;
import ru.doh1221.wintymc.server.network.netty.tcp.ConnectionHandler;
import ru.doh1221.wintymc.server.network.netty.tcp.packet.auth.Packet2Handshake;
import ru.doh1221.wintymc.server.network.netty.tcp.packet.general.Packet255DisconnectKick;
import ru.doh1221.wintymc.server.network.netty.tcp.packet.status.Packet254GetInfo;

public class InitialHandler extends ConnectionHandler {

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
    public void handle(Packet254GetInfo packet254GetInfo) {
        channel.write(createServerListPing(51, "Beta 1.7.3", "A  Minecraft Server", 0, 10));
    }

    @Override
    public void handle(Packet2Handshake packet2Handshake) {
        WintyMC.getInstance().getLogger().info(packet2Handshake.username + " [" + this.channel.getHandle().remoteAddress() + "] connected");
        channel.write(new Packet2Handshake("-")); // TODO Online Mode support

        Player player = new Player();
        player.username = packet2Handshake.username;

        setHandler(new InGameHandler(player));
    }

    @Override
    public String toString() {
        return "";
    }
}