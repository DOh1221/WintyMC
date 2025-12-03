package ru.doh1221.wintymc.server.network.netty.tcp.packet;

import io.netty.buffer.ByteBuf;
import ru.doh1221.wintymc.server.network.netty.tcp.PacketHandler;
import ru.doh1221.wintymc.server.network.netty.tcp.packet.auth.Packet1Login;
import ru.doh1221.wintymc.server.network.netty.tcp.packet.auth.Packet2Handshake;
import ru.doh1221.wintymc.server.network.netty.tcp.packet.game.chat.Packet3Chat;
import ru.doh1221.wintymc.server.network.netty.tcp.packet.game.entity.Packet5EntityEquipment;
import ru.doh1221.wintymc.server.network.netty.tcp.packet.game.entity.Packet7ClickEntity;
import ru.doh1221.wintymc.server.network.netty.tcp.packet.game.player.*;
import ru.doh1221.wintymc.server.network.netty.tcp.packet.game.player.world.*;
import ru.doh1221.wintymc.server.network.netty.tcp.packet.game.world.*;
import ru.doh1221.wintymc.server.network.netty.tcp.packet.game.player.data.Packet8SetHealth;
import ru.doh1221.wintymc.server.network.netty.tcp.packet.general.Packet0KeepAlive;
import ru.doh1221.wintymc.server.network.netty.tcp.packet.general.Packet255DisconnectKick;
import ru.doh1221.wintymc.server.network.netty.tcp.packet.general.PacketFAChannelMessage;
import ru.doh1221.wintymc.server.network.netty.tcp.packet.status.Packet254GetInfo;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class Packet {
    private static final Map<Class<? extends Packet>, Integer> classToId = new HashMap<>();
    private static final Map<Integer, Class<? extends Packet>> idToClass = new HashMap<>();
    private static final Set<Integer> serverPackets = new HashSet<>();
    private static final Set<Integer> clientPackets = new HashSet<>();
    public static final int PVN = 14;

    static {
        // AUTH
        register(0x00, true, true, Packet0KeepAlive.class);
        register(0x01, true, true, Packet1Login.class);
        register(0x02, true, true, Packet2Handshake.class);

        // INGAME
        register(0x03, true, true, Packet3Chat.class);                      // 3
        register(0x04, true, false, Packet4WorldTime.class);                // 4
        register(0x05, true, true, Packet5EntityEquipment.class);           // 5
        register(0x06, true, false, Packet6SpawnPosition.class);            // 6
        register(0x07, false, true, Packet7ClickEntity.class);              // 7
        register(0x08, true, false, Packet8SetHealth.class);                // 8
        register(0x09, true, true, Packet9Respawn.class);                   // 9
        register(0x0A, true, true, Packet10OnGround.class);                 // 10
        register(0x0B, true, true, Packet11PlayerPosition.class);           // 11
        register(0x0C, true, true, Packet12PlayerLook.class);               // 12
        register(0x0D, true, true, Packet13PlayerPositionLook.class);       // 13
        register(0x0E, false, true, Packet14BlockDestroy.class);            // 14
        register(0x0F, false, true, Packet15BlockPlace.class);              // 15
        register(0x10, false, true, Packet16HandItemChange.class);          // 16
        register(0x11, false, true, Packet17UseBed.class);                  // 17
        register(0x12, true, true, Packet18PlayerAnimation.class);          // 18
        register(0x13, true, true, Packet19EntityAction.class);             // 19
        register(0x14, true, true, Packet20SpawnPlayerEntity.class);        // 20
        register(0x15, true, true, Packet21SpawnItemEntity.class);          // 21
        register(0x16, true, true, Packet22CollectItem.class);              // 22
        register(0x17, true, true, Packet23SpawnObjectEntity.class);        // 23
        register(0x18, true, true, Packet24SpawnMobEntity.class);           // 24
        register(0x1E, true, true, Packet30Entity.class);                   // 30
        register(0x1F, true, true, Packet31EntityPositionAndLook.class);    // 31
        register(0x2E, true, true, Packet46EntityMetadata.class);           // 46
        register(0x32, true, false, Packet50PreChunk.class);                // 50
        register(0x33, true, false, Packet51MapChunk.class);                // 51
        register(0x3D, true, true, Packet61Effect.class);                   // 61
        register(0x46, true, true, Packet70GameState.class);                // 70
        register(0x64, true, true, Packet100OpenWindow.class);              // 100
        register(0x65, true, true, Packet101CloseWindow.class);             // 101
        register(0x66, true, true, Packet102ClickWindow.class);             // 102
        register(0x67, true, true, Packet103SetWindowSlot.class);           // 103
        register(0x68, true, true, Packet104WindowItem.class);              // 104

        register(0xFA, true, true, PacketFAChannelMessage.class);           // 250
        register(0xFE, false, true, Packet254GetInfo.class);                // 254
        register(0xFF, true, true, Packet255DisconnectKick.class);          // 255
    }

    private final int packetID;

    public Packet() {
        Integer id = classToId.get(this.getClass());
        if (id == null) {
            throw new IllegalStateException("Packet class not registered: " + this.getClass());
        }
        this.packetID = id;
    }

    public static void register(int id, boolean isServerPacket, boolean isClientPacket, Class<? extends Packet> clazz) {
        if (idToClass.containsKey(id)) throw new IllegalArgumentException("Duplicate packet ID: " + id);
        if (classToId.containsKey(clazz)) throw new IllegalArgumentException("Duplicate packet class: " + clazz);

        idToClass.put(id, clazz);
        classToId.put(clazz, id);
        if (isServerPacket) serverPackets.add(id);
        if (isClientPacket) clientPackets.add(id);
    }

    public static Packet read(ByteBuf in, boolean isClientSide) {
        try {
            int id = in.readUnsignedByte();
            if ((isClientSide && !clientPackets.contains(id)) || (!isClientSide && !serverPackets.contains(id))) {
                throw new IOException("Invalid packet ID for side: " + id);
            }

            Class<? extends Packet> clazz = idToClass.get(id);
            if (clazz == null) {
                throw new IOException("Unregistered packet ID: " + id);
            }

            Packet packet = clazz.getDeclaredConstructor().newInstance();
            packet.readData(in);
            return packet;
        } catch (Exception e) {
            // Don't leak exception details to peer; log and return null
            try {
                //TODO Вернуть логгинг
            } catch (Throwable ignored) {
            }
            return null;
        }
    }

    public static void write(Packet packet, ByteBuf out) throws IOException {
        out.writeByte(packet.getPacketID());
        packet.writeData(out);
    }

    public final int getPacketID() {
        return this.packetID;
    }

    public abstract void readData(ByteBuf in) throws IOException;

    public abstract void writeData(ByteBuf out) throws IOException;

    public abstract void handle(PacketHandler handler);

    public abstract int size();

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

}