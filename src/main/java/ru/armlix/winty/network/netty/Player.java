package ru.armlix.winty.network.netty;

import lombok.Getter;
import ru.armlix.winty.game.GameServer;
import ru.armlix.winty.game.entiy.LivingEntity;
import ru.armlix.winty.network.netty.tcp.ChannelWrapper;
import ru.armlix.winty.utils.location.View3D;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
public class Player extends LivingEntity {

    public Player() {
        super(GameServer.alloc);
    }

    public String username;
    public UUID uuid;
    public String displayname;
    public View3D position;
    public ChannelWrapper connection;

    public Set<Long> loadedChunks = new HashSet<>();

    public static long key(int cx, int cz) {
        return (((long) cx) << 32) ^ (cz & 0xffffffffL);
    }

}