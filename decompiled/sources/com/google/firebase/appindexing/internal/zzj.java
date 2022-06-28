package com.google.firebase.appindexing.internal;

import android.content.Context;
import android.os.Looper;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.internal.icing.zzbi;
import com.google.firebase.FirebaseExceptionMapper;
/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes3.dex */
public final class zzj extends GoogleApi<Api.ApiOptions.NoOptions> {
    public zzj(Context context) {
        super(context, zzf.zze, Api.ApiOptions.NO_OPTIONS, Looper.getMainLooper(), new FirebaseExceptionMapper());
        zzbi.zza(context);
    }
}
