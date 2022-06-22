package com.google.android.gms.auth.api;

import com.google.android.gms.auth.api.proxy.ProxyApi;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.internal.auth.zzak;
import com.google.android.gms.internal.auth.zzar;
/* loaded from: classes.dex */
public final class AuthProxy {
    public static final Api<AuthProxyOptions> API;
    public static final ProxyApi ProxyApi = new zzar();
    private static final Api.ClientKey<zzak> zzah;
    private static final Api.AbstractClientBuilder<zzak, AuthProxyOptions> zzai;

    static {
        Api.ClientKey<zzak> clientKey = new Api.ClientKey<>();
        zzah = clientKey;
        zza zzaVar = new zza();
        zzai = zzaVar;
        API = new Api<>("Auth.PROXY_API", zzaVar, clientKey);
    }
}
