package com.google.android.exoplayer2.upstream.cache;

import android.os.ConditionVariable;
import com.google.android.exoplayer2.database.DatabaseIOException;
import com.google.android.exoplayer2.database.DatabaseProvider;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.upstream.cache.ContentMetadata;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.Util;
import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
/* loaded from: classes3.dex */
public final class SimpleCache implements Cache {
    private static final int SUBDIRECTORY_COUNT = 10;
    private static final String TAG = "SimpleCache";
    private static final String UID_FILE_SUFFIX = ".uid";
    private static final HashSet<File> lockedCacheDirs = new HashSet<>();
    private final File cacheDir;
    private final CachedContentIndex contentIndex;
    private final CacheEvictor evictor;
    private final CacheFileMetadataIndex fileIndex;
    private Cache.CacheException initializationException;
    private final HashMap<String, ArrayList<Cache.Listener>> listeners;
    private final Random random;
    private boolean released;
    private long totalSpace;
    private final boolean touchCacheSpans;
    private long uid;

    public static synchronized boolean isCacheFolderLocked(File cacheFolder) {
        boolean contains;
        synchronized (SimpleCache.class) {
            contains = lockedCacheDirs.contains(cacheFolder.getAbsoluteFile());
        }
        return contains;
    }

    public static void delete(File cacheDir, DatabaseProvider databaseProvider) {
        if (!cacheDir.exists()) {
            return;
        }
        File[] files = cacheDir.listFiles();
        if (files == null) {
            cacheDir.delete();
            return;
        }
        if (databaseProvider != null) {
            long uid = loadUid(files);
            if (uid != -1) {
                try {
                    CacheFileMetadataIndex.delete(databaseProvider, uid);
                } catch (DatabaseIOException e) {
                    Log.w(TAG, "Failed to delete file metadata: " + uid);
                }
                try {
                    CachedContentIndex.delete(databaseProvider, uid);
                } catch (DatabaseIOException e2) {
                    Log.w(TAG, "Failed to delete file metadata: " + uid);
                }
            }
        }
        Util.recursiveDelete(cacheDir);
    }

    @Deprecated
    public SimpleCache(File cacheDir, CacheEvictor evictor) {
        this(cacheDir, evictor, (byte[]) null, false);
    }

    @Deprecated
    public SimpleCache(File cacheDir, CacheEvictor evictor, byte[] secretKey) {
        this(cacheDir, evictor, secretKey, secretKey != null);
    }

    @Deprecated
    public SimpleCache(File cacheDir, CacheEvictor evictor, byte[] secretKey, boolean encrypt) {
        this(cacheDir, evictor, null, secretKey, encrypt, true);
    }

    public SimpleCache(File cacheDir, CacheEvictor evictor, DatabaseProvider databaseProvider) {
        this(cacheDir, evictor, databaseProvider, null, false, false);
    }

