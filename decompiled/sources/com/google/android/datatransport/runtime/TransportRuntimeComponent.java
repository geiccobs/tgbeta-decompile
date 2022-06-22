package com.google.android.datatransport.runtime;

import android.content.Context;
import com.google.android.datatransport.runtime.scheduling.persistence.EventStore;
import java.io.Closeable;
import java.io.IOException;
/* loaded from: classes.dex */
public abstract class TransportRuntimeComponent implements Closeable {

    /* loaded from: classes.dex */
    public interface Builder {
        TransportRuntimeComponent build();

        Builder setApplicationContext(Context context);
    }

    abstract EventStore getEventStore();

    public abstract TransportRuntime getTransportRuntime();

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        getEventStore().close();
    }
}
