package com.google.android.gms.wearable.internal;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.wearable.Channel;
import com.google.android.gms.wearable.ChannelApi;
import javax.annotation.Nullable;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes.dex */
final class zzak implements ChannelApi.OpenChannelResult {
    private final Status zza;
    private final Channel zzb;

    public zzak(Status status, @Nullable Channel channel) {
        this.zza = (Status) Preconditions.checkNotNull(status);
        this.zzb = channel;
    }

    @Override // com.google.android.gms.wearable.ChannelApi.OpenChannelResult
    @Nullable
    public final Channel getChannel() {
        return this.zzb;
    }

    @Override // com.google.android.gms.common.api.Result
    public final Status getStatus() {
        return this.zza;
    }
}
