package com.google.android.gms.auth.account;

import android.app.Activity;
import android.content.Context;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.internal.auth.zzh;
import com.google.android.gms.internal.auth.zzr;
/* loaded from: classes3.dex */
public class WorkAccount {
    public static final Api<Api.ApiOptions.NoOptions> API;
    private static final Api.AbstractClientBuilder<zzr, Api.ApiOptions.NoOptions> CLIENT_BUILDER;
    private static final Api.ClientKey<zzr> CLIENT_KEY;
    @Deprecated
    public static final WorkAccountApi WorkAccountApi = new zzh();

    public static WorkAccountClient getClient(Activity activity) {
        return new WorkAccountClient(activity);
    }

    public static WorkAccountClient getClient(Context context) {
        return new WorkAccountClient(context);
    }

    private WorkAccount() {
    }

    static {
        Api.ClientKey<zzr> clientKey = new Api.ClientKey<>();
        CLIENT_KEY = clientKey;
        zzf zzfVar = new zzf();
        CLIENT_BUILDER = zzfVar;
        API = new Api<>("WorkAccount.API", zzfVar, clientKey);
    }
}
