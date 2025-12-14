package ru.doh1221.wintymc.server.network.netty.tcp.packet;

import io.netty.buffer.ByteBuf;
import ru.doh1221.wintymc.server.network.netty.tcp.PacketHandler;

import java.io.IOException;

public class Packet38EntityHealthAction extends Packet {

    public int entityID;
    public byte actionID;

    @Override
    public void readData(ByteBuf in) throws IOException {
        this.entityID = in.readInt();
        this.actionID = in.readByte();
    }

    @Override
    public void writeData(ByteBuf out) throws IOException {
        out.writeInt(this.entityID);
        out.writeByte(this.actionID);
    }

    @Override
    public void handle(PacketHandler handler) {

    }

    @Override
    public int size() {
        return 0;
    }
}
