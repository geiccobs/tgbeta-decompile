package org.telegram.messenger;

import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipException;
import org.telegram.messenger.FilePathDatabase;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
/* loaded from: classes4.dex */
public class FileLoadOperation {
    private static final int preloadMaxBytes = 2097152;
    private static final int stateDownloading = 1;
    private static final int stateFailed = 2;
    private static final int stateFinished = 3;
    private static final int stateIdle = 0;
    private boolean allowDisordererFileSave;
    private int bigFileSizeFrom;
    private long bytesCountPadding;
    private File cacheFileFinal;
    private File cacheFileGzipTemp;
    private File cacheFileParts;
    private File cacheFilePreload;
    private File cacheFileTemp;
    private File cacheIvTemp;
    private byte[] cdnCheckBytes;
    private int cdnChunkCheckSize;
    private int cdnDatacenterId;
    private HashMap<Long, TLRPC.TL_fileHash> cdnHashes;
    private byte[] cdnIv;
    private byte[] cdnKey;
    private byte[] cdnToken;
    private int currentAccount;
    private int currentDownloadChunkSize;
    private int currentMaxDownloadRequests;
    private int currentQueueType;
    private int currentType;
    private int datacenterId;
    private ArrayList<RequestInfo> delayedRequestInfos;
    private FileLoadOperationDelegate delegate;
    private int downloadChunkSize;
    private int downloadChunkSizeBig;
    private long downloadedBytes;
    private boolean encryptFile;
    private byte[] encryptIv;
    private byte[] encryptKey;
    private String ext;
    private String fileName;
    private RandomAccessFile fileOutputStream;
    private RandomAccessFile filePartsStream;
    private RandomAccessFile fileReadStream;
    private RandomAccessFile fiv;
    private boolean forceBig;
    private long foundMoovSize;
    private int initialDatacenterId;
    private boolean isCdn;
    private boolean isForceRequest;
    private boolean isPreloadVideoOperation;
    private byte[] iv;
    private byte[] key;
    protected long lastProgressUpdateTime;
    protected TLRPC.InputFileLocation location;
    private int maxCdnParts;
    private int maxDownloadRequests;
    private int maxDownloadRequestsBig;
    private int moovFound;
    private long nextAtomOffset;
    private boolean nextPartWasPreloaded;
    private long nextPreloadDownloadOffset;
    private ArrayList<Range> notCheckedCdnRanges;
    private ArrayList<Range> notLoadedBytesRanges;
    private volatile ArrayList<Range> notLoadedBytesRangesCopy;
    private ArrayList<Range> notRequestedBytesRanges;
    public Object parentObject;
    public FilePathDatabase.PathData pathSaveData;
    private volatile boolean paused;
    private boolean preloadFinished;
    private long preloadNotRequestedBytesCount;
    private RandomAccessFile preloadStream;
    private int preloadStreamFileOffset;
    private byte[] preloadTempBuffer;
    private int preloadTempBufferCount;
    private HashMap<Long, PreloadRange> preloadedBytesRanges;
    private int priority;
    private RequestInfo priorityRequestInfo;
    private int renameRetryCount;
    private ArrayList<RequestInfo> requestInfos;
    private long requestedBytesCount;
    private HashMap<Long, Integer> requestedPreloadedBytesRanges;
    private boolean requestingCdnOffsets;
    protected boolean requestingReference;
    private int requestsCount;
    private boolean reuploadingCdn;
    private boolean started;
    private volatile int state;
    private String storeFileName;
    private File storePath;
    private ArrayList<FileLoadOperationStream> streamListeners;
    private long streamPriorityStartOffset;
    private long streamStartOffset;
    private boolean supportsPreloading;
    private File tempPath;
    private long totalBytesCount;
    private int totalPreloadedBytes;
    private boolean ungzip;
    private WebFile webFile;
    private TLRPC.InputWebFileLocation webLocation;

    /* loaded from: classes4.dex */
    public interface FileLoadOperationDelegate {
        void didChangedLoadProgress(FileLoadOperation fileLoadOperation, long j, long j2);

        void didFailedLoadingFile(FileLoadOperation fileLoadOperation, int i);

        void didFinishLoadingFile(FileLoadOperation fileLoadOperation, File file);

        void saveFilePath(FilePathDatabase.PathData pathData, File file);
    }

    /* loaded from: classes4.dex */
    public static class RequestInfo {
        private long offset;
        private int requestToken;
        private TLRPC.TL_upload_file response;
        private TLRPC.TL_upload_cdnFile responseCdn;
        private TLRPC.TL_upload_webFile responseWeb;

        protected RequestInfo() {
        }
    }

    /* loaded from: classes4.dex */
    public static class Range {
        private long end;
        private long start;

        private Range(long s, long e) {
            this.start = s;
            this.end = e;
        }
    }

    /* loaded from: classes4.dex */
    public static class PreloadRange {
        private long fileOffset;
        private long length;

        private PreloadRange(long o, long l) {
            this.fileOffset = o;
            this.length = l;
        }
    }

    private void updateParams() {
        if (MessagesController.getInstance(this.currentAccount).getfileExperimentalParams) {
            this.downloadChunkSizeBig = 524288;
            this.maxDownloadRequests = 8;
            this.maxDownloadRequestsBig = 8;
        } else {
            this.downloadChunkSizeBig = 131072;
            this.maxDownloadRequests = 4;
            this.maxDownloadRequestsBig = 4;
        }
        this.maxCdnParts = (int) (FileLoader.DEFAULT_MAX_FILE_SIZE / this.downloadChunkSizeBig);
    }

    public FileLoadOperation(ImageLocation imageLocation, Object parent, String extension, long size) {
        this.downloadChunkSize = 32768;
        this.downloadChunkSizeBig = 131072;
        this.cdnChunkCheckSize = 131072;
        this.maxDownloadRequests = 4;
        this.maxDownloadRequestsBig = 4;
        this.bigFileSizeFrom = 1048576;
        this.maxCdnParts = (int) (FileLoader.DEFAULT_MAX_FILE_SIZE / 131072);
        this.preloadTempBuffer = new byte[24];
        boolean z = false;
        this.state = 0;
        updateParams();
        this.parentObject = parent;
        this.forceBig = imageLocation.imageType == 2;
        if (imageLocation.isEncrypted()) {
            TLRPC.TL_inputEncryptedFileLocation tL_inputEncryptedFileLocation = new TLRPC.TL_inputEncryptedFileLocation();
            this.location = tL_inputEncryptedFileLocation;
            tL_inputEncryptedFileLocation.id = imageLocation.location.volume_id;
            this.location.volume_id = imageLocation.location.volume_id;
            this.location.local_id = imageLocation.location.local_id;
            this.location.access_hash = imageLocation.access_hash;
            this.iv = new byte[32];
            byte[] bArr = imageLocation.iv;
            byte[] bArr2 = this.iv;
            System.arraycopy(bArr, 0, bArr2, 0, bArr2.length);
            this.key = imageLocation.key;
        } else if (imageLocation.photoPeer != null) {
            TLRPC.TL_inputPeerPhotoFileLocation inputPeerPhotoFileLocation = new TLRPC.TL_inputPeerPhotoFileLocation();
            inputPeerPhotoFileLocation.id = imageLocation.location.volume_id;
            inputPeerPhotoFileLocation.volume_id = imageLocation.location.volume_id;
            inputPeerPhotoFileLocation.local_id = imageLocation.location.local_id;
            inputPeerPhotoFileLocation.photo_id = imageLocation.photoId;
            inputPeerPhotoFileLocation.big = imageLocation.photoPeerType == 0;
            inputPeerPhotoFileLocation.peer = imageLocation.photoPeer;
            this.location = inputPeerPhotoFileLocation;
        } else if (imageLocation.stickerSet != null) {
            TLRPC.TL_inputStickerSetThumb inputStickerSetThumb = new TLRPC.TL_inputStickerSetThumb();
            inputStickerSetThumb.id = imageLocation.location.volume_id;
            inputStickerSetThumb.volume_id = imageLocation.location.volume_id;
            inputStickerSetThumb.local_id = imageLocation.location.local_id;
            inputStickerSetThumb.thumb_version = imageLocation.thumbVersion;
            inputStickerSetThumb.stickerset = imageLocation.stickerSet;
            this.location = inputStickerSetThumb;
        } else if (imageLocation.thumbSize != null) {
            if (imageLocation.photoId != 0) {
                TLRPC.TL_inputPhotoFileLocation tL_inputPhotoFileLocation = new TLRPC.TL_inputPhotoFileLocation();
                this.location = tL_inputPhotoFileLocation;
                tL_inputPhotoFileLocation.id = imageLocation.photoId;
                this.location.volume_id = imageLocation.location.volume_id;
                this.location.local_id = imageLocation.location.local_id;
                this.location.access_hash = imageLocation.access_hash;
                this.location.file_reference = imageLocation.file_reference;
                this.location.thumb_size = imageLocation.thumbSize;
                if (imageLocation.imageType == 2) {
                    this.allowDisordererFileSave = true;
                }
            } else {
                TLRPC.TL_inputDocumentFileLocation tL_inputDocumentFileLocation = new TLRPC.TL_inputDocumentFileLocation();
                this.location = tL_inputDocumentFileLocation;
                tL_inputDocumentFileLocation.id = imageLocation.documentId;
                this.location.volume_id = imageLocation.location.volume_id;
                this.location.local_id = imageLocation.location.local_id;
                this.location.access_hash = imageLocation.access_hash;
                this.location.file_reference = imageLocation.file_reference;
                this.location.thumb_size = imageLocation.thumbSize;
            }
            if (this.location.file_reference == null) {
                this.location.file_reference = new byte[0];
            }
        } else {
            TLRPC.TL_inputFileLocation tL_inputFileLocation = new TLRPC.TL_inputFileLocation();
            this.location = tL_inputFileLocation;
            tL_inputFileLocation.volume_id = imageLocation.location.volume_id;
            this.location.local_id = imageLocation.location.local_id;
            this.location.secret = imageLocation.access_hash;
            this.location.file_reference = imageLocation.file_reference;
            if (this.location.file_reference == null) {
                this.location.file_reference = new byte[0];
            }
            this.allowDisordererFileSave = true;
        }
        this.ungzip = (imageLocation.imageType == 1 || imageLocation.imageType == 3) ? true : z;
        int i = imageLocation.dc_id;
        this.datacenterId = i;
        this.initialDatacenterId = i;
        this.currentType = 16777216;
        this.totalBytesCount = size;
        this.ext = extension != null ? extension : "jpg";
    }

