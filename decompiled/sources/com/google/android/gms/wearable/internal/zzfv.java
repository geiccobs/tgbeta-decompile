package com.google.android.gms.wearable.internal;

import android.app.Activity;
import android.content.Context;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.internal.RemoteCall;
import com.google.android.gms.common.api.internal.TaskApiCall;
import com.google.android.gms.common.internal.PendingResultUtil;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.NodeClient;
import java.util.List;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
public final class zzfv extends NodeClient {
    final NodeApi zza = new zzfp();

    public zzfv(Activity activity, GoogleApi.Settings settings) {
        super(activity, settings);
    }

    @Override // com.google.android.gms.wearable.NodeClient
    public final Task<String> getCompanionPackageForNode(String str) {
        return doRead(TaskApiCall.builder().run(new RemoteCall(this, str) { // from class: com.google.android.gms.wearable.internal.zzfs
            private final zzfv zza;
            private final String zzb;

            /* JADX INFO: Access modifiers changed from: package-private */
            {
                this.zza = this;
                this.zzb = str;
            }

            @Override // com.google.android.gms.common.api.internal.RemoteCall
            public final void accept(Object obj, Object obj2) {
                zzfv zzfvVar = this.zza;
                ((zzeu) ((zzhv) obj).getService()).zzn(new zzhf(new zzft(zzfvVar, (TaskCompletionSource) obj2)), this.zzb);
            }
        }).setFeatures(com.google.android.gms.wearable.zze.zzc).setMethodKey(24023).build());
    }

    @Override // com.google.android.gms.wearable.NodeClient
    public final Task<List<Node>> getConnectedNodes() {
        NodeApi nodeApi = this.zza;
        GoogleApiClient asGoogleApiClient = asGoogleApiClient();
        return PendingResultUtil.toTask(asGoogleApiClient.enqueue(new zzfm((zzfp) nodeApi, asGoogleApiClient)), zzfr.zza);
    }

    @Override // com.google.android.gms.wearable.NodeClient
    public final Task<Node> getLocalNode() {
        NodeApi nodeApi = this.zza;
        GoogleApiClient asGoogleApiClient = asGoogleApiClient();
        return PendingResultUtil.toTask(asGoogleApiClient.enqueue(new zzfl((zzfp) nodeApi, asGoogleApiClient)), zzfq.zza);
    }

    public zzfv(Context context, GoogleApi.Settings settings) {
        super(context, settings);
    }
}
