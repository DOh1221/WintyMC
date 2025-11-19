package ru.doh1221.wintymc.server.packet.game.player.world;

import io.netty.buffer.ByteBuf;
import ru.doh1221.wintymc.server.network.netty.tcp.PacketHandler;
import ru.doh1221.wintymc.server.packet.Packet;

import java.io.IOException;

public class Packet14BlockDestroy extends Packet {

    public byte status = 0;
    public int x;
    public byte y;
    public int z;
    public byte face;

    @Override
    public void readData(ByteBuf in) throws IOException {
        this.status = in.readByte();
        this.x = in.readInt();
        this.y = in.readByte();
        this.z = in.readInt();
        this.face = in.readByte();
    }

    @Override
    public void writeData(ByteBuf out) throws IOException {
        out.writeByte(this.status);
        out.writeInt(this.x);
        out.writeByte(this.y);
        out.writeInt(this.z);
        out.writeByte(this.face);
    }

    @Override
    public void handle(PacketHandler handler) {
        handler.handle(this);
    }

    @Override
    public int size() {
        return 14;
    }
}
