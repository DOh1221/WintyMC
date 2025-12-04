package ru.doh1221.wintymc.server.game.nbt;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * Minimal NBT writer (TAG_Compound based) sufficient for generating Minecraft chunk NBTs.
 * <p>
 * Writes a named compound as used by chunk files:
 * - Root tag (TAG_Compound) with a name (usually "") and inner structure.
 * <p>
 * Supported value types:
 * - Byte, Short, Integer, Long, Float, Double
 * - byte[] (TAG_Byte_Array), int[] (TAG_Int_Array), long[] (TAG_Long_Array)
 * - String
 * - List<Object> (homogeneous; element types from above)
 * - Map<String,Object> for nested TAG_Compound
 * <p>
 * Note: This is not a full NBT implementation (no TAG_End lists, no dynamic conversions) but is enough
 * for producing chunk data compatible with many tools / viewers (old/new Anvil-compatible).
 * <p>
 * Produces uncompressed NBT bytes (so callers must compress as needed for region files).
 */
public final class NBTWriter {

    private static final byte TAG_END = 0;
    private static final byte TAG_BYTE = 1;
    private static final byte TAG_SHORT = 2;
    private static final byte TAG_INT = 3;
    private static final byte TAG_LONG = 4;
    private static final byte TAG_FLOAT = 5;
    private static final byte TAG_DOUBLE = 6;
    private static final byte TAG_BYTE_ARRAY = 7;
    private static final byte TAG_STRING = 8;
    private static final byte TAG_LIST = 9;
    private static final byte TAG_COMPOUND = 10;
    private static final byte TAG_INT_ARRAY = 11;
    private static final byte TAG_LONG_ARRAY = 12;
    private NBTWriter() {
    }

