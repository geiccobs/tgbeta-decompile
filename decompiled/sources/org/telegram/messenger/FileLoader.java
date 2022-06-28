package org.telegram.messenger;

import android.text.TextUtils;
import android.util.SparseArray;
import android.util.SparseIntArray;
import com.google.android.exoplayer2.text.ttml.TtmlNode;
import com.google.android.exoplayer2.util.MimeTypes;
import j$.util.concurrent.ConcurrentHashMap;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileUploadOperation;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
/* loaded from: classes4.dex */
public class FileLoader extends BaseController {
    public static final long DEFAULT_MAX_FILE_SIZE = 2097152000;
    public static final long DEFAULT_MAX_FILE_SIZE_PREMIUM = 4194304000L;
    public static final int IMAGE_TYPE_ANIMATION = 2;
    public static final int IMAGE_TYPE_LOTTIE = 1;
    public static final int IMAGE_TYPE_SVG = 3;
    public static final int IMAGE_TYPE_SVG_WHITE = 4;
    public static final int IMAGE_TYPE_THEME_PREVIEW = 5;
    public static final int MEDIA_DIR_AUDIO = 1;
    public static final int MEDIA_DIR_CACHE = 4;
    public static final int MEDIA_DIR_DOCUMENT = 3;
    public static final int MEDIA_DIR_FILES = 5;
    public static final int MEDIA_DIR_IMAGE = 0;
    public static final int MEDIA_DIR_IMAGE_PUBLIC = 100;
    public static final int MEDIA_DIR_VIDEO = 2;
    public static final int MEDIA_DIR_VIDEO_PUBLIC = 101;
    public static final int PRELOAD_CACHE_TYPE = 11;
    public static final int QUEUE_TYPE_AUDIO = 2;
    public static final int QUEUE_TYPE_FILE = 0;
    public static final int QUEUE_TYPE_IMAGE = 1;
    public static final int QUEUE_TYPE_PRELOAD = 3;
    private final FilePathDatabase filePathDatabase;
    private String forceLoadingFile;
    private int lastReferenceId;
    private static volatile DispatchQueue fileLoaderQueue = new DispatchQueue("fileUploadQueue");
    private static SparseArray<File> mediaDirs = null;
    private static final FileLoader[] Instance = new FileLoader[4];
    private LinkedList<FileUploadOperation> uploadOperationQueue = new LinkedList<>();
    private LinkedList<FileUploadOperation> uploadSmallOperationQueue = new LinkedList<>();
    private ConcurrentHashMap<String, FileUploadOperation> uploadOperationPaths = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, FileUploadOperation> uploadOperationPathsEnc = new ConcurrentHashMap<>();
    private int currentUploadOperationsCount = 0;
    private int currentUploadSmallOperationsCount = 0;
    private SparseArray<LinkedList<FileLoadOperation>> fileLoadOperationQueues = new SparseArray<>();
    private SparseArray<LinkedList<FileLoadOperation>> audioLoadOperationQueues = new SparseArray<>();
    private SparseArray<LinkedList<FileLoadOperation>> imageLoadOperationQueues = new SparseArray<>();
    private SparseArray<LinkedList<FileLoadOperation>> preloadingLoadOperationQueues = new SparseArray<>();
    private SparseIntArray fileLoadOperationsCount = new SparseIntArray();
    private SparseIntArray audioLoadOperationsCount = new SparseIntArray();
    private SparseIntArray imageLoadOperationsCount = new SparseIntArray();
    private SparseIntArray preloadingLoadOperationsCount = new SparseIntArray();
    private ConcurrentHashMap<String, FileLoadOperation> loadOperationPaths = new ConcurrentHashMap<>();
    private ArrayList<FileLoadOperation> activeFileLoadOperation = new ArrayList<>();
    private ConcurrentHashMap<String, Boolean> loadOperationPathsUI = new ConcurrentHashMap<>(10, 1.0f, 2);
    private HashMap<String, Long> uploadSizes = new HashMap<>();
    private HashMap<String, Boolean> loadingVideos = new HashMap<>();
    private FileLoaderDelegate delegate = null;
    private ConcurrentHashMap<Integer, Object> parentObjectReferences = new ConcurrentHashMap<>();

    /* loaded from: classes4.dex */
    public interface FileLoaderDelegate {
        void fileDidFailedLoad(String str, int i);

        void fileDidFailedUpload(String str, boolean z);

        void fileDidLoaded(String str, File file, Object obj, int i);

        void fileDidUploaded(String str, TLRPC.InputFile inputFile, TLRPC.InputEncryptedFile inputEncryptedFile, byte[] bArr, byte[] bArr2, long j);

        void fileLoadProgressChanged(FileLoadOperation fileLoadOperation, String str, long j, long j2);

        void fileUploadProgressChanged(FileUploadOperation fileUploadOperation, String str, long j, long j2, boolean z);
    }

    /* loaded from: classes4.dex */
    public interface FileResolver {
        File getFile();
    }

    static /* synthetic */ int access$708(FileLoader x0) {
        int i = x0.currentUploadSmallOperationsCount;
        x0.currentUploadSmallOperationsCount = i + 1;
        return i;
    }

    static /* synthetic */ int access$710(FileLoader x0) {
        int i = x0.currentUploadSmallOperationsCount;
        x0.currentUploadSmallOperationsCount = i - 1;
        return i;
    }

    static /* synthetic */ int access$908(FileLoader x0) {
        int i = x0.currentUploadOperationsCount;
        x0.currentUploadOperationsCount = i + 1;
        return i;
    }

    static /* synthetic */ int access$910(FileLoader x0) {
        int i = x0.currentUploadOperationsCount;
        x0.currentUploadOperationsCount = i - 1;
        return i;
    }

    public static FileLoader getInstance(int num) {
        FileLoader[] fileLoaderArr = Instance;
        FileLoader localInstance = fileLoaderArr[num];
        if (localInstance == null) {
            synchronized (FileLoader.class) {
                localInstance = fileLoaderArr[num];
                if (localInstance == null) {
                    FileLoader fileLoader = new FileLoader(num);
                    localInstance = fileLoader;
                    fileLoaderArr[num] = fileLoader;
                }
            }
        }
        return localInstance;
    }

    public FileLoader(int instance) {
        super(instance);
        this.filePathDatabase = new FilePathDatabase(instance);
    }

    public static void setMediaDirs(SparseArray<File> dirs) {
        mediaDirs = dirs;
    }

    public static File checkDirectory(int type) {
        return mediaDirs.get(type);
    }

    public static File getDirectory(int type) {
        File dir = mediaDirs.get(type);
        if (dir == null && type != 4) {
            dir = mediaDirs.get(4);
        }
        if (dir != null) {
            try {
                if (!dir.isDirectory()) {
                    dir.mkdirs();
                }
            } catch (Exception e) {
            }
        }
        return dir;
    }

    public int getFileReference(Object parentObject) {
        int reference = this.lastReferenceId;
        this.lastReferenceId = reference + 1;
        this.parentObjectReferences.put(Integer.valueOf(reference), parentObject);
        return reference;
    }

    public Object getParentObject(int reference) {
        return this.parentObjectReferences.get(Integer.valueOf(reference));
    }

    /* renamed from: setLoadingVideoInternal */
    public void m229lambda$setLoadingVideo$0$orgtelegrammessengerFileLoader(TLRPC.Document document, boolean player) {
        String key = getAttachFileName(document);
        StringBuilder sb = new StringBuilder();
        sb.append(key);
        sb.append(player ? TtmlNode.TAG_P : "");
        String dKey = sb.toString();
        this.loadingVideos.put(dKey, true);
        getNotificationCenter().postNotificationName(NotificationCenter.videoLoadingStateChanged, key);
    }

