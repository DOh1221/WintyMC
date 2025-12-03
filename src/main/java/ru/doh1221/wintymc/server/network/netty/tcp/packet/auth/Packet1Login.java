package ru.doh1221.wintymc.server.network.netty.tcp.packet.auth;

import io.netty.buffer.ByteBuf;
import ru.doh1221.wintymc.server.network.netty.tcp.PacketHandler;
import ru.doh1221.wintymc.server.network.netty.tcp.packet.Packet;
import ru.doh1221.wintymc.server.utils.Stream;

import java.io.IOException;

public class Packet1Login extends Packet {

    public int protocolVersion = 14;
    public String username = "";
    public long mapSeed = 0;
    public byte dimensionID;

    public Packet1Login() {

    }

    public Packet1Login(int protocolVersion, String username, long mapSeed, byte dimensionID) {
        this.protocolVersion = protocolVersion;
        this.username = username;
        this.mapSeed = mapSeed;
        this.dimensionID = dimensionID;
    }

    @Override
    public void readData(ByteBuf in) throws IOException {
        this.protocolVersion = in.readInt();
        this.username = Stream.readString(in, false);
        this.mapSeed = in.readLong();
        this.dimensionID = in.readByte();
    }

    @Override
    public void writeData(ByteBuf out) throws IOException {
        out.writeInt(this.protocolVersion);
        Stream.writeString(this.username, out, false);
        out.writeLong(this.mapSeed);
        out.writeByte(this.dimensionID);
    }

    @Override
    public void handle(PacketHandler handler) {
        handler.handle(this);
    }

    @Override
    public int size() {
        return 15 + username.getBytes().length;
    }
}
