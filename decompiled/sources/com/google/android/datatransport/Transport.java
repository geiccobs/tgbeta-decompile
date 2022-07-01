package com.google.android.datatransport;
/* loaded from: classes.dex */
public interface Transport<T> {
    void send(Event<T> event);
}
