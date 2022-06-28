package com.googlecode.mp4parser.h264.read;

import com.google.android.exoplayer2.metadata.icy.IcyHeaders;
import com.googlecode.mp4parser.h264.BTree;
import com.googlecode.mp4parser.h264.Debug;
import java.io.IOException;
import java.io.InputStream;
/* loaded from: classes3.dex */
public class CAVLCReader extends BitstreamReader {
    public CAVLCReader(InputStream is) throws IOException {
        super(is);
    }

    public long readNBit(int n, String message) throws IOException {
        long val = readNBit(n);
        trace(message, String.valueOf(val));
        return val;
    }

    private int readUE() throws IOException {
        int cnt = 0;
        while (read1Bit() == 0) {
            cnt++;
        }
        if (cnt <= 0) {
            return 0;
        }
        long val = readNBit(cnt);
        int res = (int) (((1 << cnt) - 1) + val);
        return res;
    }

    public int readUE(String message) throws IOException {
        int res = readUE();
        trace(message, String.valueOf(res));
        return res;
    }

    public int readSE(String message) throws IOException {
        int val = readUE();
        int sign = ((val & 1) << 1) - 1;
        int val2 = ((val >> 1) + (val & 1)) * sign;
        trace(message, String.valueOf(val2));
        return val2;
    }

    public boolean readBool(String message) throws IOException {
        boolean res = read1Bit() != 0;
        trace(message, res ? IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE : "0");
        return res;
    }

    public int readU(int i, String string) throws IOException {
        return (int) readNBit(i, string);
    }

    public byte[] read(int payloadSize) throws IOException {
        byte[] result = new byte[payloadSize];
        for (int i = 0; i < payloadSize; i++) {
            result[i] = (byte) readByte();
        }
        return result;
    }

    public boolean readAE() {
        throw new UnsupportedOperationException("Stan");
    }

    public int readTE(int max) throws IOException {
        if (max <= 1) {
            return 1 & (read1Bit() ^ (-1));
        }
        return readUE();
    }

    public int readAEI() {
        throw new UnsupportedOperationException("Stan");
    }

    public int readME(String string) throws IOException {
        return readUE(string);
    }

    public Object readCE(BTree bt, String message) throws IOException {
        Object i;
        do {
            int bit = read1Bit();
            bt = bt.down(bit);
            if (bt == null) {
                throw new RuntimeException("Illegal code");
            }
            i = bt.getValue();
        } while (i == null);
        trace(message, i.toString());
        return i;
    }

    public int readZeroBitCount(String message) throws IOException {
        int count = 0;
        while (read1Bit() == 0) {
            count++;
        }
        trace(message, String.valueOf(count));
        return count;
    }

    public void readTrailingBits() throws IOException {
        read1Bit();
        readRemainingByte();
    }

    private void trace(String message, String val) {
        StringBuilder traceBuilder = new StringBuilder();
        String pos = String.valueOf(BitstreamReader.bitsRead - this.debugBits.length());
        int spaces = 8 - pos.length();
        traceBuilder.append("@" + pos);
        for (int i = 0; i < spaces; i++) {
            traceBuilder.append(' ');
        }
        traceBuilder.append(message);
        int spaces2 = (100 - traceBuilder.length()) - this.debugBits.length();
        for (int i2 = 0; i2 < spaces2; i2++) {
            traceBuilder.append(' ');
        }
        traceBuilder.append(this.debugBits);
        traceBuilder.append(" (" + val + ")");
        this.debugBits.clear();
        Debug.println(traceBuilder.toString());
    }
}
