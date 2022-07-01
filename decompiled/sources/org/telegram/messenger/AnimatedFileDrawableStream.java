package org.telegram.messenger;

import java.util.concurrent.CountDownLatch;
import org.telegram.tgnet.TLRPC$Document;
/* loaded from: classes.dex */
public class AnimatedFileDrawableStream implements FileLoadOperationStream {
    private volatile boolean canceled;
    private CountDownLatch countDownLatch;
    private int currentAccount;
    private TLRPC$Document document;
    private String finishedFilePath;
    private boolean finishedLoadingFile;
    private long lastOffset;
    private final FileLoadOperation loadOperation;
    private ImageLocation location;
    private Object parentObject;
    private boolean preview;
    private final Object sync = new Object();
    private boolean waitingForLoad;

    public AnimatedFileDrawableStream(TLRPC$Document tLRPC$Document, ImageLocation imageLocation, Object obj, int i, boolean z) {
        this.document = tLRPC$Document;
        this.location = imageLocation;
        this.parentObject = obj;
        this.currentAccount = i;
        this.preview = z;
        this.loadOperation = FileLoader.getInstance(i).loadStreamFile(this, this.document, this.location, this.parentObject, 0, this.preview);
    }

    public boolean isFinishedLoadingFile() {
        return this.finishedLoadingFile;
    }

    public String getFinishedFilePath() {
        return this.finishedFilePath;
    }

    public int read(int i, int i2) {
        long[] downloadedLengthFromOffset;
        long j;
        synchronized (this.sync) {
            if (this.canceled) {
                return 0;
            }
            if (i2 == 0) {
                return 0;
            }
            long j2 = 0;
            while (j2 == 0) {
                try {
                    downloadedLengthFromOffset = this.loadOperation.getDownloadedLengthFromOffset(i, i2);
                    j = downloadedLengthFromOffset[0];
                } catch (Exception e) {
                    e = e;
                }
                try {
                    if (!this.finishedLoadingFile && downloadedLengthFromOffset[1] != 0) {
                        this.finishedLoadingFile = true;
                        this.finishedFilePath = this.loadOperation.getCacheFileFinal().getAbsolutePath();
                    }
                    if (j == 0) {
                        if (this.loadOperation.isPaused() || this.lastOffset != i || this.preview) {
                            FileLoader.getInstance(this.currentAccount).loadStreamFile(this, this.document, this.location, this.parentObject, i, this.preview);
                        }
                        synchronized (this.sync) {
                            if (this.canceled) {
                                FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.document);
                                return 0;
                            }
                            this.countDownLatch = new CountDownLatch(1);
                        }
                        if (!this.preview) {
                            FileLoader.getInstance(this.currentAccount).setLoadingVideo(this.document, false, true);
                        }
                        this.waitingForLoad = true;
                        this.countDownLatch.await();
                        this.waitingForLoad = false;
                    }
                    j2 = j;
                } catch (Exception e2) {
                    e = e2;
                    j2 = j;
                    FileLog.e((Throwable) e, false);
                    return (int) j2;
                }
            }
            this.lastOffset = i + j2;
            return (int) j2;
        }
    }

    public void cancel() {
        cancel(true);
    }

    public void cancel(boolean z) {
        synchronized (this.sync) {
            CountDownLatch countDownLatch = this.countDownLatch;
            if (countDownLatch != null) {
                countDownLatch.countDown();
                if (z && !this.canceled && !this.preview) {
                    FileLoader.getInstance(this.currentAccount).removeLoadingVideo(this.document, false, true);
                }
            }
            this.canceled = true;
        }
    }

    public void reset() {
        synchronized (this.sync) {
            this.canceled = false;
        }
    }

    public TLRPC$Document getDocument() {
        return this.document;
    }

    public ImageLocation getLocation() {
        return this.location;
    }

    public Object getParentObject() {
        return this.document;
    }

    public boolean isPreview() {
        return this.preview;
    }

    public int getCurrentAccount() {
        return this.currentAccount;
    }

    public boolean isWaitingForLoad() {
        return this.waitingForLoad;
    }

    @Override // org.telegram.messenger.FileLoadOperationStream
    public void newDataAvailable() {
        CountDownLatch countDownLatch = this.countDownLatch;
        if (countDownLatch != null) {
            countDownLatch.countDown();
        }
    }
}
