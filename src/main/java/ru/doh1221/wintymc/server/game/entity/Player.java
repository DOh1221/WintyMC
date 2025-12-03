package ru.doh1221.wintymc.server.game.entity;

import lombok.Getter;
import ru.doh1221.wintymc.server.network.netty.tcp.ChannelWrapper;
import ru.doh1221.wintymc.server.utils.location.Loc3D;
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

    public static long key(int cx, int cz) {
        return (((long) cx) << 32) ^ (cz & 0xffffffffL);
    }


}
