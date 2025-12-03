package ru.doh1221.wintymc.server.utils;

import io.netty.buffer.ByteBuf;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class Stream {
    private static final Charset UTF8 = StandardCharsets.UTF_16BE;

    public static void writeString(String s, ByteBuf buf, boolean varint) {
        if (varint) {
            byte[] content = s.getBytes(UTF8);
            writeVarInt(content.length, buf);
            buf.writeBytes(content);
        } else {
            buf.writeShort(s.length());
            for (char c : s.toCharArray()) {
                buf.writeChar(c);
            }
        }
    }

    public static String readString(ByteBuf buf, boolean varint) {
        if (varint) {
            int len = readVarInt(buf);
            byte[] content = new byte[len];
            buf.readBytes(content);
            return new String(content, UTF8);
        } else {
            short len = buf.readShort();
            char[] chars = new char[len];
            for (int i = 0; i < len; i++) {
                chars[i] = buf.readChar();
            }
            return new String(chars);
        }
    }

    public static void writeArray(byte[] b, ByteBuf buf) {
        // TODO: Check len - use Guava?
        buf.writeShort(b.length);
        buf.writeBytes(b);
    }

    public static byte[] readArray(ByteBuf buf) {
        // TODO: Check len - use Guava?
        short len = buf.readShort();
        byte[] ret = new byte[len];
        buf.readBytes(ret);
        return ret;
    }

    public static int readVarInt(ByteBuf input) {
        int out = 0;
        int bytes = 0;
        byte in;
        while (true) {
            in = input.readByte();

            out |= (in & 0x7F) << (bytes++ * 7);

            if (bytes > 32) {
                throw new RuntimeException("VarInt too big");
            }

            if ((in & 0x80) != 0x80) {
                break;
            }
        }
        return out;
    }

    public static long readVarLong(ByteBuf input) {
        long value = 0L;
        int position = 0;
        byte currentByte;

        while (true) {
            currentByte = input.readByte();
            value |= (long) (currentByte & 0x7F) << position;

            if ((currentByte & 0x80) == 0) {
                break;
            }

            position += 7;

            if (position > 64) {
                throw new RuntimeException("VarLong too big");
            }
        }

        return value;
    }

    public static void writeVarInt(int value, ByteBuf output) {
        int part;
        while (true) {
            part = value & 0x7F;

            value >>>= 7;
            if (value != 0) {
                part |= 0x80;
            }

            output.writeByte(part);

            if (value == 0) {
                break;
            }
        }
    }

    public static void writeVarLong(long value, ByteBuf output) {
        while (true) {
            int temp = (int) (value & 0x7F);
            value >>>= 7;

            if (value != 0) {
                temp |= 0x80;
            }

            output.writeByte(temp);

            if (value == 0) {
                break;
            }
        }
    }

    public static void writeUUID(ByteBuf buf, UUID uuid) {
        writeVarLong(uuid.getMostSignificantBits(), buf);
        writeVarLong(uuid.getLeastSignificantBits(), buf);
    }

    public static UUID readUUID(ByteBuf buf) {
        long mostSigBits = readVarLong(buf);
        long leastSigBits = readVarLong(buf);
        return new UUID(mostSigBits, leastSigBits);
    }

    // Helpers to compute varint sizes for sizing functions
    public static int sizeOfVarInt(int value) {
        int count = 0;
        do {
            value >>>= 7;
            count++;
        } while (value != 0);
        return count;
    }

    public static int sizeOfVarInt(int length, boolean forLengthField) {
        return sizeOfVarInt(length);
    }

    public static int sizeOfStringVarInt(String s) {
        byte[] content = s.getBytes(UTF8);
        return sizeOfVarInt(content.length) + content.length;
    }
}