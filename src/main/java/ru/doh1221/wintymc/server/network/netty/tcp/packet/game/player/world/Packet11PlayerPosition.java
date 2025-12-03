package ru.doh1221.wintymc.server.network.netty.tcp.packet.game.player.world;

import io.netty.buffer.ByteBuf;
import lombok.SneakyThrows;

public class Packet11PlayerPosition extends Packet10OnGround {

    public double x;
    public double y;
    public double stance;
    public double z;

    @SneakyThrows
    @Override
    public void readData(ByteBuf in) {
        this.x = in.readDouble();
        this.y = in.readDouble();
        this.stance = in.readDouble();
        this.z = in.readDouble();
        super.readData(in);
    }

    @Override
    public void writeData(ByteBuf out) {
        out.writeDouble(this.x);
        out.writeDouble(this.y);
        out.writeDouble(this.stance);
        out.writeDouble(this.z);
    }

    @Override
    public int size() {
        return super.size() + 32;
    }

}
