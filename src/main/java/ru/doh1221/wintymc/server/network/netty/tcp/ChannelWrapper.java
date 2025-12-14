package ru.doh1221.wintymc.server.network.netty.tcp;

import com.google.common.base.Preconditions;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;

public class ChannelWrapper {

    public Channel ch;
    @Getter
    private volatile boolean closed;
    public String disconnectReason = "Connection closed";

    public ChannelWrapper(ChannelHandlerContext ctx) {
        this.ch = ctx.channel();
    }

    public synchronized void write(Object packet) {
        if (!closed) {
            {
                ch.write(packet);
            }
            ch.flush();
        }
    }

    public synchronized void close() {
        if (!closed) {
            closed = true;
            ch.flush();
            ch.close();
        }
    }

    public void addBefore(String baseName, String name, ChannelHandler handler) {
        Preconditions.checkState(ch.eventLoop().inEventLoop(), "cannot add handler outside of event loop");
        ch.pipeline().flush();
        ch.pipeline().addBefore(baseName, name, handler);
    }

    public Channel getHandle() {
        return ch;
    }
}
