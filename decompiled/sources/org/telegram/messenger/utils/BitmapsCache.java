package org.telegram.messenger.utils;

import android.graphics.Bitmap;
import j$.util.Comparator$CC;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.Utilities;
/* loaded from: classes.dex */
public class BitmapsCache {
    private static final int N = Utilities.clamp(Runtime.getRuntime().availableProcessors() - 2, 8, 1);
    private static ThreadPoolExecutor bitmapCompressExecutor;
    byte[] bufferTmp;
    volatile boolean cacheCreated;
    RandomAccessFile cachedFile;
    int compressQuality;
    boolean error;
    final File file;
    String fileName;
    private int frameIndex;
    int h;
    volatile boolean recycled;
    private final Cacheable source;
    int w;
    ArrayList<FrameOffset> frameOffsets = new ArrayList<>();
    private final Object mutex = new Object();

    /* loaded from: classes.dex */
    public static class CacheOptions {
        public int compressQuality = 100;
    }

    /* loaded from: classes.dex */
    public interface Cacheable {
        Bitmap getFirstFrame(Bitmap bitmap);

        int getNextFrame(Bitmap bitmap);

        void prepareForGenerateCache();

        void releaseForGenerateCache();
    }

    /* loaded from: classes.dex */
    public static class Metadata {
        public int frame;
    }

    public BitmapsCache(File file, Cacheable cacheable, CacheOptions cacheOptions, int i, int i2) {
        this.source = cacheable;
        this.w = i;
        this.h = i2;
        this.compressQuality = cacheOptions.compressQuality;
        this.fileName = file.getName();
        if (bitmapCompressExecutor == null) {
            int i3 = N;
            bitmapCompressExecutor = new ThreadPoolExecutor(i3, i3, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue());
        }
        File file2 = new File(FileLoader.checkDirectory(4), "acache");
        this.file = new File(file2, this.fileName + "_" + i + "_" + i2 + ".pcache2");
    }

