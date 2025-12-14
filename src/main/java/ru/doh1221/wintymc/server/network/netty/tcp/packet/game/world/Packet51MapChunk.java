package ru.doh1221.wintymc.server.network.netty.tcp.packet.game.world;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.Setter;
import ru.doh1221.wintymc.server.network.netty.tcp.PacketHandler;
import ru.doh1221.wintymc.server.network.netty.tcp.packet.Packet;

import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
@Getter
@Setter
public class Packet51MapChunk extends Packet {

    // 16 * 128 * 16 * 5 / 2 = blocks + meta + light + skylight
    private static final int FULL_CHUNK_SIZE = 16 * 128 * 16 * 5 / 2;

    private static final int REDUCED_DEFLATE_THRESHOLD = FULL_CHUNK_SIZE / 4;
    private static final int DEFLATE_LEVEL_CHUNKS = 6;
    private static final int DEFLATE_LEVEL_PARTS = 1;

    private static final Deflater DEFLATER = new Deflater();
    private static byte[] deflateBuffer = new byte[FULL_CHUNK_SIZE + 100];

    public int chunkX;
    public int chunkY; // always 0 in vanilla
    public int chunkZ;

    public int sizeX; // +1 on read
    public int sizeY;
    public int sizeZ;

    public byte[] compressedData;
    public int compressedSize;

    public byte[] rawData;

    public Packet51MapChunk() {}

    public Packet51MapChunk(
            int chunkX,
            int chunkY,
            int chunkZ,
            int sizeX,
            int sizeY,
            int sizeZ,
            byte[] rawData
    ) {
        this.chunkX = chunkX;
        this.chunkY = chunkY;
        this.chunkZ = chunkZ;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;
        this.rawData = rawData;
    }

    @Override
    public void readData(ByteBuf in) throws IOException {

        this.chunkX = in.readInt();
        this.chunkY = in.readShort();
        this.chunkZ = in.readInt();

        this.sizeX = in.readByte() + 1;
        this.sizeY = in.readByte() + 1;
        this.sizeZ = in.readByte() + 1;

        this.compressedSize = in.readInt();

        byte[] compressed = new byte[this.compressedSize];
        in.readBytes(compressed);

        int uncompressedSize = sizeX * sizeY * sizeZ * 5 / 2;
        this.rawData = new byte[uncompressedSize];

        Inflater inflater = new Inflater();
        inflater.setInput(compressed);

        try {
            inflater.inflate(this.rawData);
        } catch (DataFormatException e) {
            throw new IOException("Bad compressed chunk data", e);
        } finally {
            inflater.end();
        }
    }

    @Override
    public void writeData(ByteBuf out) throws IOException {

        out.writeInt(this.chunkX);
        out.writeShort(this.chunkY);
        out.writeInt(this.chunkZ);

        out.writeByte(this.sizeX - 1);
        out.writeByte(this.sizeY - 1);
        out.writeByte(this.sizeZ - 1);

        out.writeInt(this.compressedSize);
        out.writeBytes(this.compressedData);
    }

    @Override
    public void handle(PacketHandler handler) {
        handler.handle(this);
    }

    @Override
    public int size() {
        return 17 + this.compressedSize;
    }

    public static void compress(Packet51MapChunk packet) {

        if (packet.compressedData != null) {
            return;
        }

        int dataSize = packet.rawData.length;

        if (deflateBuffer.length < dataSize + 100) {
            deflateBuffer = new byte[dataSize + 100];
        }

        DEFLATER.reset();
        DEFLATER.setLevel(
                dataSize < REDUCED_DEFLATE_THRESHOLD
                        ? DEFLATE_LEVEL_PARTS
                        : DEFLATE_LEVEL_CHUNKS
        );

        DEFLATER.setInput(packet.rawData);
        DEFLATER.finish();

        int size = DEFLATER.deflate(deflateBuffer);
        if (size == 0) {
            size = DEFLATER.deflate(deflateBuffer);
        }

        packet.compressedSize = size;
        packet.compressedData = new byte[size];
        System.arraycopy(deflateBuffer, 0, packet.compressedData, 0, size);
    }

    public static void decompress(Packet51MapChunk packet) throws IOException {

        if (packet.rawData != null) {
            return;
        }

        if (packet.compressedData == null || packet.compressedSize <= 0) {
            throw new IllegalStateException("Packet has no compressed data");
        }

        int expectedSize = packet.sizeX * packet.sizeY * packet.sizeZ * 5 / 2;
        byte[] out = new byte[expectedSize];

        Inflater inflater = new Inflater();
        inflater.setInput(packet.compressedData, 0, packet.compressedSize);

        try {
            int inflated = inflater.inflate(out);
            if (inflated != expectedSize) {
                throw new IOException(
                        "Invalid decompressed size: " + inflated +
                                ", expected: " + expectedSize
                );
            }
        } catch (DataFormatException e) {
            throw new IOException("Bad compressed chunk data", e);
        } finally {
            inflater.end();
        }

        packet.rawData = out;
    }

}
