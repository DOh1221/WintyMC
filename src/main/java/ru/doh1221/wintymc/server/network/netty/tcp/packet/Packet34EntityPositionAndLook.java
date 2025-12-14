package ru.doh1221.wintymc.server.network.netty.tcp.packet;

import io.netty.buffer.ByteBuf;
import ru.doh1221.wintymc.server.network.netty.tcp.PacketHandler;

import java.io.IOException;

public class Packet34EntityPositionAndLook extends Packet {
    public int entityID;
    public int x;
    public int y;
    public int z;
    public byte yaw;
    public byte pitch;

    @Override
    public void readData(ByteBuf in) throws IOException {
        this.entityID = in.readInt();
        this.x = in.readInt();
        this.y = in.readInt();
        this.z = in.readInt();
        this.yaw = in.readByte();
        this.pitch = in.readByte();
    }

    @Override
    public void writeData(ByteBuf out) throws IOException {
        out.writeInt(this.entityID);
        out.writeInt(this.x);
        out.writeInt(this.y);
        out.writeInt(this.z);
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
