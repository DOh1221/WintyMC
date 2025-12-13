package ru.doh1221.wintymc.server.utils.chunkio;

import java.io.*;
import java.util.ArrayList;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

public class RegionFile {

    private static final byte[] EMPTY_SECTOR = new byte[4096];

    private final File file;
    private RandomAccessFile raf;

    private final int[] offsets = new int[1024];
    private final int[] timestamps = new int[1024];

    private ArrayList<Boolean> freeSectors;
    private int totalSizeDelta = 0;

    private long lastModified = 0L;

    public RegionFile(File file) {
        this.file = file;

        try {
            if (file.exists()) {
                lastModified = file.lastModified();
            }

            raf = new RandomAccessFile(file, "rw");

            if (raf.length() < 4096 * 2) {
                for (int i = 0; i < 1024; i++) raf.writeInt(0); // offsets
                for (int i = 0; i < 1024; i++) raf.writeInt(0); // timestamps
                totalSizeDelta += 4096 * 2;
            }

            long padding = raf.length() % 4096;
            if (padding != 0) {
                for (long i = 0; i < padding; i++) raf.write(0);
            }

            int sectorCount = (int) (raf.length() / 4096);
            freeSectors = new ArrayList<>(sectorCount);
            for (int i = 0; i < sectorCount; i++) freeSectors.add(Boolean.TRUE);
            freeSectors.set(0, Boolean.FALSE);
            freeSectors.set(1, Boolean.FALSE);

            raf.seek(0);

            for (int i = 0; i < 1024; i++) {
                int offset = raf.readInt();
                offsets[i] = offset;

                if (offset != 0) {
                    int sectorStart = offset >> 8;
                    int sectorCountChunk = offset & 0xFF;
                    for (int j = 0; j < sectorCountChunk; j++) {
                        if (sectorStart + j < freeSectors.size()) {
                            freeSectors.set(sectorStart + j, Boolean.FALSE);
                        }
                    }
                }
            }

            for (int i = 0; i < 1024; i++) {
                timestamps[i] = raf.readInt();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized int getAndResetSizeDelta() {
        int delta = totalSizeDelta;
        totalSizeDelta = 0;
        return delta;
    }

    public synchronized DataInputStream getChunkDataInputStream(int x, int z) {
        if (isOutOfBounds(x, z)) return null;

        try {
            int offset = offsets[x + z * 32];
            if (offset == 0) return null;

            int sectorStart = offset >> 8;
            int sectorCount = offset & 0xFF;

            if (sectorStart + sectorCount > freeSectors.size()) return null;

            raf.seek((long) (sectorStart * 4096));
            int length = raf.readInt();
            byte version = raf.readByte();

            byte[] data = new byte[length - 1];
            raf.readFully(data);

            ByteArrayInputStream bais = new ByteArrayInputStream(data);

            if (version == 1) return new DataInputStream(new GZIPInputStream(bais));
            if (version == 2) return new DataInputStream(new InflaterInputStream(bais));

            return null;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public DataOutputStream getChunkDataOutputStream(int x, int z) {
        if (isOutOfBounds(x, z)) return null;
        return new DataOutputStream(new DeflaterOutputStream(new ChunkBuffer(this, x, z)));
    }

    protected synchronized void writeChunk(int x, int z, byte[] data, int length) throws IOException {
        int offset = offsets[x + z * 32];
        int sectorStart = offset >> 8;
        int sectorCount = offset & 0xFF;

        int requiredSectors = (length + 5) / 4096 + 1;
        if (requiredSectors >= 256) return;

        if (sectorStart != 0 && sectorCount == requiredSectors) {
            writeData(sectorStart, data, length);
        } else {
            for (int i = 0; i < sectorCount; i++) freeSectors.set(sectorStart + i, Boolean.TRUE);

            int newStart = findFreeSectors(requiredSectors);
            if (newStart != -1) {
                sectorStart = newStart;
                for (int i = 0; i < requiredSectors; i++) freeSectors.set(sectorStart + i, Boolean.FALSE);
                writeChunkOffset(x, z, sectorStart, requiredSectors);
                writeData(sectorStart, data, length);
            } else {
                raf.seek(raf.length());
                sectorStart = freeSectors.size();
                for (int i = 0; i < requiredSectors; i++) {
                    raf.write(EMPTY_SECTOR);
                    freeSectors.add(Boolean.FALSE);
                }
                totalSizeDelta += 4096 * requiredSectors;
                writeChunkOffset(x, z, sectorStart, requiredSectors);
                writeData(sectorStart, data, length);
            }
        }

        writeTimestamp(x, z, (int) (System.currentTimeMillis() / 1000L));
    }

    private void writeData(int sectorStart, byte[] data, int length) throws IOException {
        raf.seek((long) (sectorStart * 4096));
        raf.writeInt(length + 1);
        raf.writeByte(2);
        raf.write(data, 0, length);
    }

    private boolean isOutOfBounds(int x, int z) {
        return x < 0 || x >= 32 || z < 0 || z >= 32;
    }

    private int getOffset(int x, int z) {
        return offsets[x + z * 32];
    }

    public boolean hasChunk(int x, int z) {
        return getOffset(x, z) != 0;
    }

    private void writeChunkOffset(int x, int z, int sectorStart, int sectorCount) throws IOException {
        int offset = (sectorStart << 8) | sectorCount;
        offsets[x + z * 32] = offset;
        raf.seek((long) ((x + z * 32) * 4));
        raf.writeInt(offset);
    }

    private void writeTimestamp(int x, int z, int timestamp) throws IOException {
        timestamps[x + z * 32] = timestamp;
        raf.seek(4096 + (x + z * 32) * 4);
        raf.writeInt(timestamp);
    }

    public void close() throws IOException {
        raf.close();
    }

    private int findFreeSectors(int required) {
        int count = 0;
        for (int i = 0; i < freeSectors.size(); i++) {
            if (freeSectors.get(i)) {
                if (++count == required) return i - required + 1;
            } else {
                count = 0;
            }
        }
        return -1;
    }

}
