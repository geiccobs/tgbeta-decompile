package com.google.android.gms.wearable.internal;

import android.content.IntentFilter;
import android.net.Uri;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.wearable.MessageApi;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
public final class zzfc implements MessageApi {
    private static final PendingResult<Status> zza(GoogleApiClient googleApiClient, MessageApi.MessageListener messageListener, IntentFilter[] intentFilterArr) {
        return googleApiClient.enqueue(new zzfa(googleApiClient, messageListener, googleApiClient.registerListener(messageListener), intentFilterArr, null));
    }

    @Override // com.google.android.gms.wearable.MessageApi
    public final PendingResult<Status> addListener(GoogleApiClient googleApiClient, MessageApi.MessageListener messageListener) {
        return zza(googleApiClient, messageListener, new IntentFilter[]{zzgv.zza("com.google.android.gms.wearable.MESSAGE_RECEIVED")});
    }

    @Override // com.google.android.gms.wearable.MessageApi
    public final PendingResult<Status> removeListener(GoogleApiClient googleApiClient, MessageApi.MessageListener messageListener) {
        return googleApiClient.enqueue(new zzez(this, googleApiClient, messageListener));
    }

    @Override // com.google.android.gms.wearable.MessageApi
    public final PendingResult<MessageApi.SendMessageResult> sendMessage(GoogleApiClient googleApiClient, String str, String str2, byte[] bArr) {
        return googleApiClient.enqueue(new zzey(this, googleApiClient, str, str2, bArr));
    }

    @Override // com.google.android.gms.wearable.MessageApi
    public final PendingResult<Status> addListener(GoogleApiClient googleApiClient, MessageApi.MessageListener messageListener, Uri uri, int i) {
        boolean z;
        Preconditions.checkNotNull(uri, "uri must not be null");
        if (i == 0) {
            z = true;
        } else if (i == 1) {
            i = 1;
            z = true;
        } else {
            z = false;
        }
        Preconditions.checkArgument(z, "invalid filter type");
        return zza(googleApiClient, messageListener, new IntentFilter[]{zzgv.zzb("com.google.android.gms.wearable.MESSAGE_RECEIVED", uri, i)});
    }
}
