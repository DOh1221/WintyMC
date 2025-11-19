package ru.doh1221.wintymc.server.packet;

import io.netty.buffer.ByteBuf;
import ru.doh1221.wintymc.server.network.netty.tcp.PacketHandler;
import ru.doh1221.wintymc.server.packet.auth.Packet1Login;
import ru.doh1221.wintymc.server.packet.auth.Packet2Handshake;
import ru.doh1221.wintymc.server.packet.game.chat.Packet3Chat;
import ru.doh1221.wintymc.server.packet.game.entity.Packet5EntityEquipment;
import ru.doh1221.wintymc.server.packet.game.entity.Packet7ClickEntity;
import ru.doh1221.wintymc.server.packet.game.player.data.Packet8SetHealth;
import ru.doh1221.wintymc.server.packet.game.player.world.*;
import ru.doh1221.wintymc.server.packet.game.player.world.chunk.Packet50PreChunk;
import ru.doh1221.wintymc.server.packet.game.world.Packet4WorldTime;
import ru.doh1221.wintymc.server.packet.game.world.Packet51MapChunk;
import ru.doh1221.wintymc.server.packet.general.Packet0KeepAlive;
import ru.doh1221.wintymc.server.packet.general.Packet255DisconnectKick;
import ru.doh1221.wintymc.server.packet.general.PacketFAChannelMessage;
import ru.doh1221.wintymc.server.packet.status.Packet254GetInfo;

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

    static {
        // AUTH
        register(0x00, true, true, Packet0KeepAlive.class);
        register(0x01, true, true, Packet1Login.class);
        register(0x02, true, true, Packet2Handshake.class);

        // INGAME
        register(0x03, true, true, Packet3Chat.class);
        register(0x04, true, false, Packet4WorldTime.class);
        register(0x05, true, true, Packet5EntityEquipment.class);
        register(0x06, true, false, Packet6SpawnPosition.class);
        register(0x07, false, true, Packet7ClickEntity.class);
        register(0x08, true, false, Packet8SetHealth.class);
        register(0x09, true, true, Packet9Respawn.class);
        register(0x0A, true, true, Packet10OnGround.class);
        register(0x0B, true, true, Packet11PlayerPosition.class);
        register(0x0C, true, true, Packet12PlayerLook.class);
        register(0x0D, true, true, Packet13PlayerPositionLook.class);
        register(0x0E, false, true, Packet14BlockDestroy.class);
        register(0x0F, false, true, Packet15BlockPlace.class);
        register(0x10, false, true, Packet16HandItemChange.class);

        register(0x32, true, false, Packet50PreChunk.class);
        register(0x33, true, false, Packet51MapChunk.class);

        register(0xFA, true, true, PacketFAChannelMessage.class);
        register(0xFE, false, true, Packet254GetInfo.class);
        register(0xFF, true, true, Packet255DisconnectKick.class);
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