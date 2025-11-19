package ru.doh1221.wintymc.server.packet.general;

import io.netty.buffer.ByteBuf;
import ru.doh1221.wintymc.server.network.netty.tcp.PacketHandler;
import ru.doh1221.wintymc.server.packet.Packet;
import ru.doh1221.wintymc.server.utils.Var;

import java.io.IOException;

public class Packet255DisconnectKick extends Packet {

    public boolean clientSide = false;
    public String disconnectReason = "";

    public Packet255DisconnectKick(String s) {
        this.disconnectReason = s;
    }

    @Override
    public void readData(ByteBuf in) throws IOException {
        this.disconnectReason = Var.readString(in, false);
    }

    @Override
    public void writeData(ByteBuf out) throws IOException {
        Var.writeString(this.disconnectReason, out, false);
    }

    @Override
    public void handle(PacketHandler handler) {
        handler.handle(this);
    }

    @Override
    public int size() {
        return 2 + disconnectReason.getBytes().length;
    }
}
