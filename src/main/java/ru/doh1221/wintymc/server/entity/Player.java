package ru.doh1221.wintymc.server.entity;

import lombok.Getter;
import lombok.Setter;
import org.checkerframework.checker.units.qual.C;
import ru.doh1221.wintymc.server.game.player.ChunkManager;
import ru.doh1221.wintymc.server.network.netty.tcp.ChannelWrapper;
import ru.doh1221.wintymc.server.network.netty.tcp.packet.general.Packet255DisconnectKick;
import ru.doh1221.wintymc.server.utils.location.View3D;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
public class Player extends Entity {

    public String username;
    public UUID uuid = UUID.randomUUID();
    @Setter
    public String displayname;
    @Setter
    public double health = 0.0;
    @Setter
    public View3D position;
    public ChannelWrapper connection;
    public ChunkManager chunkManager;

    public Player(ChannelWrapper connection) {
        this.connection = connection;
        this.chunkManager = new ChunkManager(this);
    }

    public void kick(String disconnectReason) {
        this.connection.write(new Packet255DisconnectKick(disconnectReason));
        this.connection.disconnectReason = disconnectReason;
        this.connection.close();
    }

    public boolean isOnline() {
        return !connection.isClosed();
    }

}
