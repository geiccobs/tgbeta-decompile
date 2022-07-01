package com.google.firebase.appindexing.internal;

import android.content.Context;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.internal.icing.zze;
import com.google.firebase.FirebaseExceptionMapper;
/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes.dex */
public final class zzr extends GoogleApi<Api.ApiOptions.NoOptions> {
    public zzr(Context context) {
        super(context, zze.zzb, Api.ApiOptions.NO_OPTIONS, new FirebaseExceptionMapper());
    }
}
