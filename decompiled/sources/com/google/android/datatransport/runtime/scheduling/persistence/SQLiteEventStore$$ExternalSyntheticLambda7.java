package com.google.android.datatransport.runtime.scheduling.persistence;

import android.database.Cursor;
import com.google.android.datatransport.runtime.scheduling.persistence.SQLiteEventStore;
/* loaded from: classes3.dex */
public final /* synthetic */ class SQLiteEventStore$$ExternalSyntheticLambda7 implements SQLiteEventStore.Function {
    public static final /* synthetic */ SQLiteEventStore$$ExternalSyntheticLambda7 INSTANCE = new SQLiteEventStore$$ExternalSyntheticLambda7();

    private /* synthetic */ SQLiteEventStore$$ExternalSyntheticLambda7() {
    }

    @Override // com.google.android.datatransport.runtime.scheduling.persistence.SQLiteEventStore.Function
    public final Object apply(Object obj) {
        return SQLiteEventStore.lambda$getNextCallTime$5((Cursor) obj);
    }
}
