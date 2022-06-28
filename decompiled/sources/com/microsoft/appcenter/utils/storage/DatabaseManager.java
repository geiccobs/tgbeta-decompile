package com.microsoft.appcenter.utils.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteFullException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import com.microsoft.appcenter.utils.AppCenterLog;
import java.io.Closeable;
import java.util.Arrays;
/* loaded from: classes3.dex */
public class DatabaseManager implements Closeable {
    public static final String PRIMARY_KEY = "oid";
    public static final String[] SELECT_PRIMARY_KEY = {PRIMARY_KEY};
    private final Context mContext;
    private final String mDatabase;
    private final String mDefaultTable;
    private final Listener mListener;
    private SQLiteOpenHelper mSQLiteOpenHelper;
    private final ContentValues mSchema;

    /* loaded from: classes3.dex */
    public interface Listener {
        void onCreate(SQLiteDatabase sQLiteDatabase);

        void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2);
    }

    public DatabaseManager(Context context, String database, String defaultTable, int version, ContentValues schema, final String sqlCreateCommand, Listener listener) {
        this.mContext = context;
        this.mDatabase = database;
        this.mDefaultTable = defaultTable;
        this.mSchema = schema;
        this.mListener = listener;
        this.mSQLiteOpenHelper = new SQLiteOpenHelper(context, database, null, version) { // from class: com.microsoft.appcenter.utils.storage.DatabaseManager.1
            @Override // android.database.sqlite.SQLiteOpenHelper
            public void onCreate(SQLiteDatabase db) {
                db.execSQL(sqlCreateCommand);
                DatabaseManager.this.mListener.onCreate(db);
            }

            @Override // android.database.sqlite.SQLiteOpenHelper
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                DatabaseManager.this.mListener.onUpgrade(db, oldVersion, newVersion);
            }
        };
    }

    private static ContentValues buildValues(Cursor cursor, ContentValues schema) {
        ContentValues values = new ContentValues();
        for (int i = 0; i < cursor.getColumnCount(); i++) {
            if (!cursor.isNull(i)) {
                String key = cursor.getColumnName(i);
                if (key.equals(PRIMARY_KEY)) {
                    values.put(key, Long.valueOf(cursor.getLong(i)));
                } else {
                    Object specimen = schema.get(key);
                    if (specimen instanceof byte[]) {
                        values.put(key, cursor.getBlob(i));
                    } else if (specimen instanceof Double) {
                        values.put(key, Double.valueOf(cursor.getDouble(i)));
                    } else if (specimen instanceof Float) {
                        values.put(key, Float.valueOf(cursor.getFloat(i)));
                    } else if (specimen instanceof Integer) {
                        values.put(key, Integer.valueOf(cursor.getInt(i)));
                    } else if (specimen instanceof Long) {
                        values.put(key, Long.valueOf(cursor.getLong(i)));
                    } else if (specimen instanceof Short) {
                        values.put(key, Short.valueOf(cursor.getShort(i)));
                    } else if (specimen instanceof Boolean) {
                        boolean z = true;
                        if (cursor.getInt(i) != 1) {
                            z = false;
                        }
                        values.put(key, Boolean.valueOf(z));
                    } else {
                        values.put(key, cursor.getString(i));
                    }
                }
            }
        }
        return values;
    }

    public ContentValues buildValues(Cursor cursor) {
        return buildValues(cursor, this.mSchema);
    }

    public ContentValues nextValues(Cursor cursor) {
        try {
            if (cursor.moveToNext()) {
                return buildValues(cursor);
            }
            return null;
        } catch (RuntimeException e) {
            AppCenterLog.error("AppCenter", "Failed to get next cursor value: ", e);
            return null;
        }
    }

    public long put(ContentValues values, String priorityColumn) {
        Long id = null;
        Cursor cursor = null;
        while (id == null) {
            try {
                try {
                    id = Long.valueOf(getDatabase().insertOrThrow(this.mDefaultTable, null, values));
                } catch (SQLiteFullException e) {
                    AppCenterLog.debug("AppCenter", "Storage is full, trying to delete the oldest log that has the lowest priority which is lower or equal priority than the new log");
                    if (cursor == null) {
                        String priority = values.getAsString(priorityColumn);
                        SQLiteQueryBuilder queryBuilder = SQLiteUtils.newSQLiteQueryBuilder();
                        queryBuilder.appendWhere(priorityColumn + " <= ?");
                        cursor = getCursor(queryBuilder, SELECT_PRIMARY_KEY, new String[]{priority}, priorityColumn + " , " + PRIMARY_KEY);
                    }
                    if (!cursor.moveToNext()) {
                        throw e;
                    }
                    long deletedId = cursor.getLong(0);
                    delete(deletedId);
                    AppCenterLog.debug("AppCenter", "Deleted log id=" + deletedId);
                }
            } catch (RuntimeException e2) {
                id = -1L;
                AppCenterLog.error("AppCenter", String.format("Failed to insert values (%s) to database %s.", values.toString(), this.mDatabase), e2);
            }
        }
        if (cursor != null) {
            try {
                cursor.close();
            } catch (RuntimeException e3) {
            }
        }
        return id.longValue();
    }

    public void delete(long id) {
        delete(this.mDefaultTable, PRIMARY_KEY, Long.valueOf(id));
    }

    public int delete(String key, Object value) {
        return delete(this.mDefaultTable, key, value);
    }

    private int delete(String table, String key, Object value) {
        String[] whereArgs = {String.valueOf(value)};
        try {
            SQLiteDatabase database = getDatabase();
            return database.delete(table, key + " = ?", whereArgs);
        } catch (RuntimeException e) {
            AppCenterLog.error("AppCenter", String.format("Failed to delete values that match condition=\"%s\" and values=\"%s\" from database %s.", key + " = ?", Arrays.toString(whereArgs), this.mDatabase), e);
            return 0;
        }
    }

    public void clear() {
        try {
            getDatabase().delete(this.mDefaultTable, null, null);
        } catch (RuntimeException e) {
            AppCenterLog.error("AppCenter", "Failed to clear the table.", e);
        }
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        try {
            this.mSQLiteOpenHelper.close();
        } catch (RuntimeException e) {
            AppCenterLog.error("AppCenter", "Failed to close the database.", e);
        }
    }

    public final long getRowCount() {
        try {
            return DatabaseUtils.queryNumEntries(getDatabase(), this.mDefaultTable);
        } catch (RuntimeException e) {
            AppCenterLog.error("AppCenter", "Failed to get row count of database.", e);
            return -1L;
        }
    }

    public Cursor getCursor(SQLiteQueryBuilder queryBuilder, String[] columns, String[] selectionArgs, String sortOrder) throws RuntimeException {
        return getCursor(this.mDefaultTable, queryBuilder, columns, selectionArgs, sortOrder);
    }

    Cursor getCursor(String table, SQLiteQueryBuilder queryBuilder, String[] columns, String[] selectionArgs, String sortOrder) throws RuntimeException {
        if (queryBuilder == null) {
            queryBuilder = SQLiteUtils.newSQLiteQueryBuilder();
        }
        queryBuilder.setTables(table);
        return queryBuilder.query(getDatabase(), columns, null, selectionArgs, null, null, sortOrder);
    }

    SQLiteDatabase getDatabase() {
        try {
            return this.mSQLiteOpenHelper.getWritableDatabase();
        } catch (RuntimeException e) {
            AppCenterLog.warn("AppCenter", "Failed to open database. Trying to delete database (may be corrupted).", e);
            if (this.mContext.deleteDatabase(this.mDatabase)) {
                AppCenterLog.info("AppCenter", "The database was successfully deleted.");
            } else {
                AppCenterLog.warn("AppCenter", "Failed to delete database.");
            }
            return this.mSQLiteOpenHelper.getWritableDatabase();
        }
    }

    void setSQLiteOpenHelper(SQLiteOpenHelper helper) {
        this.mSQLiteOpenHelper.close();
        this.mSQLiteOpenHelper = helper;
    }

    public boolean setMaxSize(long maxStorageSizeInBytes) {
        try {
            SQLiteDatabase db = getDatabase();
            long newMaxSize = db.setMaximumSize(maxStorageSizeInBytes);
            long pageSize = db.getPageSize();
            long expectedMultipleMaxSize = maxStorageSizeInBytes / pageSize;
            if (maxStorageSizeInBytes % pageSize != 0) {
                expectedMultipleMaxSize++;
            }
            if (newMaxSize == expectedMultipleMaxSize * pageSize) {
                if (maxStorageSizeInBytes == newMaxSize) {
                    AppCenterLog.info("AppCenter", "Changed maximum database size to " + newMaxSize + " bytes.");
                    return true;
                }
                AppCenterLog.info("AppCenter", "Changed maximum database size to " + newMaxSize + " bytes (next multiple of page size).");
                return true;
            }
            AppCenterLog.error("AppCenter", "Could not change maximum database size to " + maxStorageSizeInBytes + " bytes, current maximum size is " + newMaxSize + " bytes.");
            return false;
        } catch (RuntimeException e) {
            AppCenterLog.error("AppCenter", "Could not change maximum database size.", e);
            return false;
        }
    }

    public long getMaxSize() {
        try {
            return getDatabase().getMaximumSize();
        } catch (RuntimeException e) {
            AppCenterLog.error("AppCenter", "Could not get maximum database size.", e);
            return -1L;
        }
    }
}
