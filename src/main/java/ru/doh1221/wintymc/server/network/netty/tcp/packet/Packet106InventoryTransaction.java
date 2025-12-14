package ru.doh1221.wintymc.server.network.netty.tcp.packet;

import io.netty.buffer.ByteBuf;
import ru.doh1221.wintymc.server.network.netty.tcp.PacketHandler;

import java.io.IOException;

public class Packet106InventoryTransaction extends Packet {

    public byte windowID;
    public short actionTransactionID;
    public boolean accepted;

    @Override
    public void readData(ByteBuf in) throws IOException {
        this.windowID = in.readByte();
        this.actionTransactionID = in.readShort();
        this.accepted = in.readBoolean();
    }

    @Override
    public void writeData(ByteBuf out) throws IOException {
        out.writeByte(this.windowID);
        out.writeShort(this.actionTransactionID);
        out.writeBoolean(this.accepted);
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
