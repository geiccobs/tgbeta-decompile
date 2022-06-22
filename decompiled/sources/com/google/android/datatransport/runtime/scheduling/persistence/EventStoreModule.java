package com.google.android.datatransport.runtime.scheduling.persistence;

import android.content.Context;
/* loaded from: classes.dex */
public abstract class EventStoreModule {
    public static String dbName() {
        return "com.google.android.datatransport.events";
    }

    public static EventStoreConfig storeConfig() {
        return EventStoreConfig.DEFAULT;
    }

    public static int schemaVersion() {
        return SchemaManager.SCHEMA_VERSION;
    }

    public static String packageName(Context context) {
        return context.getPackageName();
    }
}
