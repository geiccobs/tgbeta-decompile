package com.google.android.datatransport.runtime.scheduling.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
/* loaded from: classes3.dex */
public final class SchemaManager extends SQLiteOpenHelper {
    private static final String CREATE_CONTEXTS_SQL_V1 = "CREATE TABLE transport_contexts (_id INTEGER PRIMARY KEY, backend_name TEXT NOT NULL, priority INTEGER NOT NULL, next_request_ms INTEGER NOT NULL)";
    private static final String CREATE_CONTEXT_BACKEND_PRIORITY_INDEX_V1 = "CREATE UNIQUE INDEX contexts_backend_priority on transport_contexts(backend_name, priority)";
    private static final String CREATE_EVENTS_SQL_V1 = "CREATE TABLE events (_id INTEGER PRIMARY KEY, context_id INTEGER NOT NULL, transport_name TEXT NOT NULL, timestamp_ms INTEGER NOT NULL, uptime_ms INTEGER NOT NULL, payload BLOB NOT NULL, code INTEGER, num_attempts INTEGER NOT NULL,FOREIGN KEY (context_id) REFERENCES transport_contexts(_id) ON DELETE CASCADE)";
    private static final String CREATE_EVENT_BACKEND_INDEX_V1 = "CREATE INDEX events_backend_id on events(context_id)";
    private static final String CREATE_EVENT_METADATA_SQL_V1 = "CREATE TABLE event_metadata (_id INTEGER PRIMARY KEY, event_id INTEGER NOT NULL, name TEXT NOT NULL, value TEXT NOT NULL,FOREIGN KEY (event_id) REFERENCES events(_id) ON DELETE CASCADE)";
    private static final String CREATE_GLOBAL_LOG_EVENT_STATE_TABLE = "CREATE TABLE global_log_event_state (last_metrics_upload_ms BIGINT PRIMARY KEY)";
    private static final String CREATE_LOG_EVENT_DROPPED_TABLE = "CREATE TABLE log_event_dropped (log_source VARCHAR(45) NOT NULL,reason INTEGER NOT NULL,events_dropped_count BIGINT NOT NULL,PRIMARY KEY(log_source, reason))";
    private static final String CREATE_PAYLOADS_TABLE_V4 = "CREATE TABLE event_payloads (sequence_num INTEGER NOT NULL, event_id INTEGER NOT NULL, bytes BLOB NOT NULL,FOREIGN KEY (event_id) REFERENCES events(_id) ON DELETE CASCADE,PRIMARY KEY (sequence_num, event_id))";
    static final String DB_NAME = "com.google.android.datatransport.events";
    private static final String DROP_CONTEXTS_SQL = "DROP TABLE transport_contexts";
    private static final String DROP_EVENTS_SQL = "DROP TABLE events";
    private static final String DROP_EVENT_METADATA_SQL = "DROP TABLE event_metadata";
    private static final String DROP_GLOBAL_LOG_EVENT_STATE_SQL = "DROP TABLE IF EXISTS global_log_event_state";
    private static final String DROP_LOG_EVENT_DROPPED_SQL = "DROP TABLE IF EXISTS log_event_dropped";
    private static final String DROP_PAYLOADS_SQL = "DROP TABLE IF EXISTS event_payloads";
    private static final List<Migration> INCREMENTAL_MIGRATIONS;
    private static final Migration MIGRATE_TO_V1;
    private static final Migration MIGRATE_TO_V2;
    private static final Migration MIGRATE_TO_V3;
    private static final Migration MIGRATE_TO_V4;
    private static final Migration MIGRATION_TO_V5;
    private boolean configured = false;
    private final int schemaVersion;
    private static final String CREATE_INITIAL_GLOBAL_LOG_EVENT_STATE_VALUE_SQL = "INSERT INTO global_log_event_state VALUES (" + System.currentTimeMillis() + ")";
    static int SCHEMA_VERSION = 5;

    /* loaded from: classes3.dex */
    public interface Migration {
        void upgrade(SQLiteDatabase sQLiteDatabase);
    }

