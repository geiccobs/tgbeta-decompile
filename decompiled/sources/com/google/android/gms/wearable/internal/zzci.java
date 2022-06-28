package com.google.android.gms.wearable.internal;

import android.os.ParcelFileDescriptor;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wearable.DataApi;
import java.io.IOException;
import java.io.InputStream;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
public final class zzci implements DataApi.GetFdForAssetResult {
    private final Status zza;
    private volatile ParcelFileDescriptor zzb;
    private volatile InputStream zzc;
    private volatile boolean zzd = false;

    public zzci(Status status, ParcelFileDescriptor parcelFileDescriptor) {
        this.zza = status;
        this.zzb = parcelFileDescriptor;
    }

    @Override // com.google.android.gms.wearable.DataApi.GetFdForAssetResult
    public final ParcelFileDescriptor getFd() {
        if (!this.zzd) {
            return this.zzb;
        }
        throw new IllegalStateException("Cannot access the file descriptor after release().");
    }

    @Override // com.google.android.gms.wearable.DataApi.GetFdForAssetResult
    public final InputStream getInputStream() {
        if (!this.zzd) {
            if (this.zzb == null) {
                return null;
            }
            if (this.zzc == null) {
                this.zzc = new ParcelFileDescriptor.AutoCloseInputStream(this.zzb);
            }
            return this.zzc;
        }
        throw new IllegalStateException("Cannot access the input stream after release().");
    }

    @Override // com.google.android.gms.common.api.Result
    public final Status getStatus() {
        return this.zza;
    }

    @Override // com.google.android.gms.common.api.Releasable
    public final void release() {
        if (this.zzb == null) {
            return;
        }
        if (!this.zzd) {
            try {
                if (this.zzc != null) {
                    this.zzc.close();
                } else {
                    this.zzb.close();
                }
                this.zzd = true;
                this.zzb = null;
                this.zzc = null;
                return;
            } catch (IOException e) {
                return;
            }
        }
        throw new IllegalStateException("releasing an already released result.");
    }
}
