package ru.doh1221.wintymc.server.utils.nbt;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Collection;

public class NBTTagCompound extends NBTBase {

    private final Object2ObjectOpenHashMap<String, NBTBase> a = new Object2ObjectOpenHashMap<>();

    public NBTTagCompound() {
    }

    void a(DataOutput dataoutput) throws IOException {

        for (NBTBase nbtbase : this.a.values()) {
            NBTBase.write(nbtbase, dataoutput);
        }

        dataoutput.writeByte(0);
    }

    void a(DataInput datainput) throws IOException {
        this.a.clear();

        NBTBase nbtbase;

        while ((nbtbase = NBTBase.read(datainput)).a() != 0) {
            this.a.put(nbtbase.b(), nbtbase);
        }
    }

    public Collection<NBTBase> c() {
        return this.a.values();
    }

    public byte a() {
        return (byte) 10;
    }

    public void setNBTBase(String s, NBTBase nbtbase) {
        this.a.put(s, nbtbase.a(s));
    }

    public void setByte(String s, byte b0) {
        this.a.put(s, (new NBTTagByte(b0)).a(s));
    }

    public void setShort(String s, short short1) {
        this.a.put(s, (new NBTTagShort(short1)).a(s));
    }

    public void setInt(String s, int i) {
        this.a.put(s, (new NBTTagInt(i)).a(s));
    }

    public void setLong(String s, long i) {
        this.a.put(s, (new NBTTagLong(i)).a(s));
    }

    public void setFloat(String s, float f) {
        this.a.put(s, (new NBTTagFloat(f)).a(s));
    }

    public void setDouble(String s, double d0) {
        this.a.put(s, (new NBTTagDouble(d0)).a(s));
    }

    public void setString(String s, String s1) {
        this.a.put(s, (new NBTTagString(s1)).a(s));
    }

    public void setByteArray(String s, byte[] abyte) {
        this.a.put(s, (new NBTTagByteArray(abyte)).a(s));
    }

    public void setCompound(String s, NBTTagCompound nbttagcompound) {
        this.a.put(s, nbttagcompound.a(s));
    }

    public void setBoolean(String s, boolean flag) {
        this.setByte(s, (byte) (flag ? 1 : 0));
    }

    public boolean hasKey(String s) {
        return this.a.containsKey(s);
    }

    public byte getByte(String s) {
        return !this.a.containsKey(s) ? 0 : ((NBTTagByte) this.a.get(s)).a;
    }

    public short getShort(String s) {
        return !this.a.containsKey(s) ? 0 : ((NBTTagShort) this.a.get(s)).a;
    }

    public int getInt(String s) {
        return !this.a.containsKey(s) ? 0 : ((NBTTagInt) this.a.get(s)).a;
    }

    public long getLong(String s) {
        return !this.a.containsKey(s) ? 0L : ((NBTTagLong) this.a.get(s)).a;
    }

    public float getFloat(String s) {
        return !this.a.containsKey(s) ? 0.0F : ((NBTTagFloat) this.a.get(s)).a;
    }

    public double getDouble(String s) {
        return !this.a.containsKey(s) ? 0.0D : ((NBTTagDouble) this.a.get(s)).a;
    }

    public String getString(String s) {
        return !this.a.containsKey(s) ? "" : ((NBTTagString) this.a.get(s)).a;
    }

    public byte[] getByteArray(String s) {
        return !this.a.containsKey(s) ? new byte[0] : ((NBTTagByteArray) this.a.get(s)).a;
    }

    public boolean getBoolean(String s) {
        return this.getByte(s) != 0;
    }

    public NBTTagCompound getCompound(String s) {
        return !this.a.containsKey(s) ? new NBTTagCompound() : (NBTTagCompound) this.a.get(s);
    }

    public NBTTagList getNBTList(String s) {
        return !this.a.containsKey(s) ? new NBTTagList() : (NBTTagList) this.a.get(s);
    }

    public String toString() {
        return this.a.size() + " entries";
    }
}
