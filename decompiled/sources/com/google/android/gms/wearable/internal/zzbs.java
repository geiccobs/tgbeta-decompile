package com.google.android.gms.wearable.internal;

import com.google.android.gms.common.internal.Preconditions;
import javax.annotation.Nullable;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes.dex */
public final class zzbs extends zzem {
    private final Object zza = new Object();
    @Nullable
    private zzbt zzb;
    @Nullable
    private zzaw zzc;

    public final void zzb(zzbt zzbtVar) {
        zzaw zzawVar;
        synchronized (this.zza) {
            this.zzb = (zzbt) Preconditions.checkNotNull(zzbtVar);
            zzawVar = this.zzc;
        }
        if (zzawVar != null) {
            zzbtVar.zza(zzawVar);
        }
    }

    @Override // com.google.android.gms.wearable.internal.zzen
    public final void zzc(int i, int i2) {
        zzbt zzbtVar;
        zzaw zzawVar;
        synchronized (this.zza) {
            zzbtVar = this.zzb;
            zzawVar = new zzaw(i, i2);
            this.zzc = zzawVar;
        }
        if (zzbtVar != null) {
            zzbtVar.zza(zzawVar);
        }
    }
}
