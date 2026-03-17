package ru.armlix.winty.network.netty.tcp.packet.game.chat;

import io.netty.buffer.ByteBuf;
import ru.armlix.winty.network.netty.tcp.PacketHandler;
import ru.armlix.winty.network.netty.tcp.packet.Packet;
import ru.armlix.winty.utils.Stream;

import java.io.IOException;

public class Packet3Chat extends Packet {

    public boolean clientSide = false;
    public String message = "";

    public Packet3Chat() {

    }

    public Packet3Chat(String message) {
        this.message = message;
    }

    @Override
    public void readData(ByteBuf in) throws IOException {
        this.message = Stream.readString(in, false);
    }

    @Override
    public void writeData(ByteBuf out) throws IOException {
        Stream.writeString(this.message, out, false);
    }

    @Override
    public void handle(PacketHandler handler) {
        handler.handle(this);
    }

    @Override
    public int size() {
        return 2 + this.message.getBytes().length;
    }
}
