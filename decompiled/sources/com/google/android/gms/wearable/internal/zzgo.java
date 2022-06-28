package com.google.android.gms.wearable.internal;

import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.wearable.Channel;
import com.google.android.gms.wearable.ChannelApi;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
final class zzgo implements ChannelApi.ChannelListener {
    private final String zza;
    private final ChannelApi.ChannelListener zzb;

    public zzgo(String str, ChannelApi.ChannelListener channelListener) {
        this.zza = (String) Preconditions.checkNotNull(str);
        this.zzb = (ChannelApi.ChannelListener) Preconditions.checkNotNull(channelListener);
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof zzgo)) {
            return false;
        }
        zzgo zzgoVar = (zzgo) obj;
        return this.zzb.equals(zzgoVar.zzb) && this.zza.equals(zzgoVar.zza);
    }

    public final int hashCode() {
        return (this.zza.hashCode() * 31) + this.zzb.hashCode();
    }

    @Override // com.google.android.gms.wearable.ChannelApi.ChannelListener
    public final void onChannelClosed(Channel channel, int i, int i2) {
        this.zzb.onChannelClosed(channel, i, i2);
    }

    @Override // com.google.android.gms.wearable.ChannelApi.ChannelListener
    public final void onChannelOpened(Channel channel) {
        this.zzb.onChannelOpened(channel);
    }

    @Override // com.google.android.gms.wearable.ChannelApi.ChannelListener
    public final void onInputClosed(Channel channel, int i, int i2) {
        this.zzb.onInputClosed(channel, i, i2);
    }

    @Override // com.google.android.gms.wearable.ChannelApi.ChannelListener
    public final void onOutputClosed(Channel channel, int i, int i2) {
        this.zzb.onOutputClosed(channel, i, i2);
    }
}
