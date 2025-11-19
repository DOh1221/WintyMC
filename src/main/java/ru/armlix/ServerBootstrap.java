package ru.armlix;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Getter;
import lombok.Setter;
import ru.doh1221.wintymc.server.network.netty.tcp.PipelineUtils;

import java.net.InetSocketAddress;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerBootstrap {

    private EventLoopGroup eventLoopGroup;
    private final Set<Channel> boundChannels = ConcurrentHashMap.newKeySet();
    public static ScheduledExecutorService scheduler;
    @Getter
    @Setter
    private int port = 25565;
    @Getter
    @Setter
    private String host = "0.0.0.0";
    @Getter
    @Setter
    private int timeoutMillis = 10000;
    public static Logger logger = Logger.getAnonymousLogger();

    public void start() {
        scheduler = Executors.newScheduledThreadPool(1);
        ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setNameFormat("Netty IO Thread #%d")
                .build();
        eventLoopGroup = new NioEventLoopGroup(0, threadFactory);

        io.netty.bootstrap.ServerBootstrap bootstrap = new io.netty.bootstrap.ServerBootstrap()
                .group(eventLoopGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(PipelineUtils.BASE)
                .localAddress(new InetSocketAddress(host, port));

        bootstrap.bind().addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                Channel channel = future.channel();
                boundChannels.add(channel);
                logger.info("Server is now listening on " + host + ":" + port);
            } else {
                logger.log(Level.SEVERE, "Failed to bind to " + host + ":" + port, future.cause());
                shutdownEventLoop();
            }
        });

    }
    public void stop() {
        boundChannels.forEach(channel -> {
            if (channel.isOpen()) {
                channel.close().addListener(future -> {
                    if (future.isSuccess()) {
                        logger.info("Closed channel: " + channel.localAddress());
                    } else {
                        logger.warning("Failed to close channel: " + channel.localAddress());
                    }
                });
            }
        });
        shutdownEventLoop();
        scheduler.shutdownNow();
    }

        private void shutdownEventLoop() {
            if (eventLoopGroup != null && !eventLoopGroup.isShuttingDown()) {
                eventLoopGroup.shutdownGracefully().addListener(future -> {
                    if (future.isSuccess()) {
                        logger.info("Event loop group shut down gracefully.");
                    } else {
                        logger.warning("Error shutting down event loop group.");
                    }
                });
            }
        }

}
