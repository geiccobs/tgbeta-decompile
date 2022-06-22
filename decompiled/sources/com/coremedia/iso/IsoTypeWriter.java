package com.coremedia.iso;

import java.nio.ByteBuffer;
/* loaded from: classes.dex */
public final class IsoTypeWriter {
    public static void writeUInt64(ByteBuffer byteBuffer, long j) {
        byteBuffer.putLong(j);
    }

    public static void writeUInt32(ByteBuffer byteBuffer, long j) {
        byteBuffer.putInt((int) j);
    }

    public static void writeUInt24(ByteBuffer byteBuffer, int i) {
        int i2 = i & 16777215;
        writeUInt16(byteBuffer, i2 >> 8);
        writeUInt8(byteBuffer, i2);
    }

    public static void writeUInt16(ByteBuffer byteBuffer, int i) {
        int i2 = i & 65535;
        writeUInt8(byteBuffer, i2 >> 8);
        writeUInt8(byteBuffer, i2 & 255);
    }

    public static void writeUInt8(ByteBuffer byteBuffer, int i) {
        byteBuffer.put((byte) (i & 255));
    }

    public static void writeFixedPoint1616(ByteBuffer byteBuffer, double d) {
        int i = (int) (d * 65536.0d);
        byteBuffer.put((byte) (((-16777216) & i) >> 24));
        byteBuffer.put((byte) ((16711680 & i) >> 16));
        byteBuffer.put((byte) ((65280 & i) >> 8));
        byteBuffer.put((byte) (i & 255));
    }

    public static void writeFixedPoint0230(ByteBuffer byteBuffer, double d) {
        int i = (int) (d * 1.073741824E9d);
        byteBuffer.put((byte) (((-16777216) & i) >> 24));
        byteBuffer.put((byte) ((16711680 & i) >> 16));
        byteBuffer.put((byte) ((65280 & i) >> 8));
        byteBuffer.put((byte) (i & 255));
    }

    public static void writeFixedPoint88(ByteBuffer byteBuffer, double d) {
        short s = (short) (d * 256.0d);
        byteBuffer.put((byte) ((65280 & s) >> 8));
        byteBuffer.put((byte) (s & 255));
    }

    public static void writeIso639(ByteBuffer byteBuffer, String str) {
        if (str.getBytes().length != 3) {
            throw new IllegalArgumentException("\"" + str + "\" language string isn't exactly 3 characters long!");
        }
        int i = 0;
        for (int i2 = 0; i2 < 3; i2++) {
            i += (str.getBytes()[i2] - 96) << ((2 - i2) * 5);
        }
        writeUInt16(byteBuffer, i);
    }

    public static void writeUtf8String(ByteBuffer byteBuffer, String str) {
        byteBuffer.put(Utf8.convert(str));
        writeUInt8(byteBuffer, 0);
    }
}
