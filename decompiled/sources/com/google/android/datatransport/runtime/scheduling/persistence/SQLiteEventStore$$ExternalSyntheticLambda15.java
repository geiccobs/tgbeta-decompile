package com.google.android.datatransport.runtime.scheduling.persistence;

import android.database.Cursor;
import com.google.android.datatransport.runtime.scheduling.persistence.SQLiteEventStore;
/* loaded from: classes.dex */
public final /* synthetic */ class SQLiteEventStore$$ExternalSyntheticLambda15 implements SQLiteEventStore.Function {
    public static final /* synthetic */ SQLiteEventStore$$ExternalSyntheticLambda15 INSTANCE = new SQLiteEventStore$$ExternalSyntheticLambda15();

    private /* synthetic */ SQLiteEventStore$$ExternalSyntheticLambda15() {
    }

    @Override // com.google.android.datatransport.runtime.scheduling.persistence.SQLiteEventStore.Function
    public final Object apply(Object obj) {
        byte[] lambda$readPayload$15;
        lambda$readPayload$15 = SQLiteEventStore.lambda$readPayload$15((Cursor) obj);
        return lambda$readPayload$15;
    }
}
