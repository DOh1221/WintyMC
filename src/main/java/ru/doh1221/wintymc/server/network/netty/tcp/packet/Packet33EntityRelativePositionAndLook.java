package ru.doh1221.wintymc.server.network.netty.tcp.packet;

import io.netty.buffer.ByteBuf;
import ru.doh1221.wintymc.server.network.netty.tcp.PacketHandler;

import java.io.IOException;

public class Packet33EntityRelativePositionAndLook extends Packet {
    public int entityID;
    public byte x;
    public byte y;
    public byte z;
    public byte yaw;
    public byte pitch;

    @Override
    public void readData(ByteBuf in) throws IOException {
        this.entityID = in.readInt();
        this.x = in.readByte();
        this.y = in.readByte();
        this.z = in.readByte();
        this.yaw = in.readByte();
        this.pitch = in.readByte();
    }

    @Override
    public void writeData(ByteBuf out) throws IOException {
        out.writeInt(this.entityID);
        out.writeByte(this.x);
        out.writeByte(this.y);
        out.writeByte(this.z);
        out.writeByte(this.yaw);
        out.writeByte(this.pitch);
    }

    @Override
    public void handle(PacketHandler handler) {
        handler.handle(this);
    }

    @Override
    public int size() {
        return 0;
    }
}
