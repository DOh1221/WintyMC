package ru.doh1221.wintymc.server.network.netty.tcp.handler;

import ru.doh1221.wintymc.server.WintyMC;
import ru.doh1221.wintymc.server.entity.Player;
import ru.doh1221.wintymc.server.network.netty.tcp.ConnectionHandler;
import ru.doh1221.wintymc.server.network.netty.tcp.packet.auth.Packet1Login;

public class InGameHandler extends ConnectionHandler {

    public Player player;

    public InGameHandler(Player player) {
        this.player = player;
    }

    @Override
    public void handle(Packet1Login packet1Login) {
        if (packet1Login.protocolVersion > 14) {
            player.kick(WintyMC.getInstance().langMap.getTranslation("kick.server.outofdate"));
            return;
        } else if (packet1Login.protocolVersion < 14) {
            player.kick(WintyMC.getInstance().langMap.getTranslation("kick.server.outofdate"));
            return;
        }

        WintyMC.getInstance().getLogger().info(player.displayname + " [" + player.connection.getHandle().remoteAddress() + "] logged in");

        // TODO Player Data Loading

        // TODO World Chunk loading

    }

    @Override
    public String toString() {
        return "InGameHandler";
    }
}
