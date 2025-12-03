package ru.doh1221.wintymc.server.network.netty.tcp.packet.game.world;

import io.netty.buffer.ByteBuf;
import ru.doh1221.wintymc.server.network.netty.tcp.PacketHandler;
import ru.doh1221.wintymc.server.network.netty.tcp.packet.Packet;

import java.io.IOException;

public class Packet15BlockPlace extends Packet {

    public int x;
    public byte y;
    public int z;
    public byte face;
    public short itemID;
    public byte amount;
    public short damage;

    @Override
    public void readData(ByteBuf in) throws IOException {
        this.x = in.readInt();
        this.y = in.readByte();
        this.z = in.readInt();
        this.face = in.readByte();
        this.itemID = in.readShort();
        this.amount = in.readByte();
        this.damage = in.readShort();
    }

    @Override
    public void writeData(ByteBuf out) throws IOException {
        out.writeInt(this.x);
        out.writeByte(this.y);
        out.writeInt(this.z);
        out.writeByte(this.face);
        out.writeShort(this.itemID);
        out.writeByte(this.amount);
        out.writeShort(this.damage);
    };

    @Override
    public void handle(PacketHandler handler) {
        handler.handle(this);
    }

    @Override
    public int size() {
        return 14;
    }
}
