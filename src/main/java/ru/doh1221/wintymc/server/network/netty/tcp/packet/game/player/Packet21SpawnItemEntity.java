package ru.doh1221.wintymc.server.network.netty.tcp.packet.game.player;

import io.netty.buffer.ByteBuf;
import ru.doh1221.wintymc.server.network.netty.tcp.PacketHandler;
import ru.doh1221.wintymc.server.network.netty.tcp.packet.Packet;

import java.io.IOException;

public class Packet21SpawnItemEntity extends Packet {

    public int entityID;
    public short itemID;
    public byte count;
    public short damage;
    public int x;
    public int y;
    public int z;
    public byte yaw;
    public byte pitch;
    public byte roll;

    @Override
    public void readData(ByteBuf in) throws IOException {
        this.entityID = in.readInt();
        this.itemID = in.readShort();
        this.count = in.readByte();
        this.damage = in.readShort();
        this.x = in.readInt();
        this.y = in.readInt();
        this.z = in.readInt();
        this.yaw = in.readByte();
        this.pitch = in.readByte();
        this.roll = in.readByte();
    }

    @Override
    public void writeData(ByteBuf out) throws IOException {
        out.writeInt(this.entityID);
        out.writeShort(this.itemID);
        out.writeByte(this.count);
        out.writeShort(this.damage);
        out.writeInt(this.x);
        out.writeInt(this.y);
        out.writeInt(this.z);
        out.writeByte(this.yaw);
        out.writeByte(this.pitch);
        out.writeByte(this.roll);
    }

    @Override
    public void handle(PacketHandler handler) {
        handler.handle(this);
    }

    @Override
    public int size() {
        return 0x15;
    }
}
