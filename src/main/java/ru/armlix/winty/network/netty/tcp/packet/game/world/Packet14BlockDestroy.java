package ru.armlix.winty.network.netty.tcp.packet.game.world;

import io.netty.buffer.ByteBuf;
import ru.armlix.winty.network.netty.tcp.PacketHandler;
import ru.armlix.winty.network.netty.tcp.packet.Packet;

import java.io.IOException;

public class Packet14BlockDestroy extends Packet {

    public byte status;
    public int x;
    public byte y;
    public int z;
    public byte face;

    @Override
    public void readData(ByteBuf in) throws IOException {
        this.status = in.readByte();
        this.x = in.readInt();
        this.y = in.readByte();
        this.z = in.readInt();
        this.face = in.readByte();
    }

    @Override
    public void writeData(ByteBuf out) throws IOException {
        out.writeByte(this.status);
        out.writeInt(this.x);
        out.writeByte(this.y);
        out.writeInt(this.z);
        out.writeByte(this.face);
    }

    @Override
    public void handle(PacketHandler handler) {
        handler.handle(this);
    }

    @Override
    public int size() {
        return 14;
    }
}
