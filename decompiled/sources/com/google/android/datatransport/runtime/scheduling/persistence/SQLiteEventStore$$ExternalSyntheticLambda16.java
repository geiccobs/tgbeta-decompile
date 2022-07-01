package com.google.android.datatransport.runtime.scheduling.persistence;

import android.database.Cursor;
import com.google.android.datatransport.runtime.scheduling.persistence.SQLiteEventStore;
import java.util.List;
/* loaded from: classes.dex */
public final /* synthetic */ class SQLiteEventStore$$ExternalSyntheticLambda16 implements SQLiteEventStore.Function {
    public static final /* synthetic */ SQLiteEventStore$$ExternalSyntheticLambda16 INSTANCE = new SQLiteEventStore$$ExternalSyntheticLambda16();

    private /* synthetic */ SQLiteEventStore$$ExternalSyntheticLambda16() {
    }

    @Override // com.google.android.datatransport.runtime.scheduling.persistence.SQLiteEventStore.Function
    public final Object apply(Object obj) {
        List lambda$loadActiveContexts$9;
        lambda$loadActiveContexts$9 = SQLiteEventStore.lambda$loadActiveContexts$9((Cursor) obj);
        return lambda$loadActiveContexts$9;
    }
}
