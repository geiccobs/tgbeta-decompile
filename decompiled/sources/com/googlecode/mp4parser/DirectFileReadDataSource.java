package com.googlecode.mp4parser;

import com.googlecode.mp4parser.util.CastUtils;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
/* loaded from: classes3.dex */
public class DirectFileReadDataSource implements DataSource {
    private static final int TRANSFER_SIZE = 8192;
    private String filename;
    private RandomAccessFile raf;

    public DirectFileReadDataSource(File f) throws IOException {
        this.raf = new RandomAccessFile(f, "r");
        this.filename = f.getName();
    }

    @Override // com.googlecode.mp4parser.DataSource
    public int read(ByteBuffer byteBuffer) throws IOException {
        int len = byteBuffer.remaining();
        int totalRead = 0;
        int bytesRead = 0;
        byte[] buf = new byte[8192];
        while (totalRead < len) {
            int bytesToRead = Math.min(len - totalRead, 8192);
            bytesRead = this.raf.read(buf, 0, bytesToRead);
            if (bytesRead < 0) {
                break;
            }
            totalRead += bytesRead;
            byteBuffer.put(buf, 0, bytesRead);
        }
        if (bytesRead >= 0 || totalRead != 0) {
            return totalRead;
        }
        return -1;
    }

    public int readAllInOnce(ByteBuffer byteBuffer) throws IOException {
        byte[] buf = new byte[byteBuffer.remaining()];
        int read = this.raf.read(buf);
        byteBuffer.put(buf, 0, read);
        return read;
    }

    @Override // com.googlecode.mp4parser.DataSource
    public long size() throws IOException {
        return this.raf.length();
    }

    @Override // com.googlecode.mp4parser.DataSource
    public long position() throws IOException {
        return this.raf.getFilePointer();
    }

    @Override // com.googlecode.mp4parser.DataSource
    public void position(long nuPos) throws IOException {
        this.raf.seek(nuPos);
    }

    @Override // com.googlecode.mp4parser.DataSource
    public long transferTo(long position, long count, WritableByteChannel target) throws IOException {
        return target.write(map(position, count));
    }

    @Override // com.googlecode.mp4parser.DataSource
    public ByteBuffer map(long startPosition, long size) throws IOException {
        this.raf.seek(startPosition);
        byte[] payload = new byte[CastUtils.l2i(size)];
        this.raf.readFully(payload);
        return ByteBuffer.wrap(payload);
    }

    @Override // com.googlecode.mp4parser.DataSource, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.raf.close();
    }

    public String toString() {
        return this.filename;
    }
}
