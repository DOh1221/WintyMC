package ru.armlix.winty.network.netty;

import lombok.Getter;
import ru.armlix.winty.game.GameServer;
import ru.armlix.winty.game.entiy.living.NetworkHumanEntity;
import ru.armlix.winty.network.netty.tcp.ChannelWrapper;
import ru.armlix.winty.utils.location.Location;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
public class Player extends NetworkHumanEntity {

    public Player(ChannelWrapper channelWrapper) {
        super(GameServer.alloc, channelWrapper);
    }

    public String username;
    public UUID uuid;
    public String displayname;
    public Location position;
    public ChannelWrapper connection;

    public Set<Long> loadedChunks = new HashSet<>();

    public static long key(int cx, int cz) {
        return (((long) cx) << 32) ^ (cz & 0xffffffffL);
    }

}