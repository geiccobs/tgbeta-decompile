package com.google.android.gms.internal.mlkit_common;

import com.google.android.gms.internal.icing.zzby$$ExternalSyntheticBackport0;
import java.util.Comparator;
/* compiled from: com.google.mlkit:common@@17.0.0 */
/* loaded from: classes3.dex */
final class zzer implements Comparator<zzep> {
    @Override // java.util.Comparator
    public final /* synthetic */ int compare(zzep zzepVar, zzep zzepVar2) {
        int zzb;
        int zzb2;
        zzep zzepVar3 = zzepVar;
        zzep zzepVar4 = zzepVar2;
        zzeu zzeuVar = (zzeu) zzepVar3.iterator();
        zzeu zzeuVar2 = (zzeu) zzepVar4.iterator();
        while (zzeuVar.hasNext() && zzeuVar2.hasNext()) {
            zzb = zzep.zzb(zzeuVar.zza());
            zzb2 = zzep.zzb(zzeuVar2.zza());
            int m = zzby$$ExternalSyntheticBackport0.m(zzb, zzb2);
            if (m != 0) {
                return m;
            }
        }
        return zzby$$ExternalSyntheticBackport0.m(zzepVar3.zza(), zzepVar4.zza());
    }
}
