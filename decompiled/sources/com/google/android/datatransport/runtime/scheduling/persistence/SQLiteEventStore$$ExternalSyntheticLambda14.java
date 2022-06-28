package com.google.android.datatransport.runtime.scheduling.persistence;

import android.database.sqlite.SQLiteDatabase;
import com.google.android.datatransport.runtime.scheduling.persistence.SQLiteEventStore;
/* loaded from: classes3.dex */
public final /* synthetic */ class SQLiteEventStore$$ExternalSyntheticLambda14 implements SQLiteEventStore.Function {
    public static final /* synthetic */ SQLiteEventStore$$ExternalSyntheticLambda14 INSTANCE = new SQLiteEventStore$$ExternalSyntheticLambda14();

    private /* synthetic */ SQLiteEventStore$$ExternalSyntheticLambda14() {
    }

    @Override // com.google.android.datatransport.runtime.scheduling.persistence.SQLiteEventStore.Function
    public final Object apply(Object obj) {
        return SQLiteEventStore.lambda$clearDb$13((SQLiteDatabase) obj);
    }
}
