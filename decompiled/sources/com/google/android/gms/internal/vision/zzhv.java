package com.google.android.gms.internal.vision;

import com.google.android.gms.internal.mlkit_language_id.zzdp$$ExternalSyntheticBackport0;
import java.util.Comparator;
/* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
/* loaded from: classes.dex */
final class zzhv implements Comparator<zzht> {
    @Override // java.util.Comparator
    public final /* synthetic */ int compare(zzht zzhtVar, zzht zzhtVar2) {
        int zzb;
        int zzb2;
        zzht zzhtVar3 = zzhtVar;
        zzht zzhtVar4 = zzhtVar2;
        zzhy zzhyVar = (zzhy) zzhtVar3.iterator();
        zzhy zzhyVar2 = (zzhy) zzhtVar4.iterator();
        while (zzhyVar.hasNext() && zzhyVar2.hasNext()) {
            zzb = zzht.zzb(zzhyVar.zza());
            zzb2 = zzht.zzb(zzhyVar2.zza());
            int m = zzdp$$ExternalSyntheticBackport0.m(zzb, zzb2);
            if (m != 0) {
                return m;
            }
        }
        return zzdp$$ExternalSyntheticBackport0.m(zzhtVar3.zza(), zzhtVar4.zza());
    }
}