    public FileLoadOperation(SecureDocument secureDocument) {
        this.downloadChunkSize = 32768;
        this.downloadChunkSizeBig = 131072;
        this.cdnChunkCheckSize = 131072;
        this.maxDownloadRequests = 4;
        this.maxDownloadRequestsBig = 4;
        this.bigFileSizeFrom = 1048576;
        this.maxCdnParts = (int) (FileLoader.DEFAULT_MAX_FILE_SIZE / 131072);
        this.preloadTempBuffer = new byte[24];
        this.state = 0;
        updateParams();
        TLRPC.TL_inputSecureFileLocation tL_inputSecureFileLocation = new TLRPC.TL_inputSecureFileLocation();
        this.location = tL_inputSecureFileLocation;
        tL_inputSecureFileLocation.id = secureDocument.secureFile.id;
        this.location.access_hash = secureDocument.secureFile.access_hash;
        this.datacenterId = secureDocument.secureFile.dc_id;
        this.totalBytesCount = secureDocument.secureFile.size;
        this.allowDisordererFileSave = true;
        this.currentType = ConnectionsManager.FileTypeFile;
        this.ext = ".jpg";
    }

    public FileLoadOperation(int instance, WebFile webDocument) {
        this.downloadChunkSize = 32768;
        this.downloadChunkSizeBig = 131072;
        this.cdnChunkCheckSize = 131072;
        this.maxDownloadRequests = 4;
        this.maxDownloadRequestsBig = 4;
        this.bigFileSizeFrom = 1048576;
        this.maxCdnParts = (int) (FileLoader.DEFAULT_MAX_FILE_SIZE / 131072);
        this.preloadTempBuffer = new byte[24];
        this.state = 0;
        updateParams();
        this.currentAccount = instance;
        this.webFile = webDocument;
        this.webLocation = webDocument.location;
        this.totalBytesCount = webDocument.size;
        int i = MessagesController.getInstance(this.currentAccount).webFileDatacenterId;
        this.datacenterId = i;
        this.initialDatacenterId = i;
        String defaultExt = FileLoader.getMimeTypePart(webDocument.mime_type);
        if (webDocument.mime_type.startsWith("image/")) {
            this.currentType = 16777216;
        } else if (webDocument.mime_type.equals("audio/ogg")) {
            this.currentType = ConnectionsManager.FileTypeAudio;
        } else if (webDocument.mime_type.startsWith("video/")) {
            this.currentType = ConnectionsManager.FileTypeVideo;
        } else {
            this.currentType = ConnectionsManager.FileTypeFile;
        }
        this.allowDisordererFileSave = true;
        this.ext = ImageLoader.getHttpUrlExtension(webDocument.url, defaultExt);
    }

    /* JADX WARN: Removed duplicated region for block: B:39:0x0107 A[Catch: Exception -> 0x012e, TryCatch #0 {Exception -> 0x012e, blocks: (B:3:0x002c, B:6:0x0034, B:7:0x005e, B:9:0x0062, B:11:0x0083, B:12:0x0089, B:14:0x009a, B:16:0x00a4, B:17:0x00a7, B:18:0x00aa, B:20:0x00b4, B:25:0x00c2, B:27:0x00cc, B:29:0x00d7, B:30:0x00df, B:32:0x00e7, B:35:0x00f2, B:36:0x00fb, B:37:0x00fd, B:39:0x0107, B:40:0x010c, B:42:0x0114, B:43:0x0119, B:44:0x011d, B:46:0x0125), top: B:50:0x002c }] */
    /* JADX WARN: Removed duplicated region for block: B:40:0x010c A[Catch: Exception -> 0x012e, TryCatch #0 {Exception -> 0x012e, blocks: (B:3:0x002c, B:6:0x0034, B:7:0x005e, B:9:0x0062, B:11:0x0083, B:12:0x0089, B:14:0x009a, B:16:0x00a4, B:17:0x00a7, B:18:0x00aa, B:20:0x00b4, B:25:0x00c2, B:27:0x00cc, B:29:0x00d7, B:30:0x00df, B:32:0x00e7, B:35:0x00f2, B:36:0x00fb, B:37:0x00fd, B:39:0x0107, B:40:0x010c, B:42:0x0114, B:43:0x0119, B:44:0x011d, B:46:0x0125), top: B:50:0x002c }] */
    /* JADX WARN: Removed duplicated region for block: B:46:0x0125 A[Catch: Exception -> 0x012e, TRY_LEAVE, TryCatch #0 {Exception -> 0x012e, blocks: (B:3:0x002c, B:6:0x0034, B:7:0x005e, B:9:0x0062, B:11:0x0083, B:12:0x0089, B:14:0x009a, B:16:0x00a4, B:17:0x00a7, B:18:0x00aa, B:20:0x00b4, B:25:0x00c2, B:27:0x00cc, B:29:0x00d7, B:30:0x00df, B:32:0x00e7, B:35:0x00f2, B:36:0x00fb, B:37:0x00fd, B:39:0x0107, B:40:0x010c, B:42:0x0114, B:43:0x0119, B:44:0x011d, B:46:0x0125), top: B:50:0x002c }] */
    /* JADX WARN: Removed duplicated region for block: B:55:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public FileLoadOperation(org.telegram.tgnet.TLRPC.Document r14, java.lang.Object r15) {
        /*
            Method dump skipped, instructions count: 310
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FileLoadOperation.<init>(org.telegram.tgnet.TLRPC$Document, java.lang.Object):void");
    }

    public void setEncryptFile(boolean value) {
        this.encryptFile = value;
        if (value) {
            this.allowDisordererFileSave = false;
        }
    }

    public int getDatacenterId() {
        return this.initialDatacenterId;
    }

    public void setForceRequest(boolean forceRequest) {
        this.isForceRequest = forceRequest;
    }

    public boolean isForceRequest() {
        return this.isForceRequest;
    }

    public void setPriority(int value) {
        this.priority = value;
    }

    public int getPriority() {
        return this.priority;
    }

    public void setPaths(int instance, String name, int queueType, File store, File temp, String finalName) {
        this.storePath = store;
        this.tempPath = temp;
        this.currentAccount = instance;
        this.fileName = name;
        this.storeFileName = finalName;
        this.currentQueueType = queueType;
    }

    public int getQueueType() {
        return this.currentQueueType;
    }

    public boolean wasStarted() {
        return this.started && !this.paused;
    }

    public int getCurrentType() {
        return this.currentType;
    }

    private void removePart(ArrayList<Range> ranges, long start, long end) {
        if (ranges == null || end < start) {
            return;
        }
        int count = ranges.size();
        boolean modified = false;
        int a = 0;
        while (true) {
            if (a >= count) {
                break;
            }
            Range range = ranges.get(a);
            if (start == range.end) {
                range.end = end;
                modified = true;
                break;
            } else if (end == range.start) {
                range.start = start;
                modified = true;
                break;
            } else {
                a++;
            }
        }
        Collections.sort(ranges, FileLoadOperation$$ExternalSyntheticLambda2.INSTANCE);
        int a2 = 0;
        while (a2 < ranges.size() - 1) {
            Range r1 = ranges.get(a2);
            Range r2 = ranges.get(a2 + 1);
            if (r1.end == r2.start) {
                r1.end = r2.end;
                ranges.remove(a2 + 1);
                a2--;
            }
            a2++;
        }
        if (!modified) {
            ranges.add(new Range(start, end));
        }
    }

    public static /* synthetic */ int lambda$removePart$0(Range o1, Range o2) {
        if (o1.start <= o2.start) {
            if (o1.start < o2.start) {
                return -1;
            }
            return 0;
        }
        return 1;
    }

