package com.google.android.datatransport.runtime.scheduling.persistence;

import android.database.sqlite.SQLiteDatabase;
import com.google.android.datatransport.runtime.scheduling.persistence.SchemaManager;
/* loaded from: classes.dex */
public final /* synthetic */ class SchemaManager$$ExternalSyntheticLambda4 implements SchemaManager.Migration {
    public static final /* synthetic */ SchemaManager$$ExternalSyntheticLambda4 INSTANCE = new SchemaManager$$ExternalSyntheticLambda4();

    private /* synthetic */ SchemaManager$$ExternalSyntheticLambda4() {
    }

    @Override // com.google.android.datatransport.runtime.scheduling.persistence.SchemaManager.Migration
    public final void upgrade(SQLiteDatabase sQLiteDatabase) {
        SchemaManager.lambda$static$4(sQLiteDatabase);
    }
}
