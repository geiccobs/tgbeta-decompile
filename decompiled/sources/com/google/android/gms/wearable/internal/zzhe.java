package com.google.android.gms.wearable.internal;

import android.os.ParcelFileDescriptor;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.internal.BaseImplementation;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.wearable.Channel;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
final class zzhe extends zzgx<Channel.GetOutputStreamResult> {
    private final zzbs zza;

    public zzhe(BaseImplementation.ResultHolder<Channel.GetOutputStreamResult> resultHolder, zzbs zzbsVar) {
        super(resultHolder);
        this.zza = (zzbs) Preconditions.checkNotNull(zzbsVar);
    }

    @Override // com.google.android.gms.wearable.internal.zza, com.google.android.gms.wearable.internal.zzeq
    public final void zzu(zzdo zzdoVar) {
        zzbn zzbnVar;
        ParcelFileDescriptor parcelFileDescriptor = zzdoVar.zzb;
        if (parcelFileDescriptor != null) {
            zzbnVar = new zzbn(new ParcelFileDescriptor.AutoCloseOutputStream(parcelFileDescriptor));
            this.zza.zzb(new zzbm(zzbnVar));
        } else {
            zzbnVar = null;
        }
        zzF(new zzbh(new Status(zzdoVar.zza), zzbnVar));
    }
}
