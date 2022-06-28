package org.telegram.messenger.secretmedia;

import android.net.Uri;
import com.google.android.exoplayer2.upstream.BaseDataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.TransferListener;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.Utilities;
/* loaded from: classes4.dex */
public final class EncryptedFileDataSource extends BaseDataSource {
    private long bytesRemaining;
    private RandomAccessFile file;
    private int fileOffset;
    private byte[] iv;
    private byte[] key;
    private boolean opened;
    private Uri uri;

    /* loaded from: classes4.dex */
    public static class EncryptedFileDataSourceException extends IOException {
        public EncryptedFileDataSourceException(IOException cause) {
            super(cause);
        }
    }

    public EncryptedFileDataSource() {
        super(false);
        this.key = new byte[32];
        this.iv = new byte[16];
    }

    @Deprecated
    public EncryptedFileDataSource(TransferListener listener) {
        this();
        if (listener != null) {
            addTransferListener(listener);
        }
    }

    @Override // com.google.android.exoplayer2.upstream.DataSource
    public long open(DataSpec dataSpec) throws EncryptedFileDataSourceException {
        try {
            this.uri = dataSpec.uri;
            File path = new File(dataSpec.uri.getPath());
            String name = path.getName();
            File internalCacheDir = FileLoader.getInternalCacheDir();
            File keyPath = new File(internalCacheDir, name + ".key");
            RandomAccessFile keyFile = new RandomAccessFile(keyPath, "r");
            keyFile.read(this.key);
            keyFile.read(this.iv);
            keyFile.close();
            RandomAccessFile randomAccessFile = new RandomAccessFile(path, "r");
            this.file = randomAccessFile;
            randomAccessFile.seek(dataSpec.position);
            this.fileOffset = (int) dataSpec.position;
            long length = dataSpec.length == -1 ? this.file.length() - dataSpec.position : dataSpec.length;
            this.bytesRemaining = length;
            if (length < 0) {
                throw new EOFException();
            }
            this.opened = true;
            transferStarted(dataSpec);
            return this.bytesRemaining;
        } catch (IOException e) {
            throw new EncryptedFileDataSourceException(e);
        }
    }

    @Override // com.google.android.exoplayer2.upstream.DataSource
    public int read(byte[] buffer, int offset, int readLength) throws EncryptedFileDataSourceException {
        if (readLength == 0) {
            return 0;
        }
        long j = this.bytesRemaining;
        if (j == 0) {
            return -1;
        }
        try {
            int bytesRead = this.file.read(buffer, offset, (int) Math.min(j, readLength));
            Utilities.aesCtrDecryptionByteArray(buffer, this.key, this.iv, offset, bytesRead, this.fileOffset);
            this.fileOffset += bytesRead;
            if (bytesRead > 0) {
                this.bytesRemaining -= bytesRead;
                bytesTransferred(bytesRead);
            }
            return bytesRead;
        } catch (IOException e) {
            throw new EncryptedFileDataSourceException(e);
        }
    }

    @Override // com.google.android.exoplayer2.upstream.DataSource
    public Uri getUri() {
        return this.uri;
    }

    @Override // com.google.android.exoplayer2.upstream.DataSource
    public void close() throws EncryptedFileDataSourceException {
        this.uri = null;
        this.fileOffset = 0;
        try {
            try {
                RandomAccessFile randomAccessFile = this.file;
                if (randomAccessFile != null) {
                    randomAccessFile.close();
                }
            } catch (IOException e) {
                throw new EncryptedFileDataSourceException(e);
            }
        } finally {
            this.file = null;
            if (this.opened) {
                this.opened = false;
                transferEnded();
            }
        }
    }
}
