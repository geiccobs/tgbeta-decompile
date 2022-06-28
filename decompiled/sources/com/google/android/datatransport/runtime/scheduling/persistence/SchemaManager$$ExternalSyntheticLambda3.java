package com.google.android.datatransport.runtime.scheduling.persistence;

import android.database.sqlite.SQLiteDatabase;
import com.google.android.datatransport.runtime.scheduling.persistence.SchemaManager;
/* loaded from: classes3.dex */
public final /* synthetic */ class SchemaManager$$ExternalSyntheticLambda3 implements SchemaManager.Migration {
    public static final /* synthetic */ SchemaManager$$ExternalSyntheticLambda3 INSTANCE = new SchemaManager$$ExternalSyntheticLambda3();

    private /* synthetic */ SchemaManager$$ExternalSyntheticLambda3() {
    }

    @Override // com.google.android.datatransport.runtime.scheduling.persistence.SchemaManager.Migration
    public final void upgrade(SQLiteDatabase sQLiteDatabase) {
        SchemaManager.lambda$static$3(sQLiteDatabase);
    }
}
