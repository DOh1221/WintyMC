package ru.doh1221.wintymc.server.packet.game.entity;

import io.netty.buffer.ByteBuf;
import ru.doh1221.wintymc.server.network.netty.tcp.PacketHandler;
import ru.doh1221.wintymc.server.packet.Packet;

import java.io.IOException;

public class Packet5EntityEquipment extends Packet {

    public int entityID = 0;
    public short inventorySlot = 0;
    public short itemID = 0;
    public short metadata = 0;

    @Override
    public void readData(ByteBuf in) throws IOException {
        this.entityID = in.readInt();
        this.inventorySlot = in.readShort();
        this.itemID = in.readShort();
        this.metadata = in.readShort();
    }

    @Override
    public void writeData(ByteBuf out) throws IOException {
        out.writeInt(this.entityID);
        out.writeShort(this.inventorySlot);
        out.writeShort(this.itemID);
        out.writeShort(this.metadata);
    }

    @Override
    public void handle(PacketHandler handler) {
        handler.handle(this);
    }

    @Override
    public int size() {
        return 10;
    }
}
