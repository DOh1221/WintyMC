package ru.armlix.winty.network.netty.tcp.packet.game.player;

import io.netty.buffer.ByteBuf;
import ru.armlix.winty.network.netty.tcp.PacketHandler;
import ru.armlix.winty.network.netty.tcp.packet.Packet;

import java.io.IOException;

public class Packet104WindowItem extends Packet {

    public byte worldInfo = 0;

    @Override
    public void readData(ByteBuf in) throws IOException {

    }

    @Override
    public void writeData(ByteBuf out) throws IOException {
        System.out.println("NOT IMPLEMENTED YES");
    }

    @Override
    public void handle(PacketHandler handler) {
        handler.handle(this);
    }

    @Override
    public int size() {
        return 0x64;
    }
}
