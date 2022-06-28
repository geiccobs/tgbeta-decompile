package org.telegram.messenger;

import android.net.Uri;
import com.google.android.exoplayer2.upstream.BaseDataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.microsoft.appcenter.ingestion.models.CommonProperties;
import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.CountDownLatch;
import org.telegram.tgnet.TLRPC;
/* loaded from: classes4.dex */
public class FileStreamLoadOperation extends BaseDataSource implements FileLoadOperationStream {
    private long bytesRemaining;
    private CountDownLatch countDownLatch;
    private int currentAccount;
    private int currentOffset;
    private TLRPC.Document document;
    private RandomAccessFile file;
    private FileLoadOperation loadOperation;
    private boolean opened;
    private Object parentObject;
    private Uri uri;

    public FileStreamLoadOperation() {
        super(false);
    }

    @Deprecated
    public FileStreamLoadOperation(TransferListener listener) {
        this();
        if (listener != null) {
            addTransferListener(listener);
        }
    }

    @Override // com.google.android.exoplayer2.upstream.DataSource
    public long open(DataSpec dataSpec) throws IOException {
        Uri uri = dataSpec.uri;
        this.uri = uri;
        int intValue = Utilities.parseInt((CharSequence) uri.getQueryParameter("account")).intValue();
        this.currentAccount = intValue;
        this.parentObject = FileLoader.getInstance(intValue).getParentObject(Utilities.parseInt((CharSequence) this.uri.getQueryParameter("rid")).intValue());
        TLRPC.TL_document tL_document = new TLRPC.TL_document();
        this.document = tL_document;
        tL_document.access_hash = Utilities.parseLong(this.uri.getQueryParameter("hash")).longValue();
        this.document.id = Utilities.parseLong(this.uri.getQueryParameter("id")).longValue();
        this.document.size = Utilities.parseLong(this.uri.getQueryParameter("size")).longValue();
        this.document.dc_id = Utilities.parseInt((CharSequence) this.uri.getQueryParameter("dc")).intValue();
        this.document.mime_type = this.uri.getQueryParameter("mime");
        this.document.file_reference = Utilities.hexToBytes(this.uri.getQueryParameter("reference"));
        TLRPC.TL_documentAttributeFilename filename = new TLRPC.TL_documentAttributeFilename();
        filename.file_name = this.uri.getQueryParameter(CommonProperties.NAME);
        this.document.attributes.add(filename);
        if (this.document.mime_type.startsWith("video")) {
            this.document.attributes.add(new TLRPC.TL_documentAttributeVideo());
        } else if (this.document.mime_type.startsWith("audio")) {
            this.document.attributes.add(new TLRPC.TL_documentAttributeAudio());
        }
        FileLoader fileLoader = FileLoader.getInstance(this.currentAccount);
        TLRPC.Document document = this.document;
        Object obj = this.parentObject;
        int i = (int) dataSpec.position;
        this.currentOffset = i;
        this.loadOperation = fileLoader.loadStreamFile(this, document, null, obj, i, false);
        long j = dataSpec.length == -1 ? this.document.size - dataSpec.position : dataSpec.length;
        this.bytesRemaining = j;
        if (j < 0) {
            throw new EOFException();
        }
        this.opened = true;
        transferStarted(dataSpec);
        if (this.loadOperation != null) {
            RandomAccessFile randomAccessFile = new RandomAccessFile(this.loadOperation.getCurrentFile(), "r");
            this.file = randomAccessFile;
            randomAccessFile.seek(this.currentOffset);
        }
        return this.bytesRemaining;
    }

    @Override // com.google.android.exoplayer2.upstream.DataSource
    public int read(byte[] buffer, int offset, int readLength) throws IOException {
        if (readLength == 0) {
            return 0;
        }
        long j = this.bytesRemaining;
        if (j == 0) {
            return -1;
        }
        int availableLength = 0;
        if (j < readLength) {
            readLength = (int) j;
        }
        while (availableLength == 0) {
            try {
                if (!this.opened) {
                    break;
                }
                availableLength = (int) this.loadOperation.getDownloadedLengthFromOffset(this.currentOffset, readLength)[0];
                if (availableLength == 0) {
                    FileLoader.getInstance(this.currentAccount).loadStreamFile(this, this.document, null, this.parentObject, this.currentOffset, false);
                    CountDownLatch countDownLatch = new CountDownLatch(1);
                    this.countDownLatch = countDownLatch;
                    countDownLatch.await();
                }
            } catch (Exception e) {
                throw new IOException(e);
            }
        }
        if (!this.opened) {
            return 0;
        }
        this.file.readFully(buffer, offset, availableLength);
        this.currentOffset += availableLength;
        this.bytesRemaining -= availableLength;
        bytesTransferred(availableLength);
        return availableLength;
    }

    @Override // com.google.android.exoplayer2.upstream.DataSource
    public Uri getUri() {
        return this.uri;
    }

    @Override // com.google.android.exoplayer2.upstream.DataSource
    public void close() {
        FileLoadOperation fileLoadOperation = this.loadOperation;
        if (fileLoadOperation != null) {
            fileLoadOperation.removeStreamListener(this);
        }
        RandomAccessFile randomAccessFile = this.file;
        if (randomAccessFile != null) {
            try {
                randomAccessFile.close();
            } catch (Exception e) {
                FileLog.e(e);
            }
            this.file = null;
        }
        this.uri = null;
        if (this.opened) {
            this.opened = false;
            transferEnded();
        }
        CountDownLatch countDownLatch = this.countDownLatch;
        if (countDownLatch != null) {
            countDownLatch.countDown();
        }
    }

    @Override // org.telegram.messenger.FileLoadOperationStream
    public void newDataAvailable() {
        CountDownLatch countDownLatch = this.countDownLatch;
        if (countDownLatch != null) {
            countDownLatch.countDown();
        }
    }
}