    private void addPart(ArrayList<Range> ranges, long start, long end, boolean save) {
        boolean modified;
        if (ranges == null || end < start) {
            return;
        }
        int count = ranges.size();
        int a = 0;
        while (true) {
            if (a >= count) {
                modified = false;
                break;
            }
            Range range = ranges.get(a);
            if (start <= range.start) {
                if (end < range.end) {
                    if (end > range.start) {
                        range.start = end;
                        modified = true;
                        break;
                    }
                    a++;
                } else {
                    ranges.remove(a);
                    modified = true;
                    break;
                }
            } else if (end >= range.end) {
                if (start < range.end) {
                    range.end = start;
                    modified = true;
                    break;
                }
                a++;
            } else {
                Range newRange = new Range(range.start, start);
                ranges.add(0, newRange);
                range.start = end;
                modified = true;
                break;
            }
        }
        if (save) {
            if (modified) {
                try {
                    this.filePartsStream.seek(0L);
                    int count2 = ranges.size();
                    this.filePartsStream.writeInt(count2);
                    for (int a2 = 0; a2 < count2; a2++) {
                        Range range2 = ranges.get(a2);
                        this.filePartsStream.writeLong(range2.start);
                        this.filePartsStream.writeLong(range2.end);
                    }
                } catch (Exception e) {
                    FileLog.e(e);
                }
                notifyStreamListeners();
            } else if (BuildVars.LOGS_ENABLED) {
                FileLog.e(this.cacheFileFinal + " downloaded duplicate file part " + start + " - " + end);
            }
        }
    }

    private void notifyStreamListeners() {
        ArrayList<FileLoadOperationStream> arrayList = this.streamListeners;
        if (arrayList != null) {
            int count = arrayList.size();
            for (int a = 0; a < count; a++) {
                this.streamListeners.get(a).newDataAvailable();
            }
        }
    }

    public File getCacheFileFinal() {
        return this.cacheFileFinal;
    }

