package ru.doh1221.wintymc.server.network.netty.tcp.packet;

import io.netty.buffer.ByteBuf;
import ru.doh1221.wintymc.server.network.netty.tcp.PacketHandler;
import ru.doh1221.wintymc.server.utils.Stream;

import java.io.IOException;

public class Packet130Sign extends Packet {

    public int x;
    public short y;
    public int z;
    public String[] lines = new String[]{"", "", "", ""};

    @Override
    public void readData(ByteBuf in) throws IOException {
        this.x = in.readInt();
        this.y = in.readShort();
        this.z = in.readInt();
        for(int x = 0; x < lines.length; x++) {
            lines[x] = Stream.readString(in, false);
        }
    }

    @Override
    public void writeData(ByteBuf out) throws IOException {
        out.writeInt(this.x);
        out.writeShort(this.y);
        out.writeInt(this.z);
        for (String line : lines) {
            Stream.writeString(line, out, false);
        }
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
