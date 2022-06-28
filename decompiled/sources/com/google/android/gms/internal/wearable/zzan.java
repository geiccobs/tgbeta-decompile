package com.google.android.gms.internal.wearable;

import com.google.android.gms.internal.icing.zzby$$ExternalSyntheticBackport0;
import java.util.Comparator;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
final class zzan implements Comparator<zzau> {
    @Override // java.util.Comparator
    public final /* bridge */ /* synthetic */ int compare(zzau zzauVar, zzau zzauVar2) {
        zzau zzauVar3 = zzauVar;
        zzau zzauVar4 = zzauVar2;
        zzam zzamVar = new zzam(zzauVar3);
        zzam zzamVar2 = new zzam(zzauVar4);
        while (zzamVar.hasNext() && zzamVar2.hasNext()) {
            int m = zzby$$ExternalSyntheticBackport0.m(zzamVar.zza() & 255, zzamVar2.zza() & 255);
            if (m != 0) {
                return m;
            }
        }
        return zzby$$ExternalSyntheticBackport0.m(zzauVar3.zzc(), zzauVar4.zzc());
    }
}
