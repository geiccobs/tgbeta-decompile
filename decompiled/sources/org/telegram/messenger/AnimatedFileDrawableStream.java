package org.telegram.messenger;

import java.util.concurrent.CountDownLatch;
import org.telegram.tgnet.TLRPC;
/* loaded from: classes4.dex */
public class AnimatedFileDrawableStream implements FileLoadOperationStream {
    private volatile boolean canceled;
    private CountDownLatch countDownLatch;
    private int currentAccount;
    private TLRPC.Document document;
    private String finishedFilePath;
    private boolean finishedLoadingFile;
    private boolean ignored;
    private long lastOffset;
    private FileLoadOperation loadOperation;
    private ImageLocation location;
    private Object parentObject;
    private boolean preview;
    private final Object sync = new Object();
    private boolean waitingForLoad;

    public AnimatedFileDrawableStream(TLRPC.Document d, ImageLocation l, Object p, int a, boolean prev) {
        this.document = d;
        this.location = l;
        this.parentObject = p;
        this.currentAccount = a;
        this.preview = prev;
        this.loadOperation = FileLoader.getInstance(a).loadStreamFile(this, this.document, this.location, this.parentObject, 0, this.preview);
    }

    public boolean isFinishedLoadingFile() {
        return this.finishedLoadingFile;
    }

    public String getFinishedFilePath() {
        return this.finishedFilePath;
    }

    public int read(int offset, int readLength) {
        long[] result;
        long availableLength;
        synchronized (this.sync) {
            if (this.canceled) {
                return 0;
            }
            if (readLength == 0) {
                return 0;
            }
            long availableLength2 = 0;
            while (availableLength2 == 0) {
                try {
                    result = this.loadOperation.getDownloadedLengthFromOffset(offset, readLength);
                    availableLength = result[0];
                } catch (Exception e) {
                    e = e;
                }
                try {
                    if (!this.finishedLoadingFile && result[1] != 0) {
                        this.finishedLoadingFile = true;
                        this.finishedFilePath = this.loadOperation.getCacheFileFinal().getAbsolutePath();
                    }
                    if (availableLength == 0) {
                        if (this.loadOperation.isPaused() || this.lastOffset != offset || this.preview) {
                            FileLoader.getInstance(this.currentAccount).loadStreamFile(this, this.document, this.location, this.parentObject, offset, this.preview);
                        }
                        synchronized (this.sync) {
                            if (this.canceled) {
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
                    availableLength2 = availableLength;
                } catch (Exception e2) {
                    e = e2;
                    availableLength2 = availableLength;
                    FileLog.e((Throwable) e, false);
                    return (int) availableLength2;
                }
            }
            this.lastOffset = offset + availableLength2;
            return (int) availableLength2;
        }
    }

    public void cancel() {
        cancel(true);
    }

    public void cancel(boolean removeLoading) {
        synchronized (this.sync) {
            CountDownLatch countDownLatch = this.countDownLatch;
            if (countDownLatch != null) {
                countDownLatch.countDown();
                if (removeLoading && !this.canceled && !this.preview) {
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

    public TLRPC.Document getDocument() {
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
