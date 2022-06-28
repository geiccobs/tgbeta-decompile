package com.google.android.exoplayer2.upstream.cache;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import com.google.android.exoplayer2.database.DatabaseIOException;
import com.google.android.exoplayer2.database.DatabaseProvider;
import com.google.android.exoplayer2.database.VersionTable;
import com.google.android.exoplayer2.upstream.cache.ContentMetadata;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.AtomicFile;
import com.google.android.exoplayer2.util.ReusableBufferedOutputStream;
import com.google.android.exoplayer2.util.Util;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
/* loaded from: classes3.dex */
public class CachedContentIndex {
    static final String FILE_NAME_ATOMIC = "cached_content_index.exi";
    private static final int INCREMENTAL_METADATA_READ_LENGTH = 10485760;
    private final SparseArray<String> idToKey;
    private final HashMap<String, CachedContent> keyToContent;
    private final SparseBooleanArray newIds;
    private Storage previousStorage;
    private final SparseBooleanArray removedIds;
    private Storage storage;

    /* loaded from: classes3.dex */
    public interface Storage {
        void delete() throws IOException;

        boolean exists() throws IOException;

        void initialize(long j);

        void load(HashMap<String, CachedContent> hashMap, SparseArray<String> sparseArray) throws IOException;

        void onRemove(CachedContent cachedContent, boolean z);

        void onUpdate(CachedContent cachedContent);

        void storeFully(HashMap<String, CachedContent> hashMap) throws IOException;

        void storeIncremental(HashMap<String, CachedContent> hashMap) throws IOException;
    }

    public static boolean isIndexFile(String fileName) {
        return fileName.startsWith(FILE_NAME_ATOMIC);
    }

    public static void delete(DatabaseProvider databaseProvider, long uid) throws DatabaseIOException {
        DatabaseStorage.delete(databaseProvider, uid);
    }

    public CachedContentIndex(DatabaseProvider databaseProvider) {
        this(databaseProvider, null, null, false, false);
    }

    public CachedContentIndex(DatabaseProvider databaseProvider, File legacyStorageDir, byte[] legacyStorageSecretKey, boolean legacyStorageEncrypt, boolean preferLegacyStorage) {
        Assertions.checkState((databaseProvider == null && legacyStorageDir == null) ? false : true);
        this.keyToContent = new HashMap<>();
        this.idToKey = new SparseArray<>();
        this.removedIds = new SparseBooleanArray();
        this.newIds = new SparseBooleanArray();
        Storage legacyStorage = null;
        Storage databaseStorage = databaseProvider != null ? new DatabaseStorage(databaseProvider) : null;
        legacyStorage = legacyStorageDir != null ? new LegacyStorage(new File(legacyStorageDir, FILE_NAME_ATOMIC), legacyStorageSecretKey, legacyStorageEncrypt) : legacyStorage;
        if (databaseStorage == null || (legacyStorage != null && preferLegacyStorage)) {
            this.storage = legacyStorage;
            this.previousStorage = databaseStorage;
            return;
        }
        this.storage = databaseStorage;
        this.previousStorage = legacyStorage;
    }

    public void initialize(long uid) throws IOException {
        Storage storage;
        this.storage.initialize(uid);
        Storage storage2 = this.previousStorage;
        if (storage2 != null) {
            storage2.initialize(uid);
        }
        if (!this.storage.exists() && (storage = this.previousStorage) != null && storage.exists()) {
            this.previousStorage.load(this.keyToContent, this.idToKey);
            this.storage.storeFully(this.keyToContent);
        } else {
            this.storage.load(this.keyToContent, this.idToKey);
        }
        Storage storage3 = this.previousStorage;
        if (storage3 != null) {
            storage3.delete();
            this.previousStorage = null;
        }
    }

    public void store() throws IOException {
        this.storage.storeIncremental(this.keyToContent);
        int removedIdCount = this.removedIds.size();
        for (int i = 0; i < removedIdCount; i++) {
            this.idToKey.remove(this.removedIds.keyAt(i));
        }
        this.removedIds.clear();
        this.newIds.clear();
    }

    public CachedContent getOrAdd(String key) {
        CachedContent cachedContent = this.keyToContent.get(key);
        return cachedContent == null ? addNew(key) : cachedContent;
    }

    public CachedContent get(String key) {
        return this.keyToContent.get(key);
    }

