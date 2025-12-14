package ru.doh1221.wintymc.server.network.netty.tcp.packet;

import io.netty.buffer.ByteBuf;
import ru.doh1221.wintymc.server.network.netty.tcp.PacketHandler;
import ru.doh1221.wintymc.server.utils.Stream;

import java.io.IOException;

public class Packet25SpawnPainting extends Packet {

    public int entityID;
    public String title;
    public int x;
    public int y;
    public int z;
    public byte direction;

    @Override
    public void readData(ByteBuf in) throws IOException {
        this.entityID = in.readInt();
        this.title = Stream.readString(in, false);
        this.x = in.readInt();
        this.y = in.readInt();
        this.z = in.readInt();
        this.direction = in.readByte();
    }

    @Override
    public void writeData(ByteBuf out) throws IOException {
        out.writeInt(this.entityID);
        Stream.writeString(this.title, out, false);
        out.writeInt(this.x);
        out.writeInt(this.y);
        out.writeInt(this.z);
        out.writeByte(this.direction);
    }

    @Override
    public void handle(PacketHandler handler) {
        handler.handle(this);
    }

    @Override
    public int size() {
        return 0x1B;
    }

}
