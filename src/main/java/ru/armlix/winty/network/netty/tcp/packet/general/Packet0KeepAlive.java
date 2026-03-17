package ru.armlix.winty.network.netty.tcp.packet.general;

import io.netty.buffer.ByteBuf;
import ru.armlix.winty.network.netty.tcp.PacketHandler;
import ru.armlix.winty.network.netty.tcp.packet.Packet;

import java.io.IOException;

public class Packet0KeepAlive extends Packet {

    public boolean clientSide = false;

    @Override
    public void readData(ByteBuf in) throws IOException {

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
