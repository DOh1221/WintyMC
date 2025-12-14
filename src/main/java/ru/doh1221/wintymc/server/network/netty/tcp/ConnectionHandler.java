package ru.doh1221.wintymc.server.network.netty.tcp;

import ru.doh1221.wintymc.server.network.netty.PipelineUtils;

public abstract class ConnectionHandler extends PacketHandler {
    public ChannelWrapper channel;

    public final void setHandler(ConnectionHandler handler) {
        ((HandlerBoss) channel.getHandle().pipeline().get(PipelineUtils.BOSS_HANDLER)).setHandler(handler);
    }

    @Override
    public abstract String toString();

    public void exception(Throwable t) throws Exception {
    }

    public void connected(ChannelWrapper channel) throws Exception {
    }

    public void disconnected(ChannelWrapper channel) throws Exception {
    }
}
