package com.google.android.exoplayer2.database;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.google.android.exoplayer2.util.Log;
import com.microsoft.appcenter.ingestion.models.CommonProperties;
/* loaded from: classes3.dex */
public final class ExoDatabaseProvider extends SQLiteOpenHelper implements DatabaseProvider {
    public static final String DATABASE_NAME = "exoplayer_internal.db";
    private static final String TAG = "ExoDatabaseProvider";
    private static final int VERSION = 1;

    public ExoDatabaseProvider(Context context) {
        super(context.getApplicationContext(), DATABASE_NAME, (SQLiteDatabase.CursorFactory) null, 1);
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onCreate(SQLiteDatabase db) {
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        wipeDatabase(db);
    }

    private static void wipeDatabase(SQLiteDatabase db) {
        String[] columns = {CommonProperties.TYPE, CommonProperties.NAME};
        Cursor cursor = db.query("sqlite_master", columns, null, null, null, null, null);
        while (cursor.moveToNext()) {
            try {
                String type = cursor.getString(0);
                String name = cursor.getString(1);
                if (!"sqlite_sequence".equals(name)) {
                    String sql = "DROP " + type + " IF EXISTS " + name;
                    try {
                        db.execSQL(sql);
                    } catch (SQLException e) {
                        Log.e(TAG, "Error executing " + sql, e);
                    }
                }
            } catch (Throwable th) {
                if (cursor != null) {
                    try {
                        cursor.close();
                    } catch (Throwable th2) {
                    }
                }
                throw th;
            }
        }
        if (cursor != null) {
            cursor.close();
        }
    }
}
