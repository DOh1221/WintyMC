package ru.doh1221.wintymc.server.network.netty.tcp.packet.game.player;

import io.netty.buffer.ByteBuf;
import ru.doh1221.wintymc.server.network.netty.tcp.PacketHandler;
import ru.doh1221.wintymc.server.network.netty.tcp.packet.Packet;
import ru.doh1221.wintymc.server.utils.Stream;

import java.io.IOException;

public class Packet20SpawnPlayerEntity extends Packet {

    public int entityID;
    public String username;
    public byte x;
    public byte y;
    public byte z;
    public byte yaw;
    public byte pitch;
    public short heldItem;

    @Override
    public void readData(ByteBuf in) throws IOException {
        this.entityID = in.readInt();
        this.username = Stream.readString(in, false);
        this.x = in.readByte();
        this.y = in.readByte();
        this.z = in.readByte();
        this.yaw = in.readByte();
        this.pitch = in.readByte();
        this.heldItem = in.readShort();
    }

    @Override
    public void writeData(ByteBuf out) throws IOException {
        out.writeInt(this.entityID);
        Stream.writeString(this.username, out, false);
        out.writeByte(this.x);
        out.writeByte(this.y);
        out.writeByte(this.z);
        out.writeByte(this.yaw);
        out.writeByte(this.pitch);
        out.writeShort(this.heldItem);
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
