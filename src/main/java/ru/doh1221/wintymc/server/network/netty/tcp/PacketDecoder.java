package ru.doh1221.wintymc.server.network.netty.tcp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import ru.doh1221.wintymc.server.packet.Packet;

import java.util.List;

public class PacketDecoder extends ReplayingDecoder<Void> {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        Packet packet = Packet.read(in, true);
        if (packet != null) {
            out.add(packet);
        }
    }
}
