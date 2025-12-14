package ru.doh1221.wintymc.server.entity;

import lombok.Getter;
import ru.doh1221.wintymc.server.network.netty.tcp.ChannelWrapper;
import ru.doh1221.wintymc.server.network.netty.tcp.packet.general.Packet255DisconnectKick;
import ru.doh1221.wintymc.server.utils.location.View3D;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
public class Player extends Entity {

    public String username;
    public UUID uuid;
    public String displayname;
    public double health;
    public View3D position;
    public ChannelWrapper connection;

    public Set<Long> loadedChunks = new HashSet<>();

    public void kick(String disconnectReason) {
        this.connection.write(new Packet255DisconnectKick(disconnectReason));
        this.connection.disconnectReason = disconnectReason;
        this.connection.close();
    }

}
