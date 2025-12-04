package ru.doh1221.wintymc.server.network.netty.tcp.packet.game.world;

import io.netty.buffer.ByteBuf;
import ru.doh1221.wintymc.server.network.netty.tcp.PacketHandler;
import ru.doh1221.wintymc.server.network.netty.tcp.packet.Packet;

import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class Packet51MapChunk extends Packet {

    private static final int CHUNK_SIZE = 16 * 128 * 16 * 5 / 2;
    private static final int REDUCED_DEFLATE_THRESHOLD = CHUNK_SIZE / 4;
    private static final int DEFLATE_LEVEL_CHUNKS = 6;
    private static final int DEFLATE_LEVEL_PARTS = 1;
    private static final Deflater deflater = new Deflater();
    private static byte[] deflateBuffer = new byte[CHUNK_SIZE + 100];
    public int a;
    public int b;
    public int c;
    public int d;
    public int e;
    public int f;
    public byte[] g;
    public int h;
    public byte[] rawData;

    public Packet51MapChunk() {

    }
    public Packet51MapChunk(int i, int j, int k, int l, int i1, int j1, byte[] data) {
        this.a = i;
        this.b = j;
        this.c = k;
        this.d = l;
        this.e = i1;
        this.f = j1;
        this.rawData = data; // CraftBukkit
    }

    public static void compress(Packet51MapChunk mapChunk) {

        // If 'packet.g' is set then this packet has already been compressed.
        if (mapChunk.g != null) {
            return;
        }

        int dataSize = mapChunk.rawData.length;
        if (deflateBuffer.length < dataSize + 100) {
            deflateBuffer = new byte[dataSize + 100];
        }

        deflater.reset();
        deflater.setLevel(dataSize < REDUCED_DEFLATE_THRESHOLD ? DEFLATE_LEVEL_PARTS : DEFLATE_LEVEL_CHUNKS);
        deflater.setInput(mapChunk.rawData);
        deflater.finish();
        int size = deflater.deflate(deflateBuffer);
        if (size == 0) {
            size = deflater.deflate(deflateBuffer);
        }

        // copy compressed data to packet
        mapChunk.g = new byte[size];
        mapChunk.h = size;
        System.arraycopy(deflateBuffer, 0, mapChunk.g, 0, size);
    }

    public void readData(ByteBuf datainputstream) throws IOException { // CraftBukkit - throws IOEXception
        this.a = datainputstream.readInt();
        this.b = datainputstream.readShort();
        this.c = datainputstream.readInt();
        this.d = datainputstream.readByte() + 1;
        this.e = datainputstream.readByte() + 1;
        this.f = datainputstream.readByte() + 1;
        this.h = datainputstream.readInt();
        byte[] abyte = new byte[this.h];

        datainputstream.readBytes(abyte);
        this.g = new byte[this.d * this.e * this.f * 5 / 2];
        Inflater inflater = new Inflater();

        inflater.setInput(abyte);

        try {
            inflater.inflate(this.g);
        } catch (DataFormatException dataformatexception) {
            throw new IOException("Bad compressed data format");
        } finally {
            inflater.end();
        }
    }

    public void writeData(ByteBuf dataoutputstream) throws IOException { // CraftBukkit - throws IOException
        dataoutputstream.writeInt(this.a);
        dataoutputstream.writeShort(this.b);
        dataoutputstream.writeInt(this.c);
        dataoutputstream.writeByte(this.d - 1);
        dataoutputstream.writeByte(this.e - 1);
        dataoutputstream.writeByte(this.f - 1);
        dataoutputstream.writeInt(this.h);
        dataoutputstream.writeBytes(this.g);
    }

    @Override
    public void handle(PacketHandler handler) {

    }

    @Override
    public int size() {
        return 0;
    }
}
