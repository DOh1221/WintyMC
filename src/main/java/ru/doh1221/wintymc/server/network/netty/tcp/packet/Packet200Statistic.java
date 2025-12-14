package ru.doh1221.wintymc.server.network.netty.tcp.packet;

import io.netty.buffer.ByteBuf;
import ru.doh1221.wintymc.server.network.netty.tcp.PacketHandler;

import java.io.IOException;

public class Packet200Statistic extends Packet {

    public int statisticID;
    public byte amount;

    @Override
    public void readData(ByteBuf in) throws IOException {
        this.statisticID = in.readInt();
        this.amount = in.readByte();
    }

    @Override
    public void writeData(ByteBuf out) throws IOException {
        out.writeInt(this.statisticID);
        out.writeByte(this.amount);
    }

    @Override
    public void handle(PacketHandler handler) {
        handler.handle(this);
    }

    @Override
    public int size() {
        return 0;
    }
}
