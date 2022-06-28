package com.google.android.exoplayer2.upstream.cache;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import com.google.android.exoplayer2.database.DatabaseIOException;
import com.google.android.exoplayer2.database.DatabaseProvider;
import com.google.android.exoplayer2.database.VersionTable;
import com.google.android.exoplayer2.util.Assertions;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
/* loaded from: classes3.dex */
final class CacheFileMetadataIndex {
    private static final int COLUMN_INDEX_LAST_TOUCH_TIMESTAMP = 2;
    private static final int COLUMN_INDEX_LENGTH = 1;
    private static final int COLUMN_INDEX_NAME = 0;
    private static final String COLUMN_NAME = "name";
    private static final String TABLE_PREFIX = "ExoPlayerCacheFileMetadata";
    private static final String TABLE_SCHEMA = "(name TEXT PRIMARY KEY NOT NULL,length INTEGER NOT NULL,last_touch_timestamp INTEGER NOT NULL)";
    private static final int TABLE_VERSION = 1;
    private static final String WHERE_NAME_EQUALS = "name = ?";
    private final DatabaseProvider databaseProvider;
    private String tableName;
    private static final String COLUMN_LENGTH = "length";
    private static final String COLUMN_LAST_TOUCH_TIMESTAMP = "last_touch_timestamp";
    private static final String[] COLUMNS = {"name", COLUMN_LENGTH, COLUMN_LAST_TOUCH_TIMESTAMP};

    public static void delete(DatabaseProvider databaseProvider, long uid) throws DatabaseIOException {
        String hexUid = Long.toHexString(uid);
        try {
            String tableName = getTableName(hexUid);
            SQLiteDatabase writableDatabase = databaseProvider.getWritableDatabase();
            writableDatabase.beginTransactionNonExclusive();
            VersionTable.removeVersion(writableDatabase, 2, hexUid);
            dropTable(writableDatabase, tableName);
            writableDatabase.setTransactionSuccessful();
            writableDatabase.endTransaction();
        } catch (SQLException e) {
            throw new DatabaseIOException(e);
        }
    }

    public CacheFileMetadataIndex(DatabaseProvider databaseProvider) {
        this.databaseProvider = databaseProvider;
    }

    public void initialize(long uid) throws DatabaseIOException {
        try {
            String hexUid = Long.toHexString(uid);
            this.tableName = getTableName(hexUid);
            SQLiteDatabase readableDatabase = this.databaseProvider.getReadableDatabase();
            int version = VersionTable.getVersion(readableDatabase, 2, hexUid);
            if (version != 1) {
                SQLiteDatabase writableDatabase = this.databaseProvider.getWritableDatabase();
                writableDatabase.beginTransactionNonExclusive();
                VersionTable.setVersion(writableDatabase, 2, hexUid, 1);
                dropTable(writableDatabase, this.tableName);
                writableDatabase.execSQL("CREATE TABLE " + this.tableName + " " + TABLE_SCHEMA);
                writableDatabase.setTransactionSuccessful();
                writableDatabase.endTransaction();
            }
        } catch (SQLException e) {
            throw new DatabaseIOException(e);
        }
    }

    public Map<String, CacheFileMetadata> getAll() throws DatabaseIOException {
        try {
            Cursor cursor = getCursor();
            Map<String, CacheFileMetadata> fileMetadata = new HashMap<>(cursor.getCount());
            while (cursor.moveToNext()) {
                String name = cursor.getString(0);
                long length = cursor.getLong(1);
                long lastTouchTimestamp = cursor.getLong(2);
                fileMetadata.put(name, new CacheFileMetadata(length, lastTouchTimestamp));
            }
            if (cursor != null) {
                cursor.close();
            }
            return fileMetadata;
        } catch (SQLException e) {
            throw new DatabaseIOException(e);
        }
    }

    public void set(String name, long length, long lastTouchTimestamp) throws DatabaseIOException {
        Assertions.checkNotNull(this.tableName);
        try {
            SQLiteDatabase writableDatabase = this.databaseProvider.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("name", name);
            values.put(COLUMN_LENGTH, Long.valueOf(length));
            values.put(COLUMN_LAST_TOUCH_TIMESTAMP, Long.valueOf(lastTouchTimestamp));
            writableDatabase.replaceOrThrow(this.tableName, null, values);
        } catch (SQLException e) {
            throw new DatabaseIOException(e);
        }
    }

    public void remove(String name) throws DatabaseIOException {
        Assertions.checkNotNull(this.tableName);
        try {
            SQLiteDatabase writableDatabase = this.databaseProvider.getWritableDatabase();
            writableDatabase.delete(this.tableName, WHERE_NAME_EQUALS, new String[]{name});
        } catch (SQLException e) {
            throw new DatabaseIOException(e);
        }
    }

    public void removeAll(Set<String> names) throws DatabaseIOException {
        Assertions.checkNotNull(this.tableName);
        try {
            SQLiteDatabase writableDatabase = this.databaseProvider.getWritableDatabase();
            writableDatabase.beginTransactionNonExclusive();
            for (String name : names) {
                writableDatabase.delete(this.tableName, WHERE_NAME_EQUALS, new String[]{name});
            }
            writableDatabase.setTransactionSuccessful();
            writableDatabase.endTransaction();
        } catch (SQLException e) {
            throw new DatabaseIOException(e);
        }
    }

    private Cursor getCursor() {
        Assertions.checkNotNull(this.tableName);
        return this.databaseProvider.getReadableDatabase().query(this.tableName, COLUMNS, null, null, null, null, null);
    }

    private static void dropTable(SQLiteDatabase writableDatabase, String tableName) {
        writableDatabase.execSQL("DROP TABLE IF EXISTS " + tableName);
    }

    private static String getTableName(String hexUid) {
        return TABLE_PREFIX + hexUid;
    }
}
