package ru.doh1221.wintymc.server.game.world.mcregion;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

/**
 * Simple Region (.mca) writer/reader implementing the Anvil region file layout:
 * - header: 4096 bytes offsets table (1024 ints) + 4096 bytes timestamps (1024 ints)
 * - chunks are stored in sectors of 4096 bytes, the offset entry stores sector start (3 bytes) and sector count (1 byte)
 *
 * This implementation is minimal but correct for writing/reading contiguous chunk data and is designed
 * for speed (buffers, bulk writes). It uses RandomAccessFile for direct writes to specific offsets.
 *
 * Note: This implementation overwrites existing regions and appends chunks sequentially.
 */
public final class RegionFile implements AutoCloseable {

    private static final int SECTOR_BYTES = 4096;
    private static final int HEADER_SECTORS = 2; // 2 sectors (offsets + timestamps)
    private static final int CHUNKS_PER_REGION = 32;
    private static final int OFFSET_TABLE_ENTRIES = CHUNKS_PER_REGION * CHUNKS_PER_REGION; // 1024

    private final RandomAccessFile raf;
    private final File file;

    // track next free sector index (starts after header)
    private int nextFreeSector;

    public RegionFile(Path path) throws IOException {
        this.file = path.toFile();
        this.raf = new RandomAccessFile(file, "rw");
        if (raf.length() < SECTOR_BYTES * HEADER_SECTORS) {
            // initialize with zeroed header
            raf.setLength(SECTOR_BYTES * HEADER_SECTORS);
            raf.seek(0);
            byte[] zeros = new byte[SECTOR_BYTES * HEADER_SECTORS];
            raf.write(zeros);
            this.nextFreeSector = HEADER_SECTORS;
        } else {
            // compute next free sector by scanning offsets table and finding max used sector
            raf.seek(0);
            byte[] header = new byte[SECTOR_BYTES];
            raf.readFully(header);
            int maxSector = HEADER_SECTORS;
            for (int i = 0; i < OFFSET_TABLE_ENTRIES; i++) {
                int entry = ((header[i * 4] & 0xFF) << 24) | ((header[i * 4 + 1] & 0xFF) << 16) | ((header[i * 4 + 2] & 0xFF) << 8) | (header[i * 4 + 3] & 0xFF);
                int sectorOffset = (entry >>> 8) & 0xFFFFFF;
                int sectorCount = entry & 0xFF;
                if (sectorOffset != 0 && sectorCount != 0) {
                    maxSector = Math.max(maxSector, sectorOffset + sectorCount);
                }
            }
            this.nextFreeSector = maxSector;
        }
    }

    /**
     * Write a single chunk's already-serialized-and-uncompressed NBT data to the region.
     * The method will compress with ZLIB (compression type 2) and store into sectors.
     *
     * @param localChunkX chunk X inside region [0..31]
     * @param localChunkZ chunk Z inside region [0..31]
     * @param nbtUncompressed raw NBT bytes
     */
    public synchronized void writeChunk(int localChunkX, int localChunkZ, byte[] nbtUncompressed) throws IOException {
        if (localChunkX < 0 || localChunkX >= CHUNKS_PER_REGION || localChunkZ < 0 || localChunkZ >= CHUNKS_PER_REGION)
            throw new IllegalArgumentException("Chunk coords out of range");

        // compress with zlib (Deflate)
        byte[] compressed = compressZlib(nbtUncompressed);

        int totalLength = 4 + 1 + compressed.length; // 4 bytes length + 1 byte compression type + data
        int requiredSectors = (totalLength + SECTOR_BYTES - 1) / SECTOR_BYTES;

        int writeSector = nextFreeSector;
        long writePos = (long) writeSector * SECTOR_BYTES;

        // ensure file length
        long requiredLen = writePos + (long) requiredSectors * SECTOR_BYTES;
        if (raf.length() < requiredLen) raf.setLength(requiredLen);

        // write chunk header and data
        raf.seek(writePos);
        raf.writeInt(totalLength); // big-endian
        raf.writeByte(2); // compression type: 2 = zlib/deflate
        raf.write(compressed);

        // pad remaining bytes in last sector
        long remaining = (long) requiredSectors * SECTOR_BYTES - totalLength;
        if (remaining > 0) {
            byte[] pad = new byte[(int) remaining];
            raf.write(pad);
        }

        // update offset table entry and timestamp
        int offsetEntry = (writeSector << 8) | requiredSectors;
        int tableIndex = localChunkX + localChunkZ * CHUNKS_PER_REGION;
        raf.seek(tableIndex * 4L);
        raf.writeInt(offsetEntry);

        // timestamp (epoch seconds)
        raf.seek(SECTOR_BYTES + tableIndex * 4L);
        int now = (int) TimeUnit.MILLISECONDS.toSeconds(Instant.now().toEpochMilli());
        raf.writeInt(now);

        nextFreeSector += requiredSectors;
    }

    /**
     * Read (decompress) the chunk NBT bytes for a local chunk coordinate.
     *
     * @return uncompressed NBT bytes or null if chunk not present
     */
    public synchronized byte[] readChunk(int localChunkX, int localChunkZ) throws IOException {
        if (localChunkX < 0 || localChunkX >= CHUNKS_PER_REGION || localChunkZ < 0 || localChunkZ >= CHUNKS_PER_REGION)
            throw new IllegalArgumentException("Chunk coords out of range");

        int tableIndex = localChunkX + localChunkZ * CHUNKS_PER_REGION;
        raf.seek(tableIndex * 4L);
        int entry = raf.readInt();
        if (entry == 0) return null;
        int sectorOffset = (entry >>> 8) & 0xFFFFFF;
        int sectorCount = entry & 0xFF;
        if (sectorOffset == 0 || sectorCount == 0) return null;

        long pos = (long) sectorOffset * SECTOR_BYTES;
        raf.seek(pos);
        int len = raf.readInt();
        if (len <= 0 || len > sectorCount * SECTOR_BYTES) throw new IOException("Invalid chunk length");
        int compressionType = raf.readUnsignedByte();
        int dataLen = len - 1;
        byte[] data = new byte[dataLen];
        raf.readFully(data);

        return switch (compressionType) {
            case 2 -> decompressZlib(data);
            case 1 -> throw new IOException("GZIP compression in region not supported in this minimal reader");
            default -> throw new IOException("Unknown compression type: " + compressionType);
        };
    }

    private static byte[] compressZlib(byte[] src) throws IOException {
        try (ByteArrayOutputStreamEx baos = new ByteArrayOutputStreamEx(); DeflaterOutputStream dos = new DeflaterOutputStream(baos, new Deflater(Deflater.BEST_SPEED))) {
            dos.write(src);
            dos.finish();
            return baos.toByteArray();
        }
    }

    private static byte[] decompressZlib(byte[] compressed) throws IOException {
        try (InflaterInputStream iis = new InflaterInputStream(new java.io.ByteArrayInputStream(compressed)); ByteArrayOutputStreamEx baos = new ByteArrayOutputStreamEx()) {
            byte[] buf = new byte[4096];
            int r;
            while ((r = iis.read(buf)) != -1) baos.write(buf, 0, r);
            return baos.toByteArray();
        } catch (EOFException eof) {
            throw new IOException("Truncated compressed data", eof);
        }
    }

    @Override
    public synchronized void close() throws IOException {
        raf.close();
    }

    // small optimized BAOS to expose buffer quickly
    private static final class ByteArrayOutputStreamEx extends ByteArrayOutputStream {
        public ByteArrayOutputStreamEx() { super(); }
        byte[] toByteArrayInternal() { return this.buf; }
    }
}