package ru.doh1221.wintymc.server.network.netty.tcp.packet.game.player;

import io.netty.buffer.ByteBuf;
import ru.doh1221.wintymc.server.network.netty.tcp.PacketHandler;
import ru.doh1221.wintymc.server.network.netty.tcp.packet.Packet;

import java.io.IOException;

public class Packet100OpenWindow extends Packet {

    public byte worldInfo = 0;

    @Override
    public void readData(ByteBuf in) throws IOException {
        System.out.println("ewgewgewge");
    }

    @Override
    public void writeData(ByteBuf out) throws IOException {
        out.writeByte(this.worldInfo);
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
