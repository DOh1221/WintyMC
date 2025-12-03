package ru.doh1221.wintymc.server.network.netty.udp;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.AddressedEnvelope;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import ru.doh1221.wintymc.server.WintyMC;

/**
 * UDP Query handler for the server.
 *
 * Responsibilities are split to improve readability:
 * - validate incoming packet header
 * - dispatch by query type
 * - build responses (short and long)
 * - manage short-lived challenge sessions
 *
 * The handler is intentionally final and uses explicit dependencies injected via constructor
 * (WintyMC instance and a QueryListener) to make behavior and side-effects visible and testable.
 */
public final class QueryHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    // Magic bytes and types used by the query protocol
    private static final int MAGIC_FIRST = 0xFE;
    private static final int MAGIC_SECOND = 0xFD;
    private static final byte TYPE_CHALLENGE = 0x09;
    private static final byte TYPE_STAT = 0x00;

    private final WintyMC server;
    private final Logger logger;
    private final Random random;
    private final Cache<InetAddress, QuerySession> sessions;

    /**
     * Create a QueryHandler.
     *
     * @param server   main server singleton (injected rather than fetched statically to ease testing)
     */
    public QueryHandler(WintyMC server) {
        this.server = Objects.requireNonNull(server, "server");
        this.logger = server.getLogger() != null ? server.getLogger() : Logger.getLogger(QueryHandler.class.getName());
        this.random = new Random();
        this.sessions = CacheBuilder.newBuilder()
                .expireAfterWrite(30, TimeUnit.SECONDS)
                .build();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) {
        try {
            handlePacket(ctx, msg);
        } catch (Throwable t) {
            // Keep logging minimal but informative
            logger.log(Level.WARNING, "Error whilst handling query packet from " + msg.sender(), t);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.log(Level.WARNING, "Error whilst handling query packet from " + ctx.channel().remoteAddress(), cause);
    }

    // -------------------- Core packet handling --------------------

    private void handlePacket(ChannelHandlerContext ctx, DatagramPacket msg) {
        ByteBuf in = msg.content();

        // Validate magic bytes
        if (!hasValidMagic(in)) {
            logger.log(Level.WARNING, "Query - Incorrect magic!: {0}", msg.sender());
            return;
        }

        // Prepare response buffer and envelope early
        ByteBuf out = ctx.alloc().buffer();
        AddressedEnvelope<ByteBuf, ?> response = new DatagramPacket(out, msg.sender());

        // Read the packet type and session id (session id echoes back)
        byte type = in.readByte();
        int sessionId = in.readInt();

        switch (type) {
            case TYPE_CHALLENGE:
                handleChallenge(out, sessionId, msg.sender().getAddress());
                break;
            case TYPE_STAT:
                handleStat(in, out, sessionId, msg.sender().getAddress());
                break;
            default:
                logger.log(Level.WARNING, "Query - Unknown type {0} from {1}", new Object[]{type, msg.sender()});
                out.release();
                return;
        }

        ctx.writeAndFlush(response);
    }

    private boolean hasValidMagic(ByteBuf in) {
        if (in.readableBytes() < 2) {
            return false;
        }
        int first = in.readUnsignedByte();
        int second = in.readUnsignedByte();
        return first == MAGIC_FIRST && second == MAGIC_SECOND;
    }

    // -------------------- Handlers for query types --------------------

    private void handleChallenge(ByteBuf out, int sessionId, InetAddress senderAddress) {
        // Write response header: type and session id
        out.writeByte(TYPE_CHALLENGE);
        out.writeInt(sessionId);

        // Create challenge token and store session
        int challengeToken = random.nextInt();
        sessions.put(senderAddress, new QuerySession(challengeToken, System.currentTimeMillis()));

        // Protocol expects the token as ASCII decimal string with NUL terminator
        writeAsciiDecimalString(out, challengeToken);
    }

    private void handleStat(ByteBuf in, ByteBuf out, int sessionId, InetAddress senderAddress) {
        // Validate session token present in packet
        if (in.readableBytes() < 4) {
            throw new IllegalStateException("Stat request does not contain challenge token");
        }
        int challengeToken = in.readInt();

        QuerySession session = sessions.getIfPresent(senderAddress);
        if (session == null || session.getToken() != challengeToken) {
            throw new IllegalStateException("No valid session for " + senderAddress);
        }

        // Write response header: type and session id
        out.writeByte(TYPE_STAT);
        out.writeInt(sessionId);

        // There are two variants of stat response:
        // - short (no extra bytes requested)
        // - long  (4 bytes of padding requested)
        int remaining = in.readableBytes();
        if (remaining == 0) {
            writeShortResponse(out);
        } else if (remaining == 4) {
            writeLongResponse(out);
        } else {
            throw new IllegalStateException("Invalid data request packet (remaining=" + remaining + ")");
        }
    }

    // -------------------- Building responses --------------------

    private void writeShortResponse(ByteBuf out) {
        // Short response (legacy): sequence of NUL-terminated strings and some numeric fields
        writeString(out, "TEST");                        // MOTD
        writeString(out, "SMP");                                      // Game Type
        writeString(out, "BungeeCord_Proxy");                         // World Name (example value kept)
        writeAsciiDecimalString(out, 2);        // Online count as ASCII
        writeAsciiDecimalString(out, 20);       // Max players as ASCII
        writeShortLE(out, 25565);              // Port (little endian short)
        writeString(out, "TEST");         // IP
    }

    private void writeLongResponse(ByteBuf out) {
        // Long response starts with a fixed magic sequence then key/value pairs.
        out.writeBytes(new byte[]{0x73, 0x70, 0x6C, 0x69, 0x74, 0x6E, 0x75, 0x6D, 0x00, (byte) 0x80, 0x00});

        Map<String, String> data = new LinkedHashMap<>();
        data.put("hostname", "TEST");
        data.put("gametype", "SMP");
        // Extended info block
        data.put("game_id", "MINECRAFT");
        data.put("version", "TEST");
        data.put("plugins", "");
        // End extended block
        data.put("map", "BungeeCord_Proxy"); // kept from original, but consider fixing literal
        data.put("numplayers", Integer.toString(2));
        data.put("maxplayers", Integer.toString(20));
        data.put("hostport", Integer.toString(25565));
        data.put("hostip", "TEST");

        // Write key/value pairs
        for (Map.Entry<String, String> e : data.entrySet()) {
            writeString(out, e.getKey());
            writeString(out, e.getValue());
        }
        out.writeByte(0x00); // Null terminator for the key/value block

        // Player list block: header + list of player names, each NUL-terminated
        writeString(out, "\01player_\00");
        String[] test = new String[]{"TestPlayer1", "TestPlayer2"};
        for (String s : test) {
            writeString(out, s);
        }
        out.writeByte(0x00); // Null terminator for the player list
    }

    // -------------------- Low-level write helpers --------------------

    private void writeShortLE(ByteBuf buf, int value) {
        buf.writeShortLE(value);
    }

    /**
     * Writes ASCII representation of an integer as a NUL-terminated string.
     * Matches original implementation that wrote decimal digits and a trailing 0x00.
     */
    private void writeAsciiDecimalString(ByteBuf buf, int value) {
        writeString(buf, Integer.toString(value));
    }

    /**
     * Writes a NUL-terminated ASCII string to the buffer.
     * Note: this writes raw bytes of the Java char sequence (assumes ASCII for protocol text).
     */
    private void writeString(ByteBuf buf, String s) {
        if (s == null) {
            buf.writeByte(0x00);
            return;
        }
        for (int i = 0; i < s.length(); i++) {
            buf.writeByte((byte) s.charAt(i));
        }
        buf.writeByte(0x00);
    }

    // -------------------- Small helper types --------------------

    /**
     * Simple immutable session record that stores challenge token and creation time.
     */
    private static final class QuerySession {
        private final int token;
        private final long createdAt;

        QuerySession(int token, long createdAt) {
            this.token = token;
            this.createdAt = createdAt;
        }

        int getToken() {
            return token;
        }

        long getCreatedAt() {
            return createdAt;
        }
    }

}