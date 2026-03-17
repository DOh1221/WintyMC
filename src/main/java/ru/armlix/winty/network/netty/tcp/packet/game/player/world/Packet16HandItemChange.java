package ru.armlix.winty.network.netty.tcp.packet.game.player.world;

import io.netty.buffer.ByteBuf;
import ru.armlix.winty.network.netty.tcp.PacketHandler;
import ru.armlix.winty.network.netty.tcp.packet.Packet;

import java.io.IOException;

public class Packet16HandItemChange extends Packet {

    public short slot;

    @Override
    public void readData(ByteBuf in) throws IOException {
        this.slot = in.readShort();
    }

    @Override
    public void writeData(ByteBuf out) throws IOException {
        out.writeShort(this.slot);
    }

    @Override
    public void handle(PacketHandler handler) {
        handler.handle(this);
    }

    @Override
    public int size() {
        return 2;
    }
}
