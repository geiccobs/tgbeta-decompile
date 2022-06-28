package org.telegram.messenger;

import android.os.Looper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.SQLite.SQLiteDatabase;
import org.telegram.SQLite.SQLiteException;
import org.telegram.SQLite.SQLitePreparedStatement;
/* loaded from: classes4.dex */
public class FilePathDatabase {
    private static final String DATABASE_BACKUP_NAME = "file_to_path_backup";
    private static final String DATABASE_NAME = "file_to_path";
    private static final int LAST_DB_VERSION = 1;
    private File cacheFile;
    private final int currentAccount;
    private SQLiteDatabase database;
    private final DispatchQueue dispatchQueue;
    private File shmCacheFile;

    public FilePathDatabase(int currentAccount) {
        this.currentAccount = currentAccount;
        DispatchQueue dispatchQueue = new DispatchQueue("files_database_queue_" + currentAccount);
        this.dispatchQueue = dispatchQueue;
        dispatchQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.FilePathDatabase$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                FilePathDatabase.this.m234lambda$new$0$orgtelegrammessengerFilePathDatabase();
            }
        });
    }

    /* renamed from: lambda$new$0$org-telegram-messenger-FilePathDatabase */
    public /* synthetic */ void m234lambda$new$0$orgtelegrammessengerFilePathDatabase() {
        createDatabase(0, false);
    }

    public void createDatabase(int tryCount, boolean fromBackup) {
        File filesDir = ApplicationLoader.getFilesDirFixed();
        if (this.currentAccount != 0) {
            filesDir = new File(filesDir, "account" + this.currentAccount + "/");
            filesDir.mkdirs();
        }
        this.cacheFile = new File(filesDir, "file_to_path.db");
        this.shmCacheFile = new File(filesDir, "file_to_path.db-shm");
        boolean createTable = false;
        if (!this.cacheFile.exists()) {
            createTable = true;
        }
        try {
            SQLiteDatabase sQLiteDatabase = new SQLiteDatabase(this.cacheFile.getPath());
            this.database = sQLiteDatabase;
            sQLiteDatabase.executeFast("PRAGMA secure_delete = ON").stepThis().dispose();
            this.database.executeFast("PRAGMA temp_store = MEMORY").stepThis().dispose();
            if (!createTable) {
                int version = this.database.executeInt("PRAGMA user_version", new Object[0]).intValue();
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("current files db version = " + version);
                }
                if (version == 0) {
                    throw new Exception("malformed");
                }
            } else {
                this.database.executeFast("CREATE TABLE paths(document_id INTEGER, dc_id INTEGER, type INTEGER, path TEXT, PRIMARY KEY(document_id, dc_id, type));").stepThis().dispose();
                this.database.executeFast("PRAGMA user_version = 1").stepThis().dispose();
            }
            if (!fromBackup) {
                createBackup();
            }
            FileLog.d("files db created from_backup= " + fromBackup);
        } catch (Exception e) {
            if (tryCount < 4) {
                if (!fromBackup && restoreBackup()) {
                    createDatabase(tryCount + 1, true);
                    return;
                }
                this.cacheFile.delete();
                this.shmCacheFile.delete();
                createDatabase(tryCount + 1, false);
            }
            if (BuildVars.DEBUG_VERSION) {
                FileLog.e(e);
            }
        }
    }

    private void createBackup() {
        File filesDir = ApplicationLoader.getFilesDirFixed();
        if (this.currentAccount != 0) {
            filesDir = new File(filesDir, "account" + this.currentAccount + "/");
            filesDir.mkdirs();
        }
        File backupCacheFile = new File(filesDir, "file_to_path_backup.db");
        try {
            AndroidUtilities.copyFile(this.cacheFile, backupCacheFile);
            FileLog.d("file db backup created " + backupCacheFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean restoreBackup() {
        File filesDir = ApplicationLoader.getFilesDirFixed();
        if (this.currentAccount != 0) {
            filesDir = new File(filesDir, "account" + this.currentAccount + "/");
            filesDir.mkdirs();
        }
        File backupCacheFile = new File(filesDir, "file_to_path_backup.db");
        if (!backupCacheFile.exists()) {
            return false;
        }
        try {
            return AndroidUtilities.copyFile(backupCacheFile, this.cacheFile);
        } catch (IOException e) {
            FileLog.e(e);
            return false;
        }
    }

    public String getPath(final long documentId, final int dc, final int type, boolean useQueue) {
        if (useQueue) {
            if (BuildVars.DEBUG_VERSION && this.dispatchQueue.getHandler() != null && Thread.currentThread() == this.dispatchQueue.getHandler().getLooper().getThread()) {
                throw new RuntimeException("Error, lead to infinity loop");
            }
            final CountDownLatch syncLatch = new CountDownLatch(1);
            final String[] res = new String[1];
            System.currentTimeMillis();
            this.dispatchQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.FilePathDatabase$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    FilePathDatabase.this.m233lambda$getPath$1$orgtelegrammessengerFilePathDatabase(documentId, dc, type, res, syncLatch);
                }
            });
            try {
                syncLatch.await();
            } catch (Exception e) {
            }
            return res[0];
        }
        String res2 = null;
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            SQLiteCursor cursor = sQLiteDatabase.queryFinalized("SELECT path FROM paths WHERE document_id = " + documentId + " AND dc_id = " + dc + " AND type = " + type, new Object[0]);
            if (cursor.next()) {
                res2 = cursor.stringValue(0);
                if (BuildVars.DEBUG_VERSION) {
                    FileLog.d("get file path id=" + documentId + " dc=" + dc + " type=" + type + " path=" + res2);
                }
            }
            cursor.dispose();
        } catch (SQLiteException e2) {
            FileLog.e(e2);
        }
        return res2;
    }

    /* renamed from: lambda$getPath$1$org-telegram-messenger-FilePathDatabase */
    public /* synthetic */ void m233lambda$getPath$1$orgtelegrammessengerFilePathDatabase(long documentId, int dc, int type, String[] res, CountDownLatch syncLatch) {
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            SQLiteCursor cursor = sQLiteDatabase.queryFinalized("SELECT path FROM paths WHERE document_id = " + documentId + " AND dc_id = " + dc + " AND type = " + type, new Object[0]);
            if (cursor.next()) {
                res[0] = cursor.stringValue(0);
                if (BuildVars.DEBUG_VERSION) {
                    FileLog.d("get file path id=" + documentId + " dc=" + dc + " type=" + type + " path=" + res[0]);
                }
            }
            cursor.dispose();
        } catch (SQLiteException e) {
            FileLog.e(e);
        }
        syncLatch.countDown();
    }

    public void putPath(final long id, final int dc, final int type, final String path) {
        this.dispatchQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.FilePathDatabase$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                FilePathDatabase.this.m235lambda$putPath$2$orgtelegrammessengerFilePathDatabase(id, dc, type, path);
            }
        });
    }

    /* renamed from: lambda$putPath$2$org-telegram-messenger-FilePathDatabase */
    public /* synthetic */ void m235lambda$putPath$2$orgtelegrammessengerFilePathDatabase(long id, int dc, int type, String path) {
        if (BuildVars.DEBUG_VERSION) {
            FileLog.d("put file path id=" + id + " dc=" + dc + " type=" + type + " path=" + path);
        }
        try {
            if (path != null) {
                SQLitePreparedStatement state = this.database.executeFast("REPLACE INTO paths VALUES(?, ?, ?, ?)");
                state.requery();
                state.bindLong(1, id);
                state.bindInteger(2, dc);
                state.bindInteger(3, type);
                state.bindString(4, path);
                state.step();
            } else {
                SQLiteDatabase sQLiteDatabase = this.database;
                sQLiteDatabase.executeFast("DELETE FROM paths WHERE document_id = " + id + " AND dc_id = " + dc + " AND type = " + type).stepThis().dispose();
            }
        } catch (SQLiteException e) {
            FileLog.e(e);
        }
    }

    public void checkMediaExistance(ArrayList<MessageObject> messageObjects) {
        if (messageObjects.isEmpty()) {
            return;
        }
        final ArrayList<MessageObject> arrayListFinal = new ArrayList<>(messageObjects);
        final CountDownLatch syncLatch = new CountDownLatch(1);
        long time = System.currentTimeMillis();
        this.dispatchQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.FilePathDatabase$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                FilePathDatabase.lambda$checkMediaExistance$3(arrayListFinal, syncLatch);
            }
        });
        try {
            syncLatch.await();
        } catch (InterruptedException e) {
            FileLog.e(e);
        }
        FileLog.d("checkMediaExistance size=" + messageObjects.size() + " time=" + (System.currentTimeMillis() - time));
        if (BuildVars.DEBUG_VERSION && Thread.currentThread() == Looper.getMainLooper().getThread()) {
            FileLog.e(new Exception("warning, not allowed in main thread"));
        }
    }

    public static /* synthetic */ void lambda$checkMediaExistance$3(ArrayList arrayListFinal, CountDownLatch syncLatch) {
        for (int i = 0; i < arrayListFinal.size(); i++) {
            try {
                MessageObject messageObject = (MessageObject) arrayListFinal.get(i);
                messageObject.checkMediaExistance(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        syncLatch.countDown();
    }

    /* loaded from: classes4.dex */
    public static class PathData {
        public final int dc;
        public final long id;
        public final int type;

        public PathData(long documentId, int dcId, int type) {
            this.id = documentId;
            this.dc = dcId;
            this.type = type;
        }
    }
}
