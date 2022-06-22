package com.google.android.gms.common.internal.service;

import android.content.Context;
import android.os.RemoteException;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.internal.RemoteCall;
import com.google.android.gms.common.api.internal.TaskApiCall;
import com.google.android.gms.common.internal.zaaa;
import com.google.android.gms.common.internal.zaac;
import com.google.android.gms.internal.base.zae;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
/* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
/* loaded from: classes.dex */
public final class zaq extends GoogleApi<Api.ApiOptions.NoOptions> implements zaac {
    private static final Api.ClientKey<zar> zaa;
    private static final Api.AbstractClientBuilder<zar, Api.ApiOptions.NoOptions> zab;
    private static final Api<Api.ApiOptions.NoOptions> zac;

    public zaq(Context context) {
        super(context, zac, Api.ApiOptions.NO_OPTIONS, GoogleApi.Settings.DEFAULT_SETTINGS);
    }

    @Override // com.google.android.gms.common.internal.zaac
    public final Task<Void> zaa(zaaa zaaaVar) {
        return doBestEffortWrite(TaskApiCall.builder().setFeatures(zae.zaa).setAutoResolveMissingFeatures(false).run(new RemoteCall(zaaaVar) { // from class: com.google.android.gms.common.internal.service.zap
            private final zaaa zaa;

            /* JADX INFO: Access modifiers changed from: package-private */
            {
                this.zaa = zaaaVar;
            }

            @Override // com.google.android.gms.common.api.internal.RemoteCall
            public final void accept(Object obj, Object obj2) {
                zaq.zaa(this.zaa, (zar) obj, (TaskCompletionSource) obj2);
            }
        }).build());
    }

    public static final /* synthetic */ void zaa(zaaa zaaaVar, zar zarVar, TaskCompletionSource taskCompletionSource) throws RemoteException {
        ((zak) zarVar.getService()).zaa(zaaaVar);
        taskCompletionSource.setResult(null);
    }

    static {
        Api.ClientKey<zar> clientKey = new Api.ClientKey<>();
        zaa = clientKey;
        zas zasVar = new zas();
        zab = zasVar;
        zac = new Api<>("ClientTelemetry.API", zasVar, clientKey);
    }
}
