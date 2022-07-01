package com.google.android.datatransport.runtime.scheduling.persistence;

import android.database.sqlite.SQLiteDatabase;
import com.google.android.datatransport.runtime.scheduling.persistence.SchemaManager;
/* loaded from: classes.dex */
public final /* synthetic */ class SchemaManager$$ExternalSyntheticLambda1 implements SchemaManager.Migration {
    public static final /* synthetic */ SchemaManager$$ExternalSyntheticLambda1 INSTANCE = new SchemaManager$$ExternalSyntheticLambda1();

    private /* synthetic */ SchemaManager$$ExternalSyntheticLambda1() {
    }

    @Override // com.google.android.datatransport.runtime.scheduling.persistence.SchemaManager.Migration
    public final void upgrade(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL("ALTER TABLE events ADD COLUMN payload_encoding TEXT");
    }
}
