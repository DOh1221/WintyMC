package ru.doh1221.wintymc.server.packet.game.player.world;

import io.netty.buffer.ByteBuf;
import lombok.SneakyThrows;

public class Packet12PlayerLook extends Packet10OnGround {

    public float yaw;
    public float pitch;

    @SneakyThrows
    @Override
    public void readData(ByteBuf in) {
        this.yaw = in.readFloat();
        this.pitch = in.readFloat();
        super.readData(in);
    }

    @Override
    public void writeData(ByteBuf out) {
        out.writeFloat(this.yaw);
        out.writeFloat(this.pitch);
    }

    @Override
    public int size() {
        return super.size() + 8;
    }

}
