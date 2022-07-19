package com.microsoft.appcenter.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import com.microsoft.appcenter.Constants;
import com.microsoft.appcenter.ingestion.models.Log;
import com.microsoft.appcenter.utils.AppCenterLog;
import com.microsoft.appcenter.utils.crypto.CryptoUtils;
import com.microsoft.appcenter.utils.storage.DatabaseManager;
import com.microsoft.appcenter.utils.storage.FileManager;
import com.microsoft.appcenter.utils.storage.SQLiteUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.json.JSONException;
/* loaded from: classes.dex */
public class DatabasePersistence extends Persistence {
    static final ContentValues SCHEMA = getContentValues("", "", "", "", "", 0);
    private final Context mContext;
    final DatabaseManager mDatabaseManager;
    private final File mLargePayloadDirectory;
    final Set<Long> mPendingDbIdentifiers;
    final Map<String, List<Long>> mPendingDbIdentifiersGroups;

    public DatabasePersistence(Context context) {
        this(context, 6, SCHEMA);
    }

    DatabasePersistence(Context context, int i, ContentValues contentValues) {
        this.mContext = context;
        this.mPendingDbIdentifiersGroups = new HashMap();
        this.mPendingDbIdentifiers = new HashSet();
        this.mDatabaseManager = new DatabaseManager(context, "com.microsoft.appcenter.persistence", "logs", i, contentValues, "CREATE TABLE IF NOT EXISTS `logs`(`oid` INTEGER PRIMARY KEY AUTOINCREMENT,`target_token` TEXT,`type` TEXT,`priority` INTEGER,`log` TEXT,`persistence_group` TEXT,`target_key` TEXT);", new DatabaseManager.Listener(this) { // from class: com.microsoft.appcenter.persistence.DatabasePersistence.1
            @Override // com.microsoft.appcenter.utils.storage.DatabaseManager.Listener
            public void onCreate(SQLiteDatabase sQLiteDatabase) {
                sQLiteDatabase.execSQL("CREATE INDEX `ix_logs_priority` ON logs (`priority`)");
            }

            @Override // com.microsoft.appcenter.utils.storage.DatabaseManager.Listener
            public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i2, int i3) {
                sQLiteDatabase.execSQL("DROP TABLE `logs`");
                sQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS `logs`(`oid` INTEGER PRIMARY KEY AUTOINCREMENT,`target_token` TEXT,`type` TEXT,`priority` INTEGER,`log` TEXT,`persistence_group` TEXT,`target_key` TEXT);");
                sQLiteDatabase.execSQL("CREATE INDEX `ix_logs_priority` ON logs (`priority`)");
            }
        });
        File file = new File(Constants.FILES_PATH + "/appcenter/database_large_payloads");
        this.mLargePayloadDirectory = file;
        file.mkdirs();
    }

    private static ContentValues getContentValues(String str, String str2, String str3, String str4, String str5, int i) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("persistence_group", str);
        contentValues.put("log", str2);
        contentValues.put("target_token", str3);
        contentValues.put("type", str4);
        contentValues.put("target_key", str5);
        contentValues.put("priority", Integer.valueOf(i));
        return contentValues;
    }

    @Override // com.microsoft.appcenter.persistence.Persistence
    public boolean setMaxStorageSize(long j) {
        return this.mDatabaseManager.setMaxSize(j);
    }

    /* JADX WARN: Code restructure failed: missing block: B:23:0x00aa, code lost:
        r8 = null;
     */
    @Override // com.microsoft.appcenter.persistence.Persistence
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public long putLog(com.microsoft.appcenter.ingestion.models.Log r17, java.lang.String r18, int r19) throws com.microsoft.appcenter.persistence.Persistence.PersistenceException {
        /*
            Method dump skipped, instructions count: 346
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.microsoft.appcenter.persistence.DatabasePersistence.putLog(com.microsoft.appcenter.ingestion.models.Log, java.lang.String, int):long");
    }

    File getLargePayloadGroupDirectory(String str) {
        return new File(this.mLargePayloadDirectory, str);
    }

    File getLargePayloadFile(File file, long j) {
        return new File(file, j + ".json");
    }

    private void deleteLog(File file, long j) {
        getLargePayloadFile(file, j).delete();
        this.mDatabaseManager.delete(j);
    }

    @Override // com.microsoft.appcenter.persistence.Persistence
    public void deleteLogs(String str, String str2) {
        AppCenterLog.debug("AppCenter", "Deleting logs from the Persistence database for " + str + " with " + str2);
        AppCenterLog.debug("AppCenter", "The IDs for deleting log(s) is/are:");
        Map<String, List<Long>> map = this.mPendingDbIdentifiersGroups;
        List<Long> remove = map.remove(str + str2);
        File largePayloadGroupDirectory = getLargePayloadGroupDirectory(str);
        if (remove != null) {
            for (Long l : remove) {
                AppCenterLog.debug("AppCenter", "\t" + l);
                deleteLog(largePayloadGroupDirectory, l.longValue());
                this.mPendingDbIdentifiers.remove(l);
            }
        }
    }

    @Override // com.microsoft.appcenter.persistence.Persistence
    public void deleteLogs(String str) {
        AppCenterLog.debug("AppCenter", "Deleting all logs from the Persistence database for " + str);
        File largePayloadGroupDirectory = getLargePayloadGroupDirectory(str);
        File[] listFiles = largePayloadGroupDirectory.listFiles();
        if (listFiles != null) {
            for (File file : listFiles) {
                file.delete();
            }
        }
        largePayloadGroupDirectory.delete();
        AppCenterLog.debug("AppCenter", "Deleted " + this.mDatabaseManager.delete("persistence_group", str) + " logs.");
        Iterator<String> it = this.mPendingDbIdentifiersGroups.keySet().iterator();
        while (it.hasNext()) {
            if (it.next().startsWith(str)) {
                it.remove();
            }
        }
    }

    @Override // com.microsoft.appcenter.persistence.Persistence
    public int countLogs(String str) {
        SQLiteQueryBuilder newSQLiteQueryBuilder = SQLiteUtils.newSQLiteQueryBuilder();
        newSQLiteQueryBuilder.appendWhere("persistence_group = ?");
        int i = 0;
        try {
            Cursor cursor = this.mDatabaseManager.getCursor(newSQLiteQueryBuilder, new String[]{"COUNT(*)"}, new String[]{str}, null);
            cursor.moveToNext();
            i = cursor.getInt(0);
            cursor.close();
        } catch (RuntimeException e) {
            AppCenterLog.error("AppCenter", "Failed to get logs count: ", e);
        }
        return i;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.microsoft.appcenter.persistence.Persistence
    public String getLogs(String str, Collection<String> collection, int i, List<Log> list) {
        Cursor cursor;
        AppCenterLog.debug("AppCenter", "Trying to get " + i + " logs from the Persistence database for " + str);
        SQLiteQueryBuilder newSQLiteQueryBuilder = SQLiteUtils.newSQLiteQueryBuilder();
        newSQLiteQueryBuilder.appendWhere("persistence_group = ?");
        ArrayList arrayList = new ArrayList();
        arrayList.add(str);
        if (!collection.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (int i2 = 0; i2 < collection.size(); i2++) {
                sb.append("?,");
            }
            sb.deleteCharAt(sb.length() - 1);
            newSQLiteQueryBuilder.appendWhere(" AND ");
            newSQLiteQueryBuilder.appendWhere("target_key NOT IN (" + sb.toString() + ")");
            arrayList.addAll(collection);
        }
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        ArrayList<Long> arrayList2 = new ArrayList();
        File largePayloadGroupDirectory = getLargePayloadGroupDirectory(str);
        String[] strArr = (String[]) arrayList.toArray(new String[0]);
        try {
            cursor = this.mDatabaseManager.getCursor(newSQLiteQueryBuilder, null, strArr, "priority DESC, oid");
        } catch (RuntimeException e) {
            AppCenterLog.error("AppCenter", "Failed to get logs: ", e);
            cursor = null;
        }
        int i3 = 0;
        while (cursor != null) {
            ContentValues nextValues = this.mDatabaseManager.nextValues(cursor);
            if (nextValues == null || i3 >= i) {
                break;
            }
            Long asLong = nextValues.getAsLong("oid");
            if (asLong == null) {
                AppCenterLog.error("AppCenter", "Empty database record, probably content was larger than 2MB, need to delete as it's now corrupted.");
                Iterator<Long> it = getLogsIds(newSQLiteQueryBuilder, strArr).iterator();
                while (true) {
                    if (it.hasNext()) {
                        Long next = it.next();
                        if (!this.mPendingDbIdentifiers.contains(next) && !linkedHashMap.containsKey(next)) {
                            deleteLog(largePayloadGroupDirectory, next.longValue());
                            AppCenterLog.error("AppCenter", "Empty database corrupted empty record deleted, id=" + next);
                            break;
                        }
                    }
                }
            } else if (!this.mPendingDbIdentifiers.contains(asLong)) {
                try {
                    String asString = nextValues.getAsString("log");
                    if (asString == null) {
                        File largePayloadFile = getLargePayloadFile(largePayloadGroupDirectory, asLong.longValue());
                        AppCenterLog.debug("AppCenter", "Read payload file " + largePayloadFile);
                        asString = FileManager.read(largePayloadFile);
                        if (asString == null) {
                            throw new JSONException("Log payload is null and not stored as a file.");
                            break;
                        }
                    }
                    Log deserializeLog = getLogSerializer().deserializeLog(asString, nextValues.getAsString("type"));
                    String asString2 = nextValues.getAsString("target_token");
                    if (asString2 != null) {
                        deserializeLog.addTransmissionTarget(CryptoUtils.getInstance(this.mContext).decrypt(asString2).getDecryptedData());
                    }
                    linkedHashMap.put(asLong, deserializeLog);
                    i3++;
                } catch (JSONException e2) {
                    AppCenterLog.error("AppCenter", "Cannot deserialize a log in the database", e2);
                    arrayList2.add(asLong);
                }
            }
        }
        if (cursor != null) {
            try {
                cursor.close();
            } catch (RuntimeException unused) {
            }
        }
        if (arrayList2.size() > 0) {
            for (Long l : arrayList2) {
                deleteLog(largePayloadGroupDirectory, l.longValue());
            }
            AppCenterLog.warn("AppCenter", "Deleted logs that cannot be deserialized");
        }
        if (linkedHashMap.size() <= 0) {
            AppCenterLog.debug("AppCenter", "No logs found in the Persistence database at the moment");
            return null;
        }
        String uuid = UUID.randomUUID().toString();
        AppCenterLog.debug("AppCenter", "Returning " + linkedHashMap.size() + " log(s) with an ID, " + uuid);
        AppCenterLog.debug("AppCenter", "The SID/ID pairs for returning log(s) is/are:");
        ArrayList arrayList3 = new ArrayList();
        for (Map.Entry entry : linkedHashMap.entrySet()) {
            Long l2 = (Long) entry.getKey();
            this.mPendingDbIdentifiers.add(l2);
            arrayList3.add(l2);
            list.add(entry.getValue());
            AppCenterLog.debug("AppCenter", "\t" + ((Log) entry.getValue()).getSid() + " / " + l2);
        }
        this.mPendingDbIdentifiersGroups.put(str + uuid, arrayList3);
        return uuid;
    }

    @Override // com.microsoft.appcenter.persistence.Persistence
    public void clearPendingLogState() {
        this.mPendingDbIdentifiers.clear();
        this.mPendingDbIdentifiersGroups.clear();
        AppCenterLog.debug("AppCenter", "Cleared pending log states");
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        this.mDatabaseManager.close();
    }

    private List<Long> getLogsIds(SQLiteQueryBuilder sQLiteQueryBuilder, String[] strArr) {
        ArrayList arrayList = new ArrayList();
        try {
            Cursor cursor = this.mDatabaseManager.getCursor(sQLiteQueryBuilder, DatabaseManager.SELECT_PRIMARY_KEY, strArr, null);
            while (cursor.moveToNext()) {
                arrayList.add(this.mDatabaseManager.buildValues(cursor).getAsLong("oid"));
            }
            cursor.close();
        } catch (RuntimeException e) {
            AppCenterLog.error("AppCenter", "Failed to get corrupted ids: ", e);
        }
        return arrayList;
    }
}
