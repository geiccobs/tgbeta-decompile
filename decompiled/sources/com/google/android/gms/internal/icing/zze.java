package com.google.android.gms.internal.icing;

import com.google.android.gms.common.api.Api;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes.dex */
public final class zze {
    public static final Api.ClientKey<zzae> zza;
    public static final Api<Api.ApiOptions.NoOptions> zzb;
    private static final Api.AbstractClientBuilder<zzae, Api.ApiOptions.NoOptions> zzd;

    static {
        Api.ClientKey<zzae> clientKey = new Api.ClientKey<>();
        zza = clientKey;
        zzd zzdVar = new zzd();
        zzd = zzdVar;
        zzb = new Api<>("AppDataSearch.LIGHTWEIGHT_API", zzdVar, clientKey);
        new zzal();
    }
}
