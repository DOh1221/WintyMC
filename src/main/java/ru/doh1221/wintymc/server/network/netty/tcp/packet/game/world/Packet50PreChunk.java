package ru.doh1221.wintymc.server.network.netty.tcp.packet.game.world;

import io.netty.buffer.ByteBuf;
import ru.doh1221.wintymc.server.network.netty.tcp.PacketHandler;
import ru.doh1221.wintymc.server.network.netty.tcp.packet.Packet;

import java.io.IOException;

public class Packet50PreChunk extends Packet {

    public int x;
    public int z;
    public boolean shouldLoad;

    public Packet50PreChunk() {

    }

    public Packet50PreChunk(int x, int z, boolean shouldLoad) {
        this.x = x;
        this.z = z;
        this.shouldLoad = shouldLoad;
    }

    @Override
    public void readData(ByteBuf in) throws IOException {
        this.x = in.readInt();
        this.z = in.readInt();
        this.shouldLoad = in.readBoolean();
    }

    @Override
    public void writeData(ByteBuf out) throws IOException {
        out.writeInt(this.x);
        out.writeInt(this.z);
        out.writeBoolean(this.shouldLoad);
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
