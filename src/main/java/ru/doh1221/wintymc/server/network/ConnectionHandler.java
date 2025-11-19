package ru.doh1221.wintymc.server.network;

import ru.doh1221.wintymc.server.network.netty.tcp.PacketHandler;

public abstract class ConnectionHandler extends PacketHandler {
    public ChannelWrapper channel;

    @Override
    public abstract String toString();

    public void exception(Throwable t) throws Exception {
    }

    public void connected(ChannelWrapper channel) throws Exception {
    }

    public void disconnected(ChannelWrapper channel) throws Exception {
    }
}
