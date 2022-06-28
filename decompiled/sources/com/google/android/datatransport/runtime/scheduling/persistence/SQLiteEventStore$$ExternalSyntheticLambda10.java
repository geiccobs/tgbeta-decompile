package com.google.android.datatransport.runtime.scheduling.persistence;

import android.database.Cursor;
import com.google.android.datatransport.runtime.scheduling.persistence.SQLiteEventStore;
/* loaded from: classes3.dex */
public final /* synthetic */ class SQLiteEventStore$$ExternalSyntheticLambda10 implements SQLiteEventStore.Function {
    public static final /* synthetic */ SQLiteEventStore$$ExternalSyntheticLambda10 INSTANCE = new SQLiteEventStore$$ExternalSyntheticLambda10();

    private /* synthetic */ SQLiteEventStore$$ExternalSyntheticLambda10() {
    }

    @Override // com.google.android.datatransport.runtime.scheduling.persistence.SQLiteEventStore.Function
    public final Object apply(Object obj) {
        return SQLiteEventStore.lambda$readPayload$15((Cursor) obj);
    }
}
