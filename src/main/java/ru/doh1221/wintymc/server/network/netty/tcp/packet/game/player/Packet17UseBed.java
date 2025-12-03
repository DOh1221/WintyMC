package ru.doh1221.wintymc.server.network.netty.tcp.packet.game.player;

import io.netty.buffer.ByteBuf;
import ru.doh1221.wintymc.server.network.netty.tcp.PacketHandler;
import ru.doh1221.wintymc.server.network.netty.tcp.packet.Packet;

import java.io.IOException;

public class Packet17UseBed extends Packet {

    public int entityID;
    public byte unused;
    public int x;
    public byte y;
    public int z;

    @Override
    public void readData(ByteBuf in) throws IOException {
        this.entityID = in.readInt();
        this.unused = in.readByte();
        this.x = in.readInt();
        this.y = in.readByte();
        this.z = in.readInt();
    }

    @Override
    public void writeData(ByteBuf out) throws IOException {
        out.writeInt(this.entityID);
        out.writeByte(this.unused);
        out.writeInt(this.x);
        out.writeByte(this.y);
        out.writeInt(this.z);
    }

    @Override
    public void handle(PacketHandler handler) {
        handler.handle(this);
    }

    @Override
    public int size() {
        return 1;
    }
}