    /* JADX WARN: Illegal instructions before constructor call */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public SimpleCache(java.io.File r8, com.google.android.exoplayer2.upstream.cache.CacheEvictor r9, com.google.android.exoplayer2.database.DatabaseProvider r10, byte[] r11, boolean r12, boolean r13) {
        /*
            r7 = this;
            com.google.android.exoplayer2.upstream.cache.CachedContentIndex r6 = new com.google.android.exoplayer2.upstream.cache.CachedContentIndex
            r0 = r6
            r1 = r10
            r2 = r8
            r3 = r11
            r4 = r12
            r5 = r13
            r0.<init>(r1, r2, r3, r4, r5)
            if (r10 == 0) goto L15
            if (r13 != 0) goto L15
            com.google.android.exoplayer2.upstream.cache.CacheFileMetadataIndex r0 = new com.google.android.exoplayer2.upstream.cache.CacheFileMetadataIndex
            r0.<init>(r10)
            goto L16
        L15:
            r0 = 0
        L16:
            r7.<init>(r8, r9, r6, r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.upstream.cache.SimpleCache.<init>(java.io.File, com.google.android.exoplayer2.upstream.cache.CacheEvictor, com.google.android.exoplayer2.database.DatabaseProvider, byte[], boolean, boolean):void");
    }

    /* JADX WARN: Type inference failed for: r1v2, types: [com.google.android.exoplayer2.upstream.cache.SimpleCache$1] */
    SimpleCache(File cacheDir, CacheEvictor evictor, CachedContentIndex contentIndex, CacheFileMetadataIndex fileIndex) {
        if (!lockFolder(cacheDir)) {
            throw new IllegalStateException("Another SimpleCache instance uses the folder: " + cacheDir);
        }
        this.cacheDir = cacheDir;
        this.evictor = evictor;
        this.contentIndex = contentIndex;
        this.fileIndex = fileIndex;
        this.listeners = new HashMap<>();
        this.random = new Random();
        this.touchCacheSpans = evictor.requiresCacheSpanTouches();
        this.uid = -1L;
        final ConditionVariable conditionVariable = new ConditionVariable();
        new Thread("SimpleCache.initialize()") { // from class: com.google.android.exoplayer2.upstream.cache.SimpleCache.1
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                synchronized (SimpleCache.this) {
                    conditionVariable.open();
                    SimpleCache.this.initialize();
                    SimpleCache.this.evictor.onCacheInitialized();
                }
            }
        }.start();
        conditionVariable.block();
    }

    public synchronized void checkInitialization() throws Cache.CacheException {
        Cache.CacheException cacheException = this.initializationException;
        if (cacheException != null) {
            throw cacheException;
        }
    }

    @Override // com.google.android.exoplayer2.upstream.cache.Cache
    public synchronized long getUid() {
        return this.uid;
    }

    @Override // com.google.android.exoplayer2.upstream.cache.Cache
    public synchronized void release() {
        if (this.released) {
            return;
        }
        this.listeners.clear();
        removeStaleSpans();
        try {
            this.contentIndex.store();
            unlockFolder(this.cacheDir);
            this.released = true;
        } catch (IOException e) {
            Log.e(TAG, "Storing index file failed", e);
            unlockFolder(this.cacheDir);
            this.released = true;
        }
    }

    @Override // com.google.android.exoplayer2.upstream.cache.Cache
    public synchronized NavigableSet<CacheSpan> addListener(String key, Cache.Listener listener) {
        Assertions.checkState(!this.released);
        ArrayList<Cache.Listener> listenersForKey = this.listeners.get(key);
        if (listenersForKey == null) {
            listenersForKey = new ArrayList<>();
            this.listeners.put(key, listenersForKey);
        }
        listenersForKey.add(listener);
        return getCachedSpans(key);
    }

    @Override // com.google.android.exoplayer2.upstream.cache.Cache
    public synchronized void removeListener(String key, Cache.Listener listener) {
        if (this.released) {
            return;
        }
        ArrayList<Cache.Listener> listenersForKey = this.listeners.get(key);
        if (listenersForKey != null) {
            listenersForKey.remove(listener);
            if (listenersForKey.isEmpty()) {
                this.listeners.remove(key);
            }
        }
    }

    @Override // com.google.android.exoplayer2.upstream.cache.Cache
    public synchronized NavigableSet<CacheSpan> getCachedSpans(String key) {
        TreeSet treeSet;
        Assertions.checkState(!this.released);
        CachedContent cachedContent = this.contentIndex.get(key);
        if (cachedContent != null && !cachedContent.isEmpty()) {
            treeSet = new TreeSet((Collection) cachedContent.getSpans());
        }
        treeSet = new TreeSet();
        return treeSet;
    }

    @Override // com.google.android.exoplayer2.upstream.cache.Cache
    public synchronized Set<String> getKeys() {
        Assertions.checkState(!this.released);
        return new HashSet(this.contentIndex.getKeys());
    }

