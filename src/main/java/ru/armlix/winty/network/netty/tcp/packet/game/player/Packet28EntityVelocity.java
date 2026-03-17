package ru.armlix.winty.network.netty.tcp.packet.game.player;

import io.netty.buffer.ByteBuf;
import ru.armlix.winty.network.netty.tcp.PacketHandler;
import ru.armlix.winty.network.netty.tcp.packet.Packet;

import java.io.IOException;

public class Packet28EntityVelocity extends Packet {

    public int entityID;
    public short xVel;
    public short yVel;
    public short zVel;

    @Override
    public void readData(ByteBuf in) throws IOException {
        this.entityID = in.readInt();
        this.xVel = in.readShort();
        this.yVel = in.readShort();
        this.zVel = in.readShort();
    }

    @Override
    public void writeData(ByteBuf out) throws IOException {
        out.writeInt(this.entityID);
        out.writeShort(this.xVel);
        out.writeShort(this.yVel);
        out.writeShort(this.zVel);
    }

    @Override
    public void handle(PacketHandler handler) {
        handler.handle(this);
    }

    @Override
    public int size() {
        return 0x1B;
    }
}
