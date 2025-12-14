package ru.doh1221.wintymc.server.network.netty.tcp.packet;

import io.netty.buffer.ByteBuf;
import ru.doh1221.wintymc.server.network.netty.tcp.PacketHandler;

import java.io.IOException;

public class Packet39MountEntity extends Packet {

    public int entityRiderID;
    public int entityRideeID;

    @Override
    public void readData(ByteBuf in) throws IOException {
        this.entityRiderID = in.readByte();
        this.entityRideeID = in.readByte();
    }

    @Override
    public void writeData(ByteBuf out) throws IOException {
        out.writeByte(this.entityRiderID);
        out.writeByte(this.entityRideeID);
    }

    @Override
    public void handle(PacketHandler handler) {

    }

    @Override
    public int size() {
        return 0;
    }
}