    @Override // com.google.android.exoplayer2.upstream.cache.Cache
    public synchronized long getCacheSpace() {
        Assertions.checkState(!this.released);
        return this.totalSpace;
    }

    @Override // com.google.android.exoplayer2.upstream.cache.Cache
    public synchronized CacheSpan startReadWrite(String key, long position) throws InterruptedException, Cache.CacheException {
        CacheSpan span;
        Assertions.checkState(!this.released);
        checkInitialization();
        while (true) {
            span = startReadWriteNonBlocking(key, position);
            if (span == null) {
                wait();
            }
        }
        return span;
    }

    @Override // com.google.android.exoplayer2.upstream.cache.Cache
    public synchronized CacheSpan startReadWriteNonBlocking(String key, long position) throws Cache.CacheException {
        Assertions.checkState(!this.released);
        checkInitialization();
        SimpleCacheSpan span = getSpan(key, position);
        if (span.isCached) {
            return touchSpan(key, span);
        }
        CachedContent cachedContent = this.contentIndex.getOrAdd(key);
        if (!cachedContent.isLocked()) {
            cachedContent.setLocked(true);
            return span;
        }
        return null;
    }

    @Override // com.google.android.exoplayer2.upstream.cache.Cache
    public synchronized File startFile(String key, long position, long length) throws Cache.CacheException {
        CachedContent cachedContent;
        File fileDir;
        long lastTouchTimestamp;
        Assertions.checkState(!this.released);
        checkInitialization();
        cachedContent = this.contentIndex.get(key);
        Assertions.checkNotNull(cachedContent);
        Assertions.checkState(cachedContent.isLocked());
        if (!this.cacheDir.exists()) {
            this.cacheDir.mkdirs();
            removeStaleSpans();
        }
        this.evictor.onStartFile(this, key, position, length);
        fileDir = new File(this.cacheDir, Integer.toString(this.random.nextInt(10)));
        if (!fileDir.exists()) {
            fileDir.mkdir();
        }
        lastTouchTimestamp = System.currentTimeMillis();
        return SimpleCacheSpan.getCacheFile(fileDir, cachedContent.id, position, lastTouchTimestamp);
    }

    @Override // com.google.android.exoplayer2.upstream.cache.Cache
    public synchronized void commitFile(File file, long length) throws Cache.CacheException {
        boolean z = true;
        Assertions.checkState(!this.released);
        if (!file.exists()) {
            return;
        }
        if (length == 0) {
            file.delete();
            return;
        }
        SimpleCacheSpan span = (SimpleCacheSpan) Assertions.checkNotNull(SimpleCacheSpan.createCacheEntry(file, length, this.contentIndex));
        CachedContent cachedContent = (CachedContent) Assertions.checkNotNull(this.contentIndex.get(span.key));
        Assertions.checkState(cachedContent.isLocked());
        long contentLength = ContentMetadata.CC.getContentLength(cachedContent.getMetadata());
        if (contentLength != -1) {
            if (span.position + span.length > contentLength) {
                z = false;
            }
            Assertions.checkState(z);
        }
        if (this.fileIndex != null) {
            String fileName = file.getName();
            try {
                this.fileIndex.set(fileName, span.length, span.lastTouchTimestamp);
            } catch (IOException e) {
                throw new Cache.CacheException(e);
            }
        }
        addSpan(span);
        try {
            this.contentIndex.store();
            notifyAll();
        } catch (IOException e2) {
            throw new Cache.CacheException(e2);
        }
    }

    @Override // com.google.android.exoplayer2.upstream.cache.Cache
    public synchronized void releaseHoleSpan(CacheSpan holeSpan) {
        Assertions.checkState(!this.released);
        CachedContent cachedContent = this.contentIndex.get(holeSpan.key);
        Assertions.checkNotNull(cachedContent);
        Assertions.checkState(cachedContent.isLocked());
        cachedContent.setLocked(false);
        this.contentIndex.maybeRemove(cachedContent.key);
        notifyAll();
    }

