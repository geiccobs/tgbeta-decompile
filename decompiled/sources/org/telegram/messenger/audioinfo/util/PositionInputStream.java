package org.telegram.messenger.audioinfo.util;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
/* loaded from: classes4.dex */
public class PositionInputStream extends FilterInputStream {
    private long position;
    private long positionMark;

    public PositionInputStream(InputStream delegate) {
        this(delegate, 0L);
    }

    public PositionInputStream(InputStream delegate, long position) {
        super(delegate);
        this.position = position;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public synchronized void mark(int readlimit) {
        this.positionMark = this.position;
        super.mark(readlimit);
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public synchronized void reset() throws IOException {
        super.reset();
        this.position = this.positionMark;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int read() throws IOException {
        int data = super.read();
        if (data >= 0) {
            this.position++;
        }
        return data;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int read(byte[] b, int off, int len) throws IOException {
        long p = this.position;
        int read = super.read(b, off, len);
        if (read > 0) {
            this.position = read + p;
        }
        return read;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public final int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public long skip(long n) throws IOException {
        long p = this.position;
        long skipped = super.skip(n);
        this.position = p + skipped;
        return skipped;
    }

    public long getPosition() {
        return this.position;
    }
}
