package ru.doh1221.wintymc.server.network.netty.tcp.packet;

import io.netty.buffer.ByteBuf;
import ru.doh1221.wintymc.server.network.netty.tcp.PacketHandler;

import java.io.IOException;

public class Packet131ItemData extends Packet {

    public short itemID;
    public short mapID;
    public byte dataSize;
    public byte[] binaryData;

    @Override
    public void readData(ByteBuf in) throws IOException {
        this.itemID = in.readShort();
        this.mapID = in.readShort();
        this.dataSize = in.readByte();
        this.binaryData = new byte[this.dataSize];
        in.readBytes(this.binaryData);
    }

    @Override
    public void writeData(ByteBuf out) throws IOException {
        out.writeShort(this.itemID);
        out.writeShort(this.mapID);
        out.writeByte(this.binaryData.length);
        out.writeBytes(this.binaryData);
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
