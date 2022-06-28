package org.telegram.SQLite;

import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;
/* loaded from: classes3.dex */
public class SQLiteDatabase {
    private boolean inTransaction;
    private boolean isOpen = true;
    private final long sqliteHandle;

    native void beginTransaction(long j);

    native void closedb(long j) throws SQLiteException;

    native void commitTransaction(long j);

    native long opendb(String str, String str2) throws SQLiteException;

    public long getSQLiteHandle() {
        return this.sqliteHandle;
    }

    public SQLiteDatabase(String fileName) throws SQLiteException {
        this.sqliteHandle = opendb(fileName, ApplicationLoader.getFilesDirFixed().getPath());
    }

    public boolean tableExists(String tableName) throws SQLiteException {
        checkOpened();
        return executeInt("SELECT rowid FROM sqlite_master WHERE type='table' AND name=?;", tableName) != null;
    }

    public SQLitePreparedStatement executeFast(String sql) throws SQLiteException {
        return new SQLitePreparedStatement(this, sql);
    }

    public Integer executeInt(String sql, Object... args) throws SQLiteException {
        checkOpened();
        SQLiteCursor cursor = queryFinalized(sql, args);
        try {
            if (cursor.next()) {
                return Integer.valueOf(cursor.intValue(0));
            }
            return null;
        } finally {
            cursor.dispose();
        }
    }

    public void explainQuery(String sql, Object... args) throws SQLiteException {
        checkOpened();
        SQLiteCursor cursor = new SQLitePreparedStatement(this, "EXPLAIN QUERY PLAN " + sql).query(args);
        while (cursor.next()) {
            int count = cursor.getColumnCount();
            StringBuilder builder = new StringBuilder();
            for (int a = 0; a < count; a++) {
                builder.append(cursor.stringValue(a));
                builder.append(", ");
            }
            FileLog.d("EXPLAIN QUERY PLAN " + builder.toString());
        }
        cursor.dispose();
    }

    public SQLiteCursor queryFinalized(String sql, Object... args) throws SQLiteException {
        checkOpened();
        return new SQLitePreparedStatement(this, sql).query(args);
    }

    public void close() {
        if (this.isOpen) {
            try {
                commitTransaction();
                closedb(this.sqliteHandle);
            } catch (SQLiteException e) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e(e.getMessage(), e);
                }
            }
            this.isOpen = false;
        }
    }

    void checkOpened() throws SQLiteException {
        if (!this.isOpen) {
            throw new SQLiteException("Database closed");
        }
    }

    public void finalize() throws Throwable {
        super.finalize();
        close();
    }

    public void beginTransaction() throws SQLiteException {
        if (this.inTransaction) {
            throw new SQLiteException("database already in transaction");
        }
        this.inTransaction = true;
        beginTransaction(this.sqliteHandle);
    }

    public void commitTransaction() {
        if (!this.inTransaction) {
            return;
        }
        this.inTransaction = false;
        commitTransaction(this.sqliteHandle);
    }
}
