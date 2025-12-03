package ru.doh1221.wintymc.server.network.netty.tcp.packet.game.world;

import io.netty.buffer.ByteBuf;
import ru.doh1221.wintymc.server.network.netty.tcp.PacketHandler;
import ru.doh1221.wintymc.server.network.netty.tcp.packet.Packet;

import java.io.IOException;

public class Packet6SpawnPosition extends Packet {

    public int x = 0;
    public byte y = 0;
    public int z = 0;

    public Packet6SpawnPosition() {

    }

    public Packet6SpawnPosition(int x, int y, int z) {
        this.x = x;
        this.y = (byte) y;
        this.z = z;
    }

    @Override
    public void readData(ByteBuf in) throws IOException {
        this.x = in.readInt();
        this.y = (byte) in.readInt();
        this.z = in.readInt();
    }

    @Override
    public void writeData(ByteBuf out) throws IOException {
        out.writeInt(this.x);
        out.writeInt(this.y);
        out.writeInt(this.z);
    }

    @Override
    public void handle(PacketHandler handler) {
        handler.handle(this);
    }

    @Override
    public int size() {
        return 12;
    }
}
