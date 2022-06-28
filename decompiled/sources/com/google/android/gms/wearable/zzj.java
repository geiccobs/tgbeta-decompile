package com.google.android.gms.wearable;

import com.google.android.gms.wearable.ChannelClient;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
final class zzj extends ChannelClient.ChannelCallback {
    final /* synthetic */ WearableListenerService zza;

    @Override // com.google.android.gms.wearable.ChannelClient.ChannelCallback
    public final void onChannelClosed(ChannelClient.Channel channel, int i, int i2) {
        this.zza.onChannelClosed(channel, i, i2);
    }

    @Override // com.google.android.gms.wearable.ChannelClient.ChannelCallback
    public final void onChannelOpened(ChannelClient.Channel channel) {
        this.zza.onChannelOpened(channel);
    }

    @Override // com.google.android.gms.wearable.ChannelClient.ChannelCallback
    public final void onInputClosed(ChannelClient.Channel channel, int i, int i2) {
        this.zza.onInputClosed(channel, i, i2);
    }

    @Override // com.google.android.gms.wearable.ChannelClient.ChannelCallback
    public final void onOutputClosed(ChannelClient.Channel channel, int i, int i2) {
        this.zza.onOutputClosed(channel, i, i2);
    }
}
