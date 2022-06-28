package com.google.android.gms.wearable.internal;

import com.google.android.gms.common.api.internal.ListenerHolder;
import com.google.android.gms.wearable.ChannelApi;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
final class zzhz implements ListenerHolder.Notifier<ChannelApi.ChannelListener> {
    final /* synthetic */ zzax zza;

    public zzhz(zzax zzaxVar) {
        this.zza = zzaxVar;
    }

    @Override // com.google.android.gms.common.api.internal.ListenerHolder.Notifier
    public final /* bridge */ /* synthetic */ void notifyListener(ChannelApi.ChannelListener channelListener) {
        this.zza.zza(channelListener);
    }

    @Override // com.google.android.gms.common.api.internal.ListenerHolder.Notifier
    public final void onNotifyListenerFailed() {
    }
}
