package com.googlecode.mp4parser;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
/* loaded from: classes.dex */
public interface DataSource extends Closeable {
    @Override // java.io.Closeable, java.lang.AutoCloseable
    void close() throws IOException;

    ByteBuffer map(long j, long j2) throws IOException;

    long position() throws IOException;

    void position(long j) throws IOException;

    long transferTo(long j, long j2, WritableByteChannel writableByteChannel) throws IOException;
}
