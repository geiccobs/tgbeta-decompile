package com.google.android.gms.common.api;

import android.util.Log;
import androidx.annotation.RecentlyNonNull;
import com.google.android.gms.common.api.Result;
/* compiled from: com.google.android.gms:play-services-basement@@17.5.0 */
/* loaded from: classes.dex */
public abstract class ResultCallbacks<R extends Result> implements ResultCallback<R> {
    public abstract void onFailure(@RecentlyNonNull Status status);

    public abstract void onSuccess(@RecentlyNonNull R r);

    @Override // com.google.android.gms.common.api.ResultCallback
    public final void onResult(@RecentlyNonNull R r) {
        Status status = r.getStatus();
        if (status.isSuccess()) {
            onSuccess(r);
            return;
        }
        onFailure(status);
        if (!(r instanceof Releasable)) {
            return;
        }
        try {
            ((Releasable) r).release();
        } catch (RuntimeException e) {
            String valueOf = String.valueOf(r);
            StringBuilder sb = new StringBuilder(valueOf.length() + 18);
            sb.append("Unable to release ");
            sb.append(valueOf);
            Log.w("ResultCallbacks", sb.toString(), e);
        }
    }
}
