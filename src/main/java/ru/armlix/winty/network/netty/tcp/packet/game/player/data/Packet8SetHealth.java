package ru.armlix.winty.network.netty.tcp.packet.game.player.data;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import ru.armlix.winty.network.netty.tcp.packet.Packet;
import ru.armlix.winty.network.netty.tcp.PacketHandler;

import java.io.IOException;

@AllArgsConstructor
public class Packet8SetHealth extends Packet {

    public short currentHealth = 0;

    @Override
    public void readData(ByteBuf in) throws IOException {
        this.currentHealth = in.readShort();
    }

    @Override
    public void writeData(ByteBuf out) throws IOException {
        out.writeShort(this.currentHealth);
    }

    @Override
    public void handle(PacketHandler handler) {
        handler.handle(this);
    }

    @Override
    public int size() {
        return 2;
    }
}
