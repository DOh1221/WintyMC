package ru.doh1221.wintymc.server.world;

public class NibbleArray {
    private final byte[] data;

    public NibbleArray(int size) {
        this.data = new byte[size / 2];
    }

    private int index(int x, int y, int z) {
        return (x << 11) | (z << 7) | y;
    }

    public byte get(int x, int y, int z) {
        int i = index(x, y, z);
        int v = Byte.toUnsignedInt(data[i >> 1]);
        return (byte) ((i & 1) == 0 ? (v & 0x0F) : (v >> 4));
    }

    public void set(int x, int y, int z, byte value) {
        int i = index(x, y, z);
        int pos = i >> 1;
        int v = Byte.toUnsignedInt(data[pos]);

        if ((i & 1) == 0) {
            v = (v & 0xF0) | (value & 0x0F);
        } else {
            v = (v & 0x0F) | ((value & 0x0F) << 4);
        }

        data[pos] = (byte) v;
    }

    public byte[] raw() {
        return data;
    }
}
