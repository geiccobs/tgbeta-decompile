package com.google.android.datatransport.runtime.scheduling.persistence;

import android.database.Cursor;
import com.google.android.datatransport.runtime.scheduling.persistence.SQLiteEventStore;
/* loaded from: classes.dex */
public final /* synthetic */ class SQLiteEventStore$$ExternalSyntheticLambda20 implements SQLiteEventStore.Function {
    public static final /* synthetic */ SQLiteEventStore$$ExternalSyntheticLambda20 INSTANCE = new SQLiteEventStore$$ExternalSyntheticLambda20();

    private /* synthetic */ SQLiteEventStore$$ExternalSyntheticLambda20() {
    }

    @Override // com.google.android.datatransport.runtime.scheduling.persistence.SQLiteEventStore.Function
    public final Object apply(Object obj) {
        return Boolean.valueOf(((Cursor) obj).moveToNext());
    }
}
