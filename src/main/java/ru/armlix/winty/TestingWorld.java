package ru.armlix.winty;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import ru.armlix.winty.game.chunking.chunk.IChunkPopulator;
import ru.armlix.winty.game.chunking.chunk.IChunkProvider;
import ru.armlix.winty.game.chunking.chunk.Chunk;
import ru.armlix.winty.game.chunking.chunk.FlatMapGenerator;
import ru.armlix.winty.game.chunking.chunk.FullWriterAsyncProvider;
import ru.armlix.winty.game.chunking.chunk.FullWriterSyncProvider;
import ru.armlix.winty.game.world.World;
import ru.armlix.winty.game.world.WorldInfo;
import ru.armlix.winty.network.netty.PipelineUtils;

import java.net.InetSocketAddress;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class TestingWorld {

    public static World world;

    public static void main(String[] args) {

        WorldInfo info = new WorldInfo("world", UUID.randomUUID(), (short) 10, (short) 12, 12000L, 1859812L, true, 12, new FlatMapGenerator(), new IChunkPopulator() {
            @Override
            public void populateChunk(WorldInfo info, Chunk chunk) {

            }
        });

        world = new World(info, new FullWriterAsyncProvider(Executors.newFixedThreadPool(1)));

        world.init();

        ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setNameFormat("NETTY IO THREAD #%d")
                .build();
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup(0, threadFactory);

        ServerBootstrap bootstrap = new io.netty.bootstrap.ServerBootstrap()
                .group(eventLoopGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(PipelineUtils.BASE)
                .localAddress("localhost", 25565);

        bootstrap.bind().addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {

            } else {
                System.out.println("Failed binding to bind address: " + future.cause().getMessage());
            }
        });


        }

}
