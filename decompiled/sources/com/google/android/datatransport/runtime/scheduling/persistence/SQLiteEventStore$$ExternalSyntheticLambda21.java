package com.google.android.datatransport.runtime.scheduling.persistence;

import android.database.sqlite.SQLiteDatabase;
import com.google.android.datatransport.runtime.scheduling.persistence.SQLiteEventStore;
import java.util.List;
/* loaded from: classes.dex */
public final /* synthetic */ class SQLiteEventStore$$ExternalSyntheticLambda21 implements SQLiteEventStore.Function {
    public static final /* synthetic */ SQLiteEventStore$$ExternalSyntheticLambda21 INSTANCE = new SQLiteEventStore$$ExternalSyntheticLambda21();

    private /* synthetic */ SQLiteEventStore$$ExternalSyntheticLambda21() {
    }

    @Override // com.google.android.datatransport.runtime.scheduling.persistence.SQLiteEventStore.Function
    public final Object apply(Object obj) {
        List lambda$loadActiveContexts$10;
        lambda$loadActiveContexts$10 = SQLiteEventStore.lambda$loadActiveContexts$10((SQLiteDatabase) obj);
        return lambda$loadActiveContexts$10;
    }
}
