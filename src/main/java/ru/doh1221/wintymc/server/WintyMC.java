package ru.doh1221.wintymc.server;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Getter;
import ru.doh1221.wintymc.server.network.netty.PipelineUtils;
import ru.doh1221.wintymc.server.network.netty.udp.QueryHandler;
import ru.doh1221.wintymc.server.utils.PropertiesConfig;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WintyMC {

    @Getter
    public Logger logger = Logger.getLogger("Minecraft");

    //SOFTWARE
    public String minecraftVersionName = "Beta 1.7.3";
    public String softwareName = "WintyMC";
    public String version = "1.0.1-SNAPSHOT";
    public String description =
            "WintyMC is open-source project\n" +
            "Help us to make it better - https://github.com/DOh1221/WintyMC";

    //Settings

    PropertiesConfig config = null;

    private final Set<Channel> boundChannels = ConcurrentHashMap.newKeySet();
    private EventLoopGroup eventLoopGroup;
    public boolean enableSTATUS = false;
    public boolean enableRCON = false;

    public int queryPort = 25565;
    public int serverPort = 25565;

    public boolean showOfflineMessage = true;
    public boolean stillStarting = false;
    public String defaultMotd = "A Minecraft Beta 1.7.3 server!";
    public long maxPlayers = 20L;
    public HashMap onlinePlayers = new HashMap();

    private static WintyMC minecraftServer;

    public static WintyMC getInstance() {
        return minecraftServer;
    }

    public void startServer() throws IOException {
        stillStarting = true;
        LoggingConfig.install();
        logger.info("Starting Minecraft " + minecraftVersionName + " server (" + softwareName + " ver. " + version + ")");
        logger.info(description);
        if(showOfflineMessage) {
            logger.info("\n##########################################" +
                    "\n# You are running this server in OFFLINE mode" +
                    "\n# That means, that every player can join without any verification" +
                    "\n# And it can log into any account on this server" +
                    "\n# If you don't want to see this message, install " +
                    "authorization plugin or turn on online mode!" +
                    "\n##########################################" +
                    "\n"
            );
        }

        ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setNameFormat("NETTY IO THREAD #%d")
                .build();
        eventLoopGroup = new NioEventLoopGroup(0, threadFactory);

        config = new PropertiesConfig(".", "server.properties", "WintyMC Configuration File");

        config.addDefault("enable-jmx-monitoring", "true");
        config.addDefault("enable-query", "true");
        config.addDefault("enable-status", "true");
        config.addDefault("enable-rcon", "true");

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

        if(config.getBoolean("enable-query")) {
            new Bootstrap()
                    .channel( NioDatagramChannel.class )
                    .group( eventLoopGroup )
                    .handler( new QueryHandler( getInstance() ) )
                    .localAddress( new InetSocketAddress(config.getString("query-ip"), config.getInt("query.port") ))
                    .bind().addListener( future -> {
                        if (future.isSuccess()) {
                            getLogger().info("Query started on " + config.getString("query-ip") + ":" + config.getInt("query.port"));
                        } else {
                            getLogger().severe("Failed to bind query port " + config.getString("query-ip") + ":" + config.getInt("query.port"));
                        } });
        }

        ServerBootstrap bootstrap = new io.netty.bootstrap.ServerBootstrap()
                .group(eventLoopGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(PipelineUtils.BASE)
                .localAddress(new InetSocketAddress(config.getString("server-ip"), config.getInt("server.port")));

        bootstrap.bind().addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                Channel channel = future.channel();
                boundChannels.add(channel);
                logger.info("Server started on " + config.getString("server-ip") + ":" + config.getInt("server.port"));
            } else {
                logger.log(Level.SEVERE, "Failed to bind to " + config.getString("server-ip") + ":" + config.getInt("server.port"), future.cause());
            }
        });

        logger.info("Initializing worlds...");



    }

    public static void main(String[] args) throws IOException {
        minecraftServer = new WintyMC();
        minecraftServer.startServer();
    }

}