    @Override // com.google.android.exoplayer2.upstream.cache.Cache
    public synchronized void removeSpan(CacheSpan span) {
        Assertions.checkState(!this.released);
        removeSpanInternal(span);
    }

    /* JADX WARN: Code restructure failed: missing block: B:11:0x001b, code lost:
        if (r0.getCachedBytesLength(r8, r10) >= r10) goto L13;
     */
    @Override // com.google.android.exoplayer2.upstream.cache.Cache
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public synchronized boolean isCached(java.lang.String r7, long r8, long r10) {
        /*
            r6 = this;
            monitor-enter(r6)
            boolean r0 = r6.released     // Catch: java.lang.Throwable -> L21
            r1 = 1
            r2 = 0
            if (r0 != 0) goto L9
            r0 = 1
            goto La
        L9:
            r0 = 0
        La:
            com.google.android.exoplayer2.util.Assertions.checkState(r0)     // Catch: java.lang.Throwable -> L21
            com.google.android.exoplayer2.upstream.cache.CachedContentIndex r0 = r6.contentIndex     // Catch: java.lang.Throwable -> L21
            com.google.android.exoplayer2.upstream.cache.CachedContent r0 = r0.get(r7)     // Catch: java.lang.Throwable -> L21
            if (r0 == 0) goto L1e
            long r3 = r0.getCachedBytesLength(r8, r10)     // Catch: java.lang.Throwable -> L21
            int r5 = (r3 > r10 ? 1 : (r3 == r10 ? 0 : -1))
            if (r5 < 0) goto L1e
            goto L1f
        L1e:
            r1 = 0
        L1f:
            monitor-exit(r6)
            return r1
        L21:
            r7 = move-exception
            monitor-exit(r6)
            throw r7
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.upstream.cache.SimpleCache.isCached(java.lang.String, long, long):boolean");
    }

    @Override // com.google.android.exoplayer2.upstream.cache.Cache
    public synchronized long getCachedLength(String key, long position, long length) {
        CachedContent cachedContent;
        Assertions.checkState(!this.released);
        cachedContent = this.contentIndex.get(key);
        return cachedContent != null ? cachedContent.getCachedBytesLength(position, length) : -length;
    }

    @Override // com.google.android.exoplayer2.upstream.cache.Cache
    public synchronized void applyContentMetadataMutations(String key, ContentMetadataMutations mutations) throws Cache.CacheException {
        Assertions.checkState(!this.released);
        checkInitialization();
        this.contentIndex.applyContentMetadataMutations(key, mutations);
        try {
            this.contentIndex.store();
        } catch (IOException e) {
            throw new Cache.CacheException(e);
        }
    }

    @Override // com.google.android.exoplayer2.upstream.cache.Cache
    public synchronized ContentMetadata getContentMetadata(String key) {
        Assertions.checkState(!this.released);
        return this.contentIndex.getContentMetadata(key);
    }

    public void initialize() {
        if (!this.cacheDir.exists() && !this.cacheDir.mkdirs()) {
            String message = "Failed to create cache directory: " + this.cacheDir;
            Log.e(TAG, message);
            this.initializationException = new Cache.CacheException(message);
            return;
        }
        File[] files = this.cacheDir.listFiles();
        if (files == null) {
            String message2 = "Failed to list cache directory files: " + this.cacheDir;
            Log.e(TAG, message2);
            this.initializationException = new Cache.CacheException(message2);
            return;
        }
        long loadUid = loadUid(files);
        this.uid = loadUid;
        if (loadUid == -1) {
            try {
                this.uid = createUid(this.cacheDir);
            } catch (IOException e) {
                String message3 = "Failed to create cache UID: " + this.cacheDir;
                Log.e(TAG, message3, e);
                this.initializationException = new Cache.CacheException(message3, e);
                return;
            }
        }
        try {
            this.contentIndex.initialize(this.uid);
            CacheFileMetadataIndex cacheFileMetadataIndex = this.fileIndex;
            if (cacheFileMetadataIndex != null) {
                cacheFileMetadataIndex.initialize(this.uid);
                Map<String, CacheFileMetadata> fileMetadata = this.fileIndex.getAll();
                loadDirectory(this.cacheDir, true, files, fileMetadata);
                this.fileIndex.removeAll(fileMetadata.keySet());
            } else {
                loadDirectory(this.cacheDir, true, files, null);
            }
            this.contentIndex.removeEmpty();
            try {
                this.contentIndex.store();
            } catch (IOException e2) {
                Log.e(TAG, "Storing index file failed", e2);
            }
        } catch (IOException e3) {
            String message4 = "Failed to initialize cache indices: " + this.cacheDir;
            Log.e(TAG, message4, e3);
            this.initializationException = new Cache.CacheException(message4, e3);
        }
    }

    private void loadDirectory(File directory, boolean isRoot, File[] files, Map<String, CacheFileMetadata> fileMetadata) {
        long lastTouchTimestamp;
        long length;
        if (files == null || files.length == 0) {
            if (!isRoot) {
                directory.delete();
                return;
            }
            return;
        }
        for (File file : files) {
            String fileName = file.getName();
            if (isRoot && fileName.indexOf(46) == -1) {
                loadDirectory(file, false, file.listFiles(), fileMetadata);
            } else if (!isRoot || (!CachedContentIndex.isIndexFile(fileName) && !fileName.endsWith(UID_FILE_SUFFIX))) {
                CacheFileMetadata metadata = fileMetadata != null ? fileMetadata.remove(fileName) : null;
                if (metadata == null) {
                    length = -1;
                    lastTouchTimestamp = -9223372036854775807L;
                } else {
                    long length2 = metadata.length;
                    long lastTouchTimestamp2 = metadata.lastTouchTimestamp;
                    length = length2;
                    lastTouchTimestamp = lastTouchTimestamp2;
                }
                SimpleCacheSpan span = SimpleCacheSpan.createCacheEntry(file, length, lastTouchTimestamp, this.contentIndex);
                if (span != null) {
                    addSpan(span);
                } else {
                    file.delete();
                }
            }
        }
    }

    private SimpleCacheSpan touchSpan(String key, SimpleCacheSpan span) {
        if (!this.touchCacheSpans) {
            return span;
        }
        String fileName = ((File) Assertions.checkNotNull(span.file)).getName();
        long length = span.length;
        long lastTouchTimestamp = System.currentTimeMillis();
        boolean updateFile = false;
        CacheFileMetadataIndex cacheFileMetadataIndex = this.fileIndex;
        if (cacheFileMetadataIndex != null) {
            try {
                cacheFileMetadataIndex.set(fileName, length, lastTouchTimestamp);
            } catch (IOException e) {
                Log.w(TAG, "Failed to update index with new touch timestamp.");
            }
        } else {
            updateFile = true;
        }
        SimpleCacheSpan newSpan = this.contentIndex.get(key).setLastTouchTimestamp(span, lastTouchTimestamp, updateFile);
        notifySpanTouched(span, newSpan);
        return newSpan;
    }

    private SimpleCacheSpan getSpan(String key, long position) {
        SimpleCacheSpan span;
        CachedContent cachedContent = this.contentIndex.get(key);
        if (cachedContent == null) {
            return SimpleCacheSpan.createOpenHole(key, position);
        }
        while (true) {
            span = cachedContent.getSpan(position);
            if (!span.isCached || span.file.length() == span.length) {
                break;
            }
            removeStaleSpans();
        }
        return span;
    }

    private void addSpan(SimpleCacheSpan span) {
        this.contentIndex.getOrAdd(span.key).addSpan(span);
        this.totalSpace += span.length;
        notifySpanAdded(span);
    }

    private void removeSpanInternal(CacheSpan span) {
        CachedContent cachedContent = this.contentIndex.get(span.key);
        if (cachedContent == null || !cachedContent.removeSpan(span)) {
            return;
        }
        this.totalSpace -= span.length;
        if (this.fileIndex != null) {
            String fileName = span.file.getName();
            try {
                this.fileIndex.remove(fileName);
            } catch (IOException e) {
                Log.w(TAG, "Failed to remove file index entry for: " + fileName);
            }
        }
        this.contentIndex.maybeRemove(cachedContent.key);
        notifySpanRemoved(span);
    }

    private void removeStaleSpans() {
        ArrayList<CacheSpan> spansToBeRemoved = new ArrayList<>();
        for (CachedContent cachedContent : this.contentIndex.getAll()) {
            Iterator<SimpleCacheSpan> it = cachedContent.getSpans().iterator();
            while (it.hasNext()) {
                CacheSpan span = it.next();
                if (span.file.length() != span.length) {
                    spansToBeRemoved.add(span);
                }
            }
        }
        for (int i = 0; i < spansToBeRemoved.size(); i++) {
            removeSpanInternal(spansToBeRemoved.get(i));
        }
    }

    private void notifySpanRemoved(CacheSpan span) {
        ArrayList<Cache.Listener> keyListeners = this.listeners.get(span.key);
        if (keyListeners != null) {
            for (int i = keyListeners.size() - 1; i >= 0; i--) {
                keyListeners.get(i).onSpanRemoved(this, span);
            }
        }
        this.evictor.onSpanRemoved(this, span);
    }

    private void notifySpanAdded(SimpleCacheSpan span) {
        ArrayList<Cache.Listener> keyListeners = this.listeners.get(span.key);
        if (keyListeners != null) {
            for (int i = keyListeners.size() - 1; i >= 0; i--) {
                keyListeners.get(i).onSpanAdded(this, span);
            }
        }
        this.evictor.onSpanAdded(this, span);
    }

    private void notifySpanTouched(SimpleCacheSpan oldSpan, CacheSpan newSpan) {
        ArrayList<Cache.Listener> keyListeners = this.listeners.get(oldSpan.key);
        if (keyListeners != null) {
            for (int i = keyListeners.size() - 1; i >= 0; i--) {
                keyListeners.get(i).onSpanTouched(this, oldSpan, newSpan);
            }
        }
        this.evictor.onSpanTouched(this, oldSpan, newSpan);
    }

    private static long loadUid(File[] files) {
        int length = files.length;
        for (int i = 0; i < length; i++) {
            File file = files[i];
            String fileName = file.getName();
            if (fileName.endsWith(UID_FILE_SUFFIX)) {
                try {
                    return parseUid(fileName);
                } catch (NumberFormatException e) {
                    Log.e(TAG, "Malformed UID file: " + file);
                    file.delete();
                }
            }
        }
        return -1L;
    }

    private static long createUid(File directory) throws IOException {
        long uid = new SecureRandom().nextLong();
        long uid2 = uid == Long.MIN_VALUE ? 0L : Math.abs(uid);
        String hexUid = Long.toString(uid2, 16);
        File hexUidFile = new File(directory, hexUid + UID_FILE_SUFFIX);
        if (!hexUidFile.createNewFile()) {
            throw new IOException("Failed to create UID file: " + hexUidFile);
        }
        return uid2;
    }

    private static long parseUid(String fileName) {
        return Long.parseLong(fileName.substring(0, fileName.indexOf(46)), 16);
    }

    private static synchronized boolean lockFolder(File cacheDir) {
        boolean add;
        synchronized (SimpleCache.class) {
            add = lockedCacheDirs.add(cacheDir.getAbsoluteFile());
        }
        return add;
    }

    private static synchronized void unlockFolder(File cacheDir) {
        synchronized (SimpleCache.class) {
            lockedCacheDirs.remove(cacheDir.getAbsoluteFile());
        }
    }
}