    static {
        SchemaManager$$ExternalSyntheticLambda0 schemaManager$$ExternalSyntheticLambda0 = SchemaManager$$ExternalSyntheticLambda0.INSTANCE;
        MIGRATE_TO_V1 = schemaManager$$ExternalSyntheticLambda0;
        SchemaManager$$ExternalSyntheticLambda1 schemaManager$$ExternalSyntheticLambda1 = SchemaManager$$ExternalSyntheticLambda1.INSTANCE;
        MIGRATE_TO_V2 = schemaManager$$ExternalSyntheticLambda1;
        SchemaManager$$ExternalSyntheticLambda2 schemaManager$$ExternalSyntheticLambda2 = SchemaManager$$ExternalSyntheticLambda2.INSTANCE;
        MIGRATE_TO_V3 = schemaManager$$ExternalSyntheticLambda2;
        SchemaManager$$ExternalSyntheticLambda3 schemaManager$$ExternalSyntheticLambda3 = SchemaManager$$ExternalSyntheticLambda3.INSTANCE;
        MIGRATE_TO_V4 = schemaManager$$ExternalSyntheticLambda3;
        SchemaManager$$ExternalSyntheticLambda4 schemaManager$$ExternalSyntheticLambda4 = SchemaManager$$ExternalSyntheticLambda4.INSTANCE;
        MIGRATION_TO_V5 = schemaManager$$ExternalSyntheticLambda4;
        INCREMENTAL_MIGRATIONS = Arrays.asList(schemaManager$$ExternalSyntheticLambda0, schemaManager$$ExternalSyntheticLambda1, schemaManager$$ExternalSyntheticLambda2, schemaManager$$ExternalSyntheticLambda3, schemaManager$$ExternalSyntheticLambda4);
    }

    public static /* synthetic */ void lambda$static$0(SQLiteDatabase db) {
        db.execSQL(CREATE_EVENTS_SQL_V1);
        db.execSQL(CREATE_EVENT_METADATA_SQL_V1);
        db.execSQL(CREATE_CONTEXTS_SQL_V1);
        db.execSQL(CREATE_EVENT_BACKEND_INDEX_V1);
        db.execSQL(CREATE_CONTEXT_BACKEND_PRIORITY_INDEX_V1);
    }

    public static /* synthetic */ void lambda$static$1(SQLiteDatabase db) {
        db.execSQL("ALTER TABLE transport_contexts ADD COLUMN extras BLOB");
        db.execSQL("CREATE UNIQUE INDEX contexts_backend_priority_extras on transport_contexts(backend_name, priority, extras)");
        db.execSQL("DROP INDEX contexts_backend_priority");
    }

    public static /* synthetic */ void lambda$static$3(SQLiteDatabase db) {
        db.execSQL("ALTER TABLE events ADD COLUMN inline BOOLEAN NOT NULL DEFAULT 1");
        db.execSQL(DROP_PAYLOADS_SQL);
        db.execSQL(CREATE_PAYLOADS_TABLE_V4);
    }

    public static /* synthetic */ void lambda$static$4(SQLiteDatabase db) {
        db.execSQL(CREATE_LOG_EVENT_DROPPED_TABLE);
        db.execSQL(CREATE_GLOBAL_LOG_EVENT_STATE_TABLE);
        db.execSQL(CREATE_INITIAL_GLOBAL_LOG_EVENT_STATE_VALUE_SQL);
    }

    @Inject
    public SchemaManager(Context context, @Named("SQLITE_DB_NAME") String dbName, @Named("SCHEMA_VERSION") int schemaVersion) {
        super(context, dbName, (SQLiteDatabase.CursorFactory) null, schemaVersion);
        this.schemaVersion = schemaVersion;
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onConfigure(SQLiteDatabase db) {
        this.configured = true;
        db.rawQuery("PRAGMA busy_timeout=0;", new String[0]).close();
        if (Build.VERSION.SDK_INT >= 16) {
            db.setForeignKeyConstraintsEnabled(true);
        }
    }

    private void ensureConfigured(SQLiteDatabase db) {
        if (!this.configured) {
            onConfigure(db);
        }
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onCreate(SQLiteDatabase db) {
        onCreate(db, this.schemaVersion);
    }

    private void onCreate(SQLiteDatabase db, int version) {
        ensureConfigured(db);
        upgrade(db, 0, version);
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        ensureConfigured(db);
        upgrade(db, oldVersion, newVersion);
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_EVENTS_SQL);
        db.execSQL(DROP_EVENT_METADATA_SQL);
        db.execSQL(DROP_CONTEXTS_SQL);
        db.execSQL(DROP_PAYLOADS_SQL);
        db.execSQL(DROP_LOG_EVENT_DROPPED_SQL);
        db.execSQL(DROP_GLOBAL_LOG_EVENT_STATE_SQL);
        onCreate(db, newVersion);
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onOpen(SQLiteDatabase db) {
        ensureConfigured(db);
    }

    private void upgrade(SQLiteDatabase db, int fromVersion, int toVersion) {
        List<Migration> list = INCREMENTAL_MIGRATIONS;
        if (toVersion > list.size()) {
            throw new IllegalArgumentException("Migration from " + fromVersion + " to " + toVersion + " was requested, but cannot be performed. Only " + list.size() + " migrations are provided");
        }
        for (int version = fromVersion; version < toVersion; version++) {
            INCREMENTAL_MIGRATIONS.get(version).upgrade(db);
        }
    }
}
