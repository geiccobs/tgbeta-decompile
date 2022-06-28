package com.google.android.datatransport.runtime.scheduling.persistence;

import android.database.Cursor;
import com.google.android.datatransport.runtime.scheduling.persistence.SQLiteEventStore;
/* loaded from: classes3.dex */
public final /* synthetic */ class SQLiteEventStore$$ExternalSyntheticLambda12 implements SQLiteEventStore.Function {
    public static final /* synthetic */ SQLiteEventStore$$ExternalSyntheticLambda12 INSTANCE = new SQLiteEventStore$$ExternalSyntheticLambda12();

    private /* synthetic */ SQLiteEventStore$$ExternalSyntheticLambda12() {
    }

    @Override // com.google.android.datatransport.runtime.scheduling.persistence.SQLiteEventStore.Function
    public final Object apply(Object obj) {
        Boolean valueOf;
        Cursor cursor = (Cursor) obj;
        valueOf = Boolean.valueOf(cursor.getCount() > 0);
        return valueOf;
    }
}
