package ru.armlix.winty.network.netty.tcp.packet.game.player;

import io.netty.buffer.ByteBuf;
import ru.armlix.winty.network.netty.tcp.PacketHandler;
import ru.armlix.winty.network.netty.tcp.packet.Packet;

import java.io.IOException;

public class Packet24SpawnMobEntity extends Packet {

    public int entityID;
    public byte mobType;
    public int x;
    public int y;
    public int z;
    public byte yaw;
    public byte pitch;

    @Override
    public void readData(ByteBuf in) throws IOException {
        this.entityID = in.readInt();
        this.mobType = in.readByte();
        this.x = in.readInt();
        this.y = in.readInt();
        this.z = in.readInt();
        this.yaw = in.readByte();
        this.pitch = in.readByte();
    }

    @Override
    public void writeData(ByteBuf out) throws IOException {
        out.writeInt(this.entityID);
        out.writeByte(this.mobType);
        out.writeInt(this.x);
        out.writeInt(this.y);
        out.writeInt(this.z);
        out.writeByte(this.yaw);
        out.writeByte(this.pitch);
    }


    @Override
    public void handle(PacketHandler handler) {
        handler.handle(this);
    }

    @Override
    public int size() {
        return 0x1B;
    }
}
