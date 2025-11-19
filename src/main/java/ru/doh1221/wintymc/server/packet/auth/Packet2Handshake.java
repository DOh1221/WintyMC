package ru.doh1221.wintymc.server.packet.auth;

import io.netty.buffer.ByteBuf;
import ru.doh1221.wintymc.server.network.netty.tcp.PacketHandler;
import ru.doh1221.wintymc.server.packet.Packet;
import ru.doh1221.wintymc.server.utils.Var;

import java.io.IOException;

public class Packet2Handshake extends Packet {

    public boolean clientSide = false;
    public String username = "";

    public Packet2Handshake() {

    }

    public Packet2Handshake(String username) {
        this.username = username;
    }

    @Override
    public void readData(ByteBuf in) throws IOException {
        this.username = Var.readString(in, false);
    }

    @Override
    public void writeData(ByteBuf out) throws IOException {
        Var.writeString(this.username, out, false);
    }

    @Override
    public void handle(PacketHandler handler) {
        handler.handle(this);
    }

    @Override
    public int size() {
        return 2 + username.getBytes().length;
    }
}