    public File getCurrentFile() {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final File[] result = new File[1];
        Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.FileLoadOperation$$ExternalSyntheticLambda14
            @Override // java.lang.Runnable
            public final void run() {
                FileLoadOperation.this.m206lambda$getCurrentFile$1$orgtelegrammessengerFileLoadOperation(result, countDownLatch);
            }
        });
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e(e);
        }
        return result[0];
    }

    /* renamed from: lambda$getCurrentFile$1$org-telegram-messenger-FileLoadOperation */
    public /* synthetic */ void m206lambda$getCurrentFile$1$orgtelegrammessengerFileLoadOperation(File[] result, CountDownLatch countDownLatch) {
        if (this.state == 3) {
            result[0] = this.cacheFileFinal;
        } else {
            result[0] = this.cacheFileTemp;
        }
        countDownLatch.countDown();
    }

    private long getDownloadedLengthFromOffsetInternal(ArrayList<Range> ranges, long offset, long length) {
        if (ranges == null || this.state == 3 || ranges.isEmpty()) {
            if (this.state == 3) {
                return length;
            }
            long j = this.downloadedBytes;
            if (j != 0) {
                return Math.min(length, Math.max(j - offset, 0L));
            }
            return 0L;
        }
        int count = ranges.size();
        Range minRange = null;
        long availableLength = length;
        int a = 0;
        while (true) {
            if (a >= count) {
                break;
            }
            Range range = ranges.get(a);
            if (offset <= range.start && (minRange == null || range.start < minRange.start)) {
                minRange = range;
            }
            if (range.start > offset || range.end <= offset) {
                a++;
            } else {
                availableLength = 0;
                break;
            }
        }
        int a2 = (availableLength > 0L ? 1 : (availableLength == 0L ? 0 : -1));
        if (a2 == 0) {
            return 0L;
        }
        if (minRange != null) {
            return Math.min(length, minRange.start - offset);
        }
        return Math.min(length, Math.max(this.totalBytesCount - offset, 0L));
    }

    public float getDownloadedLengthFromOffset(float progress) {
        ArrayList<Range> ranges = this.notLoadedBytesRangesCopy;
        long j = this.totalBytesCount;
        if (j == 0 || ranges == null) {
            return 0.0f;
        }
        return (((float) getDownloadedLengthFromOffsetInternal(ranges, (int) (((float) j) * progress), j)) / ((float) this.totalBytesCount)) + progress;
    }

    public long[] getDownloadedLengthFromOffset(final int offset, final long length) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final long[] result = new long[2];
        Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.FileLoadOperation$$ExternalSyntheticLambda13
            @Override // java.lang.Runnable
            public final void run() {
                FileLoadOperation.this.m207xcb1e2f1b(result, offset, length, countDownLatch);
            }
        });
        try {
            countDownLatch.await();
        } catch (Exception e) {
        }
        return result;
    }

    /* renamed from: lambda$getDownloadedLengthFromOffset$2$org-telegram-messenger-FileLoadOperation */
    public /* synthetic */ void m207xcb1e2f1b(long[] result, int offset, long length, CountDownLatch countDownLatch) {
        result[0] = getDownloadedLengthFromOffsetInternal(this.notLoadedBytesRanges, offset, length);
        if (this.state == 3) {
            result[1] = 1;
        }
        countDownLatch.countDown();
    }

    public String getFileName() {
        return this.fileName;
    }

    public void removeStreamListener(final FileLoadOperationStream operation) {
        Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.FileLoadOperation$$ExternalSyntheticLambda8
            @Override // java.lang.Runnable
            public final void run() {
                FileLoadOperation.this.m210xc3d0b3c8(operation);
            }
        });
    }

    /* renamed from: lambda$removeStreamListener$3$org-telegram-messenger-FileLoadOperation */
    public /* synthetic */ void m210xc3d0b3c8(FileLoadOperationStream operation) {
        ArrayList<FileLoadOperationStream> arrayList = this.streamListeners;
        if (arrayList == null) {
            return;
        }
        arrayList.remove(operation);
    }

    private void copyNotLoadedRanges() {
        if (this.notLoadedBytesRanges == null) {
            return;
        }
        this.notLoadedBytesRangesCopy = new ArrayList<>(this.notLoadedBytesRanges);
    }

    public void pause() {
        if (this.state != 1) {
            return;
        }
        this.paused = true;
    }

    public boolean start() {
        return start(null, 0L, false);
    }

    /* JADX WARN: Code restructure failed: missing block: B:117:0x0415, code lost:
        if (r5 != r39.cacheFileFinal.length()) goto L118;
     */
    /* JADX WARN: Code restructure failed: missing block: B:210:0x0692, code lost:
        r35 = r4;
        r36 = r5;
        r33 = r6;
        r38 = r10;
     */
    /* JADX WARN: Removed duplicated region for block: B:121:0x0422  */
    /* JADX WARN: Removed duplicated region for block: B:222:0x070b  */
    /* JADX WARN: Removed duplicated region for block: B:235:0x0746  */
    /* JADX WARN: Removed duplicated region for block: B:250:0x07b1  */
    /* JADX WARN: Removed duplicated region for block: B:253:0x07bb  */
    /* JADX WARN: Removed duplicated region for block: B:266:0x0815  */
    /* JADX WARN: Removed duplicated region for block: B:273:0x0840  */
    /* JADX WARN: Removed duplicated region for block: B:279:0x086c  */
    /* JADX WARN: Removed duplicated region for block: B:284:0x08a9  */
    /* JADX WARN: Removed duplicated region for block: B:297:0x08fa  */
    /* JADX WARN: Removed duplicated region for block: B:300:0x0902  */
    /* JADX WARN: Removed duplicated region for block: B:308:0x0925 A[Catch: Exception -> 0x092a, TRY_LEAVE, TryCatch #4 {Exception -> 0x092a, blocks: (B:306:0x0914, B:308:0x0925), top: B:335:0x0914 }] */
    /* JADX WARN: Removed duplicated region for block: B:314:0x0933  */
    /* JADX WARN: Removed duplicated region for block: B:316:0x0938  */
    /* JADX WARN: Removed duplicated region for block: B:317:0x0948  */
    /* JADX WARN: Removed duplicated region for block: B:341:0x0716 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean start(final org.telegram.messenger.FileLoadOperationStream r40, final long r41, final boolean r43) {
        /*
            Method dump skipped, instructions count: 2424
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FileLoadOperation.start(org.telegram.messenger.FileLoadOperationStream, long, boolean):boolean");
    }

    /* renamed from: lambda$start$4$org-telegram-messenger-FileLoadOperation */
    public /* synthetic */ void m213lambda$start$4$orgtelegrammessengerFileLoadOperation(boolean steamPriority, long streamOffset, FileLoadOperationStream stream, boolean alreadyStarted) {
        if (this.streamListeners == null) {
            this.streamListeners = new ArrayList<>();
        }
        if (steamPriority) {
            int i = this.currentDownloadChunkSize;
            long offset = (streamOffset / i) * i;
            RequestInfo requestInfo = this.priorityRequestInfo;
            if (requestInfo != null && requestInfo.offset != offset) {
                this.requestInfos.remove(this.priorityRequestInfo);
                this.requestedBytesCount -= this.currentDownloadChunkSize;
                removePart(this.notRequestedBytesRanges, this.priorityRequestInfo.offset, this.priorityRequestInfo.offset + this.currentDownloadChunkSize);
                if (this.priorityRequestInfo.requestToken != 0) {
                    ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.priorityRequestInfo.requestToken, true);
                    this.requestsCount--;
                }
                if (BuildVars.DEBUG_VERSION) {
                    FileLog.d("frame get cancel request at offset " + this.priorityRequestInfo.offset);
                }
                this.priorityRequestInfo = null;
            }
            if (this.priorityRequestInfo == null) {
                this.streamPriorityStartOffset = offset;
            }
        } else {
            int i2 = this.currentDownloadChunkSize;
            this.streamStartOffset = (streamOffset / i2) * i2;
        }
        this.streamListeners.add(stream);
        if (alreadyStarted) {
            if (this.preloadedBytesRanges != null && getDownloadedLengthFromOffsetInternal(this.notLoadedBytesRanges, this.streamStartOffset, 1L) == 0 && this.preloadedBytesRanges.get(Long.valueOf(this.streamStartOffset)) != null) {
                this.nextPartWasPreloaded = true;
            }
            startDownloadRequest();
            this.nextPartWasPreloaded = false;
        }
    }

    /* renamed from: lambda$start$5$org-telegram-messenger-FileLoadOperation */
    public /* synthetic */ void m214lambda$start$5$orgtelegrammessengerFileLoadOperation(boolean[] preloaded) {
        long j = this.totalBytesCount;
        if (j != 0 && ((this.isPreloadVideoOperation && preloaded[0]) || this.downloadedBytes == j)) {
            try {
                onFinishLoadingFile(false);
                return;
            } catch (Exception e) {
                onFail(true, 0);
                return;
            }
        }
        startDownloadRequest();
    }

    public void updateProgress() {
        FileLoadOperationDelegate fileLoadOperationDelegate = this.delegate;
        if (fileLoadOperationDelegate != null) {
            long j = this.downloadedBytes;
            long j2 = this.totalBytesCount;
            if (j != j2 && j2 > 0) {
                fileLoadOperationDelegate.didChangedLoadProgress(this, j, j2);
            }
        }
    }

    public boolean isPaused() {
        return this.paused;
    }

    public void setIsPreloadVideoOperation(final boolean value) {
        boolean z = this.isPreloadVideoOperation;
        if (z != value) {
            if (value && this.totalBytesCount <= 2097152) {
                return;
            }
            if (!value && z) {
                if (this.state == 3) {
                    this.isPreloadVideoOperation = value;
                    this.state = 0;
                    this.preloadFinished = false;
                    start();
                    return;
                } else if (this.state == 1) {
                    Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.FileLoadOperation$$ExternalSyntheticLambda11
                        @Override // java.lang.Runnable
                        public final void run() {
                            FileLoadOperation.this.m212xc9269a74(value);
                        }
                    });
                    return;
                } else {
                    this.isPreloadVideoOperation = value;
                    return;
                }
            }
            this.isPreloadVideoOperation = value;
        }
    }

    /* renamed from: lambda$setIsPreloadVideoOperation$6$org-telegram-messenger-FileLoadOperation */
    public /* synthetic */ void m212xc9269a74(boolean value) {
        this.requestedBytesCount = 0L;
        clearOperaion(null, true);
        this.isPreloadVideoOperation = value;
        startDownloadRequest();
    }

    public boolean isPreloadVideoOperation() {
        return this.isPreloadVideoOperation;
    }

    public boolean isPreloadFinished() {
        return this.preloadFinished;
    }

    public void cancel() {
        cancel(false);
    }

    public void cancel(final boolean deleteFiles) {
        Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.FileLoadOperation$$ExternalSyntheticLambda9
            @Override // java.lang.Runnable
            public final void run() {
                FileLoadOperation.this.m205lambda$cancel$7$orgtelegrammessengerFileLoadOperation(deleteFiles);
            }
        });
    }

    /* renamed from: lambda$cancel$7$org-telegram-messenger-FileLoadOperation */
    public /* synthetic */ void m205lambda$cancel$7$orgtelegrammessengerFileLoadOperation(boolean deleteFiles) {
        if (this.state != 3 && this.state != 2) {
            if (this.requestInfos != null) {
                for (int a = 0; a < this.requestInfos.size(); a++) {
                    RequestInfo requestInfo = this.requestInfos.get(a);
                    if (requestInfo.requestToken != 0) {
                        ConnectionsManager.getInstance(this.currentAccount).cancelRequest(requestInfo.requestToken, true);
                    }
                }
            }
            onFail(false, 1);
        }
        if (deleteFiles) {
            File file = this.cacheFileFinal;
            if (file != null) {
                try {
                    if (!file.delete()) {
                        this.cacheFileFinal.deleteOnExit();
                    }
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
            File file2 = this.cacheFileTemp;
            if (file2 != null) {
                try {
                    if (!file2.delete()) {
                        this.cacheFileTemp.deleteOnExit();
                    }
                } catch (Exception e2) {
                    FileLog.e(e2);
                }
            }
            File file3 = this.cacheFileParts;
            if (file3 != null) {
                try {
                    if (!file3.delete()) {
                        this.cacheFileParts.deleteOnExit();
                    }
                } catch (Exception e3) {
                    FileLog.e(e3);
                }
            }
            File file4 = this.cacheIvTemp;
            if (file4 != null) {
                try {
                    if (!file4.delete()) {
                        this.cacheIvTemp.deleteOnExit();
                    }
                } catch (Exception e4) {
                    FileLog.e(e4);
                }
            }
            File file5 = this.cacheFilePreload;
            if (file5 != null) {
                try {
                    if (!file5.delete()) {
                        this.cacheFilePreload.deleteOnExit();
                    }
                } catch (Exception e5) {
                    FileLog.e(e5);
                }
            }
        }
    }

    private void cleanup() {
        try {
            RandomAccessFile randomAccessFile = this.fileOutputStream;
            if (randomAccessFile != null) {
                try {
                    randomAccessFile.getChannel().close();
                } catch (Exception e) {
                    FileLog.e(e);
                }
                this.fileOutputStream.close();
                this.fileOutputStream = null;
            }
        } catch (Exception e2) {
            FileLog.e(e2);
        }
        try {
            RandomAccessFile randomAccessFile2 = this.preloadStream;
            if (randomAccessFile2 != null) {
                try {
                    randomAccessFile2.getChannel().close();
                } catch (Exception e3) {
                    FileLog.e(e3);
                }
                this.preloadStream.close();
                this.preloadStream = null;
            }
        } catch (Exception e4) {
            FileLog.e(e4);
        }
        try {
            RandomAccessFile randomAccessFile3 = this.fileReadStream;
            if (randomAccessFile3 != null) {
                try {
                    randomAccessFile3.getChannel().close();
                } catch (Exception e5) {
                    FileLog.e(e5);
                }
                this.fileReadStream.close();
                this.fileReadStream = null;
            }
        } catch (Exception e6) {
            FileLog.e(e6);
        }
        try {
            RandomAccessFile randomAccessFile4 = this.filePartsStream;
            if (randomAccessFile4 != null) {
                try {
                    randomAccessFile4.getChannel().close();
                } catch (Exception e7) {
                    FileLog.e(e7);
                }
                this.filePartsStream.close();
                this.filePartsStream = null;
            }
        } catch (Exception e8) {
            FileLog.e(e8);
        }
        try {
            RandomAccessFile randomAccessFile5 = this.fiv;
            if (randomAccessFile5 != null) {
                randomAccessFile5.close();
                this.fiv = null;
            }
        } catch (Exception e9) {
            FileLog.e(e9);
        }
        if (this.delayedRequestInfos != null) {
            for (int a = 0; a < this.delayedRequestInfos.size(); a++) {
                RequestInfo requestInfo = this.delayedRequestInfos.get(a);
                if (requestInfo.response != null) {
                    requestInfo.response.disableFree = false;
                    requestInfo.response.freeResources();
                } else if (requestInfo.responseWeb != null) {
                    requestInfo.responseWeb.disableFree = false;
                    requestInfo.responseWeb.freeResources();
                } else if (requestInfo.responseCdn != null) {
                    requestInfo.responseCdn.disableFree = false;
                    requestInfo.responseCdn.freeResources();
                }
            }
            this.delayedRequestInfos.clear();
        }
    }

    private void onFinishLoadingFile(final boolean increment) {
        boolean renameResult;
        String newFileName;
        if (this.state != 1) {
            return;
        }
        this.state = 3;
        notifyStreamListeners();
        cleanup();
        if (this.isPreloadVideoOperation) {
            this.preloadFinished = true;
            if (BuildVars.DEBUG_VERSION) {
                FileLog.d("finished preloading file to " + this.cacheFileTemp + " loaded " + this.totalPreloadedBytes + " of " + this.totalBytesCount);
            }
        } else {
            File file = this.cacheIvTemp;
            if (file != null) {
                file.delete();
                this.cacheIvTemp = null;
            }
            File file2 = this.cacheFileParts;
            if (file2 != null) {
                file2.delete();
                this.cacheFileParts = null;
            }
            File file3 = this.cacheFilePreload;
            if (file3 != null) {
                file3.delete();
                this.cacheFilePreload = null;
            }
            if (this.cacheFileTemp != null) {
                if (this.ungzip) {
                    try {
                        GZIPInputStream gzipInputStream = new GZIPInputStream(new FileInputStream(this.cacheFileTemp));
                        FileLoader.copyFile(gzipInputStream, this.cacheFileGzipTemp, 2097152);
                        gzipInputStream.close();
                        this.cacheFileTemp.delete();
                        this.cacheFileTemp = this.cacheFileGzipTemp;
                        this.ungzip = false;
                    } catch (ZipException e) {
                        this.ungzip = false;
                    } catch (Throwable e2) {
                        FileLog.e(e2);
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.e("unable to ungzip temp = " + this.cacheFileTemp + " to final = " + this.cacheFileFinal);
                        }
                    }
                }
                if (!this.ungzip) {
                    if (this.parentObject instanceof TLRPC.TL_theme) {
                        try {
                            renameResult = AndroidUtilities.copyFile(this.cacheFileTemp, this.cacheFileFinal);
                        } catch (Exception e3) {
                            FileLog.e(e3);
                            renameResult = false;
                        }
                    } else {
                        try {
                            if (this.pathSaveData != null) {
                                this.cacheFileFinal = new File(this.storePath, this.storeFileName);
                                int count = 1;
                                while (this.cacheFileFinal.exists()) {
                                    int lastDotIndex = this.storeFileName.lastIndexOf(46);
                                    if (lastDotIndex > 0) {
                                        newFileName = this.storeFileName.substring(0, lastDotIndex) + " (" + count + ")" + this.storeFileName.substring(lastDotIndex);
                                    } else {
                                        newFileName = this.storeFileName + " (" + count + ")";
                                    }
                                    this.cacheFileFinal = new File(this.storePath, newFileName);
                                    count++;
                                }
                            }
                            renameResult = this.cacheFileTemp.renameTo(this.cacheFileFinal);
                        } catch (Exception e4) {
                            FileLog.e(e4);
                            renameResult = false;
                        }
                    }
                    if (!renameResult) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.e("unable to rename temp = " + this.cacheFileTemp + " to final = " + this.cacheFileFinal + " retry = " + this.renameRetryCount);
                        }
                        int i = this.renameRetryCount + 1;
                        this.renameRetryCount = i;
                        if (i < 3) {
                            this.state = 1;
                            Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.FileLoadOperation$$ExternalSyntheticLambda10
                                @Override // java.lang.Runnable
                                public final void run() {
                                    FileLoadOperation.this.m209x905dc9cf(increment);
                                }
                            }, 200L);
                            return;
                        }
                        this.cacheFileFinal = this.cacheFileTemp;
                    } else if (this.pathSaveData != null && this.cacheFileFinal.exists()) {
                        this.delegate.saveFilePath(this.pathSaveData, this.cacheFileFinal);
                    }
                } else {
                    onFail(false, 0);
                    return;
                }
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("finished downloading file to " + this.cacheFileFinal);
            }
            if (increment) {
                int i2 = this.currentType;
                if (i2 == 50331648) {
                    StatsController.getInstance(this.currentAccount).incrementReceivedItemsCount(ApplicationLoader.getCurrentNetworkType(), 3, 1);
                } else if (i2 == 33554432) {
                    StatsController.getInstance(this.currentAccount).incrementReceivedItemsCount(ApplicationLoader.getCurrentNetworkType(), 2, 1);
                } else if (i2 == 16777216) {
                    StatsController.getInstance(this.currentAccount).incrementReceivedItemsCount(ApplicationLoader.getCurrentNetworkType(), 4, 1);
                } else if (i2 == 67108864) {
                    StatsController.getInstance(this.currentAccount).incrementReceivedItemsCount(ApplicationLoader.getCurrentNetworkType(), 5, 1);
                }
            }
        }
        this.delegate.didFinishLoadingFile(this, this.cacheFileFinal);
    }

    /* renamed from: lambda$onFinishLoadingFile$8$org-telegram-messenger-FileLoadOperation */
    public /* synthetic */ void m209x905dc9cf(boolean increment) {
        try {
            onFinishLoadingFile(increment);
        } catch (Exception e) {
            onFail(false, 0);
        }
    }

    private void delayRequestInfo(RequestInfo requestInfo) {
        this.delayedRequestInfos.add(requestInfo);
        if (requestInfo.response != null) {
            requestInfo.response.disableFree = true;
        } else if (requestInfo.responseWeb != null) {
            requestInfo.responseWeb.disableFree = true;
        } else if (requestInfo.responseCdn != null) {
            requestInfo.responseCdn.disableFree = true;
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:47:0x00f6, code lost:
        return 0;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private long findNextPreloadDownloadOffset(long r16, long r18, org.telegram.tgnet.NativeByteBuffer r20) {
        /*
            Method dump skipped, instructions count: 247
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FileLoadOperation.findNextPreloadDownloadOffset(long, long, org.telegram.tgnet.NativeByteBuffer):long");
    }

    private void requestFileOffsets(long offset) {
        if (this.requestingCdnOffsets) {
            return;
        }
        this.requestingCdnOffsets = true;
        TLRPC.TL_upload_getCdnFileHashes req = new TLRPC.TL_upload_getCdnFileHashes();
        req.file_token = this.cdnToken;
        req.offset = offset;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.FileLoadOperation$$ExternalSyntheticLambda3
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                FileLoadOperation.this.m211x6ac3f205(tLObject, tL_error);
            }
        }, null, null, 0, this.datacenterId, 1, true);
    }

    /* renamed from: lambda$requestFileOffsets$9$org-telegram-messenger-FileLoadOperation */
    public /* synthetic */ void m211x6ac3f205(TLObject response, TLRPC.TL_error error) {
        if (error != null) {
            onFail(false, 0);
            return;
        }
        this.requestingCdnOffsets = false;
        TLRPC.Vector vector = (TLRPC.Vector) response;
        if (!vector.objects.isEmpty()) {
            if (this.cdnHashes == null) {
                this.cdnHashes = new HashMap<>();
            }
            for (int a = 0; a < vector.objects.size(); a++) {
                TLRPC.TL_fileHash hash = (TLRPC.TL_fileHash) vector.objects.get(a);
                this.cdnHashes.put(Long.valueOf(hash.offset), hash);
            }
        }
        for (int a2 = 0; a2 < this.delayedRequestInfos.size(); a2++) {
            RequestInfo delayedRequestInfo = this.delayedRequestInfos.get(a2);
            if (this.notLoadedBytesRanges != null || this.downloadedBytes == delayedRequestInfo.offset) {
                this.delayedRequestInfos.remove(a2);
                if (!processRequestResult(delayedRequestInfo, null)) {
                    if (delayedRequestInfo.response == null) {
                        if (delayedRequestInfo.responseWeb == null) {
                            if (delayedRequestInfo.responseCdn != null) {
                                delayedRequestInfo.responseCdn.disableFree = false;
                                delayedRequestInfo.responseCdn.freeResources();
                                return;
                            }
                            return;
                        }
                        delayedRequestInfo.responseWeb.disableFree = false;
                        delayedRequestInfo.responseWeb.freeResources();
                        return;
                    }
                    delayedRequestInfo.response.disableFree = false;
                    delayedRequestInfo.response.freeResources();
                    return;
                }
                return;
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:217:0x05e5 A[Catch: Exception -> 0x05f6, TryCatch #2 {Exception -> 0x05f6, blocks: (B:10:0x0044, B:12:0x0048, B:14:0x0052, B:16:0x0056, B:18:0x005c, B:19:0x0064, B:21:0x006a, B:22:0x0072, B:24:0x0078, B:27:0x0084, B:30:0x008e, B:32:0x0097, B:34:0x00a6, B:37:0x00b4, B:39:0x00bc, B:41:0x00d0, B:42:0x010a, B:44:0x010e, B:46:0x0132, B:47:0x015a, B:49:0x015e, B:50:0x0165, B:52:0x018b, B:54:0x01a0, B:56:0x01b5, B:57:0x01c1, B:58:0x01cb, B:59:0x01d0, B:60:0x01d8, B:62:0x01df, B:64:0x0200, B:66:0x0204, B:68:0x020a, B:70:0x0210, B:76:0x021c, B:77:0x0228, B:79:0x022c, B:81:0x023a, B:88:0x0256, B:92:0x025e, B:105:0x027a, B:107:0x027e, B:109:0x0299, B:111:0x02a1, B:116:0x02b5, B:117:0x02cb, B:118:0x02cc, B:119:0x02d0, B:121:0x02d4, B:123:0x0313, B:125:0x0317, B:127:0x0324, B:128:0x0344, B:130:0x0375, B:132:0x038a, B:134:0x039a, B:137:0x03a6, B:140:0x03ad, B:142:0x03c9, B:144:0x03d0, B:146:0x03d8, B:156:0x03f4, B:158:0x0405, B:160:0x041f, B:165:0x0430, B:166:0x0437, B:167:0x0438, B:169:0x0447, B:171:0x0487, B:173:0x0497, B:175:0x049b, B:177:0x049f, B:178:0x04e8, B:180:0x04ec, B:181:0x050a, B:183:0x0514, B:187:0x0549, B:189:0x054d, B:190:0x0559, B:192:0x0561, B:194:0x0566, B:197:0x0577, B:199:0x057f, B:201:0x058b, B:204:0x0596, B:205:0x0599, B:207:0x05a5, B:209:0x05ab, B:210:0x05ba, B:212:0x05c0, B:213:0x05cf, B:215:0x05d5, B:217:0x05e5, B:218:0x05ea, B:220:0x05f0), top: B:262:0x0044 }] */
    /* JADX WARN: Removed duplicated region for block: B:218:0x05ea A[Catch: Exception -> 0x05f6, TryCatch #2 {Exception -> 0x05f6, blocks: (B:10:0x0044, B:12:0x0048, B:14:0x0052, B:16:0x0056, B:18:0x005c, B:19:0x0064, B:21:0x006a, B:22:0x0072, B:24:0x0078, B:27:0x0084, B:30:0x008e, B:32:0x0097, B:34:0x00a6, B:37:0x00b4, B:39:0x00bc, B:41:0x00d0, B:42:0x010a, B:44:0x010e, B:46:0x0132, B:47:0x015a, B:49:0x015e, B:50:0x0165, B:52:0x018b, B:54:0x01a0, B:56:0x01b5, B:57:0x01c1, B:58:0x01cb, B:59:0x01d0, B:60:0x01d8, B:62:0x01df, B:64:0x0200, B:66:0x0204, B:68:0x020a, B:70:0x0210, B:76:0x021c, B:77:0x0228, B:79:0x022c, B:81:0x023a, B:88:0x0256, B:92:0x025e, B:105:0x027a, B:107:0x027e, B:109:0x0299, B:111:0x02a1, B:116:0x02b5, B:117:0x02cb, B:118:0x02cc, B:119:0x02d0, B:121:0x02d4, B:123:0x0313, B:125:0x0317, B:127:0x0324, B:128:0x0344, B:130:0x0375, B:132:0x038a, B:134:0x039a, B:137:0x03a6, B:140:0x03ad, B:142:0x03c9, B:144:0x03d0, B:146:0x03d8, B:156:0x03f4, B:158:0x0405, B:160:0x041f, B:165:0x0430, B:166:0x0437, B:167:0x0438, B:169:0x0447, B:171:0x0487, B:173:0x0497, B:175:0x049b, B:177:0x049f, B:178:0x04e8, B:180:0x04ec, B:181:0x050a, B:183:0x0514, B:187:0x0549, B:189:0x054d, B:190:0x0559, B:192:0x0561, B:194:0x0566, B:197:0x0577, B:199:0x057f, B:201:0x058b, B:204:0x0596, B:205:0x0599, B:207:0x05a5, B:209:0x05ab, B:210:0x05ba, B:212:0x05c0, B:213:0x05cf, B:215:0x05d5, B:217:0x05e5, B:218:0x05ea, B:220:0x05f0), top: B:262:0x0044 }] */
    /* JADX WARN: Removed duplicated region for block: B:76:0x021c A[Catch: Exception -> 0x05f6, TryCatch #2 {Exception -> 0x05f6, blocks: (B:10:0x0044, B:12:0x0048, B:14:0x0052, B:16:0x0056, B:18:0x005c, B:19:0x0064, B:21:0x006a, B:22:0x0072, B:24:0x0078, B:27:0x0084, B:30:0x008e, B:32:0x0097, B:34:0x00a6, B:37:0x00b4, B:39:0x00bc, B:41:0x00d0, B:42:0x010a, B:44:0x010e, B:46:0x0132, B:47:0x015a, B:49:0x015e, B:50:0x0165, B:52:0x018b, B:54:0x01a0, B:56:0x01b5, B:57:0x01c1, B:58:0x01cb, B:59:0x01d0, B:60:0x01d8, B:62:0x01df, B:64:0x0200, B:66:0x0204, B:68:0x020a, B:70:0x0210, B:76:0x021c, B:77:0x0228, B:79:0x022c, B:81:0x023a, B:88:0x0256, B:92:0x025e, B:105:0x027a, B:107:0x027e, B:109:0x0299, B:111:0x02a1, B:116:0x02b5, B:117:0x02cb, B:118:0x02cc, B:119:0x02d0, B:121:0x02d4, B:123:0x0313, B:125:0x0317, B:127:0x0324, B:128:0x0344, B:130:0x0375, B:132:0x038a, B:134:0x039a, B:137:0x03a6, B:140:0x03ad, B:142:0x03c9, B:144:0x03d0, B:146:0x03d8, B:156:0x03f4, B:158:0x0405, B:160:0x041f, B:165:0x0430, B:166:0x0437, B:167:0x0438, B:169:0x0447, B:171:0x0487, B:173:0x0497, B:175:0x049b, B:177:0x049f, B:178:0x04e8, B:180:0x04ec, B:181:0x050a, B:183:0x0514, B:187:0x0549, B:189:0x054d, B:190:0x0559, B:192:0x0561, B:194:0x0566, B:197:0x0577, B:199:0x057f, B:201:0x058b, B:204:0x0596, B:205:0x0599, B:207:0x05a5, B:209:0x05ab, B:210:0x05ba, B:212:0x05c0, B:213:0x05cf, B:215:0x05d5, B:217:0x05e5, B:218:0x05ea, B:220:0x05f0), top: B:262:0x0044 }] */
    /* JADX WARN: Removed duplicated region for block: B:77:0x0228 A[Catch: Exception -> 0x05f6, TryCatch #2 {Exception -> 0x05f6, blocks: (B:10:0x0044, B:12:0x0048, B:14:0x0052, B:16:0x0056, B:18:0x005c, B:19:0x0064, B:21:0x006a, B:22:0x0072, B:24:0x0078, B:27:0x0084, B:30:0x008e, B:32:0x0097, B:34:0x00a6, B:37:0x00b4, B:39:0x00bc, B:41:0x00d0, B:42:0x010a, B:44:0x010e, B:46:0x0132, B:47:0x015a, B:49:0x015e, B:50:0x0165, B:52:0x018b, B:54:0x01a0, B:56:0x01b5, B:57:0x01c1, B:58:0x01cb, B:59:0x01d0, B:60:0x01d8, B:62:0x01df, B:64:0x0200, B:66:0x0204, B:68:0x020a, B:70:0x0210, B:76:0x021c, B:77:0x0228, B:79:0x022c, B:81:0x023a, B:88:0x0256, B:92:0x025e, B:105:0x027a, B:107:0x027e, B:109:0x0299, B:111:0x02a1, B:116:0x02b5, B:117:0x02cb, B:118:0x02cc, B:119:0x02d0, B:121:0x02d4, B:123:0x0313, B:125:0x0317, B:127:0x0324, B:128:0x0344, B:130:0x0375, B:132:0x038a, B:134:0x039a, B:137:0x03a6, B:140:0x03ad, B:142:0x03c9, B:144:0x03d0, B:146:0x03d8, B:156:0x03f4, B:158:0x0405, B:160:0x041f, B:165:0x0430, B:166:0x0437, B:167:0x0438, B:169:0x0447, B:171:0x0487, B:173:0x0497, B:175:0x049b, B:177:0x049f, B:178:0x04e8, B:180:0x04ec, B:181:0x050a, B:183:0x0514, B:187:0x0549, B:189:0x054d, B:190:0x0559, B:192:0x0561, B:194:0x0566, B:197:0x0577, B:199:0x057f, B:201:0x058b, B:204:0x0596, B:205:0x0599, B:207:0x05a5, B:209:0x05ab, B:210:0x05ba, B:212:0x05c0, B:213:0x05cf, B:215:0x05d5, B:217:0x05e5, B:218:0x05ea, B:220:0x05f0), top: B:262:0x0044 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean processRequestResult(org.telegram.messenger.FileLoadOperation.RequestInfo r46, org.telegram.tgnet.TLRPC.TL_error r47) {
        /*
            Method dump skipped, instructions count: 1794
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FileLoadOperation.processRequestResult(org.telegram.messenger.FileLoadOperation$RequestInfo, org.telegram.tgnet.TLRPC$TL_error):boolean");
    }

    public void onFail(boolean thread, final int reason) {
        cleanup();
        this.state = 2;
        FileLoadOperationDelegate fileLoadOperationDelegate = this.delegate;
        if (fileLoadOperationDelegate != null) {
            if (thread) {
                Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.FileLoadOperation$$ExternalSyntheticLambda6
                    @Override // java.lang.Runnable
                    public final void run() {
                        FileLoadOperation.this.m208lambda$onFail$10$orgtelegrammessengerFileLoadOperation(reason);
                    }
                });
            } else {
                fileLoadOperationDelegate.didFailedLoadingFile(this, reason);
            }
        }
    }

    /* renamed from: lambda$onFail$10$org-telegram-messenger-FileLoadOperation */
    public /* synthetic */ void m208lambda$onFail$10$orgtelegrammessengerFileLoadOperation(int reason) {
        this.delegate.didFailedLoadingFile(this, reason);
    }

    private void clearOperaion(RequestInfo currentInfo, boolean preloadChanged) {
        long minOffset = Long.MAX_VALUE;
        for (int a = 0; a < this.requestInfos.size(); a++) {
            RequestInfo info = this.requestInfos.get(a);
            minOffset = Math.min(info.offset, minOffset);
            if (this.isPreloadVideoOperation) {
                this.requestedPreloadedBytesRanges.remove(Long.valueOf(info.offset));
            } else {
                removePart(this.notRequestedBytesRanges, info.offset, this.currentDownloadChunkSize + info.offset);
            }
            if (currentInfo != info && info.requestToken != 0) {
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(info.requestToken, true);
            }
        }
        this.requestInfos.clear();
        for (int a2 = 0; a2 < this.delayedRequestInfos.size(); a2++) {
            RequestInfo info2 = this.delayedRequestInfos.get(a2);
            if (this.isPreloadVideoOperation) {
                this.requestedPreloadedBytesRanges.remove(Long.valueOf(info2.offset));
            } else {
                removePart(this.notRequestedBytesRanges, info2.offset, this.currentDownloadChunkSize + info2.offset);
            }
            if (info2.response != null) {
                info2.response.disableFree = false;
                info2.response.freeResources();
            } else if (info2.responseWeb != null) {
                info2.responseWeb.disableFree = false;
                info2.responseWeb.freeResources();
            } else if (info2.responseCdn != null) {
                info2.responseCdn.disableFree = false;
                info2.responseCdn.freeResources();
            }
            minOffset = Math.min(info2.offset, minOffset);
        }
        this.delayedRequestInfos.clear();
        this.requestsCount = 0;
        if (!preloadChanged && this.isPreloadVideoOperation) {
            this.requestedBytesCount = this.totalPreloadedBytes;
        } else if (this.notLoadedBytesRanges == null) {
            this.downloadedBytes = minOffset;
            this.requestedBytesCount = minOffset;
        }
    }

    private void requestReference(RequestInfo requestInfo) {
        if (this.requestingReference) {
            return;
        }
        clearOperaion(requestInfo, false);
        this.requestingReference = true;
        Object obj = this.parentObject;
        if (obj instanceof MessageObject) {
            MessageObject messageObject = (MessageObject) obj;
            if (messageObject.getId() < 0 && messageObject.messageOwner.media.webpage != null) {
                this.parentObject = messageObject.messageOwner.media.webpage;
            }
        }
        FileRefController.getInstance(this.currentAccount).requestReference(this.parentObject, this.location, this, requestInfo);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void startDownloadRequest() {
        int count;
        int count2;
        long downloadOffset;
        final TLRPC.TL_upload_getFile tL_upload_getFile;
        int flags;
        HashMap<Long, PreloadRange> hashMap;
        ArrayList<Range> arrayList;
        long downloadOffset2;
        long downloadOffset3;
        if (!this.paused && !this.reuploadingCdn && this.state == 1) {
            long j = 0;
            if (this.streamPriorityStartOffset == 0) {
                if (this.nextPartWasPreloaded || this.requestInfos.size() + this.delayedRequestInfos.size() < this.currentMaxDownloadRequests) {
                    if (this.isPreloadVideoOperation) {
                        if (this.requestedBytesCount <= 2097152) {
                            if (this.moovFound != 0 && this.requestInfos.size() > 0) {
                                return;
                            }
                        } else {
                            return;
                        }
                    }
                } else {
                    return;
                }
            }
            boolean z = false;
            if (this.streamPriorityStartOffset == 0 && !this.nextPartWasPreloaded && ((!this.isPreloadVideoOperation || this.moovFound != 0) && this.totalBytesCount > 0)) {
                int count3 = Math.max(0, this.currentMaxDownloadRequests - this.requestInfos.size());
                count = count3;
            } else {
                count = 1;
            }
            int a = 0;
            while (a < count) {
                if (this.isPreloadVideoOperation) {
                    if (this.moovFound != 0 && this.preloadNotRequestedBytesCount <= j) {
                        return;
                    }
                    if (this.nextPreloadDownloadOffset == -1) {
                        downloadOffset3 = 0;
                        boolean found = false;
                        int tries = (2097152 / this.currentDownloadChunkSize) + 2;
                        while (true) {
                            if (tries == 0) {
                                break;
                            } else if (!this.requestedPreloadedBytesRanges.containsKey(Long.valueOf(downloadOffset3))) {
                                found = true;
                                break;
                            } else {
                                int i = this.currentDownloadChunkSize;
                                downloadOffset3 += i;
                                long j2 = this.totalBytesCount;
                                if (downloadOffset3 > j2) {
                                    break;
                                }
                                if (this.moovFound == 2 && downloadOffset3 == i * 8) {
                                    downloadOffset3 = ((j2 - 1048576) / i) * i;
                                }
                                tries--;
                            }
                        }
                        if (!found && this.requestInfos.isEmpty()) {
                            onFinishLoadingFile(z);
                        }
                    } else {
                        downloadOffset3 = this.nextPreloadDownloadOffset;
                    }
                    if (this.requestedPreloadedBytesRanges == null) {
                        this.requestedPreloadedBytesRanges = new HashMap<>();
                    }
                    this.requestedPreloadedBytesRanges.put(Long.valueOf(downloadOffset3), 1);
                    if (BuildVars.DEBUG_VERSION) {
                        FileLog.d("start next preload from " + downloadOffset3 + " size " + this.totalBytesCount + " for " + this.cacheFilePreload);
                    }
                    this.preloadNotRequestedBytesCount -= this.currentDownloadChunkSize;
                    count2 = count;
                    downloadOffset = downloadOffset3;
                } else {
                    ArrayList<Range> arrayList2 = this.notRequestedBytesRanges;
                    if (arrayList2 != null) {
                        long streamOffset = this.streamPriorityStartOffset;
                        if (streamOffset == 0) {
                            streamOffset = this.streamStartOffset;
                        }
                        int size = arrayList2.size();
                        long minStart = Long.MAX_VALUE;
                        long minStreamStart = Long.MAX_VALUE;
                        int b = 0;
                        while (true) {
                            if (b >= size) {
                                count2 = count;
                                break;
                            }
                            Range range = this.notRequestedBytesRanges.get(b);
                            if (streamOffset != 0) {
                                if (range.start > streamOffset || range.end <= streamOffset) {
                                    if (streamOffset < range.start && range.start < minStreamStart) {
                                        minStreamStart = range.start;
                                    }
                                } else {
                                    minStreamStart = streamOffset;
                                    minStart = Long.MAX_VALUE;
                                    count2 = count;
                                    break;
                                }
                            }
                            minStart = Math.min(minStart, range.start);
                            b++;
                            count = count;
                        }
                        if (minStreamStart != Long.MAX_VALUE) {
                            downloadOffset2 = minStreamStart;
                        } else if (minStart != Long.MAX_VALUE) {
                            downloadOffset2 = minStart;
                        } else {
                            return;
                        }
                        downloadOffset = downloadOffset2;
                    } else {
                        count2 = count;
                        downloadOffset = this.requestedBytesCount;
                    }
                }
                if (!this.isPreloadVideoOperation && (arrayList = this.notRequestedBytesRanges) != null) {
                    addPart(arrayList, downloadOffset, downloadOffset + this.currentDownloadChunkSize, false);
                }
                long j3 = this.totalBytesCount;
                if (j3 <= 0 || downloadOffset < j3) {
                    boolean isLast = j3 <= 0 || a == count2 + (-1) || (j3 > 0 && ((long) this.currentDownloadChunkSize) + downloadOffset >= j3);
                    int connectionType = this.requestsCount % 2 == 0 ? 2 : ConnectionsManager.ConnectionTypeDownload2;
                    int flags2 = this.isForceRequest ? 32 : 0;
                    if (this.isCdn) {
                        TLRPC.TL_upload_getCdnFile req = new TLRPC.TL_upload_getCdnFile();
                        req.file_token = this.cdnToken;
                        req.offset = downloadOffset;
                        req.limit = this.currentDownloadChunkSize;
                        tL_upload_getFile = req;
                        flags = flags2 | 1;
                    } else if (this.webLocation != null) {
                        TLRPC.TL_upload_getWebFile req2 = new TLRPC.TL_upload_getWebFile();
                        req2.location = this.webLocation;
                        req2.offset = (int) downloadOffset;
                        req2.limit = this.currentDownloadChunkSize;
                        tL_upload_getFile = req2;
                        flags = flags2;
                    } else {
                        TLRPC.TL_upload_getFile req3 = new TLRPC.TL_upload_getFile();
                        req3.location = this.location;
                        req3.offset = downloadOffset;
                        req3.limit = this.currentDownloadChunkSize;
                        req3.cdn_supported = true;
                        tL_upload_getFile = req3;
                        flags = flags2;
                    }
                    this.requestedBytesCount += this.currentDownloadChunkSize;
                    final RequestInfo requestInfo = new RequestInfo();
                    this.requestInfos.add(requestInfo);
                    requestInfo.offset = downloadOffset;
                    if (!this.isPreloadVideoOperation && this.supportsPreloading && this.preloadStream != null && (hashMap = this.preloadedBytesRanges) != null) {
                        PreloadRange range2 = hashMap.get(Long.valueOf(requestInfo.offset));
                        if (range2 != null) {
                            requestInfo.response = new TLRPC.TL_upload_file();
                            try {
                                if (BuildVars.DEBUG_VERSION && range2.length > 2147483647L) {
                                    throw new RuntimeException("cast long to integer");
                                    break;
                                }
                                NativeByteBuffer buffer = new NativeByteBuffer((int) range2.length);
                                this.preloadStream.seek(range2.fileOffset);
                                this.preloadStream.getChannel().read(buffer.buffer);
                                try {
                                    buffer.buffer.position(0);
                                    requestInfo.response.bytes = buffer;
                                    Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.FileLoadOperation$$ExternalSyntheticLambda7
                                        @Override // java.lang.Runnable
                                        public final void run() {
                                            FileLoadOperation.this.m215xe3dafa6a(requestInfo);
                                        }
                                    });
                                    j = 0;
                                } catch (Exception e) {
                                }
                            } catch (Exception e2) {
                            }
                            a++;
                            count = count2;
                            z = false;
                        }
                    }
                    if (this.streamPriorityStartOffset != 0) {
                        if (BuildVars.DEBUG_VERSION) {
                            FileLog.d("frame get offset = " + this.streamPriorityStartOffset);
                        }
                        this.streamPriorityStartOffset = 0L;
                        this.priorityRequestInfo = requestInfo;
                    }
                    TLRPC.InputFileLocation inputFileLocation = this.location;
                    if (!(inputFileLocation instanceof TLRPC.TL_inputPeerPhotoFileLocation)) {
                        j = 0;
                    } else {
                        TLRPC.TL_inputPeerPhotoFileLocation inputPeerPhotoFileLocation = (TLRPC.TL_inputPeerPhotoFileLocation) inputFileLocation;
                        j = 0;
                        if (inputPeerPhotoFileLocation.photo_id == 0) {
                            requestReference(requestInfo);
                            a++;
                            count = count2;
                            z = false;
                        }
                    }
                    requestInfo.requestToken = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_upload_getFile, new RequestDelegate() { // from class: org.telegram.messenger.FileLoadOperation$$ExternalSyntheticLambda5
                        @Override // org.telegram.tgnet.RequestDelegate
                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            FileLoadOperation.this.m217xf2a564a8(requestInfo, tL_upload_getFile, tLObject, tL_error);
                        }
                    }, null, null, flags, this.isCdn ? this.cdnDatacenterId : this.datacenterId, connectionType, isLast);
                    this.requestsCount++;
                    a++;
                    count = count2;
                    z = false;
                } else {
                    return;
                }
            }
        }
    }

    /* renamed from: lambda$startDownloadRequest$11$org-telegram-messenger-FileLoadOperation */
    public /* synthetic */ void m215xe3dafa6a(RequestInfo requestInfo) {
        processRequestResult(requestInfo, null);
        requestInfo.response.freeResources();
    }

    /* renamed from: lambda$startDownloadRequest$13$org-telegram-messenger-FileLoadOperation */
    public /* synthetic */ void m217xf2a564a8(final RequestInfo requestInfo, TLObject request, TLObject response, TLRPC.TL_error error) {
        if (!this.requestInfos.contains(requestInfo)) {
            return;
        }
        if (requestInfo == this.priorityRequestInfo) {
            if (BuildVars.DEBUG_VERSION) {
                FileLog.d("frame get request completed " + this.priorityRequestInfo.offset);
            }
            this.priorityRequestInfo = null;
        }
        if (error != null) {
            if (FileRefController.isFileRefError(error.text)) {
                requestReference(requestInfo);
                return;
            } else if ((request instanceof TLRPC.TL_upload_getCdnFile) && error.text.equals("FILE_TOKEN_INVALID")) {
                this.isCdn = false;
                clearOperaion(requestInfo, false);
                startDownloadRequest();
                return;
            }
        }
        if (response instanceof TLRPC.TL_upload_fileCdnRedirect) {
            TLRPC.TL_upload_fileCdnRedirect res = (TLRPC.TL_upload_fileCdnRedirect) response;
            if (!res.file_hashes.isEmpty()) {
                if (this.cdnHashes == null) {
                    this.cdnHashes = new HashMap<>();
                }
                for (int a1 = 0; a1 < res.file_hashes.size(); a1++) {
                    TLRPC.TL_fileHash hash = res.file_hashes.get(a1);
                    this.cdnHashes.put(Long.valueOf(hash.offset), hash);
                }
            }
            if (res.encryption_iv == null || res.encryption_key == null || res.encryption_iv.length != 16 || res.encryption_key.length != 32) {
                TLRPC.TL_error error2 = new TLRPC.TL_error();
                error2.text = "bad redirect response";
                error2.code = 400;
                processRequestResult(requestInfo, error2);
                return;
            }
            this.isCdn = true;
            if (this.notCheckedCdnRanges == null) {
                ArrayList<Range> arrayList = new ArrayList<>();
                this.notCheckedCdnRanges = arrayList;
                arrayList.add(new Range(0L, this.maxCdnParts));
            }
            this.cdnDatacenterId = res.dc_id;
            this.cdnIv = res.encryption_iv;
            this.cdnKey = res.encryption_key;
            this.cdnToken = res.file_token;
            clearOperaion(requestInfo, false);
            startDownloadRequest();
        } else if (response instanceof TLRPC.TL_upload_cdnFileReuploadNeeded) {
            if (!this.reuploadingCdn) {
                clearOperaion(requestInfo, false);
                this.reuploadingCdn = true;
                TLRPC.TL_upload_reuploadCdnFile req = new TLRPC.TL_upload_reuploadCdnFile();
                req.file_token = this.cdnToken;
                req.request_token = ((TLRPC.TL_upload_cdnFileReuploadNeeded) response).request_token;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.FileLoadOperation$$ExternalSyntheticLambda4
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        FileLoadOperation.this.m216xeb402f89(requestInfo, tLObject, tL_error);
                    }
                }, null, null, 0, this.datacenterId, 1, true);
            }
        } else {
            if (response instanceof TLRPC.TL_upload_file) {
                requestInfo.response = (TLRPC.TL_upload_file) response;
            } else if (response instanceof TLRPC.TL_upload_webFile) {
                requestInfo.responseWeb = (TLRPC.TL_upload_webFile) response;
                if (this.totalBytesCount == 0 && requestInfo.responseWeb.size != 0) {
                    this.totalBytesCount = requestInfo.responseWeb.size;
                }
            } else {
                requestInfo.responseCdn = (TLRPC.TL_upload_cdnFile) response;
            }
            if (response != null) {
                int i = this.currentType;
                if (i == 50331648) {
                    StatsController.getInstance(this.currentAccount).incrementReceivedBytesCount(response.networkType, 3, response.getObjectSize() + 4);
                } else if (i == 33554432) {
                    StatsController.getInstance(this.currentAccount).incrementReceivedBytesCount(response.networkType, 2, response.getObjectSize() + 4);
                } else if (i == 16777216) {
                    StatsController.getInstance(this.currentAccount).incrementReceivedBytesCount(response.networkType, 4, response.getObjectSize() + 4);
                } else if (i == 67108864) {
                    StatsController.getInstance(this.currentAccount).incrementReceivedBytesCount(response.networkType, 5, response.getObjectSize() + 4);
                }
            }
            processRequestResult(requestInfo, error);
        }
    }

    /* renamed from: lambda$startDownloadRequest$12$org-telegram-messenger-FileLoadOperation */
    public /* synthetic */ void m216xeb402f89(RequestInfo requestInfo, TLObject response1, TLRPC.TL_error error1) {
        this.reuploadingCdn = false;
        if (error1 == null) {
            TLRPC.Vector vector = (TLRPC.Vector) response1;
            if (!vector.objects.isEmpty()) {
                if (this.cdnHashes == null) {
                    this.cdnHashes = new HashMap<>();
                }
                for (int a1 = 0; a1 < vector.objects.size(); a1++) {
                    TLRPC.TL_fileHash hash = (TLRPC.TL_fileHash) vector.objects.get(a1);
                    this.cdnHashes.put(Long.valueOf(hash.offset), hash);
                }
            }
            startDownloadRequest();
        } else if (error1.text.equals("FILE_TOKEN_INVALID") || error1.text.equals("REQUEST_TOKEN_INVALID")) {
            this.isCdn = false;
            clearOperaion(requestInfo, false);
            startDownloadRequest();
        } else {
            onFail(false, 0);
        }
    }

    public void setDelegate(FileLoadOperationDelegate delegate) {
        this.delegate = delegate;
    }
}
