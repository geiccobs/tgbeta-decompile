package com.google.android.gms.location;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.internal.RemoteCall;
import com.google.android.gms.common.api.internal.TaskApiCall;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import java.util.List;
/* compiled from: com.google.android.gms:play-services-location@@18.0.0 */
/* loaded from: classes3.dex */
public class GeofencingClient extends GoogleApi<Api.ApiOptions.NoOptions> {
    public static final /* synthetic */ int zza = 0;

    public GeofencingClient(Activity activity) {
        super(activity, LocationServices.API, Api.ApiOptions.NO_OPTIONS, GoogleApi.Settings.DEFAULT_SETTINGS);
    }

    public Task<Void> addGeofences(GeofencingRequest geofencingRequest, PendingIntent pendingIntent) {
        return doWrite(TaskApiCall.builder().run(new RemoteCall(geofencingRequest.zza(getContextAttributionTag()), pendingIntent) { // from class: com.google.android.gms.location.zzaq
            private final GeofencingRequest zza;
            private final PendingIntent zzb;

            /* JADX INFO: Access modifiers changed from: package-private */
            {
                this.zza = zza2;
                this.zzb = pendingIntent;
            }

            @Override // com.google.android.gms.common.api.internal.RemoteCall
            public final void accept(Object obj, Object obj2) {
                GeofencingRequest geofencingRequest2 = this.zza;
                PendingIntent pendingIntent2 = this.zzb;
                int i = GeofencingClient.zza;
                ((com.google.android.gms.internal.location.zzaz) obj).zzv(geofencingRequest2, pendingIntent2, new zzat((TaskCompletionSource) obj2));
            }
        }).setMethodKey(2424).build());
    }

    public Task<Void> removeGeofences(PendingIntent pendingIntent) {
        return doWrite(TaskApiCall.builder().run(new RemoteCall(pendingIntent) { // from class: com.google.android.gms.location.zzar
            private final PendingIntent zza;

            /* JADX INFO: Access modifiers changed from: package-private */
            {
                this.zza = pendingIntent;
            }

            @Override // com.google.android.gms.common.api.internal.RemoteCall
            public final void accept(Object obj, Object obj2) {
                PendingIntent pendingIntent2 = this.zza;
                int i = GeofencingClient.zza;
                ((com.google.android.gms.internal.location.zzaz) obj).zzx(pendingIntent2, new zzat((TaskCompletionSource) obj2));
            }
        }).setMethodKey(2425).build());
    }

    public GeofencingClient(Context context) {
        super(context, LocationServices.API, Api.ApiOptions.NO_OPTIONS, GoogleApi.Settings.DEFAULT_SETTINGS);
    }

    public Task<Void> removeGeofences(List<String> list) {
        return doWrite(TaskApiCall.builder().run(new RemoteCall(list) { // from class: com.google.android.gms.location.zzas
            private final List zza;

            /* JADX INFO: Access modifiers changed from: package-private */
            {
                this.zza = list;
            }

            @Override // com.google.android.gms.common.api.internal.RemoteCall
            public final void accept(Object obj, Object obj2) {
                List<String> list2 = this.zza;
                int i = GeofencingClient.zza;
                ((com.google.android.gms.internal.location.zzaz) obj).zzy(list2, new zzat((TaskCompletionSource) obj2));
            }
        }).setMethodKey(2425).build());
    }
}
