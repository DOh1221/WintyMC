package ru.doh1221.wintymc.server.network.netty.tcp.packet.game.player;

import io.netty.buffer.ByteBuf;
import ru.doh1221.wintymc.server.network.netty.tcp.PacketHandler;
import ru.doh1221.wintymc.server.network.netty.tcp.packet.Packet;

import java.io.IOException;

public class Packet18PlayerAnimation extends Packet {

    public int entityID;
    public byte animationID;

    @Override
    public void readData(ByteBuf in) throws IOException {
        this.entityID = in.readInt();
        this.animationID = in.readByte();
    }

    @Override
    public void writeData(ByteBuf out) throws IOException {
        out.writeInt(this.entityID);
        out.writeByte(this.animationID);
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
