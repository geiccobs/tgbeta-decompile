package com.google.android.exoplayer2.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes3.dex */
public final class VersionTable {
    private static final String COLUMN_FEATURE = "feature";
    private static final String COLUMN_INSTANCE_UID = "instance_uid";
    private static final String COLUMN_VERSION = "version";
    public static final int FEATURE_CACHE_CONTENT_METADATA = 1;
    public static final int FEATURE_CACHE_FILE_METADATA = 2;
    public static final int FEATURE_OFFLINE = 0;
    private static final String PRIMARY_KEY = "PRIMARY KEY (feature, instance_uid)";
    private static final String SQL_CREATE_TABLE_IF_NOT_EXISTS = "CREATE TABLE IF NOT EXISTS ExoPlayerVersions (feature INTEGER NOT NULL,instance_uid TEXT NOT NULL,version INTEGER NOT NULL,PRIMARY KEY (feature, instance_uid))";
    private static final String TABLE_NAME = "ExoPlayerVersions";
    public static final int VERSION_UNSET = -1;
    private static final String WHERE_FEATURE_AND_INSTANCE_UID_EQUALS = "feature = ? AND instance_uid = ?";

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    private @interface Feature {
    }

    private VersionTable() {
    }

    public static void setVersion(SQLiteDatabase writableDatabase, int feature, String instanceUid, int version) throws DatabaseIOException {
        try {
            writableDatabase.execSQL(SQL_CREATE_TABLE_IF_NOT_EXISTS);
            ContentValues values = new ContentValues();
            values.put(COLUMN_FEATURE, Integer.valueOf(feature));
            values.put(COLUMN_INSTANCE_UID, instanceUid);
            values.put(COLUMN_VERSION, Integer.valueOf(version));
            writableDatabase.replaceOrThrow(TABLE_NAME, null, values);
        } catch (SQLException e) {
            throw new DatabaseIOException(e);
        }
    }

    public static void removeVersion(SQLiteDatabase writableDatabase, int feature, String instanceUid) throws DatabaseIOException {
        try {
            if (!tableExists(writableDatabase, TABLE_NAME)) {
                return;
            }
            writableDatabase.delete(TABLE_NAME, WHERE_FEATURE_AND_INSTANCE_UID_EQUALS, featureAndInstanceUidArguments(feature, instanceUid));
        } catch (SQLException e) {
            throw new DatabaseIOException(e);
        }
    }

    public static int getVersion(SQLiteDatabase database, int feature, String instanceUid) throws DatabaseIOException {
        try {
            if (!tableExists(database, TABLE_NAME)) {
                return -1;
            }
            Cursor cursor = database.query(TABLE_NAME, new String[]{COLUMN_VERSION}, WHERE_FEATURE_AND_INSTANCE_UID_EQUALS, featureAndInstanceUidArguments(feature, instanceUid), null, null, null);
            if (cursor.getCount() != 0) {
                cursor.moveToNext();
                int i = cursor.getInt(0);
                if (cursor != null) {
                    cursor.close();
                }
                return i;
            }
            if (cursor != null) {
                cursor.close();
            }
            return -1;
        } catch (SQLException e) {
            throw new DatabaseIOException(e);
        }
    }

    static boolean tableExists(SQLiteDatabase readableDatabase, String tableName) {
        long count = DatabaseUtils.queryNumEntries(readableDatabase, "sqlite_master", "tbl_name = ?", new String[]{tableName});
        return count > 0;
    }

    private static String[] featureAndInstanceUidArguments(int feature, String instance) {
        return new String[]{Integer.toString(feature), instance};
    }
}
