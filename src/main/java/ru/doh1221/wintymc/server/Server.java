package ru.doh1221.wintymc.server;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import ru.doh1221.wintymc.server.network.netty.tcp.PipelineUtils;

import java.net.InetSocketAddress;
import java.util.concurrent.ThreadFactory;


public class Server {

    public static void main(String[] args) {
        ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setNameFormat("Netty IO Thread #%d")
                .build();
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup(0, threadFactory);

        io.netty.bootstrap.ServerBootstrap bootstrap = new io.netty.bootstrap.ServerBootstrap()
                .group(eventLoopGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(PipelineUtils.BASE)
                .localAddress(new InetSocketAddress("0.0.0.0", 25565));

        bootstrap.bind().addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                Channel channel = future.channel();
                System.out.println("Started");
            } else {

            }
        });
    }

}
