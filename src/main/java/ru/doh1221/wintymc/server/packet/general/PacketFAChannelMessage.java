package ru.doh1221.wintymc.server.packet.general;

import io.netty.buffer.ByteBuf;
import ru.doh1221.wintymc.server.network.netty.tcp.PacketHandler;
import ru.doh1221.wintymc.server.packet.Packet;
import ru.doh1221.wintymc.server.utils.Var;

import java.io.IOException;

public class PacketFAChannelMessage extends Packet {

    public String channel;
    public short length;
    public byte[] rawdata;

    @Override
    public void readData(ByteBuf in) throws IOException {
        this.channel = Var.readString(in, false);
        this.length = in.readShort();
        rawdata = new byte[this.length];
        in.readBytes(rawdata);
    }

    @Override
    public void writeData(ByteBuf out) throws IOException {
        Var.writeString(this.channel, out, false);
        out.writeShort(this.length);
        out.writeBytes(this.rawdata);
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
