package com.google.firebase.iid.internal;

import com.google.android.gms.tasks.Task;
/* compiled from: com.google.firebase:firebase-iid-interop@@17.1.0 */
/* loaded from: classes.dex */
public interface FirebaseInstanceIdInternal {

    /* compiled from: com.google.firebase:firebase-iid-interop@@17.1.0 */
    /* loaded from: classes.dex */
    public interface NewTokenListener {
    }

    void addNewTokenListener(NewTokenListener newTokenListener);

    String getToken();

    Task<String> getTokenTask();
}
