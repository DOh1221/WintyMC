package ru.armlix.winty.network.netty.tcp.packet.game.player.world;

import io.netty.buffer.ByteBuf;
import ru.armlix.winty.network.netty.tcp.PacketHandler;
import ru.armlix.winty.network.netty.tcp.packet.Packet;

import java.io.IOException;

public class Packet9Respawn extends Packet {

    public byte dimension = 0;

    @Override
    public void readData(ByteBuf in) throws IOException {
        this.dimension = in.readByte();
    }

    @Override
    public void writeData(ByteBuf out) throws IOException {
        out.writeByte(this.dimension);
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
