package ru.doh1221.wintymc.server;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Getter;
import ru.doh1221.wintymc.server.configuration.LanguageConfig;
import ru.doh1221.wintymc.server.configuration.LanguageMapping;
import ru.doh1221.wintymc.server.configuration.LoggingConfig;
import ru.doh1221.wintymc.server.configuration.PropertiesConfig;
import ru.doh1221.wintymc.server.game.world.ThreadWorldTime;
import ru.doh1221.wintymc.server.game.world.World;
import ru.doh1221.wintymc.server.game.world.implement.StoneGen;
import ru.doh1221.wintymc.server.network.netty.PipelineUtils;
import ru.doh1221.wintymc.server.utils.location.View3D;

import java.net.InetSocketAddress;
import java.util.Random;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WintyMC {

    private static WintyMC minecraftServer;
    @Getter
    public Logger logger = Logger.getLogger("Minecraft");
    //SOFTWARE
    public String minecraftVersionName = "Beta 1.7.3";
    public String softwareName = "WintyMC";

    //Settings
    public String version = "1.0.1-SNAPSHOT";
    public boolean enableSTATUS = false;
    public boolean enableRCON = false;
    public ThreadWorldTime timeTicker;
    public LanguageMapping langMap;
    public World world;
    public int queryPort = 25565;
    public int serverPort = 25565;
    public boolean showOfflineMessage = true;
    PropertiesConfig config = null;
    private EventLoopGroup eventLoopGroup;
    @Getter
    private boolean starting = false; // TODO сделаю потом так чтобы при старте сервера, если игрок заходит, его кикало если он ещё полностью не запущен

    public static WintyMC getInstance() {
        return minecraftServer;
    }

    public static void main(String[] args) {
        minecraftServer = new WintyMC();
        minecraftServer.startServer();
    }

    public void startServer() {
        starting = true;
        LoggingConfig.install();
        long startTime = System.nanoTime();
        logger.info("Starting Minecraft " + minecraftVersionName + " server (" + softwareName + " ver. " + version + ")");
        logger.info(
                """
                        WintyMC is open-source project\
                        Help us to make it better - https://github.com/DOh1221/WintyMC\
                        """);
        if (showOfflineMessage) {
            logger.info(
                    """
                            
                             ##########################################\
                            
                             # You are running this server in OFFLINE mode\
                            
                             # That means, that every player can join without any verification\
                            
                             # And it can log into any account on this server\
                            
                             # If you don't want to see this message, install \
                             authorization plugin or turn on online mode!\
                            
                             ##########################################\
                            
                            """
            );
        }

        ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setNameFormat("NETTY IO THREAD #%d")
                .build();
        eventLoopGroup = new NioEventLoopGroup(0, threadFactory);

        config = new PropertiesConfig(".", "server.properties", "WintyMC Configuration File");
        langMap = new LanguageConfig(".", "language.properties", "WintyMC Language File");

        config.addDefault("enable-query", "true");
        config.addDefault("enable-status", "true");
        config.addDefault("enable-rcon", "true");

        config.addDefault("level-name", "world");

        config.addDefault("region-file-compression", "deflate");
        config.addDefault("query.port", "25565");
        config.addDefault("query-ip", "0.0.0.0");
        config.addDefault("server-ip", "0.0.0.0");
        config.addDefault("server.port", "25565");

        config.saveProperties();
        config.loadProperties();

        this.enableSTATUS = config.getBoolean("enable-status");
        this.enableRCON = config.getBoolean("enable-rcon");

        this.queryPort = config.getInt("query.port");
        this.serverPort = config.getInt("server.port");

        if (config.getBoolean("enable-query")) {
            new Bootstrap()
                    .channel(NioDatagramChannel.class)
                    .group(eventLoopGroup)
                    .handler(PipelineUtils.QUERY)
                    .localAddress(new InetSocketAddress(config.getString("query-ip"), config.getInt("query.port")))
                    .bind().addListener(future -> {
                        if (future.isSuccess()) {
                            getLogger().info("Query started on " + config.getString("query-ip") + ":" + config.getInt("query.port"));
                        } else {
                            getLogger().severe("Failed to bind query port " + config.getString("query-ip") + ":" + config.getInt("query.port"));
                        }
                    });
        }

        ServerBootstrap bootstrap = new io.netty.bootstrap.ServerBootstrap()
                .group(eventLoopGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(PipelineUtils.BASE)
                .localAddress(new InetSocketAddress(config.getString("server-ip"), config.getInt("server.port")));

        bootstrap.bind().addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                logger.info("Server started on " + config.getString("server-ip") + ":" + config.getInt("server.port"));
            } else {
                logger.log(Level.SEVERE, "Failed to bind to " + config.getString("server-ip") + ":" + config.getInt("server.port"), future.cause());
            }
        });

        logger.info("Starting ticking threads...");

        timeTicker = new ThreadWorldTime();

        timeTicker.start();

        logger.info("Initializing worlds...");

        // TODO Загрузка миров

        world = new World(new View3D(0, 0, 0, 0.0F, 0.0F), new StoneGen(), new Random().nextInt());
        world.initialize();
        world.startTicking();

        long endTime = System.nanoTime() - startTime;
        String s2 = String.format("%.3fs", (double) endTime / 1000000000);
        getLogger().info("Done (" + s2 + ")! For help, type \"help\" or \"?\"");
    }

}
