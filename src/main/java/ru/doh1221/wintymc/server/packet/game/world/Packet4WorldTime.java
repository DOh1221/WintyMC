package ru.doh1221.wintymc.server.packet.game.world;

import io.netty.buffer.ByteBuf;
import ru.doh1221.wintymc.server.network.netty.tcp.PacketHandler;
import ru.doh1221.wintymc.server.packet.Packet;

import java.io.IOException;

public class Packet4WorldTime extends Packet {

    public long worldTime = 0L;

    public Packet4WorldTime() {

    }

    public Packet4WorldTime(long time) {
        this.worldTime = time;
    }

    @Override
    public void readData(ByteBuf in) throws IOException {
        this.worldTime = in.readLong();
    }

    @Override
    public void writeData(ByteBuf out) throws IOException {
        out.writeLong(this.worldTime);
    }

    @Override
    public void handle(PacketHandler handler) {
        handler.handle(this);
    }

    @Override
    public int size() {
        return 8;
    }
}