    public void createCache() {
        try {
            try {
                long currentTimeMillis = System.currentTimeMillis();
                RandomAccessFile randomAccessFile = new RandomAccessFile(this.file, "rw");
                if (this.file.exists()) {
                    try {
                        this.cacheCreated = randomAccessFile.readBoolean();
                        if (this.cacheCreated) {
                            randomAccessFile.close();
                            return;
                        }
                        this.file.delete();
                    } catch (Exception unused) {
                    }
                }
                randomAccessFile.close();
                final RandomAccessFile randomAccessFile2 = new RandomAccessFile(this.file, "rw");
                int i = N;
                final Bitmap[] bitmapArr = new Bitmap[i];
                final ByteArrayOutputStream[] byteArrayOutputStreamArr = new ByteArrayOutputStream[i];
                final CountDownLatch[] countDownLatchArr = new CountDownLatch[i];
                for (int i2 = 0; i2 < N; i2++) {
                    bitmapArr[i2] = Bitmap.createBitmap(this.w, this.h, Bitmap.Config.ARGB_8888);
                    byteArrayOutputStreamArr[i2] = new ByteArrayOutputStream(this.w * this.h * 2);
                }
                ArrayList arrayList = new ArrayList();
                randomAccessFile2.writeBoolean(false);
                randomAccessFile2.writeInt(0);
                this.source.prepareForGenerateCache();
                long j = 0;
                long j2 = 0;
                int i3 = 0;
                int i4 = 0;
                while (true) {
                    if (countDownLatchArr[i3] != null) {
                        try {
                            countDownLatchArr[i3].await();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    long currentTimeMillis2 = System.currentTimeMillis();
                    if (this.source.getNextFrame(bitmapArr[i3]) != 1) {
                        break;
                    }
                    long currentTimeMillis3 = j2 + (System.currentTimeMillis() - currentTimeMillis2);
                    countDownLatchArr[i3] = new CountDownLatch(1);
                    final int i5 = i3;
                    long j3 = j;
                    final int i6 = i4;
                    final ArrayList arrayList2 = arrayList;
                    long j4 = currentTimeMillis;
                    CountDownLatch[] countDownLatchArr2 = countDownLatchArr;
                    bitmapCompressExecutor.execute(new Runnable() { // from class: org.telegram.messenger.utils.BitmapsCache$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            BitmapsCache.this.lambda$createCache$0(bitmapArr, i5, byteArrayOutputStreamArr, i6, randomAccessFile2, arrayList2, countDownLatchArr);
                        }
                    });
                    int i7 = i3 + 1;
                    i4++;
                    i3 = i7 >= N ? 0 : i7;
                    countDownLatchArr = countDownLatchArr2;
                    j2 = currentTimeMillis3;
                    j = j3;
                    arrayList = arrayList2;
                    currentTimeMillis = j4;
                }
                for (int i8 = 0; i8 < N; i8++) {
                    if (countDownLatchArr[i8] != null) {
                        try {
                            countDownLatchArr[i8].await();
                        } catch (InterruptedException e2) {
                            e2.printStackTrace();
                        }
                    }
                    if (bitmapArr[i8] != null) {
                        try {
                            bitmapArr[i8].recycle();
                        } catch (Exception unused2) {
                        }
                    }
                    if (byteArrayOutputStreamArr[i8] != null) {
                        byteArrayOutputStreamArr[i8].buf = null;
                    }
                }
                int length = (int) randomAccessFile2.length();
                Collections.sort(arrayList, Comparator$CC.comparingInt(BitmapsCache$$ExternalSyntheticLambda1.INSTANCE));
                randomAccessFile2.writeInt(arrayList.size());
                for (int i9 = 0; i9 < arrayList.size(); i9++) {
                    randomAccessFile2.writeInt(((FrameOffset) arrayList.get(i9)).frameOffset);
                    randomAccessFile2.writeInt(((FrameOffset) arrayList.get(i9)).frameSize);
                }
                randomAccessFile2.seek(j);
                randomAccessFile2.writeBoolean(true);
                randomAccessFile2.writeInt(length);
                randomAccessFile2.close();
                if (BuildVars.DEBUG_VERSION) {
                    FileLog.d("generate cache for time = " + (System.currentTimeMillis() - currentTimeMillis) + " drawFrameTime = " + j2 + " comressTime = " + j + " fileSize = " + AndroidUtilities.formatFileSize(this.file.length()) + " " + this.fileName);
                }
            } finally {
                this.source.releaseForGenerateCache();
            }
        } catch (FileNotFoundException e3) {
            e3.printStackTrace();
        } catch (IOException e4) {
            e4.printStackTrace();
        }
    }

    public /* synthetic */ void lambda$createCache$0(Bitmap[] bitmapArr, int i, ByteArrayOutputStream[] byteArrayOutputStreamArr, int i2, RandomAccessFile randomAccessFile, ArrayList arrayList, CountDownLatch[] countDownLatchArr) {
        bitmapArr[i].compress(Bitmap.CompressFormat.WEBP, this.compressQuality, byteArrayOutputStreamArr[i]);
        int i3 = byteArrayOutputStreamArr[i].count;
        try {
            synchronized (this.mutex) {
                FrameOffset frameOffset = new FrameOffset(i2);
                frameOffset.frameOffset = (int) randomAccessFile.length();
                arrayList.add(frameOffset);
                randomAccessFile.write(byteArrayOutputStreamArr[i].buf, 0, i3);
                frameOffset.frameSize = i3;
                byteArrayOutputStreamArr[i].reset();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        countDownLatchArr[i].countDown();
    }

    public int getFrame(Bitmap bitmap, Metadata metadata) {
        int frame = getFrame(this.frameIndex, bitmap);
        metadata.frame = this.frameIndex;
        if (this.cacheCreated && !this.frameOffsets.isEmpty()) {
            int i = this.frameIndex + 1;
            this.frameIndex = i;
            if (i >= this.frameOffsets.size()) {
                this.frameIndex = 0;
            }
        }
        return frame;
    }

    /* JADX WARN: Removed duplicated region for block: B:61:0x00d0 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public int getFrame(int r10, android.graphics.Bitmap r11) {
        /*
            Method dump skipped, instructions count: 217
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.utils.BitmapsCache.getFrame(int, android.graphics.Bitmap):int");
    }

    public boolean needGenCache() {
        return !this.cacheCreated;
    }

    public void recycle() {
        RandomAccessFile randomAccessFile = this.cachedFile;
        if (randomAccessFile != null) {
            try {
                randomAccessFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.cachedFile = null;
        }
        this.recycled = true;
    }

    /* loaded from: classes.dex */
    public class FrameOffset {
        int frameOffset;
        int frameSize;
        final int index;

        private FrameOffset(BitmapsCache bitmapsCache, int i) {
            this.index = i;
        }
    }

    /* loaded from: classes.dex */
    public static class ByteArrayOutputStream extends OutputStream {
        protected byte[] buf;
        protected int count;

        public ByteArrayOutputStream(int i) {
            this.buf = new byte[i];
        }

        private void ensureCapacity(int i) {
            if (i - this.buf.length > 0) {
                grow(i);
            }
        }

        private void grow(int i) {
            int length = this.buf.length << 1;
            if (length - i < 0) {
                length = i;
            }
            if (length - 2147483639 > 0) {
                length = hugeCapacity(i);
            }
            this.buf = Arrays.copyOf(this.buf, length);
        }

        private static int hugeCapacity(int i) {
            if (i >= 0) {
                return i > 2147483639 ? Integer.MAX_VALUE : 2147483639;
            }
            throw new OutOfMemoryError();
        }

        @Override // java.io.OutputStream
        public synchronized void write(int i) {
            ensureCapacity(this.count + 1);
            byte[] bArr = this.buf;
            int i2 = this.count;
            bArr[i2] = (byte) i;
            this.count = i2 + 1;
        }

        @Override // java.io.OutputStream
        public synchronized void write(byte[] bArr, int i, int i2) {
            if (i >= 0) {
                if (i <= bArr.length && i2 >= 0 && (i + i2) - bArr.length <= 0) {
                    ensureCapacity(this.count + i2);
                    System.arraycopy(bArr, i, this.buf, this.count, i2);
                    this.count += i2;
                }
            }
            throw new IndexOutOfBoundsException();
        }

        public synchronized void reset() {
            this.count = 0;
        }
    }
}
