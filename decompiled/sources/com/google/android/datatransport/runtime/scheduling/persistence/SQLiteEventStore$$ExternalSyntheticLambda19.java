package com.google.android.datatransport.runtime.scheduling.persistence;

import android.database.Cursor;
import com.google.android.datatransport.runtime.scheduling.persistence.SQLiteEventStore;
/* loaded from: classes.dex */
public final /* synthetic */ class SQLiteEventStore$$ExternalSyntheticLambda19 implements SQLiteEventStore.Function {
    public static final /* synthetic */ SQLiteEventStore$$ExternalSyntheticLambda19 INSTANCE = new SQLiteEventStore$$ExternalSyntheticLambda19();

    private /* synthetic */ SQLiteEventStore$$ExternalSyntheticLambda19() {
    }

    @Override // com.google.android.datatransport.runtime.scheduling.persistence.SQLiteEventStore.Function
    public final Object apply(Object obj) {
        Boolean lambda$recordLogEventDropped$17;
        lambda$recordLogEventDropped$17 = SQLiteEventStore.lambda$recordLogEventDropped$17((Cursor) obj);
        return lambda$recordLogEventDropped$17;
    }
}
