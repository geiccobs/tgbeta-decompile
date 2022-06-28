package com.google.firebase.encoders.proto;

import java.io.OutputStream;
/* loaded from: classes3.dex */
final class LengthCountingOutputStream extends OutputStream {
    private long length = 0;

    @Override // java.io.OutputStream
    public void write(int b) {
        this.length++;
    }

    @Override // java.io.OutputStream
    public void write(byte[] b) {
        this.length += b.length;
    }

    @Override // java.io.OutputStream
    public void write(byte[] b, int off, int len) {
        if (off < 0 || off > b.length || len < 0 || off + len > b.length || off + len < 0) {
            throw new IndexOutOfBoundsException();
        }
        this.length += len;
    }

    public long getLength() {
        return this.length;
    }
}
