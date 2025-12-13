package ru.doh1221.wintymc.server.utils.world;

import lombok.SneakyThrows;

import java.io.ByteArrayOutputStream;

class ChunkBuffer extends ByteArrayOutputStream {

    private int x;
    private int z;

    final RegionFile region;

    public ChunkBuffer(RegionFile regionfile, int x, int z) {
        super(8096);
        this.region = regionfile;
        this.x = x;
        this.z = z;
    }

    @SneakyThrows
    public void close() {
        this.region.writeChunk(this.x, this.z, this.buf, this.count);
    }
}
