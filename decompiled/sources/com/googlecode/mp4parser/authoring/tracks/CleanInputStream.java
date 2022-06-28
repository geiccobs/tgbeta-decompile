package com.googlecode.mp4parser.authoring.tracks;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
/* loaded from: classes3.dex */
public class CleanInputStream extends FilterInputStream {
    int prevprev = -1;
    int prev = -1;

    public CleanInputStream(InputStream in) {
        super(in);
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public boolean markSupported() {
        return false;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int read() throws IOException {
        int c = super.read();
        if (c == 3 && this.prevprev == 0 && this.prev == 0) {
            this.prevprev = -1;
            this.prev = -1;
            c = super.read();
        }
        this.prevprev = this.prev;
        this.prev = c;
        return c;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int read(byte[] b, int off, int len) throws IOException {
        if (b == null) {
            throw new NullPointerException();
        }
        if (off < 0 || len < 0 || len > b.length - off) {
            throw new IndexOutOfBoundsException();
        }
        if (len == 0) {
            return 0;
        }
        int c = read();
        if (c == -1) {
            return -1;
        }
        b[off] = (byte) c;
        int i = 1;
        while (i < len) {
            try {
                int c2 = read();
                if (c2 == -1) {
                    break;
                }
                b[off + i] = (byte) c2;
                i++;
            } catch (IOException e) {
            }
        }
        return i;
    }
}
