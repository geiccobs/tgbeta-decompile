package org.telegram.messenger.audioinfo.util;

import java.io.IOException;
import java.io.InputStream;
/* loaded from: classes4.dex */
public class RangeInputStream extends PositionInputStream {
    private final long endPosition;

    public RangeInputStream(InputStream delegate, long position, long length) throws IOException {
        super(delegate, position);
        this.endPosition = position + length;
    }

    public long getRemainingLength() {
        return this.endPosition - getPosition();
    }

    @Override // org.telegram.messenger.audioinfo.util.PositionInputStream, java.io.FilterInputStream, java.io.InputStream
    public int read() throws IOException {
        if (getPosition() == this.endPosition) {
            return -1;
        }
        return super.read();
    }

    @Override // org.telegram.messenger.audioinfo.util.PositionInputStream, java.io.FilterInputStream, java.io.InputStream
    public int read(byte[] b, int off, int len) throws IOException {
        long position = getPosition() + len;
        long j = this.endPosition;
        if (position > j && (len = (int) (j - getPosition())) == 0) {
            return -1;
        }
        return super.read(b, off, len);
    }

    @Override // org.telegram.messenger.audioinfo.util.PositionInputStream, java.io.FilterInputStream, java.io.InputStream
    public long skip(long n) throws IOException {
        long j = this.endPosition;
        if (getPosition() + n > j) {
            n = (int) (j - getPosition());
        }
        return super.skip(n);
    }
}
