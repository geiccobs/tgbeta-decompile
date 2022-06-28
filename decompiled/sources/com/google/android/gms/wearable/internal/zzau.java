package com.google.android.gms.wearable.internal;

import com.google.android.gms.wearable.Channel;
import com.google.android.gms.wearable.ChannelApi;
import com.google.android.gms.wearable.ChannelClient;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
public final class zzau implements ChannelApi.ChannelListener {
    private final ChannelClient.ChannelCallback zza;

    public zzau(ChannelClient.ChannelCallback channelCallback) {
        this.zza = channelCallback;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && getClass() == obj.getClass()) {
            return this.zza.equals(((zzau) obj).zza);
        }
        return false;
    }

    public final int hashCode() {
        return this.zza.hashCode();
    }

    @Override // com.google.android.gms.wearable.ChannelApi.ChannelListener
    public final void onChannelClosed(Channel channel, int i, int i2) {
        zzbi zzd;
        ChannelClient.ChannelCallback channelCallback = this.zza;
        zzd = zzav.zzd(channel);
        channelCallback.onChannelClosed(zzd, i, i2);
    }

    @Override // com.google.android.gms.wearable.ChannelApi.ChannelListener
    public final void onChannelOpened(Channel channel) {
        zzbi zzd;
        ChannelClient.ChannelCallback channelCallback = this.zza;
        zzd = zzav.zzd(channel);
        channelCallback.onChannelOpened(zzd);
    }

    @Override // com.google.android.gms.wearable.ChannelApi.ChannelListener
    public final void onInputClosed(Channel channel, int i, int i2) {
        zzbi zzd;
        ChannelClient.ChannelCallback channelCallback = this.zza;
        zzd = zzav.zzd(channel);
        channelCallback.onInputClosed(zzd, i, i2);
    }

    @Override // com.google.android.gms.wearable.ChannelApi.ChannelListener
    public final void onOutputClosed(Channel channel, int i, int i2) {
        zzbi zzd;
        ChannelClient.ChannelCallback channelCallback = this.zza;
        zzd = zzav.zzd(channel);
        channelCallback.onOutputClosed(zzd, i, i2);
    }
}
