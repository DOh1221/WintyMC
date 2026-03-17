package ru.armlix.winty.network.netty.tcp.packet.game.player;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import ru.armlix.winty.network.netty.tcp.PacketHandler;
import ru.armlix.winty.network.netty.tcp.packet.Packet;

import java.io.IOException;

@AllArgsConstructor
public class Packet70GameState extends Packet {

    public byte gameState = 0;

    @Override
    public void readData(ByteBuf in) throws IOException {
        this.gameState = in.readByte();
    }

    @Override
    public void writeData(ByteBuf out) throws IOException {
        out.writeByte(this.gameState);
    }

    @Override
    public void handle(PacketHandler handler) {
        handler.handle(this);
    }

    @Override
    public int size() {
        return 0x64;
    }
}
