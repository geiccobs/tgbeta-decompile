package com.google.android.datatransport.runtime.scheduling.persistence;

import android.database.Cursor;
import com.google.android.datatransport.runtime.scheduling.persistence.SQLiteEventStore;
/* loaded from: classes.dex */
public final /* synthetic */ class SQLiteEventStore$$ExternalSyntheticLambda17 implements SQLiteEventStore.Function {
    public static final /* synthetic */ SQLiteEventStore$$ExternalSyntheticLambda17 INSTANCE = new SQLiteEventStore$$ExternalSyntheticLambda17();

    private /* synthetic */ SQLiteEventStore$$ExternalSyntheticLambda17() {
    }

    @Override // com.google.android.datatransport.runtime.scheduling.persistence.SQLiteEventStore.Function
    public final Object apply(Object obj) {
        Long lambda$getNextCallTime$5;
        lambda$getNextCallTime$5 = SQLiteEventStore.lambda$getNextCallTime$5((Cursor) obj);
        return lambda$getNextCallTime$5;
    }
}
