package ru.doh1221.wintymc.server.network.netty.tcp.packet.game.entity;

import io.netty.buffer.ByteBuf;
import ru.doh1221.wintymc.server.network.netty.tcp.PacketHandler;
import ru.doh1221.wintymc.server.network.netty.tcp.packet.Packet;

import java.io.IOException;

public class Packet7ClickEntity extends Packet {

    public int playerID = 0;
    public int entityID = 0;
    public boolean leftClicked = false;

    @Override
    public void readData(ByteBuf in) throws IOException {
        this.playerID = in.readInt();
        this.entityID = in.readInt();
        this.leftClicked = in.readBoolean();
    }

    @Override
    public void writeData(ByteBuf out) throws IOException {
        out.writeInt(this.playerID);
        out.writeInt(this.entityID);
        out.writeBoolean(this.leftClicked);
    }

    @Override
    public void handle(PacketHandler handler) {
        handler.handle(this);
    }

    @Override
    public int size() {
        return 9;
    }
}