    public void setLoadingVideo(final TLRPC.Document document, final boolean player, boolean schedule) {
        if (document == null) {
            return;
        }
        if (schedule) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.FileLoader$$ExternalSyntheticLambda11
                @Override // java.lang.Runnable
                public final void run() {
                    FileLoader.this.m229lambda$setLoadingVideo$0$orgtelegrammessengerFileLoader(document, player);
                }
            });
        } else {
            m229lambda$setLoadingVideo$0$orgtelegrammessengerFileLoader(document, player);
        }
    }

    public void setLoadingVideoForPlayer(TLRPC.Document document, boolean player) {
        if (document == null) {
            return;
        }
        String key = getAttachFileName(document);
        HashMap<String, Boolean> hashMap = this.loadingVideos;
        StringBuilder sb = new StringBuilder();
        sb.append(key);
        String str = "";
        sb.append(player ? str : TtmlNode.TAG_P);
        if (hashMap.containsKey(sb.toString())) {
            HashMap<String, Boolean> hashMap2 = this.loadingVideos;
            StringBuilder sb2 = new StringBuilder();
            sb2.append(key);
            if (player) {
                str = TtmlNode.TAG_P;
            }
            sb2.append(str);
            hashMap2.put(sb2.toString(), true);
        }
    }

    /* renamed from: removeLoadingVideoInternal */
    public void m227lambda$removeLoadingVideo$1$orgtelegrammessengerFileLoader(TLRPC.Document document, boolean player) {
        String key = getAttachFileName(document);
        StringBuilder sb = new StringBuilder();
        sb.append(key);
        sb.append(player ? TtmlNode.TAG_P : "");
        String dKey = sb.toString();
        if (this.loadingVideos.remove(dKey) != null) {
            getNotificationCenter().postNotificationName(NotificationCenter.videoLoadingStateChanged, key);
        }
    }

    public void removeLoadingVideo(final TLRPC.Document document, final boolean player, boolean schedule) {
        if (document == null) {
            return;
        }
        if (schedule) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.FileLoader$$ExternalSyntheticLambda10
                @Override // java.lang.Runnable
                public final void run() {
                    FileLoader.this.m227lambda$removeLoadingVideo$1$orgtelegrammessengerFileLoader(document, player);
                }
            });
        } else {
            m227lambda$removeLoadingVideo$1$orgtelegrammessengerFileLoader(document, player);
        }
    }

    public boolean isLoadingVideo(TLRPC.Document document, boolean player) {
        if (document != null) {
            HashMap<String, Boolean> hashMap = this.loadingVideos;
            StringBuilder sb = new StringBuilder();
            sb.append(getAttachFileName(document));
            sb.append(player ? TtmlNode.TAG_P : "");
            if (hashMap.containsKey(sb.toString())) {
                return true;
            }
        }
        return false;
    }

    public boolean isLoadingVideoAny(TLRPC.Document document) {
        return isLoadingVideo(document, false) || isLoadingVideo(document, true);
    }

    public void cancelFileUpload(final String location, final boolean enc) {
        fileLoaderQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.FileLoader$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                FileLoader.this.m218lambda$cancelFileUpload$2$orgtelegrammessengerFileLoader(enc, location);
            }
        });
    }

    /* renamed from: lambda$cancelFileUpload$2$org-telegram-messenger-FileLoader */
    public /* synthetic */ void m218lambda$cancelFileUpload$2$orgtelegrammessengerFileLoader(boolean enc, String location) {
        FileUploadOperation operation;
        if (!enc) {
            operation = this.uploadOperationPaths.get(location);
        } else {
            operation = this.uploadOperationPathsEnc.get(location);
        }
        this.uploadSizes.remove(location);
        if (operation != null) {
            this.uploadOperationPathsEnc.remove(location);
            this.uploadOperationQueue.remove(operation);
            this.uploadSmallOperationQueue.remove(operation);
            operation.cancel();
        }
    }

    public void checkUploadNewDataAvailable(final String location, final boolean encrypted, final long newAvailableSize, final long finalSize) {
        fileLoaderQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.FileLoader$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                FileLoader.this.m223x5d598c02(encrypted, location, newAvailableSize, finalSize);
            }
        });
    }

    /* renamed from: lambda$checkUploadNewDataAvailable$3$org-telegram-messenger-FileLoader */
    public /* synthetic */ void m223x5d598c02(boolean encrypted, String location, long newAvailableSize, long finalSize) {
        FileUploadOperation operation;
        if (encrypted) {
            operation = this.uploadOperationPathsEnc.get(location);
        } else {
            operation = this.uploadOperationPaths.get(location);
        }
        if (operation != null) {
            operation.checkNewDataAvailable(newAvailableSize, finalSize);
        } else if (finalSize != 0) {
            this.uploadSizes.put(location, Long.valueOf(finalSize));
        }
    }

    public void onNetworkChanged(final boolean slow) {
        fileLoaderQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.FileLoader$$ExternalSyntheticLambda13
            @Override // java.lang.Runnable
            public final void run() {
                FileLoader.this.m226lambda$onNetworkChanged$4$orgtelegrammessengerFileLoader(slow);
            }
        });
    }

    /* renamed from: lambda$onNetworkChanged$4$org-telegram-messenger-FileLoader */
    public /* synthetic */ void m226lambda$onNetworkChanged$4$orgtelegrammessengerFileLoader(boolean slow) {
        for (Map.Entry<String, FileUploadOperation> entry : this.uploadOperationPaths.entrySet()) {
            entry.getValue().onNetworkChanged(slow);
        }
        for (Map.Entry<String, FileUploadOperation> entry2 : this.uploadOperationPathsEnc.entrySet()) {
            entry2.getValue().onNetworkChanged(slow);
        }
    }

    public void uploadFile(String location, boolean encrypted, boolean small, int type) {
        uploadFile(location, encrypted, small, 0L, type, false);
    }

    public void uploadFile(final String location, final boolean encrypted, final boolean small, final long estimatedSize, final int type, final boolean forceSmallFile) {
        if (location == null) {
            return;
        }
        fileLoaderQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.FileLoader$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                FileLoader.this.m230lambda$uploadFile$5$orgtelegrammessengerFileLoader(encrypted, location, estimatedSize, type, forceSmallFile, small);
            }
        });
    }

    /* JADX WARN: Removed duplicated region for block: B:23:0x0060  */
    /* JADX WARN: Removed duplicated region for block: B:24:0x0066  */
    /* JADX WARN: Removed duplicated region for block: B:26:0x006d  */
    /* JADX WARN: Removed duplicated region for block: B:29:0x007b  */
    /* JADX WARN: Removed duplicated region for block: B:33:0x008c  */
    /* renamed from: lambda$uploadFile$5$org-telegram-messenger-FileLoader */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void m230lambda$uploadFile$5$orgtelegrammessengerFileLoader(boolean r17, java.lang.String r18, long r19, int r21, boolean r22, boolean r23) {
        /*
            r16 = this;
            r0 = r16
            r9 = r17
            r10 = r18
            r11 = r23
            if (r9 == 0) goto L13
            j$.util.concurrent.ConcurrentHashMap<java.lang.String, org.telegram.messenger.FileUploadOperation> r1 = r0.uploadOperationPathsEnc
            boolean r1 = r1.containsKey(r10)
            if (r1 == 0) goto L1c
            return
        L13:
            j$.util.concurrent.ConcurrentHashMap<java.lang.String, org.telegram.messenger.FileUploadOperation> r1 = r0.uploadOperationPaths
            boolean r1 = r1.containsKey(r10)
            if (r1 == 0) goto L1c
            return
        L1c:
            r1 = r19
            r12 = 0
            int r3 = (r1 > r12 ? 1 : (r1 == r12 ? 0 : -1))
            if (r3 == 0) goto L37
            java.util.HashMap<java.lang.String, java.lang.Long> r3 = r0.uploadSizes
            java.lang.Object r3 = r3.get(r10)
            java.lang.Long r3 = (java.lang.Long) r3
            if (r3 == 0) goto L37
            r1 = 0
            java.util.HashMap<java.lang.String, java.lang.Long> r4 = r0.uploadSizes
            r4.remove(r10)
            r14 = r1
            goto L38
        L37:
            r14 = r1
        L38:
            org.telegram.messenger.FileUploadOperation r8 = new org.telegram.messenger.FileUploadOperation
            int r2 = r0.currentAccount
            r1 = r8
            r3 = r18
            r4 = r17
            r5 = r14
            r7 = r21
            r1.<init>(r2, r3, r4, r5, r7)
            org.telegram.messenger.FileLoader$FileLoaderDelegate r1 = r0.delegate
            if (r1 == 0) goto L5d
            int r2 = (r19 > r12 ? 1 : (r19 == r12 ? 0 : -1))
            if (r2 == 0) goto L5d
            r4 = 0
            r2 = r8
            r3 = r18
            r6 = r19
            r12 = r8
            r8 = r17
            r1.fileUploadProgressChanged(r2, r3, r4, r6, r8)
            goto L5e
        L5d:
            r12 = r8
        L5e:
            if (r9 == 0) goto L66
            j$.util.concurrent.ConcurrentHashMap<java.lang.String, org.telegram.messenger.FileUploadOperation> r1 = r0.uploadOperationPathsEnc
            r1.put(r10, r12)
            goto L6b
        L66:
            j$.util.concurrent.ConcurrentHashMap<java.lang.String, org.telegram.messenger.FileUploadOperation> r1 = r0.uploadOperationPaths
            r1.put(r10, r12)
        L6b:
            if (r22 == 0) goto L70
            r12.setForceSmallFile()
        L70:
            org.telegram.messenger.FileLoader$1 r1 = new org.telegram.messenger.FileLoader$1
            r1.<init>(r9, r10, r11)
            r12.setDelegate(r1)
            r1 = 1
            if (r11 == 0) goto L8c
            int r2 = r0.currentUploadSmallOperationsCount
            if (r2 >= r1) goto L86
            int r2 = r2 + r1
            r0.currentUploadSmallOperationsCount = r2
            r12.start()
            goto L9c
        L86:
            java.util.LinkedList<org.telegram.messenger.FileUploadOperation> r1 = r0.uploadSmallOperationQueue
            r1.add(r12)
            goto L9c
        L8c:
            int r2 = r0.currentUploadOperationsCount
            if (r2 >= r1) goto L97
            int r2 = r2 + r1
            r0.currentUploadOperationsCount = r2
            r12.start()
            goto L9c
        L97:
            java.util.LinkedList<org.telegram.messenger.FileUploadOperation> r1 = r0.uploadOperationQueue
            r1.add(r12)
        L9c:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FileLoader.m230lambda$uploadFile$5$orgtelegrammessengerFileLoader(boolean, java.lang.String, long, int, boolean, boolean):void");
    }

    /* renamed from: org.telegram.messenger.FileLoader$1 */
    /* loaded from: classes4.dex */
    public class AnonymousClass1 implements FileUploadOperation.FileUploadOperationDelegate {
        final /* synthetic */ boolean val$encrypted;
        final /* synthetic */ String val$location;
        final /* synthetic */ boolean val$small;

        AnonymousClass1(boolean z, String str, boolean z2) {
            FileLoader.this = this$0;
            this.val$encrypted = z;
            this.val$location = str;
            this.val$small = z2;
        }

        @Override // org.telegram.messenger.FileUploadOperation.FileUploadOperationDelegate
        public void didFinishUploadingFile(final FileUploadOperation operation, final TLRPC.InputFile inputFile, final TLRPC.InputEncryptedFile inputEncryptedFile, final byte[] key, final byte[] iv) {
            DispatchQueue dispatchQueue = FileLoader.fileLoaderQueue;
            final boolean z = this.val$encrypted;
            final String str = this.val$location;
            final boolean z2 = this.val$small;
            dispatchQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.FileLoader$1$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    FileLoader.AnonymousClass1.this.m232xdfee369(z, str, z2, inputFile, inputEncryptedFile, key, iv, operation);
                }
            });
        }

        /* renamed from: lambda$didFinishUploadingFile$0$org-telegram-messenger-FileLoader$1 */
        public /* synthetic */ void m232xdfee369(boolean encrypted, String location, boolean small, TLRPC.InputFile inputFile, TLRPC.InputEncryptedFile inputEncryptedFile, byte[] key, byte[] iv, FileUploadOperation operation) {
            FileUploadOperation operation12;
            FileUploadOperation operation122;
            if (encrypted) {
                FileLoader.this.uploadOperationPathsEnc.remove(location);
            } else {
                FileLoader.this.uploadOperationPaths.remove(location);
            }
            if (small) {
                FileLoader.access$710(FileLoader.this);
                if (FileLoader.this.currentUploadSmallOperationsCount < 1 && (operation122 = (FileUploadOperation) FileLoader.this.uploadSmallOperationQueue.poll()) != null) {
                    FileLoader.access$708(FileLoader.this);
                    operation122.start();
                }
            } else {
                FileLoader.access$910(FileLoader.this);
                if (FileLoader.this.currentUploadOperationsCount < 1 && (operation12 = (FileUploadOperation) FileLoader.this.uploadOperationQueue.poll()) != null) {
                    FileLoader.access$908(FileLoader.this);
                    operation12.start();
                }
            }
            if (FileLoader.this.delegate != null) {
                FileLoader.this.delegate.fileDidUploaded(location, inputFile, inputEncryptedFile, key, iv, operation.getTotalFileSize());
            }
        }

        @Override // org.telegram.messenger.FileUploadOperation.FileUploadOperationDelegate
        public void didFailedUploadingFile(FileUploadOperation operation) {
            DispatchQueue dispatchQueue = FileLoader.fileLoaderQueue;
            final boolean z = this.val$encrypted;
            final String str = this.val$location;
            final boolean z2 = this.val$small;
            dispatchQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.FileLoader$1$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    FileLoader.AnonymousClass1.this.m231x2708d300(z, str, z2);
                }
            });
        }

        /* renamed from: lambda$didFailedUploadingFile$1$org-telegram-messenger-FileLoader$1 */
        public /* synthetic */ void m231x2708d300(boolean encrypted, String location, boolean small) {
            FileUploadOperation operation1;
            FileUploadOperation operation12;
            if (encrypted) {
                FileLoader.this.uploadOperationPathsEnc.remove(location);
            } else {
                FileLoader.this.uploadOperationPaths.remove(location);
            }
            if (FileLoader.this.delegate != null) {
                FileLoader.this.delegate.fileDidFailedUpload(location, encrypted);
            }
            if (small) {
                FileLoader.access$710(FileLoader.this);
                if (FileLoader.this.currentUploadSmallOperationsCount < 1 && (operation12 = (FileUploadOperation) FileLoader.this.uploadSmallOperationQueue.poll()) != null) {
                    FileLoader.access$708(FileLoader.this);
                    operation12.start();
                    return;
                }
                return;
            }
            FileLoader.access$910(FileLoader.this);
            if (FileLoader.this.currentUploadOperationsCount < 1 && (operation1 = (FileUploadOperation) FileLoader.this.uploadOperationQueue.poll()) != null) {
                FileLoader.access$908(FileLoader.this);
                operation1.start();
            }
        }

        @Override // org.telegram.messenger.FileUploadOperation.FileUploadOperationDelegate
        public void didChangedUploadProgress(FileUploadOperation operation, long uploadedSize, long totalSize) {
            if (FileLoader.this.delegate != null) {
                FileLoader.this.delegate.fileUploadProgressChanged(operation, this.val$location, uploadedSize, totalSize, this.val$encrypted);
            }
        }
    }

    private LinkedList<FileLoadOperation> getLoadOperationQueue(int datacenterId, int type) {
        SparseArray<LinkedList<FileLoadOperation>> queues;
        if (type == 3) {
            queues = this.preloadingLoadOperationQueues;
        } else if (type == 2) {
            queues = this.audioLoadOperationQueues;
        } else if (type == 1) {
            queues = this.imageLoadOperationQueues;
        } else {
            queues = this.fileLoadOperationQueues;
        }
        LinkedList<FileLoadOperation> queue = queues.get(datacenterId);
        if (queue == null) {
            LinkedList<FileLoadOperation> queue2 = new LinkedList<>();
            queues.put(datacenterId, queue2);
            return queue2;
        }
        return queue;
    }

    private SparseIntArray getLoadOperationCount(int type) {
        if (type == 3) {
            return this.preloadingLoadOperationsCount;
        }
        if (type == 2) {
            return this.audioLoadOperationsCount;
        }
        if (type == 1) {
            return this.imageLoadOperationsCount;
        }
        return this.fileLoadOperationsCount;
    }

    public void setForceStreamLoadingFile(final TLRPC.FileLocation location, final String ext) {
        if (location == null) {
            return;
        }
        fileLoaderQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.FileLoader$$ExternalSyntheticLambda12
            @Override // java.lang.Runnable
            public final void run() {
                FileLoader.this.m228xbf67814c(location, ext);
            }
        });
    }

    /* renamed from: lambda$setForceStreamLoadingFile$6$org-telegram-messenger-FileLoader */
    public /* synthetic */ void m228xbf67814c(TLRPC.FileLocation location, String ext) {
        String attachFileName = getAttachFileName(location, ext);
        this.forceLoadingFile = attachFileName;
        FileLoadOperation operation = this.loadOperationPaths.get(attachFileName);
        if (operation != null) {
            if (operation.isPreloadVideoOperation()) {
                operation.setIsPreloadVideoOperation(false);
            }
            operation.setForceRequest(true);
            int datacenterId = operation.getDatacenterId();
            int queueType = operation.getQueueType();
            LinkedList<FileLoadOperation> downloadQueue = getLoadOperationQueue(datacenterId, queueType);
            SparseIntArray count = getLoadOperationCount(queueType);
            int index = downloadQueue.indexOf(operation);
            if (index >= 0) {
                downloadQueue.remove(index);
                if (operation.start()) {
                    count.put(datacenterId, count.get(datacenterId) + 1);
                }
                if (queueType == 0 && operation.wasStarted() && !this.activeFileLoadOperation.contains(operation)) {
                    pauseCurrentFileLoadOperations(operation);
                    this.activeFileLoadOperation.add(operation);
                    return;
                }
                return;
            }
            pauseCurrentFileLoadOperations(operation);
            operation.start();
            if (queueType == 0 && !this.activeFileLoadOperation.contains(operation)) {
                this.activeFileLoadOperation.add(operation);
            }
        }
    }

    public void cancelLoadFile(TLRPC.Document document) {
        cancelLoadFile(document, false);
    }

    public void cancelLoadFile(TLRPC.Document document, boolean deleteFile) {
        cancelLoadFile(document, null, null, null, null, null, deleteFile);
    }

    public void cancelLoadFile(SecureDocument document) {
        cancelLoadFile(null, document, null, null, null, null, false);
    }

    public void cancelLoadFile(WebFile document) {
        cancelLoadFile(null, null, document, null, null, null, false);
    }

    public void cancelLoadFile(TLRPC.PhotoSize photo) {
        cancelLoadFile(photo, false);
    }

    public void cancelLoadFile(TLRPC.PhotoSize photo, boolean deleteFile) {
        cancelLoadFile(null, null, null, photo.location, null, null, deleteFile);
    }

    public void cancelLoadFile(TLRPC.FileLocation location, String ext) {
        cancelLoadFile(location, ext, false);
    }

    public void cancelLoadFile(TLRPC.FileLocation location, String ext, boolean deleteFile) {
        cancelLoadFile(null, null, null, location, ext, null, deleteFile);
    }

    public void cancelLoadFile(String fileName) {
        cancelLoadFile(null, null, null, null, null, fileName, true);
    }

    public void cancelLoadFiles(ArrayList<String> fileNames) {
        int N = fileNames.size();
        for (int a = 0; a < N; a++) {
            cancelLoadFile(null, null, null, null, null, fileNames.get(a), true);
        }
    }

    private void cancelLoadFile(TLRPC.Document document, SecureDocument secureDocument, WebFile webDocument, TLRPC.FileLocation location, String locationExt, String name, final boolean deleteFile) {
        final String fileName;
        if (location == null && document == null && webDocument == null && secureDocument == null && TextUtils.isEmpty(name)) {
            return;
        }
        if (location != null) {
            fileName = getAttachFileName(location, locationExt);
        } else if (document != null) {
            fileName = getAttachFileName(document);
        } else if (secureDocument != null) {
            fileName = getAttachFileName(secureDocument);
        } else if (webDocument != null) {
            fileName = getAttachFileName(webDocument);
        } else {
            fileName = name;
        }
        boolean removed = this.loadOperationPathsUI.remove(fileName) != null;
        fileLoaderQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.FileLoader$$ExternalSyntheticLambda7
            @Override // java.lang.Runnable
            public final void run() {
                FileLoader.this.m219lambda$cancelLoadFile$7$orgtelegrammessengerFileLoader(fileName, deleteFile);
            }
        });
        if (removed && document != null) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.FileLoader$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    FileLoader.this.m220lambda$cancelLoadFile$8$orgtelegrammessengerFileLoader();
                }
            });
        }
    }

    /* renamed from: lambda$cancelLoadFile$7$org-telegram-messenger-FileLoader */
    public /* synthetic */ void m219lambda$cancelLoadFile$7$orgtelegrammessengerFileLoader(String fileName, boolean deleteFile) {
        FileLoadOperation operation = this.loadOperationPaths.remove(fileName);
        if (operation != null) {
            int queueType = operation.getQueueType();
            int datacenterId = operation.getDatacenterId();
            LinkedList<FileLoadOperation> queue = getLoadOperationQueue(datacenterId, queueType);
            if (!queue.remove(operation)) {
                SparseIntArray count = getLoadOperationCount(queueType);
                count.put(datacenterId, count.get(datacenterId) - 1);
            }
            if (queueType == 0) {
                this.activeFileLoadOperation.remove(operation);
            }
            operation.cancel(deleteFile);
        }
    }

    /* renamed from: lambda$cancelLoadFile$8$org-telegram-messenger-FileLoader */
    public /* synthetic */ void m220lambda$cancelLoadFile$8$orgtelegrammessengerFileLoader() {
        getNotificationCenter().postNotificationName(NotificationCenter.onDownloadingFilesChanged, new Object[0]);
    }

    public boolean isLoadingFile(String fileName) {
        return fileName != null && this.loadOperationPathsUI.containsKey(fileName);
    }

    public float getBufferedProgressFromPosition(float position, String fileName) {
        FileLoadOperation loadOperation;
        if (!TextUtils.isEmpty(fileName) && (loadOperation = this.loadOperationPaths.get(fileName)) != null) {
            return loadOperation.getDownloadedLengthFromOffset(position);
        }
        return 0.0f;
    }

    public void loadFile(ImageLocation imageLocation, Object parentObject, String ext, int priority, int cacheType) {
        int cacheType2;
        if (imageLocation == null) {
            return;
        }
        if (cacheType == 0 && (imageLocation.isEncrypted() || (imageLocation.photoSize != null && imageLocation.getSize() == 0))) {
            cacheType2 = 1;
        } else {
            cacheType2 = cacheType;
        }
        loadFile(imageLocation.document, imageLocation.secureDocument, imageLocation.webFile, imageLocation.location, imageLocation, parentObject, ext, imageLocation.getSize(), priority, cacheType2);
    }

    public void loadFile(SecureDocument secureDocument, int priority) {
        if (secureDocument == null) {
            return;
        }
        loadFile(null, secureDocument, null, null, null, null, null, 0L, priority, 1);
    }

    public void loadFile(TLRPC.Document document, Object parentObject, int priority, int cacheType) {
        int cacheType2;
        if (document == null) {
            return;
        }
        if (cacheType == 0 && document.key != null) {
            cacheType2 = 1;
        } else {
            cacheType2 = cacheType;
        }
        loadFile(document, null, null, null, null, parentObject, null, 0L, priority, cacheType2);
    }

    public void loadFile(WebFile document, int priority, int cacheType) {
        loadFile(null, null, document, null, null, null, null, 0L, priority, cacheType);
    }

    private void pauseCurrentFileLoadOperations(FileLoadOperation newOperation) {
        int a = 0;
        while (a < this.activeFileLoadOperation.size()) {
            FileLoadOperation operation = this.activeFileLoadOperation.get(a);
            if (operation != newOperation && operation.getDatacenterId() == newOperation.getDatacenterId() && !operation.getFileName().equals(this.forceLoadingFile)) {
                this.activeFileLoadOperation.remove(operation);
                a--;
                int datacenterId = operation.getDatacenterId();
                int queueType = operation.getQueueType();
                LinkedList<FileLoadOperation> downloadQueue = getLoadOperationQueue(datacenterId, queueType);
                SparseIntArray count = getLoadOperationCount(queueType);
                downloadQueue.add(0, operation);
                if (operation.wasStarted()) {
                    count.put(datacenterId, count.get(datacenterId) - 1);
                }
                operation.pause();
            }
            a++;
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:109:0x026a, code lost:
        if (r42.imageType == 2) goto L111;
     */
    /* JADX WARN: Code restructure failed: missing block: B:137:0x02d5, code lost:
        if (r5 == 2) goto L139;
     */
    /* JADX WARN: Removed duplicated region for block: B:156:0x031d  */
    /* JADX WARN: Removed duplicated region for block: B:226:0x044a  */
    /* JADX WARN: Removed duplicated region for block: B:228:0x0482  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private org.telegram.messenger.FileLoadOperation loadFileInternal(final org.telegram.tgnet.TLRPC.Document r38, org.telegram.messenger.SecureDocument r39, org.telegram.messenger.WebFile r40, org.telegram.tgnet.TLRPC.TL_fileLocationToBeDeprecated r41, org.telegram.messenger.ImageLocation r42, final java.lang.Object r43, java.lang.String r44, long r45, int r47, org.telegram.messenger.FileLoadOperationStream r48, int r49, boolean r50, int r51) {
        /*
            Method dump skipped, instructions count: 1169
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FileLoader.loadFileInternal(org.telegram.tgnet.TLRPC$Document, org.telegram.messenger.SecureDocument, org.telegram.messenger.WebFile, org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated, org.telegram.messenger.ImageLocation, java.lang.Object, java.lang.String, long, int, org.telegram.messenger.FileLoadOperationStream, int, boolean, int):org.telegram.messenger.FileLoadOperation");
    }

    private boolean canSaveAsFile(Object parentObject) {
        if (parentObject instanceof MessageObject) {
            MessageObject messageObject = (MessageObject) parentObject;
            return messageObject.isDocument();
        }
        return false;
    }

    private boolean canSaveToPublicStorage(Object parentObject) {
        int flag;
        if (SharedConfig.saveToGalleryFlags != 0 && !BuildVars.NO_SCOPED_STORAGE && (parentObject instanceof MessageObject)) {
            MessageObject messageObject = (MessageObject) parentObject;
            long dialogId = messageObject.getDialogId();
            if (messageObject.isAnyKindOfSticker() || getMessagesController().isChatNoForwards(getMessagesController().getChat(Long.valueOf(-dialogId))) || messageObject.messageOwner.noforwards) {
                return false;
            }
            if (dialogId >= 0) {
                flag = 1;
            } else if (ChatObject.isChannelAndNotMegaGroup(getMessagesController().getChat(Long.valueOf(-dialogId)))) {
                flag = 4;
            } else {
                flag = 2;
            }
            if ((SharedConfig.saveToGalleryFlags & flag) != 0) {
                return true;
            }
        }
        return false;
    }

    private void addOperationToQueue(FileLoadOperation operation, LinkedList<FileLoadOperation> queue) {
        int priority = operation.getPriority();
        if (priority > 0) {
            int index = queue.size();
            int a = 0;
            int size = queue.size();
            while (true) {
                if (a >= size) {
                    break;
                }
                FileLoadOperation queuedOperation = queue.get(a);
                if (queuedOperation.getPriority() >= priority) {
                    a++;
                } else {
                    index = a;
                    break;
                }
            }
            queue.add(index, operation);
            return;
        }
        queue.add(operation);
    }

    private void loadFile(final TLRPC.Document document, final SecureDocument secureDocument, final WebFile webDocument, final TLRPC.TL_fileLocationToBeDeprecated location, final ImageLocation imageLocation, final Object parentObject, final String locationExt, final long locationSize, final int priority, final int cacheType) {
        String fileName;
        if (location != null) {
            fileName = getAttachFileName(location, locationExt);
        } else if (document != null) {
            fileName = getAttachFileName(document);
        } else if (webDocument != null) {
            fileName = getAttachFileName(webDocument);
        } else {
            fileName = null;
        }
        if (cacheType != 10 && !TextUtils.isEmpty(fileName) && !fileName.contains("-2147483648")) {
            this.loadOperationPathsUI.put(fileName, true);
        }
        fileLoaderQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.FileLoader$$ExternalSyntheticLambda9
            @Override // java.lang.Runnable
            public final void run() {
                FileLoader.this.m224lambda$loadFile$9$orgtelegrammessengerFileLoader(document, secureDocument, webDocument, location, imageLocation, parentObject, locationExt, locationSize, priority, cacheType);
            }
        });
    }

    /* renamed from: lambda$loadFile$9$org-telegram-messenger-FileLoader */
    public /* synthetic */ void m224lambda$loadFile$9$orgtelegrammessengerFileLoader(TLRPC.Document document, SecureDocument secureDocument, WebFile webDocument, TLRPC.TL_fileLocationToBeDeprecated location, ImageLocation imageLocation, Object parentObject, String locationExt, long locationSize, int priority, int cacheType) {
        loadFileInternal(document, secureDocument, webDocument, location, imageLocation, parentObject, locationExt, locationSize, priority, null, 0, false, cacheType);
    }

    public FileLoadOperation loadStreamFile(final FileLoadOperationStream stream, final TLRPC.Document document, final ImageLocation location, final Object parentObject, final int offset, final boolean priority) {
        final CountDownLatch semaphore = new CountDownLatch(1);
        final FileLoadOperation[] result = new FileLoadOperation[1];
        fileLoaderQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.FileLoader$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                FileLoader.this.m225lambda$loadStreamFile$10$orgtelegrammessengerFileLoader(result, document, location, parentObject, stream, offset, priority, semaphore);
            }
        });
        try {
            semaphore.await();
        } catch (Exception e) {
            FileLog.e((Throwable) e, false);
        }
        return result[0];
    }

    /* renamed from: lambda$loadStreamFile$10$org-telegram-messenger-FileLoader */
    public /* synthetic */ void m225lambda$loadStreamFile$10$orgtelegrammessengerFileLoader(FileLoadOperation[] result, TLRPC.Document document, ImageLocation location, Object parentObject, FileLoadOperationStream stream, int offset, boolean priority, CountDownLatch semaphore) {
        String str = null;
        TLRPC.TL_fileLocationToBeDeprecated tL_fileLocationToBeDeprecated = (document != null || location == null) ? null : location.location;
        if (document == null && location != null) {
            str = "mp4";
        }
        result[0] = loadFileInternal(document, null, null, tL_fileLocationToBeDeprecated, location, parentObject, str, (document != null || location == null) ? 0L : location.currentSize, 1, stream, offset, priority, document == null ? 1 : 0);
        semaphore.countDown();
    }

    public void checkDownloadQueue(final int datacenterId, final int queueType, final String fileName) {
        fileLoaderQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.FileLoader$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                FileLoader.this.m222lambda$checkDownloadQueue$11$orgtelegrammessengerFileLoader(fileName, datacenterId, queueType);
            }
        });
    }

    /* renamed from: lambda$checkDownloadQueue$11$org-telegram-messenger-FileLoader */
    public /* synthetic */ void m222lambda$checkDownloadQueue$11$orgtelegrammessengerFileLoader(String fileName, int datacenterId, int queueType) {
        FileLoadOperation operation = this.loadOperationPaths.remove(fileName);
        LinkedList<FileLoadOperation> queue = getLoadOperationQueue(datacenterId, queueType);
        SparseIntArray operationCount = getLoadOperationCount(queueType);
        int count = operationCount.get(datacenterId);
        if (operation != null) {
            if (operation.wasStarted()) {
                count--;
                operationCount.put(datacenterId, count);
            } else {
                queue.remove(operation);
            }
            if (queueType == 0) {
                this.activeFileLoadOperation.remove(operation);
            }
        }
        while (!queue.isEmpty()) {
            FileLoadOperation operation2 = queue.get(0);
            int maxCount = 6;
            int i = 3;
            if (queueType == 3) {
                if (operation2.getPriority() == 0) {
                    maxCount = 2;
                }
            } else if (queueType == 2) {
                if (operation2.getPriority() == 0) {
                    i = 1;
                }
                maxCount = i;
            } else if (queueType == 1) {
                if (operation2.getPriority() == 0) {
                    maxCount = 2;
                }
            } else {
                if (!operation2.isForceRequest()) {
                    i = 1;
                }
                maxCount = i;
            }
            if (count < maxCount) {
                FileLoadOperation operation3 = queue.poll();
                if (operation3 != null && operation3.start()) {
                    count++;
                    operationCount.put(datacenterId, count);
                    if (queueType == 0 && !this.activeFileLoadOperation.contains(operation3)) {
                        this.activeFileLoadOperation.add(operation3);
                    }
                }
            } else {
                return;
            }
        }
    }

    public void setDelegate(FileLoaderDelegate fileLoaderDelegate) {
        this.delegate = fileLoaderDelegate;
    }

    public static String getMessageFileName(TLRPC.Message message) {
        TLRPC.WebDocument document;
        TLRPC.PhotoSize sizeFull;
        TLRPC.PhotoSize sizeFull2;
        TLRPC.PhotoSize sizeFull3;
        if (message == null) {
            return "";
        }
        if (message instanceof TLRPC.TL_messageService) {
            if (message.action.photo != null) {
                ArrayList<TLRPC.PhotoSize> sizes = message.action.photo.sizes;
                if (sizes.size() > 0 && (sizeFull3 = getClosestPhotoSizeWithSize(sizes, AndroidUtilities.getPhotoSize())) != null) {
                    return getAttachFileName(sizeFull3);
                }
            }
        } else if (message.media instanceof TLRPC.TL_messageMediaDocument) {
            return getAttachFileName(message.media.document);
        } else {
            if (message.media instanceof TLRPC.TL_messageMediaPhoto) {
                ArrayList<TLRPC.PhotoSize> sizes2 = message.media.photo.sizes;
                if (sizes2.size() > 0 && (sizeFull2 = getClosestPhotoSizeWithSize(sizes2, AndroidUtilities.getPhotoSize(), false, null, true)) != null) {
                    return getAttachFileName(sizeFull2);
                }
            } else if (message.media instanceof TLRPC.TL_messageMediaWebPage) {
                if (message.media.webpage.document != null) {
                    return getAttachFileName(message.media.webpage.document);
                }
                if (message.media.webpage.photo != null) {
                    ArrayList<TLRPC.PhotoSize> sizes3 = message.media.webpage.photo.sizes;
                    if (sizes3.size() > 0 && (sizeFull = getClosestPhotoSizeWithSize(sizes3, AndroidUtilities.getPhotoSize())) != null) {
                        return getAttachFileName(sizeFull);
                    }
                }
            } else if ((message.media instanceof TLRPC.TL_messageMediaInvoice) && (document = ((TLRPC.TL_messageMediaInvoice) message.media).photo) != null) {
                return Utilities.MD5(document.url) + "." + ImageLoader.getHttpUrlExtension(document.url, getMimeTypePart(document.mime_type));
            }
        }
        return "";
    }

    public File getPathToMessage(TLRPC.Message message) {
        return getPathToMessage(message, true);
    }

    public File getPathToMessage(TLRPC.Message message, boolean useFileDatabaseQueue) {
        TLRPC.PhotoSize sizeFull;
        TLRPC.PhotoSize sizeFull2;
        TLRPC.PhotoSize sizeFull3;
        if (message == null) {
            return new File("");
        }
        boolean z = false;
        if (message instanceof TLRPC.TL_messageService) {
            if (message.action.photo != null) {
                ArrayList<TLRPC.PhotoSize> sizes = message.action.photo.sizes;
                if (sizes.size() > 0 && (sizeFull3 = getClosestPhotoSizeWithSize(sizes, AndroidUtilities.getPhotoSize())) != null) {
                    return getPathToAttach(sizeFull3, null, false, useFileDatabaseQueue);
                }
            }
        } else if (message.media instanceof TLRPC.TL_messageMediaDocument) {
            TLRPC.Document document = message.media.document;
            if (message.media.ttl_seconds != 0) {
                z = true;
            }
            return getPathToAttach(document, null, z, useFileDatabaseQueue);
        } else if (message.media instanceof TLRPC.TL_messageMediaPhoto) {
            ArrayList<TLRPC.PhotoSize> sizes2 = message.media.photo.sizes;
            if (sizes2.size() > 0 && (sizeFull2 = getClosestPhotoSizeWithSize(sizes2, AndroidUtilities.getPhotoSize(), false, null, true)) != null) {
                if (message.media.ttl_seconds != 0) {
                    z = true;
                }
                return getPathToAttach(sizeFull2, null, z, useFileDatabaseQueue);
            }
        } else if (message.media instanceof TLRPC.TL_messageMediaWebPage) {
            if (message.media.webpage.document != null) {
                return getPathToAttach(message.media.webpage.document, null, false, useFileDatabaseQueue);
            }
            if (message.media.webpage.photo != null) {
                ArrayList<TLRPC.PhotoSize> sizes3 = message.media.webpage.photo.sizes;
                if (sizes3.size() > 0 && (sizeFull = getClosestPhotoSizeWithSize(sizes3, AndroidUtilities.getPhotoSize())) != null) {
                    return getPathToAttach(sizeFull, null, false, useFileDatabaseQueue);
                }
            }
        } else if (message.media instanceof TLRPC.TL_messageMediaInvoice) {
            return getPathToAttach(((TLRPC.TL_messageMediaInvoice) message.media).photo, null, true, useFileDatabaseQueue);
        }
        return new File("");
    }

    public File getPathToAttach(TLObject attach) {
        return getPathToAttach(attach, null, false);
    }

    public File getPathToAttach(TLObject attach, boolean forceCache) {
        return getPathToAttach(attach, null, forceCache);
    }

    public File getPathToAttach(TLObject attach, String ext, boolean forceCache) {
        return getPathToAttach(attach, null, ext, forceCache, true);
    }

    public File getPathToAttach(TLObject attach, String ext, boolean forceCache, boolean useFileDatabaseQueue) {
        return getPathToAttach(attach, null, ext, forceCache, useFileDatabaseQueue);
    }

    public File getPathToAttach(TLObject attach, String size, String ext, boolean forceCache, boolean useFileDatabaseQueue) {
        int type;
        int type2;
        long documentId;
        File dir;
        String path;
        String size2;
        File dir2;
        int type3;
        File dir3;
        File dir4;
        int type4;
        File dir5 = null;
        long documentId2 = 0;
        int dcId = 0;
        int type5 = 0;
        if (forceCache) {
            File dir6 = getDirectory(4);
            type = 0;
            type2 = 0;
            documentId = 0;
            dir = dir6;
        } else if (!(attach instanceof TLRPC.Document)) {
            if (!(attach instanceof TLRPC.Photo)) {
                if (!(attach instanceof TLRPC.PhotoSize)) {
                    if (!(attach instanceof TLRPC.TL_videoSize)) {
                        if (!(attach instanceof TLRPC.FileLocation)) {
                            if ((attach instanceof TLRPC.UserProfilePhoto) || (attach instanceof TLRPC.ChatPhoto)) {
                                if (size != null) {
                                    size2 = size;
                                } else {
                                    size2 = "s";
                                }
                                if ("s".equals(size2)) {
                                    File dir7 = getDirectory(4);
                                    type = 0;
                                    type2 = 0;
                                    documentId = 0;
                                    dir = dir7;
                                } else {
                                    File dir8 = getDirectory(0);
                                    type = 0;
                                    type2 = 0;
                                    documentId = 0;
                                    dir = dir8;
                                }
                            } else {
                                if (attach instanceof WebFile) {
                                    WebFile document = (WebFile) attach;
                                    if (document.mime_type.startsWith("image/")) {
                                        dir5 = getDirectory(0);
                                    } else if (document.mime_type.startsWith("audio/")) {
                                        dir5 = getDirectory(1);
                                    } else if (document.mime_type.startsWith("video/")) {
                                        dir5 = getDirectory(2);
                                    } else {
                                        dir5 = getDirectory(3);
                                    }
                                } else if ((attach instanceof TLRPC.TL_secureFile) || (attach instanceof SecureDocument)) {
                                    File dir9 = getDirectory(4);
                                    type = 0;
                                    type2 = 0;
                                    documentId = 0;
                                    dir = dir9;
                                }
                                type = 0;
                                type2 = 0;
                                documentId = 0;
                                dir = dir5;
                            }
                        } else {
                            TLRPC.FileLocation fileLocation = (TLRPC.FileLocation) attach;
                            if (fileLocation.key != null || (fileLocation.volume_id == -2147483648L && fileLocation.local_id < 0)) {
                                dir2 = getDirectory(4);
                            } else {
                                documentId2 = fileLocation.volume_id;
                                dcId = fileLocation.dc_id;
                                type5 = 0;
                                dir2 = getDirectory(0);
                            }
                            type = type5;
                            type2 = dcId;
                            documentId = documentId2;
                            dir = dir2;
                        }
                    } else {
                        TLRPC.TL_videoSize videoSize = (TLRPC.TL_videoSize) attach;
                        if (videoSize.location == null || videoSize.location.key != null || ((videoSize.location.volume_id == -2147483648L && videoSize.location.local_id < 0) || videoSize.size < 0)) {
                            type3 = 4;
                            dir3 = getDirectory(4);
                        } else {
                            type3 = 0;
                            dir3 = getDirectory(0);
                        }
                        long documentId3 = videoSize.location.volume_id;
                        int dcId2 = videoSize.location.dc_id;
                        type = type3;
                        type2 = dcId2;
                        documentId = documentId3;
                        dir = dir3;
                    }
                } else {
                    TLRPC.PhotoSize photoSize = (TLRPC.PhotoSize) attach;
                    if ((photoSize instanceof TLRPC.TL_photoStrippedSize) || (photoSize instanceof TLRPC.TL_photoPathSize)) {
                        dir4 = null;
                    } else if (photoSize.location == null || photoSize.location.key != null || ((photoSize.location.volume_id == -2147483648L && photoSize.location.local_id < 0) || photoSize.size < 0)) {
                        type5 = 4;
                        dir4 = getDirectory(4);
                    } else {
                        type5 = 0;
                        dir4 = getDirectory(0);
                    }
                    long documentId4 = photoSize.location.volume_id;
                    int dcId3 = photoSize.location.dc_id;
                    type = type5;
                    type2 = dcId3;
                    documentId = documentId4;
                    dir = dir4;
                }
            } else {
                return getPathToAttach(getClosestPhotoSizeWithSize(((TLRPC.Photo) attach).sizes, AndroidUtilities.getPhotoSize()), ext, false, useFileDatabaseQueue);
            }
        } else {
            TLRPC.Document document2 = (TLRPC.Document) attach;
            if (!TextUtils.isEmpty(document2.localPath)) {
                return new File(document2.localPath);
            }
            if (document2.key != null) {
                type4 = 4;
            } else if (MessageObject.isVoiceDocument(document2)) {
                type4 = 1;
            } else if (MessageObject.isVideoDocument(document2)) {
                type4 = 2;
            } else {
                type4 = 3;
            }
            long documentId5 = document2.id;
            int dcId4 = document2.dc_id;
            File dir10 = getDirectory(type4);
            type = type4;
            type2 = dcId4;
            documentId = documentId5;
            dir = dir10;
        }
        if (dir == null) {
            return new File("");
        }
        if (documentId != 0 && (path = getInstance(UserConfig.selectedAccount).getFileDatabase().getPath(documentId, type2, type, useFileDatabaseQueue)) != null) {
            return new File(path);
        }
        return new File(dir, getAttachFileName(attach, ext));
    }

    public FilePathDatabase getFileDatabase() {
        return this.filePathDatabase;
    }

    public static TLRPC.PhotoSize getClosestPhotoSizeWithSize(ArrayList<TLRPC.PhotoSize> sizes, int side) {
        return getClosestPhotoSizeWithSize(sizes, side, false);
    }

    public static TLRPC.PhotoSize getClosestPhotoSizeWithSize(ArrayList<TLRPC.PhotoSize> sizes, int side, boolean byMinSide) {
        return getClosestPhotoSizeWithSize(sizes, side, byMinSide, null, false);
    }

    public static TLRPC.PhotoSize getClosestPhotoSizeWithSize(ArrayList<TLRPC.PhotoSize> sizes, int side, boolean byMinSide, TLRPC.PhotoSize toIgnore, boolean ignoreStripped) {
        if (sizes == null || sizes.isEmpty()) {
            return null;
        }
        int lastSide = 0;
        TLRPC.PhotoSize closestObject = null;
        for (int a = 0; a < sizes.size(); a++) {
            TLRPC.PhotoSize obj = sizes.get(a);
            if (obj != null && obj != toIgnore && !(obj instanceof TLRPC.TL_photoSizeEmpty) && !(obj instanceof TLRPC.TL_photoPathSize) && (!ignoreStripped || !(obj instanceof TLRPC.TL_photoStrippedSize))) {
                if (byMinSide) {
                    int currentSide = Math.min(obj.h, obj.w);
                    if (closestObject == null || ((side > 100 && closestObject.location != null && closestObject.location.dc_id == Integer.MIN_VALUE) || (obj instanceof TLRPC.TL_photoCachedSize) || (side > lastSide && lastSide < currentSide))) {
                        closestObject = obj;
                        lastSide = currentSide;
                    }
                } else {
                    int currentSide2 = Math.max(obj.w, obj.h);
                    if (closestObject == null || ((side > 100 && closestObject.location != null && closestObject.location.dc_id == Integer.MIN_VALUE) || (obj instanceof TLRPC.TL_photoCachedSize) || (currentSide2 <= side && lastSide < currentSide2))) {
                        closestObject = obj;
                        lastSide = currentSide2;
                    }
                }
            }
        }
        return closestObject;
    }

    public static TLRPC.TL_photoPathSize getPathPhotoSize(ArrayList<TLRPC.PhotoSize> sizes) {
        if (sizes == null || sizes.isEmpty()) {
            return null;
        }
        for (int a = 0; a < sizes.size(); a++) {
            TLRPC.PhotoSize obj = sizes.get(a);
            if (!(obj instanceof TLRPC.TL_photoPathSize)) {
                return (TLRPC.TL_photoPathSize) obj;
            }
        }
        return null;
    }

    public static String getFileExtension(File file) {
        String name = file.getName();
        try {
            return name.substring(name.lastIndexOf(46) + 1);
        } catch (Exception e) {
            return "";
        }
    }

    public static String fixFileName(String fileName) {
        if (fileName != null) {
            return fileName.replaceAll("[\u0001-\u001f<>\u202e:\"/\\\\|?*\u007f]+", "").trim();
        }
        return fileName;
    }

    public static String getDocumentFileName(TLRPC.Document document) {
        if (document == null) {
            return null;
        }
        if (document.file_name_fixed != null) {
            return document.file_name_fixed;
        }
        String fileName = null;
        if (document != null) {
            if (document.file_name != null) {
                fileName = document.file_name;
            } else {
                for (int a = 0; a < document.attributes.size(); a++) {
                    TLRPC.DocumentAttribute documentAttribute = document.attributes.get(a);
                    if (documentAttribute instanceof TLRPC.TL_documentAttributeFilename) {
                        fileName = documentAttribute.file_name;
                    }
                }
            }
        }
        String fileName2 = fixFileName(fileName);
        return fileName2 != null ? fileName2 : "";
    }

    public static String getMimeTypePart(String mime) {
        int index = mime.lastIndexOf(47);
        if (index != -1) {
            return mime.substring(index + 1);
        }
        return "";
    }

    public static String getExtensionByMimeType(String mime) {
        if (mime != null) {
            char c = 65535;
            switch (mime.hashCode()) {
                case 187091926:
                    if (mime.equals("audio/ogg")) {
                        c = 2;
                        break;
                    }
                    break;
                case 1331848029:
                    if (mime.equals(MimeTypes.VIDEO_MP4)) {
                        c = 0;
                        break;
                    }
                    break;
                case 2039520277:
                    if (mime.equals("video/x-matroska")) {
                        c = 1;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                    return ".mp4";
                case 1:
                    return ".mkv";
                case 2:
                    return ".ogg";
                default:
                    return "";
            }
        }
        return "";
    }

    public static File getInternalCacheDir() {
        return ApplicationLoader.applicationContext.getCacheDir();
    }

    public static String getDocumentExtension(TLRPC.Document document) {
        String fileName = getDocumentFileName(document);
        int idx = fileName.lastIndexOf(46);
        String ext = null;
        if (idx != -1) {
            ext = fileName.substring(idx + 1);
        }
        if (ext == null || ext.length() == 0) {
            ext = document.mime_type;
        }
        if (ext == null) {
            ext = "";
        }
        return ext.toUpperCase();
    }

    public static String getAttachFileName(TLObject attach) {
        return getAttachFileName(attach, null);
    }

    public static String getAttachFileName(TLObject attach, String ext) {
        return getAttachFileName(attach, null, ext);
    }

    public static String getAttachFileName(TLObject attach, String size, String ext) {
        String docExt;
        if (attach instanceof TLRPC.Document) {
            TLRPC.Document document = (TLRPC.Document) attach;
            String docExt2 = getDocumentFileName(document);
            int idx = docExt2.lastIndexOf(46);
            if (idx == -1) {
                docExt = "";
            } else {
                docExt = docExt2.substring(idx);
            }
            if (docExt.length() <= 1) {
                docExt = getExtensionByMimeType(document.mime_type);
            }
            if (docExt.length() > 1) {
                return document.dc_id + "_" + document.id + docExt;
            }
            return document.dc_id + "_" + document.id;
        } else if (attach instanceof SecureDocument) {
            SecureDocument secureDocument = (SecureDocument) attach;
            return secureDocument.secureFile.dc_id + "_" + secureDocument.secureFile.id + ".jpg";
        } else if (attach instanceof TLRPC.TL_secureFile) {
            TLRPC.TL_secureFile secureFile = (TLRPC.TL_secureFile) attach;
            return secureFile.dc_id + "_" + secureFile.id + ".jpg";
        } else if (attach instanceof WebFile) {
            WebFile document2 = (WebFile) attach;
            return Utilities.MD5(document2.url) + "." + ImageLoader.getHttpUrlExtension(document2.url, getMimeTypePart(document2.mime_type));
        } else {
            String str = "jpg";
            if (attach instanceof TLRPC.PhotoSize) {
                TLRPC.PhotoSize photo = (TLRPC.PhotoSize) attach;
                if (photo.location == null || (photo.location instanceof TLRPC.TL_fileLocationUnavailable)) {
                    return "";
                }
                StringBuilder sb = new StringBuilder();
                sb.append(photo.location.volume_id);
                sb.append("_");
                sb.append(photo.location.local_id);
                sb.append(".");
                if (ext != null) {
                    str = ext;
                }
                sb.append(str);
                return sb.toString();
            } else if (attach instanceof TLRPC.TL_videoSize) {
                TLRPC.TL_videoSize video = (TLRPC.TL_videoSize) attach;
                if (video.location == null || (video.location instanceof TLRPC.TL_fileLocationUnavailable)) {
                    return "";
                }
                StringBuilder sb2 = new StringBuilder();
                sb2.append(video.location.volume_id);
                sb2.append("_");
                sb2.append(video.location.local_id);
                sb2.append(".");
                sb2.append(ext != null ? ext : "mp4");
                return sb2.toString();
            } else if (attach instanceof TLRPC.FileLocation) {
                if (attach instanceof TLRPC.TL_fileLocationUnavailable) {
                    return "";
                }
                TLRPC.FileLocation location = (TLRPC.FileLocation) attach;
                StringBuilder sb3 = new StringBuilder();
                sb3.append(location.volume_id);
                sb3.append("_");
                sb3.append(location.local_id);
                sb3.append(".");
                if (ext != null) {
                    str = ext;
                }
                sb3.append(str);
                return sb3.toString();
            } else if (attach instanceof TLRPC.UserProfilePhoto) {
                if (size == null) {
                    size = "s";
                }
                TLRPC.UserProfilePhoto location2 = (TLRPC.UserProfilePhoto) attach;
                if (location2.photo_small != null) {
                    if ("s".equals(size)) {
                        return getAttachFileName(location2.photo_small, ext);
                    }
                    return getAttachFileName(location2.photo_big, ext);
                }
                StringBuilder sb4 = new StringBuilder();
                sb4.append(location2.photo_id);
                sb4.append("_");
                sb4.append(size);
                sb4.append(".");
                if (ext != null) {
                    str = ext;
                }
                sb4.append(str);
                return sb4.toString();
            } else if (!(attach instanceof TLRPC.ChatPhoto)) {
                return "";
            } else {
                TLRPC.ChatPhoto location3 = (TLRPC.ChatPhoto) attach;
                if (location3.photo_small != null) {
                    if ("s".equals(size)) {
                        return getAttachFileName(location3.photo_small, ext);
                    }
                    return getAttachFileName(location3.photo_big, ext);
                }
                StringBuilder sb5 = new StringBuilder();
                sb5.append(location3.photo_id);
                sb5.append("_");
                sb5.append(size);
                sb5.append(".");
                if (ext != null) {
                    str = ext;
                }
                sb5.append(str);
                return sb5.toString();
            }
        }
    }

    public void deleteFiles(final ArrayList<File> files, final int type) {
        if (files == null || files.isEmpty()) {
            return;
        }
        fileLoaderQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.FileLoader$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                FileLoader.lambda$deleteFiles$12(files, type);
            }
        });
    }

    public static /* synthetic */ void lambda$deleteFiles$12(ArrayList files, int type) {
        for (int a = 0; a < files.size(); a++) {
            File file = (File) files.get(a);
            File encrypted = new File(file.getAbsolutePath() + ".enc");
            if (encrypted.exists()) {
                try {
                    if (!encrypted.delete()) {
                        encrypted.deleteOnExit();
                    }
                } catch (Exception e) {
                    FileLog.e(e);
                }
                try {
                    File internalCacheDir = getInternalCacheDir();
                    File key = new File(internalCacheDir, file.getName() + ".enc.key");
                    if (!key.delete()) {
                        key.deleteOnExit();
                    }
                } catch (Exception e2) {
                    FileLog.e(e2);
                }
            } else if (file.exists()) {
                try {
                    if (!file.delete()) {
                        file.deleteOnExit();
                    }
                } catch (Exception e3) {
                    FileLog.e(e3);
                }
            }
            try {
                File parentFile = file.getParentFile();
                File qFile = new File(parentFile, "q_" + file.getName());
                if (qFile.exists() && !qFile.delete()) {
                    qFile.deleteOnExit();
                }
            } catch (Exception e4) {
                FileLog.e(e4);
            }
        }
        if (type == 2) {
            ImageLoader.getInstance().clearMemory();
        }
    }

    public static boolean isVideoMimeType(String mime) {
        return MimeTypes.VIDEO_MP4.equals(mime) || (SharedConfig.streamMkv && "video/x-matroska".equals(mime));
    }

    public static boolean copyFile(InputStream sourceFile, File destFile) throws IOException {
        return copyFile(sourceFile, destFile, -1);
    }

    public static boolean copyFile(InputStream sourceFile, File destFile, int maxSize) throws IOException {
        FileOutputStream out = new FileOutputStream(destFile);
        byte[] buf = new byte[4096];
        int totalLen = 0;
        while (true) {
            int len = sourceFile.read(buf);
            if (len <= 0) {
                break;
            }
            Thread.yield();
            out.write(buf, 0, len);
            totalLen += len;
            if (maxSize > 0 && totalLen >= maxSize) {
                break;
            }
        }
        out.getFD().sync();
        out.close();
        return true;
    }

    public static boolean isSamePhoto(TLObject photo1, TLObject photo2) {
        if ((photo1 != null || photo2 == null) && (photo1 == null || photo2 != null)) {
            if (photo1 == null && photo2 == null) {
                return true;
            }
            if (photo1.getClass() != photo2.getClass()) {
                return false;
            }
            if (photo1 instanceof TLRPC.UserProfilePhoto) {
                TLRPC.UserProfilePhoto p1 = (TLRPC.UserProfilePhoto) photo1;
                TLRPC.UserProfilePhoto p2 = (TLRPC.UserProfilePhoto) photo2;
                return p1.photo_id == p2.photo_id;
            } else if (!(photo1 instanceof TLRPC.ChatPhoto)) {
                return false;
            } else {
                TLRPC.ChatPhoto p12 = (TLRPC.ChatPhoto) photo1;
                TLRPC.ChatPhoto p22 = (TLRPC.ChatPhoto) photo2;
                return p12.photo_id == p22.photo_id;
            }
        }
        return false;
    }

    public static boolean isSamePhoto(TLRPC.FileLocation location, TLRPC.Photo photo) {
        if (location == null || !(photo instanceof TLRPC.TL_photo)) {
            return false;
        }
        int N = photo.sizes.size();
        for (int b = 0; b < N; b++) {
            TLRPC.PhotoSize size = photo.sizes.get(b);
            if (size.location != null && size.location.local_id == location.local_id && size.location.volume_id == location.volume_id) {
                return true;
            }
        }
        return (-location.volume_id) == photo.id;
    }

    public static long getPhotoId(TLObject object) {
        if (object instanceof TLRPC.Photo) {
            return ((TLRPC.Photo) object).id;
        }
        if (object instanceof TLRPC.ChatPhoto) {
            return ((TLRPC.ChatPhoto) object).photo_id;
        }
        if (object instanceof TLRPC.UserProfilePhoto) {
            return ((TLRPC.UserProfilePhoto) object).photo_id;
        }
        return 0L;
    }

    public void getCurrentLoadingFiles(ArrayList<MessageObject> currentLoadingFiles) {
        currentLoadingFiles.clear();
        currentLoadingFiles.addAll(getDownloadController().downloadingFiles);
        for (int i = 0; i < currentLoadingFiles.size(); i++) {
            currentLoadingFiles.get(i).isDownloadingFile = true;
        }
    }

    public void getRecentLoadingFiles(ArrayList<MessageObject> recentLoadingFiles) {
        recentLoadingFiles.clear();
        recentLoadingFiles.addAll(getDownloadController().recentDownloadingFiles);
        for (int i = 0; i < recentLoadingFiles.size(); i++) {
            recentLoadingFiles.get(i).isDownloadingFile = true;
        }
    }

    public void checkCurrentDownloadsFiles() {
        final ArrayList<MessageObject> messagesToRemove = new ArrayList<>();
        ArrayList<MessageObject> messageObjects = new ArrayList<>(getDownloadController().recentDownloadingFiles);
        for (int i = 0; i < messageObjects.size(); i++) {
            messageObjects.get(i).checkMediaExistance();
            if (messageObjects.get(i).mediaExists) {
                messagesToRemove.add(messageObjects.get(i));
            }
        }
        if (!messagesToRemove.isEmpty()) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.FileLoader$$ExternalSyntheticLambda8
                @Override // java.lang.Runnable
                public final void run() {
                    FileLoader.this.m221x3e68e754(messagesToRemove);
                }
            });
        }
    }

    /* renamed from: lambda$checkCurrentDownloadsFiles$13$org-telegram-messenger-FileLoader */
    public /* synthetic */ void m221x3e68e754(ArrayList messagesToRemove) {
        getDownloadController().recentDownloadingFiles.removeAll(messagesToRemove);
        getNotificationCenter().postNotificationName(NotificationCenter.onDownloadingFilesChanged, new Object[0]);
    }

    public void checkMediaExistance(ArrayList<MessageObject> messageObjects) {
        getFileDatabase().checkMediaExistance(messageObjects);
    }

    public void clearRecentDownloadedFiles() {
        getDownloadController().clearRecentDownloadedFiles();
    }

    public static boolean checkUploadFileSize(int currentAccount, long length) {
        boolean premium = AccountInstance.getInstance(currentAccount).getUserConfig().isPremium();
        if (length >= DEFAULT_MAX_FILE_SIZE) {
            if (length < DEFAULT_MAX_FILE_SIZE_PREMIUM && premium) {
                return true;
            }
            return false;
        }
        return true;
    }
}