    public Collection<CachedContent> getAll() {
        return this.keyToContent.values();
    }

    public int assignIdForKey(String key) {
        return getOrAdd(key).id;
    }

    public String getKeyForId(int id) {
        return this.idToKey.get(id);
    }

    public void maybeRemove(String key) {
        CachedContent cachedContent = this.keyToContent.get(key);
        if (cachedContent != null && cachedContent.isEmpty() && !cachedContent.isLocked()) {
            this.keyToContent.remove(key);
            int id = cachedContent.id;
            boolean neverStored = this.newIds.get(id);
            this.storage.onRemove(cachedContent, neverStored);
            if (neverStored) {
                this.idToKey.remove(id);
                this.newIds.delete(id);
                return;
            }
            this.idToKey.put(id, null);
            this.removedIds.put(id, true);
        }
    }

    public void removeEmpty() {
        String[] keys = new String[this.keyToContent.size()];
        this.keyToContent.keySet().toArray(keys);
        for (String key : keys) {
            maybeRemove(key);
        }
    }

    public Set<String> getKeys() {
        return this.keyToContent.keySet();
    }

    public void applyContentMetadataMutations(String key, ContentMetadataMutations mutations) {
        CachedContent cachedContent = getOrAdd(key);
        if (cachedContent.applyMetadataMutations(mutations)) {
            this.storage.onUpdate(cachedContent);
        }
    }

    public ContentMetadata getContentMetadata(String key) {
        CachedContent cachedContent = get(key);
        return cachedContent != null ? cachedContent.getMetadata() : DefaultContentMetadata.EMPTY;
    }

    private CachedContent addNew(String key) {
        int id = getNewId(this.idToKey);
        CachedContent cachedContent = new CachedContent(id, key);
        this.keyToContent.put(key, cachedContent);
        this.idToKey.put(id, key);
        this.newIds.put(id, true);
        this.storage.onUpdate(cachedContent);
        return cachedContent;
    }

    public static Cipher getCipher() throws NoSuchPaddingException, NoSuchAlgorithmException {
        if (Util.SDK_INT == 18) {
            try {
                return Cipher.getInstance("AES/CBC/PKCS5PADDING", "BC");
            } catch (Throwable th) {
            }
        }
        return Cipher.getInstance("AES/CBC/PKCS5PADDING");
    }

    static int getNewId(SparseArray<String> idToKey) {
        int size = idToKey.size();
        int id = size == 0 ? 0 : idToKey.keyAt(size - 1) + 1;
        if (id < 0) {
            id = 0;
            while (id < size && id == idToKey.keyAt(id)) {
                id++;
            }
        }
        return id;
    }

    public static DefaultContentMetadata readContentMetadata(DataInputStream input) throws IOException {
        int size = input.readInt();
        HashMap<String, byte[]> metadata = new HashMap<>();
        for (int i = 0; i < size; i++) {
            String name = input.readUTF();
            int valueSize = input.readInt();
            if (valueSize < 0) {
                throw new IOException("Invalid value size: " + valueSize);
            }
            int bytesRead = 0;
            int nextBytesToRead = Math.min(valueSize, (int) INCREMENTAL_METADATA_READ_LENGTH);
            byte[] value = Util.EMPTY_BYTE_ARRAY;
            while (bytesRead != valueSize) {
                value = Arrays.copyOf(value, bytesRead + nextBytesToRead);
                input.readFully(value, bytesRead, nextBytesToRead);
                bytesRead += nextBytesToRead;
                nextBytesToRead = Math.min(valueSize - bytesRead, (int) INCREMENTAL_METADATA_READ_LENGTH);
            }
            metadata.put(name, value);
        }
        return new DefaultContentMetadata(metadata);
    }

