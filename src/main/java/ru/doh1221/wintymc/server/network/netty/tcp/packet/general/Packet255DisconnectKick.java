package ru.doh1221.wintymc.server.network.netty.tcp.packet.general;

import io.netty.buffer.ByteBuf;
import ru.doh1221.wintymc.server.network.netty.tcp.PacketHandler;
import ru.doh1221.wintymc.server.network.netty.tcp.packet.Packet;
import ru.doh1221.wintymc.server.utils.Stream;

import java.io.IOException;

public class Packet255DisconnectKick extends Packet {

    public boolean clientSide = false;
    public String disconnectReason = "";

    public Packet255DisconnectKick(String s) {
        this.disconnectReason = s;
    }

    @Override
    public void readData(ByteBuf in) throws IOException {
        this.disconnectReason = Stream.readString(in, false);
    }

    @Override
    public void writeData(ByteBuf out) throws IOException {
        Stream.writeString(this.disconnectReason, out, false);
    }

    @Override
    public void handle(PacketHandler handler) {
        handler.handle(this);
    }

    @Override
    public int size() {
        return 2 + disconnectReason.getBytes().length;
    }
}
