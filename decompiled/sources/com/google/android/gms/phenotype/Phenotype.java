package com.google.android.gms.phenotype;

import android.net.Uri;
import com.google.android.gms.common.api.Api;
/* loaded from: classes3.dex */
public final class Phenotype {
    @Deprecated
    private static final Api<Api.ApiOptions.NoOptions> API;
    private static final Api.AbstractClientBuilder<com.google.android.gms.internal.phenotype.zze, Api.ApiOptions.NoOptions> CLIENT_BUILDER;
    private static final Api.ClientKey<com.google.android.gms.internal.phenotype.zze> CLIENT_KEY;
    @Deprecated
    private static final zzm zzaj = new com.google.android.gms.internal.phenotype.zzd();

    /* JADX WARN: Type inference failed for: r0v1, types: [com.google.android.gms.phenotype.zzm, com.google.android.gms.internal.phenotype.zzd] */
    static {
        Api.ClientKey<com.google.android.gms.internal.phenotype.zze> clientKey = new Api.ClientKey<>();
        CLIENT_KEY = clientKey;
        zzl zzlVar = new zzl();
        CLIENT_BUILDER = zzlVar;
        API = new Api<>("Phenotype.API", zzlVar, clientKey);
    }

    private Phenotype() {
    }

    public static Uri getContentProviderUri(String str) {
        String valueOf = String.valueOf(Uri.encode(str));
        return Uri.parse(valueOf.length() != 0 ? "content://com.google.android.gms.phenotype/".concat(valueOf) : new String("content://com.google.android.gms.phenotype/"));
    }
}
