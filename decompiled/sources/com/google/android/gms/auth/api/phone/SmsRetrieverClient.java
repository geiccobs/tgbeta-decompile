package com.google.android.gms.auth.api.phone;

import android.content.Context;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.internal.ApiExceptionMapper;
import com.google.android.gms.internal.p000authapiphone.zzi;
import com.google.android.gms.tasks.Task;
/* loaded from: classes.dex */
public abstract class SmsRetrieverClient extends GoogleApi<Api.ApiOptions.NoOptions> {
    private static final Api<Api.ApiOptions.NoOptions> API;
    private static final Api.AbstractClientBuilder<zzi, Api.ApiOptions.NoOptions> CLIENT_BUILDER;
    private static final Api.ClientKey<zzi> CLIENT_KEY;

    static {
        Api.ClientKey<zzi> clientKey = new Api.ClientKey<>();
        CLIENT_KEY = clientKey;
        zza zzaVar = new zza();
        CLIENT_BUILDER = zzaVar;
        API = new Api<>("SmsRetriever.API", zzaVar, clientKey);
    }

    public SmsRetrieverClient(Context context) {
        super(context, API, (Api.ApiOptions) null, new ApiExceptionMapper());
    }

    public abstract Task<Void> startSmsRetriever();
}
