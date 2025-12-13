package ru.doh1221.wintymc.server.utils.nbt;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;

public class NBTTagList extends NBTBase {

    private ObjectList<NBTBase> a = new ObjectArrayList<>();
    private byte b;

    public NBTTagList() {
    }

    void a(DataOutput dataoutput) throws IOException {
        if (!this.a.isEmpty()) {
            this.b = this.a.getFirst().a();
        } else {
            this.b = 1;
        }

        dataoutput.writeByte(this.b);
        dataoutput.writeInt(this.a.size());

        for (NBTBase o : this.a) {
            o.a(dataoutput);
        }
    }

    void a(DataInput datainput) throws IOException {
        this.b = datainput.readByte();
        int i = datainput.readInt();

        this.a = new ObjectArrayList<>();

        for (int j = 0; j < i; ++j) {
            NBTBase nbtbase = NBTBase.a(this.b);

            Objects.requireNonNull(nbtbase).a(datainput);
            this.a.add(nbtbase);
        }
    }

    public byte a() {
        return (byte) 9;
    }

    public String toString() {
        return this.a.size() + " entries of type " + NBTBase.b(this.b);
    }

    public void a(NBTBase nbtbase) {
        this.b = nbtbase.a();
        this.a.add(nbtbase);
    }

    public NBTBase a(int i) {
        return this.a.get(i);
    }

    public int c() {
        return this.a.size();
    }
}
