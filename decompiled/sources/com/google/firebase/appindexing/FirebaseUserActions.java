package com.google.firebase.appindexing;

import android.content.Context;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.appindexing.internal.zzt;
import java.lang.ref.WeakReference;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes3.dex */
public abstract class FirebaseUserActions {
    public static final String APP_INDEXING_API_TAG = "FirebaseUserActions";
    private static WeakReference<FirebaseUserActions> zza;

    public static synchronized FirebaseUserActions getInstance(Context context) {
        FirebaseUserActions firebaseUserActions;
        synchronized (FirebaseUserActions.class) {
            Preconditions.checkNotNull(context);
            WeakReference<FirebaseUserActions> weakReference = zza;
            firebaseUserActions = weakReference == null ? null : weakReference.get();
            if (firebaseUserActions == null) {
                firebaseUserActions = new zzt(context.getApplicationContext());
                zza = new WeakReference<>(firebaseUserActions);
            }
        }
        return firebaseUserActions;
    }

    public abstract Task<Void> end(Action action);

    public abstract Task<Void> start(Action action);
}