    public static void writeContentMetadata(DefaultContentMetadata metadata, DataOutputStream output) throws IOException {
        Set<Map.Entry<String, byte[]>> entrySet = metadata.entrySet();
        output.writeInt(entrySet.size());
        for (Map.Entry<String, byte[]> entry : entrySet) {
            output.writeUTF(entry.getKey());
            byte[] value = entry.getValue();
            output.writeInt(value.length);
            output.write(value);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static class LegacyStorage implements Storage {
        private static final int FLAG_ENCRYPTED_INDEX = 1;
        private static final int VERSION = 2;
        private static final int VERSION_METADATA_INTRODUCED = 2;
        private final AtomicFile atomicFile;
        private ReusableBufferedOutputStream bufferedOutputStream;
        private boolean changed;
        private final Cipher cipher;
        private final boolean encrypt;
        private final Random random;
        private final SecretKeySpec secretKeySpec;

        public LegacyStorage(File file, byte[] secretKey, boolean encrypt) {
            Cipher cipher = null;
            SecretKeySpec secretKeySpec = null;
            if (secretKey != null) {
                Assertions.checkArgument(secretKey.length == 16);
                try {
                    cipher = CachedContentIndex.getCipher();
                    secretKeySpec = new SecretKeySpec(secretKey, "AES");
                } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
                    throw new IllegalStateException(e);
                }
            } else {
                Assertions.checkArgument(!encrypt);
            }
            this.encrypt = encrypt;
            this.cipher = cipher;
            this.secretKeySpec = secretKeySpec;
            this.random = encrypt ? new Random() : null;
            this.atomicFile = new AtomicFile(file);
        }

        @Override // com.google.android.exoplayer2.upstream.cache.CachedContentIndex.Storage
        public void initialize(long uid) {
        }

        @Override // com.google.android.exoplayer2.upstream.cache.CachedContentIndex.Storage
        public boolean exists() {
            return this.atomicFile.exists();
        }

        @Override // com.google.android.exoplayer2.upstream.cache.CachedContentIndex.Storage
        public void delete() {
            this.atomicFile.delete();
        }

        @Override // com.google.android.exoplayer2.upstream.cache.CachedContentIndex.Storage
        public void load(HashMap<String, CachedContent> content, SparseArray<String> idToKey) {
            Assertions.checkState(!this.changed);
            if (!readFile(content, idToKey)) {
                content.clear();
                idToKey.clear();
                this.atomicFile.delete();
            }
        }

        @Override // com.google.android.exoplayer2.upstream.cache.CachedContentIndex.Storage
        public void storeFully(HashMap<String, CachedContent> content) throws IOException {
            writeFile(content);
            this.changed = false;
        }

        @Override // com.google.android.exoplayer2.upstream.cache.CachedContentIndex.Storage
        public void storeIncremental(HashMap<String, CachedContent> content) throws IOException {
            if (!this.changed) {
                return;
            }
            storeFully(content);
        }

        @Override // com.google.android.exoplayer2.upstream.cache.CachedContentIndex.Storage
        public void onUpdate(CachedContent cachedContent) {
            this.changed = true;
        }

        @Override // com.google.android.exoplayer2.upstream.cache.CachedContentIndex.Storage
        public void onRemove(CachedContent cachedContent, boolean neverStored) {
            this.changed = true;
        }

        /* JADX WARN: Removed duplicated region for block: B:32:0x0073 A[Catch: all -> 0x00af, IOException -> 0x00b6, LOOP:0: B:31:0x0071->B:32:0x0073, LOOP_END, TryCatch #4 {IOException -> 0x00b6, all -> 0x00af, blocks: (B:6:0x000c, B:11:0x0028, B:13:0x0030, B:18:0x003c, B:19:0x0046, B:20:0x004e, B:25:0x005f, B:26:0x0064, B:27:0x0065, B:29:0x0069, B:30:0x006b, B:32:0x0073, B:33:0x008b), top: B:54:0x000c }] */
        /* JADX WARN: Removed duplicated region for block: B:36:0x0096  */
        /* JADX WARN: Removed duplicated region for block: B:37:0x0098  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        private boolean readFile(java.util.HashMap<java.lang.String, com.google.android.exoplayer2.upstream.cache.CachedContent> r13, android.util.SparseArray<java.lang.String> r14) {
            /*
                r12 = this;
                com.google.android.exoplayer2.util.AtomicFile r0 = r12.atomicFile
                boolean r0 = r0.exists()
                r1 = 1
                if (r0 != 0) goto La
                return r1
            La:
                r0 = 0
                r2 = 0
                java.io.BufferedInputStream r3 = new java.io.BufferedInputStream     // Catch: java.lang.Throwable -> Laf java.io.IOException -> Lb6
                com.google.android.exoplayer2.util.AtomicFile r4 = r12.atomicFile     // Catch: java.lang.Throwable -> Laf java.io.IOException -> Lb6
                java.io.InputStream r4 = r4.openRead()     // Catch: java.lang.Throwable -> Laf java.io.IOException -> Lb6
                r3.<init>(r4)     // Catch: java.lang.Throwable -> Laf java.io.IOException -> Lb6
                java.io.DataInputStream r4 = new java.io.DataInputStream     // Catch: java.lang.Throwable -> Laf java.io.IOException -> Lb6
                r4.<init>(r3)     // Catch: java.lang.Throwable -> Laf java.io.IOException -> Lb6
                r0 = r4
                int r4 = r0.readInt()     // Catch: java.lang.Throwable -> Laf java.io.IOException -> Lb6
                if (r4 < 0) goto La9
                r5 = 2
                if (r4 <= r5) goto L28
                goto La9
            L28:
                int r6 = r0.readInt()     // Catch: java.lang.Throwable -> Laf java.io.IOException -> Lb6
                r7 = r6 & 1
                if (r7 == 0) goto L65
                javax.crypto.Cipher r7 = r12.cipher     // Catch: java.lang.Throwable -> Laf java.io.IOException -> Lb6
                if (r7 != 0) goto L3a
            L36:
                com.google.android.exoplayer2.util.Util.closeQuietly(r0)
                return r2
            L3a:
                r7 = 16
                byte[] r7 = new byte[r7]     // Catch: java.lang.Throwable -> Laf java.io.IOException -> Lb6
                r0.readFully(r7)     // Catch: java.lang.Throwable -> Laf java.io.IOException -> Lb6
                javax.crypto.spec.IvParameterSpec r8 = new javax.crypto.spec.IvParameterSpec     // Catch: java.lang.Throwable -> Laf java.io.IOException -> Lb6
                r8.<init>(r7)     // Catch: java.lang.Throwable -> Laf java.io.IOException -> Lb6
                javax.crypto.Cipher r9 = r12.cipher     // Catch: java.security.InvalidAlgorithmParameterException -> L5c java.security.InvalidKeyException -> L5e java.lang.Throwable -> Laf java.io.IOException -> Lb6
                javax.crypto.spec.SecretKeySpec r10 = r12.secretKeySpec     // Catch: java.security.InvalidAlgorithmParameterException -> L5c java.security.InvalidKeyException -> L5e java.lang.Throwable -> Laf java.io.IOException -> Lb6
                r9.init(r5, r10, r8)     // Catch: java.security.InvalidAlgorithmParameterException -> L5c java.security.InvalidKeyException -> L5e java.lang.Throwable -> Laf java.io.IOException -> Lb6
                java.io.DataInputStream r5 = new java.io.DataInputStream     // Catch: java.lang.Throwable -> Laf java.io.IOException -> Lb6
                javax.crypto.CipherInputStream r9 = new javax.crypto.CipherInputStream     // Catch: java.lang.Throwable -> Laf java.io.IOException -> Lb6
                javax.crypto.Cipher r10 = r12.cipher     // Catch: java.lang.Throwable -> Laf java.io.IOException -> Lb6
                r9.<init>(r3, r10)     // Catch: java.lang.Throwable -> Laf java.io.IOException -> Lb6
                r5.<init>(r9)     // Catch: java.lang.Throwable -> Laf java.io.IOException -> Lb6
                r0 = r5
            L5b:
                goto L6b
            L5c:
                r1 = move-exception
                goto L5f
            L5e:
                r1 = move-exception
            L5f:
                java.lang.IllegalStateException r5 = new java.lang.IllegalStateException     // Catch: java.lang.Throwable -> Laf java.io.IOException -> Lb6
                r5.<init>(r1)     // Catch: java.lang.Throwable -> Laf java.io.IOException -> Lb6
                throw r5     // Catch: java.lang.Throwable -> Laf java.io.IOException -> Lb6
            L65:
                boolean r5 = r12.encrypt     // Catch: java.lang.Throwable -> Laf java.io.IOException -> Lb6
                if (r5 == 0) goto L5b
                r12.changed = r1     // Catch: java.lang.Throwable -> Laf java.io.IOException -> Lb6
            L6b:
                int r5 = r0.readInt()     // Catch: java.lang.Throwable -> Laf java.io.IOException -> Lb6
                r7 = 0
                r8 = 0
            L71:
                if (r8 >= r5) goto L8b
                com.google.android.exoplayer2.upstream.cache.CachedContent r9 = r12.readCachedContent(r4, r0)     // Catch: java.lang.Throwable -> Laf java.io.IOException -> Lb6
                java.lang.String r10 = r9.key     // Catch: java.lang.Throwable -> Laf java.io.IOException -> Lb6
                r13.put(r10, r9)     // Catch: java.lang.Throwable -> Laf java.io.IOException -> Lb6
                int r10 = r9.id     // Catch: java.lang.Throwable -> Laf java.io.IOException -> Lb6
                java.lang.String r11 = r9.key     // Catch: java.lang.Throwable -> Laf java.io.IOException -> Lb6
                r14.put(r10, r11)     // Catch: java.lang.Throwable -> Laf java.io.IOException -> Lb6
                int r10 = r12.hashCachedContent(r9, r4)     // Catch: java.lang.Throwable -> Laf java.io.IOException -> Lb6
                int r7 = r7 + r10
                int r8 = r8 + 1
                goto L71
            L8b:
                int r8 = r0.readInt()     // Catch: java.lang.Throwable -> Laf java.io.IOException -> Lb6
                int r9 = r0.read()     // Catch: java.lang.Throwable -> Laf java.io.IOException -> Lb6
                r10 = -1
                if (r9 != r10) goto L98
                r9 = 1
                goto L99
            L98:
                r9 = 0
            L99:
                if (r8 != r7) goto La3
                if (r9 != 0) goto L9e
                goto La3
            L9e:
                com.google.android.exoplayer2.util.Util.closeQuietly(r0)
                return r1
            La3:
                com.google.android.exoplayer2.util.Util.closeQuietly(r0)
                return r2
            La9:
                com.google.android.exoplayer2.util.Util.closeQuietly(r0)
                return r2
            Laf:
                r1 = move-exception
                if (r0 == 0) goto Lb5
                com.google.android.exoplayer2.util.Util.closeQuietly(r0)
            Lb5:
                throw r1
            Lb6:
                r1 = move-exception
                if (r0 == 0) goto Lbd
                com.google.android.exoplayer2.util.Util.closeQuietly(r0)
            Lbd:
                return r2
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.upstream.cache.CachedContentIndex.LegacyStorage.readFile(java.util.HashMap, android.util.SparseArray):boolean");
        }

        private void writeFile(HashMap<String, CachedContent> content) throws IOException {
            GeneralSecurityException e;
            DataOutputStream output = null;
            try {
                OutputStream outputStream = this.atomicFile.startWrite();
                ReusableBufferedOutputStream reusableBufferedOutputStream = this.bufferedOutputStream;
                if (reusableBufferedOutputStream == null) {
                    this.bufferedOutputStream = new ReusableBufferedOutputStream(outputStream);
                } else {
                    reusableBufferedOutputStream.reset(outputStream);
                }
                DataOutputStream output2 = new DataOutputStream(this.bufferedOutputStream);
                output2.writeInt(2);
                int flags = this.encrypt ? 1 : 0;
                output2.writeInt(flags);
                if (this.encrypt) {
                    byte[] initializationVector = new byte[16];
                    this.random.nextBytes(initializationVector);
                    output2.write(initializationVector);
                    IvParameterSpec ivParameterSpec = new IvParameterSpec(initializationVector);
                    try {
                        this.cipher.init(1, this.secretKeySpec, ivParameterSpec);
                        output2.flush();
                        output2 = new DataOutputStream(new CipherOutputStream(this.bufferedOutputStream, this.cipher));
                    } catch (InvalidAlgorithmParameterException e2) {
                        e = e2;
                        throw new IllegalStateException(e);
                    } catch (InvalidKeyException e3) {
                        e = e3;
                        throw new IllegalStateException(e);
                    }
                }
                output2.writeInt(content.size());
                int hashCode = 0;
                for (CachedContent cachedContent : content.values()) {
                    writeCachedContent(cachedContent, output2);
                    hashCode += hashCachedContent(cachedContent, 2);
                }
                output2.writeInt(hashCode);
                this.atomicFile.endWrite(output2);
                output = null;
            } finally {
                Util.closeQuietly(output);
            }
        }

        private int hashCachedContent(CachedContent cachedContent, int version) {
            int result = (cachedContent.id * 31) + cachedContent.key.hashCode();
            if (version < 2) {
                long length = ContentMetadata.CC.getContentLength(cachedContent.getMetadata());
                return (result * 31) + ((int) ((length >>> 32) ^ length));
            }
            return (result * 31) + cachedContent.getMetadata().hashCode();
        }

        private CachedContent readCachedContent(int version, DataInputStream input) throws IOException {
            DefaultContentMetadata metadata;
            int id = input.readInt();
            String key = input.readUTF();
            if (version >= 2) {
                metadata = CachedContentIndex.readContentMetadata(input);
            } else {
                long length = input.readLong();
                ContentMetadataMutations mutations = new ContentMetadataMutations();
                ContentMetadataMutations.setContentLength(mutations, length);
                metadata = DefaultContentMetadata.EMPTY.copyWithMutationsApplied(mutations);
            }
            return new CachedContent(id, key, metadata);
        }

        private void writeCachedContent(CachedContent cachedContent, DataOutputStream output) throws IOException {
            output.writeInt(cachedContent.id);
            output.writeUTF(cachedContent.key);
            CachedContentIndex.writeContentMetadata(cachedContent.getMetadata(), output);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static final class DatabaseStorage implements Storage {
        private static final String COLUMN_ID = "id";
        private static final int COLUMN_INDEX_ID = 0;
        private static final int COLUMN_INDEX_KEY = 1;
        private static final int COLUMN_INDEX_METADATA = 2;
        private static final String COLUMN_METADATA = "metadata";
        private static final String TABLE_PREFIX = "ExoPlayerCacheIndex";
        private static final String TABLE_SCHEMA = "(id INTEGER PRIMARY KEY NOT NULL,key TEXT NOT NULL,metadata BLOB NOT NULL)";
        private static final int TABLE_VERSION = 1;
        private static final String WHERE_ID_EQUALS = "id = ?";
        private final DatabaseProvider databaseProvider;
        private String hexUid;
        private final SparseArray<CachedContent> pendingUpdates = new SparseArray<>();
        private String tableName;
        private static final String COLUMN_KEY = "key";
        private static final String[] COLUMNS = {"id", COLUMN_KEY, "metadata"};

        public static void delete(DatabaseProvider databaseProvider, long uid) throws DatabaseIOException {
            delete(databaseProvider, Long.toHexString(uid));
        }

        public DatabaseStorage(DatabaseProvider databaseProvider) {
            this.databaseProvider = databaseProvider;
        }

        @Override // com.google.android.exoplayer2.upstream.cache.CachedContentIndex.Storage
        public void initialize(long uid) {
            String hexString = Long.toHexString(uid);
            this.hexUid = hexString;
            this.tableName = getTableName(hexString);
        }

        @Override // com.google.android.exoplayer2.upstream.cache.CachedContentIndex.Storage
        public boolean exists() throws DatabaseIOException {
            return VersionTable.getVersion(this.databaseProvider.getReadableDatabase(), 1, this.hexUid) != -1;
        }

        @Override // com.google.android.exoplayer2.upstream.cache.CachedContentIndex.Storage
        public void delete() throws DatabaseIOException {
            delete(this.databaseProvider, this.hexUid);
        }

        @Override // com.google.android.exoplayer2.upstream.cache.CachedContentIndex.Storage
        public void load(HashMap<String, CachedContent> content, SparseArray<String> idToKey) throws IOException {
            Assertions.checkState(this.pendingUpdates.size() == 0);
            try {
                int version = VersionTable.getVersion(this.databaseProvider.getReadableDatabase(), 1, this.hexUid);
                if (version != 1) {
                    SQLiteDatabase writableDatabase = this.databaseProvider.getWritableDatabase();
                    writableDatabase.beginTransactionNonExclusive();
                    initializeTable(writableDatabase);
                    writableDatabase.setTransactionSuccessful();
                    writableDatabase.endTransaction();
                }
                Cursor cursor = getCursor();
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(0);
                    String key = cursor.getString(1);
                    byte[] metadataBytes = cursor.getBlob(2);
                    ByteArrayInputStream inputStream = new ByteArrayInputStream(metadataBytes);
                    DataInputStream input = new DataInputStream(inputStream);
                    DefaultContentMetadata metadata = CachedContentIndex.readContentMetadata(input);
                    CachedContent cachedContent = new CachedContent(id, key, metadata);
                    content.put(cachedContent.key, cachedContent);
                    idToKey.put(cachedContent.id, cachedContent.key);
                }
                if (cursor != null) {
                    cursor.close();
                }
            } catch (SQLiteException e) {
                content.clear();
                idToKey.clear();
                throw new DatabaseIOException(e);
            }
        }

        @Override // com.google.android.exoplayer2.upstream.cache.CachedContentIndex.Storage
        public void storeFully(HashMap<String, CachedContent> content) throws IOException {
            try {
                SQLiteDatabase writableDatabase = this.databaseProvider.getWritableDatabase();
                writableDatabase.beginTransactionNonExclusive();
                initializeTable(writableDatabase);
                for (CachedContent cachedContent : content.values()) {
                    addOrUpdateRow(writableDatabase, cachedContent);
                }
                writableDatabase.setTransactionSuccessful();
                this.pendingUpdates.clear();
                writableDatabase.endTransaction();
            } catch (SQLException e) {
                throw new DatabaseIOException(e);
            }
        }

        @Override // com.google.android.exoplayer2.upstream.cache.CachedContentIndex.Storage
        public void storeIncremental(HashMap<String, CachedContent> content) throws IOException {
            if (this.pendingUpdates.size() == 0) {
                return;
            }
            try {
                SQLiteDatabase writableDatabase = this.databaseProvider.getWritableDatabase();
                writableDatabase.beginTransactionNonExclusive();
                for (int i = 0; i < this.pendingUpdates.size(); i++) {
                    CachedContent cachedContent = this.pendingUpdates.valueAt(i);
                    if (cachedContent == null) {
                        deleteRow(writableDatabase, this.pendingUpdates.keyAt(i));
                    } else {
                        addOrUpdateRow(writableDatabase, cachedContent);
                    }
                }
                writableDatabase.setTransactionSuccessful();
                this.pendingUpdates.clear();
                writableDatabase.endTransaction();
            } catch (SQLException e) {
                throw new DatabaseIOException(e);
            }
        }

        @Override // com.google.android.exoplayer2.upstream.cache.CachedContentIndex.Storage
        public void onUpdate(CachedContent cachedContent) {
            this.pendingUpdates.put(cachedContent.id, cachedContent);
        }

        @Override // com.google.android.exoplayer2.upstream.cache.CachedContentIndex.Storage
        public void onRemove(CachedContent cachedContent, boolean neverStored) {
            if (neverStored) {
                this.pendingUpdates.delete(cachedContent.id);
            } else {
                this.pendingUpdates.put(cachedContent.id, null);
            }
        }

        private Cursor getCursor() {
            return this.databaseProvider.getReadableDatabase().query(this.tableName, COLUMNS, null, null, null, null, null);
        }

        private void initializeTable(SQLiteDatabase writableDatabase) throws DatabaseIOException {
            VersionTable.setVersion(writableDatabase, 1, this.hexUid, 1);
            dropTable(writableDatabase, this.tableName);
            writableDatabase.execSQL("CREATE TABLE " + this.tableName + " " + TABLE_SCHEMA);
        }

        private void deleteRow(SQLiteDatabase writableDatabase, int key) {
            writableDatabase.delete(this.tableName, WHERE_ID_EQUALS, new String[]{Integer.toString(key)});
        }

        private void addOrUpdateRow(SQLiteDatabase writableDatabase, CachedContent cachedContent) throws IOException {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            CachedContentIndex.writeContentMetadata(cachedContent.getMetadata(), new DataOutputStream(outputStream));
            byte[] data = outputStream.toByteArray();
            ContentValues values = new ContentValues();
            values.put("id", Integer.valueOf(cachedContent.id));
            values.put(COLUMN_KEY, cachedContent.key);
            values.put("metadata", data);
            writableDatabase.replaceOrThrow(this.tableName, null, values);
        }

        private static void delete(DatabaseProvider databaseProvider, String hexUid) throws DatabaseIOException {
            try {
                String tableName = getTableName(hexUid);
                SQLiteDatabase writableDatabase = databaseProvider.getWritableDatabase();
                writableDatabase.beginTransactionNonExclusive();
                VersionTable.removeVersion(writableDatabase, 1, hexUid);
                dropTable(writableDatabase, tableName);
                writableDatabase.setTransactionSuccessful();
                writableDatabase.endTransaction();
            } catch (SQLException e) {
                throw new DatabaseIOException(e);
            }
        }

        private static void dropTable(SQLiteDatabase writableDatabase, String tableName) {
            writableDatabase.execSQL("DROP TABLE IF EXISTS " + tableName);
        }

        private static String getTableName(String hexUid) {
            return TABLE_PREFIX + hexUid;
        }
    }
}
