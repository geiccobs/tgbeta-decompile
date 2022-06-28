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
/* loaded from: classes3.dex */
public class DatabasePersistence extends Persistence {
    private static final String COLUMN_DATA_TYPE = "type";
    static final String COLUMN_GROUP = "persistence_group";
    static final String COLUMN_LOG = "log";
    static final String COLUMN_PRIORITY = "priority";
    static final String COLUMN_TARGET_KEY = "target_key";
    static final String COLUMN_TARGET_TOKEN = "target_token";
    static final String CREATE_LOGS_SQL = "CREATE TABLE IF NOT EXISTS `logs`(`oid` INTEGER PRIMARY KEY AUTOINCREMENT,`target_token` TEXT,`type` TEXT,`priority` INTEGER,`log` TEXT,`persistence_group` TEXT,`target_key` TEXT);";
    private static final String CREATE_PRIORITY_INDEX_LOGS = "CREATE INDEX `ix_logs_priority` ON logs (`priority`)";
    static final String DATABASE = "com.microsoft.appcenter.persistence";
    private static final String DROP_LOGS_SQL = "DROP TABLE `logs`";
    private static final String GET_SORT_ORDER = "priority DESC, oid";
    private static final String PAYLOAD_FILE_EXTENSION = ".json";
    private static final String PAYLOAD_LARGE_DIRECTORY = "/appcenter/database_large_payloads";
    private static final int PAYLOAD_MAX_SIZE = 1992294;
    static final ContentValues SCHEMA = getContentValues("", "", "", "", "", 0);
    static final String TABLE = "logs";
    private static final int VERSION = 6;
    static final int VERSION_TIMESTAMP_COLUMN = 5;
    private final Context mContext;
    final DatabaseManager mDatabaseManager;
    private final File mLargePayloadDirectory;
    final Set<Long> mPendingDbIdentifiers;
    final Map<String, List<Long>> mPendingDbIdentifiersGroups;

    public DatabasePersistence(Context context) {
        this(context, 6, SCHEMA);
    }

