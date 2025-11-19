package ru.doh1221.wintymc.server.packet.game.player.world;

import io.netty.buffer.ByteBuf;
import ru.doh1221.wintymc.server.network.netty.tcp.PacketHandler;
import ru.doh1221.wintymc.server.packet.Packet;

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
    };

    @Override
    public void handle(PacketHandler handler) {
        handler.handle(this);
    }

    @Override
    public int size() {
        return 2;
    }
}
