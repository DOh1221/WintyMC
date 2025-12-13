package ru.doh1221.wintymc.server.utils.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;

public abstract class NBTBase {

    private String a = null;

    public NBTBase() {
    }

    public static NBTBase b(DataInput datainput) throws IOException {
        byte b0 = datainput.readByte();

        if (b0 == 0) {
            return new NBTTagEnd();
        } else {
            NBTBase nbtbase = a(b0);

            Objects.requireNonNull(nbtbase).a = datainput.readUTF();
            nbtbase.a(datainput);
            return nbtbase;
        }
    }

    public static void a(NBTBase nbtbase, DataOutput dataoutput) throws IOException {
        dataoutput.writeByte(nbtbase.a());
        if (nbtbase.a() != 0) {
            dataoutput.writeUTF(nbtbase.b());
            nbtbase.a(dataoutput);
        }
    }

    public static NBTBase a(byte b0) {
        return switch (b0) {
            case 0 -> new NBTTagEnd();
            case 1 -> new NBTTagByte();
            case 2 -> new NBTTagShort();
            case 3 -> new NBTTagInt();
            case 4 -> new NBTTagLong();
            case 5 -> new NBTTagFloat();
            case 6 -> new NBTTagDouble();
            case 7 -> new NBTTagByteArray();
            case 8 -> new NBTTagString();
            case 9 -> new NBTTagList();
            case 10 -> new NBTTagCompound();
            default -> null;
        };
    }

    public static String b(byte b0) {
        return switch (b0) {
            case 0 -> "TAG_End";
            case 1 -> "TAG_Byte";
            case 2 -> "TAG_Short";
            case 3 -> "TAG_Int";
            case 4 -> "TAG_Long";
            case 5 -> "TAG_Float";
            case 6 -> "TAG_Double";
            case 7 -> "TAG_Byte_Array";
            case 8 -> "TAG_String";
            case 9 -> "TAG_List";
            case 10 -> "TAG_Compound";
            default -> "UNKNOWN";
        };
    }

    abstract void a(DataOutput dataoutput) throws IOException;

    abstract void a(DataInput datainput) throws IOException;

    public abstract byte a();

    public String b() {
        return this.a == null ? "" : this.a;
    }

    public NBTBase a(String s) {
        this.a = s;
        return this;
    }
}
