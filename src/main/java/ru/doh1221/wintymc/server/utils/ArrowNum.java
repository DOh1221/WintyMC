package ru.doh1221.wintymc.server.utils;

import io.netty.buffer.ByteBuf;

/**
 * ArrowNum — компактный формат числа с базой и экспонентой (3 байта)
 * Бинарная шкала по умолчанию (2^exp), быстрые Int/Long/Float/Double конверсии
 * <p>
 * Формат:
 * [flags (1 byte)]
 * [base (1 byte, 0..127)]
 * [exp (1 byte, -64..63)]
 * <p>
 * Flags (1 byte):
 * bit 0 — отрицательное число
 * bit 1 — бинарная шкала (2^exp)
 * bit 2 — зарезервировано
 */
public class ArrowNum {

    private boolean negative;
    private boolean binaryScale = true; // по умолчанию бинарная шкала
    private byte base;  // 0..127
    private byte exp;   // -64..63

    public ArrowNum() {
    }

    public ArrowNum(Number num) {
        fromNumber(num);
    }

    public static ArrowNum of(Number num) {
        return new ArrowNum(num);
    }

    public static ArrowNum read(ByteBuf buf) {
        ArrowNum n = new ArrowNum();
        byte flags = buf.readByte();
        n.negative = (flags & 0x1) != 0;
        n.binaryScale = (flags & 0x2) != 0;
        n.base = buf.readByte();
        n.exp = buf.readByte();
        return n;
    }

    /**
     * Конвертируем любое число в ArrowNum.
     */
    public void fromNumber(Number num) {
        if (num == null) {
            base = 0;
            exp = 0;
            negative = false;
            return;
        }

        double val = num.doubleValue();
        negative = val < 0;
        double abs = Math.abs(val);

        if (abs < 1e-12) {
            base = 0;
            exp = 0;
            return;
        }

        // бинарная шкала: 2^exp
        int e = 0;
        double tmp = abs;
        while (tmp >= 128 && e < 63) {
            tmp /= 2;
            e++;
        }
        while (tmp < 1 && e > -64) {
            tmp *= 2;
            e--;
        }
        int b = (int) Math.round(tmp);

        if (b > 127) b = 127;
        if (e > 63) e = 63;
        if (e < -64) e = -64;

        base = (byte) b;
        exp = (byte) e;
    }

    public double toDouble() {
        double val = base & 0xFF; // unsigned
        if (binaryScale) {
            val = Math.scalb(val, exp); // base * 2^exp (handles negative exp)
        } else {
            val *= Math.pow(10, exp);
        }
        return negative ? -val : val;
    }

    public long toLong() {
        return (long) toDouble();
    }

    public int toInt() {
        return (int) toDouble();
    }

    // ----------------- Сериализация -----------------

    public float toFloat() {
        return (float) toDouble();
    }

    public void write(ByteBuf buf) {
        byte flags = 0;
        if (negative) flags |= 0x1;
        if (binaryScale) flags |= 0x2;
        buf.writeByte(flags);
        buf.writeByte(base);
        buf.writeByte(exp);
    }

    @Override
    public String toString() {
        return String.format("%s%d * %s^%d",
                negative ? "-" : "",
                base & 0xFF,
                binaryScale ? "2" : "10",
                exp
        );
    }

    // ----------------- Быстрый доступ к base/exp -----------------

    public byte getBase() {
        return base;
    }

    public void setBase(byte base) {
        this.base = base;
    }

    public byte getExp() {
        return exp;
    }

    public void setExp(byte exp) {
        this.exp = exp;
    }

    public boolean isNegative() {
        return negative;
    }

    public void setNegative(boolean negative) {
        this.negative = negative;
    }

    public boolean isBinaryScale() {
        return binaryScale;
    }

    public void setBinaryScale(boolean binaryScale) {
        this.binaryScale = binaryScale;
    }
}