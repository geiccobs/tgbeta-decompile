package com.google.android.datatransport.runtime.scheduling.persistence;

import android.database.sqlite.SQLiteDatabase;
import com.google.android.datatransport.runtime.scheduling.persistence.SchemaManager;
/* loaded from: classes.dex */
public final /* synthetic */ class SchemaManager$$ExternalSyntheticLambda2 implements SchemaManager.Migration {
    public static final /* synthetic */ SchemaManager$$ExternalSyntheticLambda2 INSTANCE = new SchemaManager$$ExternalSyntheticLambda2();

    private /* synthetic */ SchemaManager$$ExternalSyntheticLambda2() {
    }

    @Override // com.google.android.datatransport.runtime.scheduling.persistence.SchemaManager.Migration
    public final void upgrade(SQLiteDatabase sQLiteDatabase) {
        SchemaManager.lambda$static$3(sQLiteDatabase);
    }
}
