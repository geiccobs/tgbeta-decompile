package com.google.android.exoplayer2.upstream.cache;

import com.google.android.exoplayer2.upstream.DataSink;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.ReusableBufferedOutputStream;
import com.google.android.exoplayer2.util.Util;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
/* loaded from: classes3.dex */
public final class CacheDataSink implements DataSink {
    public static final int DEFAULT_BUFFER_SIZE = 20480;
    public static final long DEFAULT_FRAGMENT_SIZE = 5242880;
    private static final long MIN_RECOMMENDED_FRAGMENT_SIZE = 2097152;
    private static final String TAG = "CacheDataSink";
    private final int bufferSize;
    private ReusableBufferedOutputStream bufferedOutputStream;
    private final Cache cache;
    private DataSpec dataSpec;
    private long dataSpecBytesWritten;
    private long dataSpecFragmentSize;
    private File file;
    private final long fragmentSize;
    private OutputStream outputStream;
    private long outputStreamBytesWritten;

    /* loaded from: classes3.dex */
    public static class CacheDataSinkException extends Cache.CacheException {
        public CacheDataSinkException(IOException cause) {
            super(cause);
        }
    }

    public CacheDataSink(Cache cache, long fragmentSize) {
        this(cache, fragmentSize, DEFAULT_BUFFER_SIZE);
    }

    public CacheDataSink(Cache cache, long fragmentSize, int bufferSize) {
        Assertions.checkState(fragmentSize > 0 || fragmentSize == -1, "fragmentSize must be positive or C.LENGTH_UNSET.");
        if (fragmentSize != -1 && fragmentSize < MIN_RECOMMENDED_FRAGMENT_SIZE) {
            Log.w(TAG, "fragmentSize is below the minimum recommended value of 2097152. This may cause poor cache performance.");
        }
        this.cache = (Cache) Assertions.checkNotNull(cache);
        this.fragmentSize = fragmentSize == -1 ? Long.MAX_VALUE : fragmentSize;
        this.bufferSize = bufferSize;
    }

    @Override // com.google.android.exoplayer2.upstream.DataSink
    public void open(DataSpec dataSpec) throws CacheDataSinkException {
        if (dataSpec.length == -1 && dataSpec.isFlagSet(2)) {
            this.dataSpec = null;
            return;
        }
        this.dataSpec = dataSpec;
        this.dataSpecFragmentSize = dataSpec.isFlagSet(4) ? this.fragmentSize : Long.MAX_VALUE;
        this.dataSpecBytesWritten = 0L;
        try {
            openNextOutputStream();
        } catch (IOException e) {
            throw new CacheDataSinkException(e);
        }
    }

    @Override // com.google.android.exoplayer2.upstream.DataSink
    public void write(byte[] buffer, int offset, int length) throws CacheDataSinkException {
        if (this.dataSpec == null) {
            return;
        }
        int bytesWritten = 0;
        while (bytesWritten < length) {
            try {
                if (this.outputStreamBytesWritten == this.dataSpecFragmentSize) {
                    closeCurrentOutputStream();
                    openNextOutputStream();
                }
                int bytesToWrite = (int) Math.min(length - bytesWritten, this.dataSpecFragmentSize - this.outputStreamBytesWritten);
                this.outputStream.write(buffer, offset + bytesWritten, bytesToWrite);
                bytesWritten += bytesToWrite;
                this.outputStreamBytesWritten += bytesToWrite;
                this.dataSpecBytesWritten += bytesToWrite;
            } catch (IOException e) {
                throw new CacheDataSinkException(e);
            }
        }
    }

    @Override // com.google.android.exoplayer2.upstream.DataSink
    public void close() throws CacheDataSinkException {
        if (this.dataSpec == null) {
            return;
        }
        try {
            closeCurrentOutputStream();
        } catch (IOException e) {
            throw new CacheDataSinkException(e);
        }
    }

    private void openNextOutputStream() throws IOException {
        long length;
        if (this.dataSpec.length == -1) {
            length = -1;
        } else {
            length = Math.min(this.dataSpec.length - this.dataSpecBytesWritten, this.dataSpecFragmentSize);
        }
        this.file = this.cache.startFile(this.dataSpec.key, this.dataSpec.absoluteStreamPosition + this.dataSpecBytesWritten, length);
        FileOutputStream underlyingFileOutputStream = new FileOutputStream(this.file);
        if (this.bufferSize > 0) {
            ReusableBufferedOutputStream reusableBufferedOutputStream = this.bufferedOutputStream;
            if (reusableBufferedOutputStream == null) {
                this.bufferedOutputStream = new ReusableBufferedOutputStream(underlyingFileOutputStream, this.bufferSize);
            } else {
                reusableBufferedOutputStream.reset(underlyingFileOutputStream);
            }
            this.outputStream = this.bufferedOutputStream;
        } else {
            this.outputStream = underlyingFileOutputStream;
        }
        this.outputStreamBytesWritten = 0L;
    }

    private void closeCurrentOutputStream() throws IOException {
        OutputStream outputStream = this.outputStream;
        if (outputStream == null) {
            return;
        }
        try {
            outputStream.flush();
            Util.closeQuietly(this.outputStream);
            this.outputStream = null;
            File fileToCommit = this.file;
            this.file = null;
            if (1 != 0) {
                this.cache.commitFile(fileToCommit, this.outputStreamBytesWritten);
            } else {
                fileToCommit.delete();
            }
        } catch (Throwable th) {
            Util.closeQuietly(this.outputStream);
            this.outputStream = null;
            File fileToCommit2 = this.file;
            this.file = null;
            if (0 != 0) {
                this.cache.commitFile(fileToCommit2, this.outputStreamBytesWritten);
            } else {
                fileToCommit2.delete();
            }
            throw th;
        }
    }
}
