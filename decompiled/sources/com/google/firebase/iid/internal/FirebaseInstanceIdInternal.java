package com.google.firebase.iid.internal;

import com.google.android.gms.tasks.Task;
import java.io.IOException;
/* compiled from: com.google.firebase:firebase-iid-interop@@17.1.0 */
/* loaded from: classes3.dex */
public interface FirebaseInstanceIdInternal {

    /* compiled from: com.google.firebase:firebase-iid-interop@@17.1.0 */
    /* loaded from: classes3.dex */
    public interface NewTokenListener {
        void onNewToken(String str);
    }

    void addNewTokenListener(NewTokenListener newTokenListener);

    void deleteToken(String str, String str2) throws IOException;

    String getId();

    String getToken();

    Task<String> getTokenTask();
}
