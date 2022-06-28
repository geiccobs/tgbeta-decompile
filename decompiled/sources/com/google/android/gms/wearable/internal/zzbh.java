package com.google.android.gms.wearable.internal;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.wearable.Channel;
import java.io.IOException;
import java.io.OutputStream;
import javax.annotation.Nullable;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes.dex */
final class zzbh implements Channel.GetOutputStreamResult {
    private final Status zza;
    private final OutputStream zzb;

    public zzbh(Status status, @Nullable OutputStream outputStream) {
        this.zza = (Status) Preconditions.checkNotNull(status);
        this.zzb = outputStream;
    }

    @Override // com.google.android.gms.wearable.Channel.GetOutputStreamResult
    @Nullable
    public final OutputStream getOutputStream() {
        return this.zzb;
    }

    @Override // com.google.android.gms.common.api.Result
    public final Status getStatus() {
        return this.zza;
    }

    @Override // com.google.android.gms.common.api.Releasable
    public final void release() {
        OutputStream outputStream = this.zzb;
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {
            }
        }
    }
}
