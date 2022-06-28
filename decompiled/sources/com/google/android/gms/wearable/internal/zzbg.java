package com.google.android.gms.wearable.internal;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.wearable.Channel;
import java.io.IOException;
import java.io.InputStream;
import javax.annotation.Nullable;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes.dex */
final class zzbg implements Channel.GetInputStreamResult {
    private final Status zza;
    private final InputStream zzb;

    public zzbg(Status status, @Nullable InputStream inputStream) {
        this.zza = (Status) Preconditions.checkNotNull(status);
        this.zzb = inputStream;
    }

    @Override // com.google.android.gms.wearable.Channel.GetInputStreamResult
    @Nullable
    public final InputStream getInputStream() {
        return this.zzb;
    }

    @Override // com.google.android.gms.common.api.Result
    public final Status getStatus() {
        return this.zza;
    }

    @Override // com.google.android.gms.common.api.Releasable
    public final void release() {
        InputStream inputStream = this.zzb;
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
            }
        }
    }
}
