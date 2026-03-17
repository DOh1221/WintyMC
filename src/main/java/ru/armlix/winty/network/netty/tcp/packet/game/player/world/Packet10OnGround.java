package ru.armlix.winty.network.netty.tcp.packet.game.player.world;

import io.netty.buffer.ByteBuf;
import ru.armlix.winty.network.netty.tcp.PacketHandler;
import ru.armlix.winty.network.netty.tcp.packet.Packet;

import java.io.IOException;

public class Packet10OnGround extends Packet {

    public boolean onGround = true;

    @Override
    public void readData(ByteBuf in) throws IOException {
        this.onGround = in.readBoolean();
    }

    @Override
    public void writeData(ByteBuf out) throws IOException {
        out.writeBoolean(this.onGround);
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
