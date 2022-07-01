package com.google.android.datatransport.runtime.scheduling.persistence;

import android.database.Cursor;
import com.google.android.datatransport.runtime.scheduling.persistence.SQLiteEventStore;
/* loaded from: classes.dex */
public final /* synthetic */ class SQLiteEventStore$$ExternalSyntheticLambda18 implements SQLiteEventStore.Function {
    public static final /* synthetic */ SQLiteEventStore$$ExternalSyntheticLambda18 INSTANCE = new SQLiteEventStore$$ExternalSyntheticLambda18();

    private /* synthetic */ SQLiteEventStore$$ExternalSyntheticLambda18() {
    }

    @Override // com.google.android.datatransport.runtime.scheduling.persistence.SQLiteEventStore.Function
    public final Object apply(Object obj) {
        Long lambda$getTransportContextId$2;
        lambda$getTransportContextId$2 = SQLiteEventStore.lambda$getTransportContextId$2((Cursor) obj);
        return lambda$getTransportContextId$2;
    }
}
