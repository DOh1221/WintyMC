package ru.doh1221.wintymc.server.network.netty.tcp.packet.game.player;

import io.netty.buffer.ByteBuf;
import ru.doh1221.wintymc.server.network.netty.tcp.PacketHandler;
import ru.doh1221.wintymc.server.network.netty.tcp.packet.Packet;

import java.io.IOException;

public class Packet22CollectItem extends Packet {

    public int entityItemID;
    public int entityCollectorID;

    @Override
    public void readData(ByteBuf in) throws IOException {
        this.entityItemID = in.readInt();
        this.entityCollectorID = in.readInt();
    }

    @Override
    public void writeData(ByteBuf out) throws IOException {
        out.writeInt(this.entityItemID);
        out.writeInt(this.entityCollectorID);
    }

    @Override
    public void handle(PacketHandler handler) {
        handler.handle(this);
    }

    @Override
    public int size() {
        return 0x15;
    }
}
