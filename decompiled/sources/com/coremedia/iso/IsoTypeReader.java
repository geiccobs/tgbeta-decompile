package com.coremedia.iso;

import androidx.core.view.MotionEventCompat;
import com.google.android.exoplayer2.C;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import org.telegram.tgnet.ConnectionsManager;
/* loaded from: classes3.dex */
public final class IsoTypeReader {
    public static long readUInt32BE(ByteBuffer bb) {
        long ch1 = readUInt8(bb);
        long ch2 = readUInt8(bb);
        long ch3 = readUInt8(bb);
        long ch4 = readUInt8(bb);
        return (ch4 << 24) + (ch3 << 16) + (ch2 << 8) + (ch1 << 0);
    }

    public static long readUInt32(ByteBuffer bb) {
        long i = bb.getInt();
        if (i < 0) {
            return i + 4294967296L;
        }
        return i;
    }

    public static int readUInt24(ByteBuffer bb) {
        int result = 0 + (readUInt16(bb) << 8);
        return result + byte2int(bb.get());
    }

    public static int readUInt16(ByteBuffer bb) {
        int result = 0 + (byte2int(bb.get()) << 8);
        return result + byte2int(bb.get());
    }

    public static int readUInt16BE(ByteBuffer bb) {
        int result = 0 + byte2int(bb.get());
        return result + (byte2int(bb.get()) << 8);
    }

    public static int readUInt8(ByteBuffer bb) {
        return byte2int(bb.get());
    }

    public static int byte2int(byte b) {
        return b < 0 ? b + ConnectionsManager.USE_IPV4_ONLY : b;
    }

    public static String readString(ByteBuffer byteBuffer) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        while (true) {
            int read = byteBuffer.get();
            if (read != 0) {
                out.write(read);
            } else {
                return Utf8.convert(out.toByteArray());
            }
        }
    }

    public static String readString(ByteBuffer byteBuffer, int length) {
        byte[] buffer = new byte[length];
        byteBuffer.get(buffer);
        return Utf8.convert(buffer);
    }

    public static long readUInt64(ByteBuffer byteBuffer) {
        long result = 0 + (readUInt32(byteBuffer) << 32);
        if (result < 0) {
            throw new RuntimeException("I don't know how to deal with UInt64! long is not sufficient and I don't want to use BigInt");
        }
        return result + readUInt32(byteBuffer);
    }

    public static double readFixedPoint1616(ByteBuffer bb) {
        byte[] bytes = new byte[4];
        bb.get(bytes);
        int result = 0 | ((bytes[0] << 24) & (-16777216));
        double d = result | ((bytes[1] << 16) & 16711680) | ((bytes[2] << 8) & MotionEventCompat.ACTION_POINTER_INDEX_MASK) | (bytes[3] & 255);
        Double.isNaN(d);
        return d / 65536.0d;
    }

    public static double readFixedPoint0230(ByteBuffer bb) {
        byte[] bytes = new byte[4];
        bb.get(bytes);
        int result = 0 | ((bytes[0] << 24) & (-16777216));
        double d = result | ((bytes[1] << 16) & 16711680) | ((bytes[2] << 8) & MotionEventCompat.ACTION_POINTER_INDEX_MASK) | (bytes[3] & 255);
        Double.isNaN(d);
        return d / 1.073741824E9d;
    }

    public static float readFixedPoint88(ByteBuffer bb) {
        byte[] bytes = new byte[2];
        bb.get(bytes);
        short result = (short) (((bytes[0] << 8) & MotionEventCompat.ACTION_POINTER_INDEX_MASK) | 0);
        return ((short) ((bytes[1] & 255) | result)) / 256.0f;
    }

    public static String readIso639(ByteBuffer bb) {
        int bits = readUInt16(bb);
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            int c = (bits >> ((2 - i) * 5)) & 31;
            result.append((char) (c + 96));
        }
        return result.toString();
    }

    public static String read4cc(ByteBuffer bb) {
        byte[] codeBytes = new byte[4];
        bb.get(codeBytes);
        try {
            return new String(codeBytes, C.ISO88591_NAME);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static long readUInt48(ByteBuffer byteBuffer) {
        long result = readUInt16(byteBuffer) << 32;
        if (result < 0) {
            throw new RuntimeException("I don't know how to deal with UInt64! long is not sufficient and I don't want to use BigInt");
        }
        return result + readUInt32(byteBuffer);
    }
}
