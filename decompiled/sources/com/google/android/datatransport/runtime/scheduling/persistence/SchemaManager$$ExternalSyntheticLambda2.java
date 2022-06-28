package com.google.android.datatransport.runtime.scheduling.persistence;

import android.database.sqlite.SQLiteDatabase;
import com.google.android.datatransport.runtime.scheduling.persistence.SchemaManager;
/* loaded from: classes3.dex */
public final /* synthetic */ class SchemaManager$$ExternalSyntheticLambda2 implements SchemaManager.Migration {
    public static final /* synthetic */ SchemaManager$$ExternalSyntheticLambda2 INSTANCE = new SchemaManager$$ExternalSyntheticLambda2();

    private /* synthetic */ SchemaManager$$ExternalSyntheticLambda2() {
    }

    @Override // com.google.android.datatransport.runtime.scheduling.persistence.SchemaManager.Migration
    public final void upgrade(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL("ALTER TABLE events ADD COLUMN payload_encoding TEXT");
    }
}
