package ru.doh1221.wintymc.server.utils;

public class ChunkUtils {

    public static int toChunk(double coord) {
        int block = (int) Math.floor(coord);
        return block >> 4;
    }

    public static int toChunk(int coord) {
        return coord >> 4;
    }

}
