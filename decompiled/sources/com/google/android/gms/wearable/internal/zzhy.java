package com.google.android.gms.wearable.internal;

import com.google.android.gms.common.api.internal.ListenerHolder;
import com.google.android.gms.wearable.MessageApi;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
final class zzhy implements ListenerHolder.Notifier<MessageApi.MessageListener> {
    final /* synthetic */ zzfj zza;

    public zzhy(zzfj zzfjVar) {
        this.zza = zzfjVar;
    }

    @Override // com.google.android.gms.common.api.internal.ListenerHolder.Notifier
    public final /* bridge */ /* synthetic */ void notifyListener(MessageApi.MessageListener messageListener) {
        messageListener.onMessageReceived(this.zza);
    }

    @Override // com.google.android.gms.common.api.internal.ListenerHolder.Notifier
    public final void onNotifyListenerFailed() {
    }
}