    DatabasePersistence(Context context, int version, ContentValues schema) {
        this.mContext = context;
        this.mPendingDbIdentifiersGroups = new HashMap();
        this.mPendingDbIdentifiers = new HashSet();
        this.mDatabaseManager = new DatabaseManager(context, DATABASE, TABLE, version, schema, CREATE_LOGS_SQL, new DatabaseManager.Listener() { // from class: com.microsoft.appcenter.persistence.DatabasePersistence.1
            @Override // com.microsoft.appcenter.utils.storage.DatabaseManager.Listener
            public void onCreate(SQLiteDatabase db) {
                db.execSQL(DatabasePersistence.CREATE_PRIORITY_INDEX_LOGS);
            }

            @Override // com.microsoft.appcenter.utils.storage.DatabaseManager.Listener
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                db.execSQL(DatabasePersistence.DROP_LOGS_SQL);
                db.execSQL(DatabasePersistence.CREATE_LOGS_SQL);
                db.execSQL(DatabasePersistence.CREATE_PRIORITY_INDEX_LOGS);
            }
        });
        File file = new File(Constants.FILES_PATH + PAYLOAD_LARGE_DIRECTORY);
        this.mLargePayloadDirectory = file;
        file.mkdirs();
    }

    private static ContentValues getContentValues(String group, String logJ, String targetToken, String type, String targetKey, int priority) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_GROUP, group);
        values.put(COLUMN_LOG, logJ);
        values.put(COLUMN_TARGET_TOKEN, targetToken);
        values.put("type", type);
        values.put(COLUMN_TARGET_KEY, targetKey);
        values.put(COLUMN_PRIORITY, Integer.valueOf(priority));
        return values;
    }

    @Override // com.microsoft.appcenter.persistence.Persistence
    public boolean setMaxStorageSize(long maxStorageSizeInBytes) {
        return this.mDatabaseManager.setMaxSize(maxStorageSizeInBytes);
    }

    /* JADX WARN: Code restructure failed: missing block: B:23:0x00a9, code lost:
        r11 = null;
     */
    @Override // com.microsoft.appcenter.persistence.Persistence
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public long putLog(com.microsoft.appcenter.ingestion.models.Log r21, java.lang.String r22, int r23) throws com.microsoft.appcenter.persistence.Persistence.PersistenceException {
        /*
            Method dump skipped, instructions count: 366
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.microsoft.appcenter.persistence.DatabasePersistence.putLog(com.microsoft.appcenter.ingestion.models.Log, java.lang.String, int):long");
    }

    File getLargePayloadGroupDirectory(String group) {
        return new File(this.mLargePayloadDirectory, group);
    }

    File getLargePayloadFile(File directory, long databaseId) {
        return new File(directory, databaseId + ".json");
    }

    private void deleteLog(File groupLargePayloadDirectory, long id) {
        getLargePayloadFile(groupLargePayloadDirectory, id).delete();
        this.mDatabaseManager.delete(id);
    }

    @Override // com.microsoft.appcenter.persistence.Persistence
    public void deleteLogs(String group, String id) {
        AppCenterLog.debug("AppCenter", "Deleting logs from the Persistence database for " + group + " with " + id);
        AppCenterLog.debug("AppCenter", "The IDs for deleting log(s) is/are:");
        Map<String, List<Long>> map = this.mPendingDbIdentifiersGroups;
        List<Long> dbIdentifiers = map.remove(group + id);
        File directory = getLargePayloadGroupDirectory(group);
        if (dbIdentifiers != null) {
            for (Long dbIdentifier : dbIdentifiers) {
                AppCenterLog.debug("AppCenter", "\t" + dbIdentifier);
                deleteLog(directory, dbIdentifier.longValue());
                this.mPendingDbIdentifiers.remove(dbIdentifier);
            }
        }
    }

    @Override // com.microsoft.appcenter.persistence.Persistence
    public void deleteLogs(String group) {
        AppCenterLog.debug("AppCenter", "Deleting all logs from the Persistence database for " + group);
        File directory = getLargePayloadGroupDirectory(group);
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                file.delete();
            }
        }
        directory.delete();
        int deletedCount = this.mDatabaseManager.delete(COLUMN_GROUP, group);
        AppCenterLog.debug("AppCenter", "Deleted " + deletedCount + " logs.");
        Iterator<String> iterator = this.mPendingDbIdentifiersGroups.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            if (key.startsWith(group)) {
                iterator.remove();
            }
        }
    }

    @Override // com.microsoft.appcenter.persistence.Persistence
    public int countLogs(String group) {
        SQLiteQueryBuilder builder = SQLiteUtils.newSQLiteQueryBuilder();
        builder.appendWhere("persistence_group = ?");
        int count = 0;
        try {
            Cursor cursor = this.mDatabaseManager.getCursor(builder, new String[]{"COUNT(*)"}, new String[]{group}, null);
            cursor.moveToNext();
            count = cursor.getInt(0);
            cursor.close();
        } catch (RuntimeException e) {
            AppCenterLog.error("AppCenter", "Failed to get logs count: ", e);
        }
        return count;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.microsoft.appcenter.persistence.Persistence
    public String getLogs(String group, Collection<String> pausedTargetKeys, int limit, List<Log> outLogs) {
        SQLiteQueryBuilder builder;
        List<String> selectionArgs;
        SQLiteQueryBuilder builder2;
        JSONException e;
        String logPayload;
        int i = limit;
        AppCenterLog.debug("AppCenter", "Trying to get " + i + " logs from the Persistence database for " + group);
        SQLiteQueryBuilder builder3 = SQLiteUtils.newSQLiteQueryBuilder();
        builder3.appendWhere("persistence_group = ?");
        List<String> selectionArgs2 = new ArrayList<>();
        selectionArgs2.add(group);
        if (!pausedTargetKeys.isEmpty()) {
            StringBuilder filter = new StringBuilder();
            for (int i2 = 0; i2 < pausedTargetKeys.size(); i2++) {
                filter.append("?,");
            }
            int i3 = filter.length();
            filter.deleteCharAt(i3 - 1);
            builder3.appendWhere(" AND ");
            builder3.appendWhere("target_key NOT IN (" + filter.toString() + ")");
            selectionArgs2.addAll(pausedTargetKeys);
        }
        int count = 0;
        Map<Long, Log> candidates = new LinkedHashMap<>();
        List<Long> failedDbIdentifiers = new ArrayList<>();
        File largePayloadGroupDirectory = getLargePayloadGroupDirectory(group);
        String[] selectionArgsArray = (String[]) selectionArgs2.toArray(new String[0]);
        Cursor cursor = null;
        try {
            cursor = this.mDatabaseManager.getCursor(builder3, null, selectionArgsArray, GET_SORT_ORDER);
        } catch (RuntimeException e2) {
            AppCenterLog.error("AppCenter", "Failed to get logs: ", e2);
        }
        while (cursor != null) {
            ContentValues values = this.mDatabaseManager.nextValues(cursor);
            if (values == null || count >= i) {
                break;
            }
            Long dbIdentifier = values.getAsLong(DatabaseManager.PRIMARY_KEY);
            if (dbIdentifier == null) {
                AppCenterLog.error("AppCenter", "Empty database record, probably content was larger than 2MB, need to delete as it's now corrupted.");
                List<Long> corruptedIds = getLogsIds(builder3, selectionArgsArray);
                Iterator<Long> it = corruptedIds.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        builder = builder3;
                        selectionArgs = selectionArgs2;
                        break;
                    }
                    List<Long> corruptedIds2 = corruptedIds;
                    Long corruptedId = it.next();
                    if (!this.mPendingDbIdentifiers.contains(corruptedId) && !candidates.containsKey(corruptedId)) {
                        builder = builder3;
                        selectionArgs = selectionArgs2;
                        deleteLog(largePayloadGroupDirectory, corruptedId.longValue());
                        AppCenterLog.error("AppCenter", "Empty database corrupted empty record deleted, id=" + corruptedId);
                        break;
                    }
                    builder3 = builder3;
                    selectionArgs2 = selectionArgs2;
                    corruptedIds = corruptedIds2;
                }
                builder3 = builder;
                selectionArgs2 = selectionArgs;
                i = limit;
            } else {
                SQLiteQueryBuilder builder4 = builder3;
                List<String> selectionArgs3 = selectionArgs2;
                if (this.mPendingDbIdentifiers.contains(dbIdentifier)) {
                    builder2 = builder4;
                } else {
                    try {
                        String databasePayload = values.getAsString(COLUMN_LOG);
                        if (databasePayload == null) {
                            File file = getLargePayloadFile(largePayloadGroupDirectory, dbIdentifier.longValue());
                            StringBuilder sb = new StringBuilder();
                            builder2 = builder4;
                            try {
                                sb.append("Read payload file ");
                                sb.append(file);
                                AppCenterLog.debug("AppCenter", sb.toString());
                                logPayload = FileManager.read(file);
                                if (logPayload == null) {
                                    throw new JSONException("Log payload is null and not stored as a file.");
                                    break;
                                }
                            } catch (JSONException e3) {
                                e = e3;
                                AppCenterLog.error("AppCenter", "Cannot deserialize a log in the database", e);
                                failedDbIdentifiers.add(dbIdentifier);
                                i = limit;
                                builder3 = builder2;
                                selectionArgs2 = selectionArgs3;
                            }
                        } else {
                            builder2 = builder4;
                            logPayload = databasePayload;
                        }
                        String databasePayloadType = values.getAsString("type");
                        Log log = getLogSerializer().deserializeLog(logPayload, databasePayloadType);
                        String targetToken = values.getAsString(COLUMN_TARGET_TOKEN);
                        if (targetToken != null) {
                            CryptoUtils.DecryptedData data = CryptoUtils.getInstance(this.mContext).decrypt(targetToken);
                            log.addTransmissionTarget(data.getDecryptedData());
                        }
                        candidates.put(dbIdentifier, log);
                        count++;
                    } catch (JSONException e4) {
                        e = e4;
                        builder2 = builder4;
                    }
                }
                i = limit;
                builder3 = builder2;
                selectionArgs2 = selectionArgs3;
            }
        }
        if (cursor != null) {
            try {
                cursor.close();
            } catch (RuntimeException e5) {
            }
        }
        if (failedDbIdentifiers.size() > 0) {
            for (Long l : failedDbIdentifiers) {
                deleteLog(largePayloadGroupDirectory, l.longValue());
            }
            AppCenterLog.warn("AppCenter", "Deleted logs that cannot be deserialized");
        }
        if (candidates.size() <= 0) {
            AppCenterLog.debug("AppCenter", "No logs found in the Persistence database at the moment");
            return null;
        }
        String id = UUID.randomUUID().toString();
        AppCenterLog.debug("AppCenter", "Returning " + candidates.size() + " log(s) with an ID, " + id);
        AppCenterLog.debug("AppCenter", "The SID/ID pairs for returning log(s) is/are:");
        List<Long> pendingDbIdentifiersGroup = new ArrayList<>();
        for (Iterator<Map.Entry<Long, Log>> it2 = candidates.entrySet().iterator(); it2.hasNext(); it2 = it2) {
            Map.Entry<Long, Log> entry = it2.next();
            Long dbIdentifier2 = entry.getKey();
            this.mPendingDbIdentifiers.add(dbIdentifier2);
            pendingDbIdentifiersGroup.add(dbIdentifier2);
            outLogs.add(entry.getValue());
            AppCenterLog.debug("AppCenter", "\t" + entry.getValue().getSid() + " / " + dbIdentifier2);
        }
        Map<String, List<Long>> map = this.mPendingDbIdentifiersGroups;
        map.put(group + id, pendingDbIdentifiersGroup);
        return id;
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

    private List<Long> getLogsIds(SQLiteQueryBuilder builder, String[] selectionArgs) {
        List<Long> result = new ArrayList<>();
        try {
            Cursor cursor = this.mDatabaseManager.getCursor(builder, DatabaseManager.SELECT_PRIMARY_KEY, selectionArgs, null);
            while (cursor.moveToNext()) {
                ContentValues idValues = this.mDatabaseManager.buildValues(cursor);
                Long id = idValues.getAsLong(DatabaseManager.PRIMARY_KEY);
                result.add(id);
            }
            cursor.close();
        } catch (RuntimeException e) {
            AppCenterLog.error("AppCenter", "Failed to get corrupted ids: ", e);
        }
        return result;
    }
}
