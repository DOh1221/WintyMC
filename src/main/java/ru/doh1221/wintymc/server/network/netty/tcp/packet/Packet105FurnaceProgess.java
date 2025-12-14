package ru.doh1221.wintymc.server.network.netty.tcp.packet;

import io.netty.buffer.ByteBuf;
import ru.doh1221.wintymc.server.network.netty.tcp.PacketHandler;

import java.io.IOException;

public class Packet105FurnaceProgess extends Packet {

    public byte windowID;
    public short barProgess;
    public short value;

    @Override
    public void readData(ByteBuf in) throws IOException {
        this.windowID = in.readByte();
        this.barProgess = in.readShort();
        this.value = in.readShort();
    }

    @Override
    public void writeData(ByteBuf out) throws IOException {
        out.writeByte(this.windowID);
        out.writeShort(this.barProgess);
        out.writeShort(this.value);
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
