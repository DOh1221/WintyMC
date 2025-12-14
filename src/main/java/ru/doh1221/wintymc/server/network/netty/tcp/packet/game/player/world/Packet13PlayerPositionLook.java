package ru.doh1221.wintymc.server.network.netty.tcp.packet.game.player.world;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import ru.doh1221.wintymc.server.network.netty.tcp.PacketHandler;
import ru.doh1221.wintymc.server.utils.location.View3D;

@AllArgsConstructor
public class Packet13PlayerPositionLook extends Packet10OnGround {

    public double x;
    public double y;
    public double stance;
    public double z;
    public float yaw;
    public float pitch;

    public Packet13PlayerPositionLook() {

    }

    public Packet13PlayerPositionLook(View3D view) {
        this.x = view.getX();
        this.y = view.getY();
        this.z = view.getZ();
        this.yaw = view.getYaw();
        this.pitch = view.getPitch();
        this.stance = this.y + 1.62;
    }

    @SneakyThrows
    @Override
    public void readData(ByteBuf in) {
        this.x = in.readDouble();
        this.y = in.readDouble();
        this.stance = in.readDouble();
        this.z = in.readDouble();
        this.yaw = in.readFloat();
        this.pitch = in.readFloat();
        super.readData(in);
    }

    @Override
    public void writeData(ByteBuf out) {
        out.writeDouble(this.x);
        out.writeDouble(this.y);
        out.writeDouble(this.stance);
        out.writeDouble(this.z);
        out.writeFloat(this.yaw);
        out.writeFloat(this.pitch);
        out.writeBoolean(this.onGround);
    }

    @Override
    public void handle(PacketHandler handler) {
        handler.handle(this);
    }

    @Override
    public int size() {
        return super.size() + 40;
    }

}
