package ru.doh1221.wintymc.server.network.netty.tcp;

import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import ru.doh1221.wintymc.server.network.handler.InitialHandler;

public class PipelineUtils {

    public static final Base BASE = new Base();
    public static String PACKET_DECODE_HANDLER = "packet-decoder";
    public static String PACKET_ENCODE_HANDLER = "packet-encoder";
    public static String BOSS_HANDLER = "inbound-boss";

    public final static class Base extends ChannelInitializer<Channel> {

        @Override
        public void initChannel(Channel ch) throws Exception {
            try {
                ch.config().setOption(ChannelOption.IP_TOS, 0x18);
                ch.config().setOption(ChannelOption.SO_KEEPALIVE, true);
                ch.config().setOption(ChannelOption.SO_BACKLOG, 128);
                ch.config().setOption(ChannelOption.TCP_NODELAY, true);
                ch.config().setOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
                ch.config().setOption(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);

            } catch (ChannelException ex) {
            }

            //ch.pipeline().addLast( TIMEOUT_HANDLER, new ReadTimeoutHandler(PluginBootstrap.getInstance().getTimeoutMillis(), TimeUnit.MILLISECONDS ) );
            ch.pipeline().addLast(PACKET_DECODE_HANDLER, new PacketDecoder());
            ch.pipeline().addLast(PACKET_ENCODE_HANDLER, new DefinedPacketEncoder());
            HandlerBoss boss = new HandlerBoss();
            boss.setHandler(new InitialHandler());
            ch.pipeline().addLast(BOSS_HANDLER, boss);
        }
    }

}