package ru.doh1221.wintymc.server.utils.nbt;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class CompressedStreamTools {

    public CompressedStreamTools() {}

    public static NBTTagCompound read(InputStream inputstream) throws IOException {
        DataInputStream datainputstream = new DataInputStream(new GZIPInputStream(inputstream));

        NBTTagCompound nbttagcompound;

        try {
            nbttagcompound = a((DataInput) datainputstream);
        } finally {
            datainputstream.close();
        }

        return nbttagcompound;
    }

    public static void write(NBTTagCompound nbttagcompound, OutputStream outputstream) throws IOException {
        try (DataOutputStream dataoutputstream = new DataOutputStream(new GZIPOutputStream(outputstream))) {
            a(nbttagcompound, (DataOutput) dataoutputstream);
        }
    }

    public static NBTTagCompound a(DataInput datainput) throws IOException {
        NBTBase nbtbase = NBTBase.read(datainput);

        if (nbtbase instanceof NBTTagCompound) {
            return (NBTTagCompound) nbtbase;
        } else {
            throw new IOException("Root tag must be a named compound tag");
        }
    }

    public static void a(NBTTagCompound nbttagcompound, DataOutput dataoutput) throws IOException {
        NBTBase.write(nbttagcompound, dataoutput);
    }
}
