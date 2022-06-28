package com.google.android.gms.wearable.internal;

import android.os.ParcelFileDescriptor;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.internal.BaseImplementation;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.wearable.Channel;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
final class zzhd extends zzgx<Channel.GetInputStreamResult> {
    private final zzbs zza;

    public zzhd(BaseImplementation.ResultHolder<Channel.GetInputStreamResult> resultHolder, zzbs zzbsVar) {
        super(resultHolder);
        this.zza = (zzbs) Preconditions.checkNotNull(zzbsVar);
    }

    @Override // com.google.android.gms.wearable.internal.zza, com.google.android.gms.wearable.internal.zzeq
    public final void zzt(zzdm zzdmVar) {
        zzbl zzblVar;
        ParcelFileDescriptor parcelFileDescriptor = zzdmVar.zzb;
        if (parcelFileDescriptor != null) {
            zzblVar = new zzbl(new ParcelFileDescriptor.AutoCloseInputStream(parcelFileDescriptor));
            this.zza.zzb(new zzbk(zzblVar));
        } else {
            zzblVar = null;
        }
        zzF(new zzbg(new Status(zzdmVar.zza), zzblVar));
    }
}
