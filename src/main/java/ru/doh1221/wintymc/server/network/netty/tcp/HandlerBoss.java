package ru.doh1221.wintymc.server.network.netty.tcp;


import com.google.common.base.Preconditions;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.ReadTimeoutException;
import ru.doh1221.wintymc.server.network.ChannelWrapper;
import ru.doh1221.wintymc.server.network.ConnectionHandler;
import ru.doh1221.wintymc.server.network.handler.InitialHandler;
import ru.doh1221.wintymc.server.packet.Packet;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is a primitive wrapper for {@link ConnectionHandler} instances tied to
 * channels to maintain simple states, and only call the required, adapted
 * methods when the channel is connected.
 */
public class HandlerBoss extends ChannelInboundHandlerAdapter {

    private ChannelWrapper channel;

    private ConnectionHandler handler = new InitialHandler();

    public void setHandler(ConnectionHandler handler) {
        Preconditions.checkArgument(handler != null, "handler");
        this.handler = handler;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (handler != null) {
            channel = new ChannelWrapper(ctx);
            handler.connected(channel);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (handler != null) {
            handler.disconnected(channel);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (handler != null) {
            Packet packet = (Packet) msg;
            System.out.println(packet.getPacketID());
            packet.handle(handler);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (ctx.channel().isActive()) {
            if (cause instanceof ReadTimeoutException) {
                Logger.getAnonymousLogger().info( handler + " - read timed out" );
                return;
            } else if (cause instanceof IOException) {
                Logger.getAnonymousLogger().info(  handler + " - IOException: " + cause.getMessage() );
            } else {
                Logger.getAnonymousLogger().info(  handler + " - encountered exception: " + cause.getMessage()); //cause
            }

            if (handler != null) {
                try {
                    handler.exception(cause);
                } catch (Exception ex) {
                    Logger.getAnonymousLogger().info(  handler + " - exception processing exception" );
                }
            }

            ctx.close();
        }
    }
}
