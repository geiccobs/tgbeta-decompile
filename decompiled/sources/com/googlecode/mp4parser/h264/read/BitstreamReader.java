package com.googlecode.mp4parser.h264.read;

import com.googlecode.mp4parser.h264.CharCache;
import java.io.IOException;
import java.io.InputStream;
/* loaded from: classes3.dex */
public class BitstreamReader {
    protected static int bitsRead;
    private int curByte;
    protected CharCache debugBits = new CharCache(50);
    private InputStream is;
    int nBit;
    private int nextByte;

    public BitstreamReader(InputStream is) throws IOException {
        this.is = is;
        this.curByte = is.read();
        this.nextByte = is.read();
    }

    public boolean readBool() throws IOException {
        return read1Bit() == 1;
    }

    public int read1Bit() throws IOException {
        if (this.nBit == 8) {
            advance();
            if (this.curByte == -1) {
                return -1;
            }
        }
        int i = this.curByte;
        int i2 = this.nBit;
        int res = (i >> (7 - i2)) & 1;
        this.nBit = i2 + 1;
        this.debugBits.append(res == 0 ? '0' : '1');
        bitsRead++;
        return res;
    }

    public long readNBit(int n) throws IOException {
        if (n > 64) {
            throw new IllegalArgumentException("Can not readByte more then 64 bit");
        }
        long val = 0;
        for (int i = 0; i < n; i++) {
            val = (val << 1) | read1Bit();
        }
        return val;
    }

    private void advance() throws IOException {
        this.curByte = this.nextByte;
        this.nextByte = this.is.read();
        this.nBit = 0;
    }

    public int readByte() throws IOException {
        if (this.nBit > 0) {
            advance();
        }
        int res = this.curByte;
        advance();
        return res;
    }

    public boolean moreRBSPData() throws IOException {
        if (this.nBit == 8) {
            advance();
        }
        int tail = 1 << ((8 - this.nBit) - 1);
        int mask = (tail << 1) - 1;
        int i = this.curByte;
        boolean hasTail = (i & mask) == tail;
        return i != -1 && (this.nextByte != -1 || !hasTail);
    }

    public long getBitPosition() {
        return (bitsRead * 8) + (this.nBit % 8);
    }

    public long readRemainingByte() throws IOException {
        return readNBit(8 - this.nBit);
    }

    public int peakNextBits(int n) throws IOException {
        if (n <= 8) {
            if (this.nBit == 8) {
                advance();
                if (this.curByte == -1) {
                    return -1;
                }
            }
            int[] bits = new int[16 - this.nBit];
            int cnt = 0;
            int i = this.nBit;
            while (i < 8) {
                bits[cnt] = (this.curByte >> (7 - i)) & 1;
                i++;
                cnt++;
            }
            int i2 = 0;
            while (i2 < 8) {
                bits[cnt] = (this.nextByte >> (7 - i2)) & 1;
                i2++;
                cnt++;
            }
            int result = 0;
            for (int i3 = 0; i3 < n; i3++) {
                result = (result << 1) | bits[i3];
            }
            return result;
        }
        throw new IllegalArgumentException("N should be less then 8");
    }

    public boolean isByteAligned() {
        return this.nBit % 8 == 0;
    }

    public void close() throws IOException {
    }

    public int getCurBit() {
        return this.nBit;
    }
}
