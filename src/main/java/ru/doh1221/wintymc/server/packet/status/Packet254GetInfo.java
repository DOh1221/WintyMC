package ru.doh1221.wintymc.server.packet.status;

import io.netty.buffer.ByteBuf;
import ru.doh1221.wintymc.server.network.netty.tcp.PacketHandler;
import ru.doh1221.wintymc.server.packet.Packet;

import java.io.IOException;

public class Packet254GetInfo extends Packet {

    @Override
    public void readData(ByteBuf in) throws IOException {
        in.skipBytes(in.readableBytes());
    }

    @Override
    public void writeData(ByteBuf out) throws IOException {

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
