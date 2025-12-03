package ru.doh1221.wintymc.server.network.netty.tcp;

import ru.doh1221.wintymc.server.network.netty.tcp.packet.Packet;
import ru.doh1221.wintymc.server.network.netty.tcp.packet.game.player.world.*;
import ru.doh1221.wintymc.server.network.netty.tcp.packet.auth.Packet1Login;
import ru.doh1221.wintymc.server.network.netty.tcp.packet.auth.Packet2Handshake;
import ru.doh1221.wintymc.server.network.netty.tcp.packet.game.chat.Packet3Chat;
import ru.doh1221.wintymc.server.network.netty.tcp.packet.game.entity.Packet7ClickEntity;
import ru.doh1221.wintymc.server.network.netty.tcp.packet.game.world.Packet14BlockDestroy;
import ru.doh1221.wintymc.server.network.netty.tcp.packet.general.Packet0KeepAlive;
import ru.doh1221.wintymc.server.network.netty.tcp.packet.general.Packet255DisconnectKick;
import ru.doh1221.wintymc.server.network.netty.tcp.packet.status.Packet254GetInfo;

public class PacketHandler {

    public void handle(Packet packet) {

    }

    public void handle(Packet254GetInfo packet254GetInfo) {

    }

    public void handle(Packet0KeepAlive packet0KeepAlive) {

    }

    public void handle(Packet1Login packet1Login) {

    }

    public void handle(Packet2Handshake packet2Handshake) {

    }

    public void handle(Packet3Chat packet3Chat) {

    }

    public void handle(Packet7ClickEntity packet7ClickEntity) {

    }

    public void handle(Packet9Respawn packet9Respawn) {

    }

    public void handle(Packet10OnGround packet10OnGround) {

    }

    public void handle(Packet11PlayerPosition packet11PlayerPosition) {

    }

    public void handle(Packet12PlayerLook packet12PlayerLook) {

    }

    public void handle(Packet13PlayerPositionLook packet13PlayerPositionLook) {

    }

    public void handle(Packet14BlockDestroy packet14BlockDestroy) {

    }

    public void handle(Packet16HandItemChange packet16HandItemChange) {

    }

    public void handle(Packet255DisconnectKick disconnect) {

    }

}