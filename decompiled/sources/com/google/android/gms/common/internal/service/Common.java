package com.google.android.gms.common.internal.service;

import com.google.android.gms.common.api.Api;
/* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
/* loaded from: classes3.dex */
public final class Common {
    public static final Api<Api.ApiOptions.NoOptions> API;
    public static final Api.ClientKey<zah> CLIENT_KEY;
    public static final zab zaa = new zae();
    private static final Api.AbstractClientBuilder<zah, Api.ApiOptions.NoOptions> zab;

    static {
        Api.ClientKey<zah> clientKey = new Api.ClientKey<>();
        CLIENT_KEY = clientKey;
        zac zacVar = new zac();
        zab = zacVar;
        API = new Api<>("Common.API", zacVar, clientKey);
    }
}
