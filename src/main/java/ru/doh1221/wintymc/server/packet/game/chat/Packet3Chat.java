package ru.doh1221.wintymc.server.packet.game.chat;

import io.netty.buffer.ByteBuf;
import ru.doh1221.wintymc.server.network.netty.tcp.PacketHandler;
import ru.doh1221.wintymc.server.packet.Packet;
import ru.doh1221.wintymc.server.utils.Var;

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
        this.message = Var.readString(in, false);
    }

    @Override
    public void writeData(ByteBuf out) throws IOException {
        Var.writeString(this.message, out, false);
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
