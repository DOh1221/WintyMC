package ru.doh1221.wintymc.server.network.netty.tcp.packet;

import io.netty.buffer.ByteBuf;
import ru.doh1221.wintymc.server.network.netty.tcp.PacketHandler;

import java.io.IOException;

public class Packet27PlayerMovement extends Packet {
    
    public float strafeDirection;
    public float forwardDirection;
    public float pitch;
    public float yaw;
    
    public boolean jumping;
    public boolean sneaking;    
    
    @Override
    public void readData(ByteBuf in) throws IOException {
        this.strafeDirection = in.readFloat();
        this.forwardDirection = in.readFloat();
        this.pitch = in.readFloat();
        this.yaw = in.readFloat();
        
        this.jumping = in.readBoolean();
        this.sneaking = in.readBoolean();
    }

    @Override
    public void writeData(ByteBuf out) throws IOException {
        out.writeFloat(this.strafeDirection);
        out.writeFloat(this.forwardDirection);
        out.writeFloat(this.pitch);
        out.writeFloat(this.yaw);
        
        out.writeBoolean(this.jumping);
        out.writeBoolean(this.sneaking);
    }

    @Override
    public void handle(PacketHandler handler) {
        handler.handle(this);
    }

    @Override
    public int size() {
        return 0;
    }
}
