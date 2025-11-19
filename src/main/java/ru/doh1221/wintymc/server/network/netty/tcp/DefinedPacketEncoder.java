package ru.doh1221.wintymc.server.network.netty.tcp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import ru.doh1221.wintymc.server.packet.Packet;

@ChannelHandler.Sharable
public class DefinedPacketEncoder extends MessageToByteEncoder<Packet> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet msg, ByteBuf out) throws Exception {
        Packet.write(msg, out);
    }
}