    /**
     * Write a named compound (root tag).
     *
     * @param rootName root name (use "" for anonymous root)
     * @param value    map representing compound
     * @return uncompressed NBT bytes
     */
    public static byte[] writeNamedCompound(String rootName, Map<String, Object> value) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream(); DataOutputStream out = new DataOutputStream(baos)) {
            writeTagCompound(out, rootName, value);
            out.flush();
            return baos.toByteArray();
        }
    }

    private static void writeTagCompound(DataOutputStream out, String name, Map<String, Object> map) throws IOException {
        out.writeByte(TAG_COMPOUND);
        writeString(out, name);
        // write each entry
        for (Map.Entry<String, Object> e : map.entrySet()) {
            writeAnyTag(out, e.getKey(), e.getValue());
        }
        // end
        out.writeByte(TAG_END);
    }

    @SuppressWarnings("unchecked")
    private static void writeAnyTag(DataOutputStream out, String name, Object value) throws IOException {
        if (value == null) {
            // write TAG_END with name? NBT treats TAG_End as no name; here we skip
            return;
        }
        if (value instanceof Byte) {
            out.writeByte(TAG_BYTE);
            writeString(out, name);
            out.writeByte((Byte) value);
        } else if (value instanceof Short) {
            out.writeByte(TAG_SHORT);
            writeString(out, name);
            out.writeShort((Short) value);
        } else if (value instanceof Integer) {
            out.writeByte(TAG_INT);
            writeString(out, name);
            out.writeInt((Integer) value);
        } else if (value instanceof Long) {
            out.writeByte(TAG_LONG);
            writeString(out, name);
            out.writeLong((Long) value);
        } else if (value instanceof Float) {
            out.writeByte(TAG_FLOAT);
            writeString(out, name);
            out.writeFloat((Float) value);
        } else if (value instanceof Double) {
            out.writeByte(TAG_DOUBLE);
            writeString(out, name);
            out.writeDouble((Double) value);
        } else if (value instanceof byte[] arr) {
            out.writeByte(TAG_BYTE_ARRAY);
            writeString(out, name);
            out.writeInt(arr.length);
            out.write(arr);
        } else if (value instanceof int[] arr) {
            out.writeByte(TAG_INT_ARRAY);
            writeString(out, name);
            out.writeInt(arr.length);
            for (int v : arr) out.writeInt(v);
        } else if (value instanceof long[] arr) {
            out.writeByte(TAG_LONG_ARRAY);
            writeString(out, name);
            out.writeInt(arr.length);
            for (long v : arr) out.writeLong(v);
        } else if (value instanceof String) {
            out.writeByte(TAG_STRING);
            writeString(out, name);
            writeString(out, (String) value);
        } else if (value instanceof List<?> list) {
            // empty list defaults to TAG_End as element type 0 which is invalid; prefer TAG_Byte for empty maybe, but we emit TAG_List with TAG_Byte and zero length
            byte elemType = TAG_END;
            if (!list.isEmpty()) {
                Object first = list.get(0);
                elemType = detectTagType(first);
            } else {
                elemType = TAG_COMPOUND; // empty list of compounds common for "Sections" could be empty; choose COMPOUND
            }
            out.writeByte(TAG_LIST);
            writeString(out, name);
            out.writeByte(elemType);
            out.writeInt(list.size());
            for (Object o : list) {
                writeListElement(out, elemType, o);
            }
        } else if (value instanceof Map) {
            out.writeByte(TAG_COMPOUND);
            writeString(out, name);
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) value;
            for (Map.Entry<String, Object> e : map.entrySet()) writeAnyTag(out, e.getKey(), e.getValue());
            out.writeByte(TAG_END);
        } else {
            throw new IOException("Unsupported NBT value type: " + value.getClass());
        }
    }

    private static void writeListElement(DataOutputStream out, byte elemType, Object value) throws IOException {
        switch (elemType) {
            case TAG_BYTE -> out.writeByte((Byte) value);
            case TAG_SHORT -> out.writeShort((Short) value);
            case TAG_INT -> out.writeInt((Integer) value);
            case TAG_LONG -> out.writeLong((Long) value);
            case TAG_FLOAT -> out.writeFloat((Float) value);
            case TAG_DOUBLE -> out.writeDouble((Double) value);
            case TAG_BYTE_ARRAY -> {
                byte[] arr = (byte[]) value;
                out.writeInt(arr.length);
                out.write(arr);
            }
            case TAG_STRING -> writeString(out, (String) value);
            case TAG_COMPOUND -> {
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (Map<String, Object>) value;
                for (Map.Entry<String, Object> e : map.entrySet()) writeAnyTag(out, e.getKey(), e.getValue());
                out.writeByte(TAG_END);
            }
            case TAG_INT_ARRAY -> {
                int[] arr = (int[]) value;
                out.writeInt(arr.length);
                for (int v : arr) out.writeInt(v);
            }
            case TAG_LONG_ARRAY -> {
                long[] arr = (long[]) value;
                out.writeInt(arr.length);
                for (long v : arr) out.writeLong(v);
            }
            default -> throw new IOException("Unsupported list element type: " + elemType);
        }
    }

    private static byte detectTagType(Object o) throws IOException {
        if (o instanceof Byte) return TAG_BYTE;
        if (o instanceof Short) return TAG_SHORT;
        if (o instanceof Integer) return TAG_INT;
        if (o instanceof Long) return TAG_LONG;
        if (o instanceof Float) return TAG_FLOAT;
        if (o instanceof Double) return TAG_DOUBLE;
        if (o instanceof byte[]) return TAG_BYTE_ARRAY;
        if (o instanceof String) return TAG_STRING;
        if (o instanceof Map) return TAG_COMPOUND;
        if (o instanceof int[]) return TAG_INT_ARRAY;
        if (o instanceof long[]) return TAG_LONG_ARRAY;
        throw new IOException("Cannot detect NBT tag type for " + (o == null ? "null" : o.getClass()));
    }

    private static void writeString(DataOutputStream out, String s) throws IOException {
        byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
        out.writeShort(bytes.length);
        out.write(bytes);
    }
}