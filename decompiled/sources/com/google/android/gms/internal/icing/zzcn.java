package com.google.android.gms.internal.icing;

import java.io.IOException;
import java.util.List;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes3.dex */
final class zzcn {
    private final zzcm zza;

    private zzcn(zzcm zzcmVar) {
        zzdh.zzb(zzcmVar, "output");
        this.zza = zzcmVar;
        zzcmVar.zza = this;
    }

    public static zzcn zza(zzcm zzcmVar) {
        zzcn zzcnVar = zzcmVar.zza;
        return zzcnVar != null ? zzcnVar : new zzcn(zzcmVar);
    }

    public final void zzC(int i, List<String> list) throws IOException {
        int i2 = 0;
        if (!(list instanceof zzdo)) {
            while (i2 < list.size()) {
                this.zza.zzh(i, list.get(i2));
                i2++;
            }
            return;
        }
        zzdo zzdoVar = (zzdo) list;
        while (i2 < list.size()) {
            Object zzg = zzdoVar.zzg(i2);
            if (zzg instanceof String) {
                this.zza.zzh(i, (String) zzg);
            } else {
                this.zza.zzi(i, (zzcf) zzg);
            }
            i2++;
        }
    }

    public final void zzD(int i, List<zzcf> list) throws IOException {
        for (int i2 = 0; i2 < list.size(); i2++) {
            this.zza.zzi(i, list.get(i2));
        }
    }

    public final void zzb(int i, int i2) throws IOException {
        this.zza.zzd(i, i2);
    }

    public final void zzc(int i, long j) throws IOException {
        this.zza.zze(i, j);
    }

    public final void zzd(int i, long j) throws IOException {
        this.zza.zzf(i, j);
    }

    public final void zze(int i, float f) throws IOException {
        this.zza.zzd(i, Float.floatToRawIntBits(f));
    }

    public final void zzf(int i, double d) throws IOException {
        this.zza.zzf(i, Double.doubleToRawLongBits(d));
    }

    public final void zzg(int i, int i2) throws IOException {
        this.zza.zzb(i, i2);
    }

    public final void zzh(int i, long j) throws IOException {
        this.zza.zze(i, j);
    }

    public final void zzi(int i, int i2) throws IOException {
        this.zza.zzb(i, i2);
    }

    public final void zzj(int i, long j) throws IOException {
        this.zza.zzf(i, j);
    }

    public final void zzk(int i, int i2) throws IOException {
        this.zza.zzd(i, i2);
    }

    public final void zzl(int i, boolean z) throws IOException {
        this.zza.zzg(i, z);
    }

    public final void zzm(int i, String str) throws IOException {
        this.zza.zzh(i, str);
    }

    public final void zzn(int i, zzcf zzcfVar) throws IOException {
        this.zza.zzi(i, zzcfVar);
    }

    public final void zzo(int i, int i2) throws IOException {
        this.zza.zzc(i, i2);
    }

    public final void zzp(int i, int i2) throws IOException {
        this.zza.zzc(i, (i2 >> 31) ^ (i2 + i2));
    }

    public final void zzq(int i, long j) throws IOException {
        this.zza.zze(i, (j >> 63) ^ (j + j));
    }

    public final void zzr(int i, Object obj, zzep zzepVar) throws IOException {
        zzee zzeeVar = (zzee) obj;
        zzck zzckVar = (zzck) this.zza;
        zzckVar.zzl((i << 3) | 2);
        zzbs zzbsVar = (zzbs) zzeeVar;
        int zzi = zzbsVar.zzi();
        if (zzi == -1) {
            zzi = zzepVar.zzd(zzbsVar);
            zzbsVar.zzj(zzi);
        }
        zzckVar.zzl(zzi);
        zzepVar.zzi(zzeeVar, zzckVar.zza);
    }

    public final void zzs(int i, Object obj, zzep zzepVar) throws IOException {
        zzcm zzcmVar = this.zza;
        zzcmVar.zza(i, 3);
        zzepVar.zzi((zzee) obj, zzcmVar.zza);
        zzcmVar.zza(i, 4);
    }

    public final void zzA(int i, List<Integer> list, boolean z) throws IOException {
        int i2 = 0;
        if (!z) {
            while (i2 < list.size()) {
                this.zza.zzb(i, list.get(i2).intValue());
                i2++;
            }
            return;
        }
        this.zza.zza(i, 2);
        int i3 = 0;
        for (int i4 = 0; i4 < list.size(); i4++) {
            i3 += zzcm.zzv(list.get(i4).intValue());
        }
        this.zza.zzl(i3);
        while (i2 < list.size()) {
            this.zza.zzk(list.get(i2).intValue());
            i2++;
        }
    }

    public final void zzB(int i, List<Boolean> list, boolean z) throws IOException {
        int i2 = 0;
        if (!z) {
            while (i2 < list.size()) {
                this.zza.zzg(i, list.get(i2).booleanValue());
                i2++;
            }
            return;
        }
        this.zza.zza(i, 2);
        int i3 = 0;
        for (int i4 = 0; i4 < list.size(); i4++) {
            list.get(i4).booleanValue();
            i3++;
        }
        this.zza.zzl(i3);
        while (i2 < list.size()) {
            this.zza.zzj(list.get(i2).booleanValue() ? (byte) 1 : (byte) 0);
            i2++;
        }
    }

    public final void zzE(int i, List<Integer> list, boolean z) throws IOException {
        int i2 = 0;
        if (!z) {
            while (i2 < list.size()) {
                this.zza.zzc(i, list.get(i2).intValue());
                i2++;
            }
            return;
        }
        this.zza.zza(i, 2);
        int i3 = 0;
        for (int i4 = 0; i4 < list.size(); i4++) {
            i3 += zzcm.zzw(list.get(i4).intValue());
        }
        this.zza.zzl(i3);
        while (i2 < list.size()) {
            this.zza.zzl(list.get(i2).intValue());
            i2++;
        }
    }

    public final void zzF(int i, List<Integer> list, boolean z) throws IOException {
        int i2 = 0;
        if (!z) {
            while (i2 < list.size()) {
                this.zza.zzd(i, list.get(i2).intValue());
                i2++;
            }
            return;
        }
        this.zza.zza(i, 2);
        int i3 = 0;
        for (int i4 = 0; i4 < list.size(); i4++) {
            list.get(i4).intValue();
            i3 += 4;
        }
        this.zza.zzl(i3);
        while (i2 < list.size()) {
            this.zza.zzm(list.get(i2).intValue());
            i2++;
        }
    }

    public final void zzG(int i, List<Long> list, boolean z) throws IOException {
        int i2 = 0;
        if (!z) {
            while (i2 < list.size()) {
                this.zza.zzf(i, list.get(i2).longValue());
                i2++;
            }
            return;
        }
        this.zza.zza(i, 2);
        int i3 = 0;
        for (int i4 = 0; i4 < list.size(); i4++) {
            list.get(i4).longValue();
            i3 += 8;
        }
        this.zza.zzl(i3);
        while (i2 < list.size()) {
            this.zza.zzo(list.get(i2).longValue());
            i2++;
        }
    }

    public final void zzH(int i, List<Integer> list, boolean z) throws IOException {
        int i2 = 0;
        if (!z) {
            while (i2 < list.size()) {
                zzcm zzcmVar = this.zza;
                int intValue = list.get(i2).intValue();
                zzcmVar.zzc(i, (intValue >> 31) ^ (intValue + intValue));
                i2++;
            }
            return;
        }
        this.zza.zza(i, 2);
        int i3 = 0;
        for (int i4 = 0; i4 < list.size(); i4++) {
            int intValue2 = list.get(i4).intValue();
            i3 += zzcm.zzw((intValue2 >> 31) ^ (intValue2 + intValue2));
        }
        this.zza.zzl(i3);
        while (i2 < list.size()) {
            zzcm zzcmVar2 = this.zza;
            int intValue3 = list.get(i2).intValue();
            zzcmVar2.zzl((intValue3 >> 31) ^ (intValue3 + intValue3));
            i2++;
        }
    }

    public final void zzI(int i, List<Long> list, boolean z) throws IOException {
        int i2 = 0;
        if (!z) {
            while (i2 < list.size()) {
                zzcm zzcmVar = this.zza;
                long longValue = list.get(i2).longValue();
                zzcmVar.zze(i, (longValue >> 63) ^ (longValue + longValue));
                i2++;
            }
            return;
        }
        this.zza.zza(i, 2);
        int i3 = 0;
        for (int i4 = 0; i4 < list.size(); i4++) {
            long longValue2 = list.get(i4).longValue();
            i3 += zzcm.zzx((longValue2 >> 63) ^ (longValue2 + longValue2));
        }
        this.zza.zzl(i3);
        while (i2 < list.size()) {
            zzcm zzcmVar2 = this.zza;
            long longValue3 = list.get(i2).longValue();
            zzcmVar2.zzn((longValue3 >> 63) ^ (longValue3 + longValue3));
            i2++;
        }
    }

    public final void zzt(int i, List<Integer> list, boolean z) throws IOException {
        int i2 = 0;
        if (!z) {
            while (i2 < list.size()) {
                this.zza.zzb(i, list.get(i2).intValue());
                i2++;
            }
            return;
        }
        this.zza.zza(i, 2);
        int i3 = 0;
        for (int i4 = 0; i4 < list.size(); i4++) {
            i3 += zzcm.zzv(list.get(i4).intValue());
        }
        this.zza.zzl(i3);
        while (i2 < list.size()) {
            this.zza.zzk(list.get(i2).intValue());
            i2++;
        }
    }

    public final void zzu(int i, List<Integer> list, boolean z) throws IOException {
        int i2 = 0;
        if (!z) {
            while (i2 < list.size()) {
                this.zza.zzd(i, list.get(i2).intValue());
                i2++;
            }
            return;
        }
        this.zza.zza(i, 2);
        int i3 = 0;
        for (int i4 = 0; i4 < list.size(); i4++) {
            list.get(i4).intValue();
            i3 += 4;
        }
        this.zza.zzl(i3);
        while (i2 < list.size()) {
            this.zza.zzm(list.get(i2).intValue());
            i2++;
        }
    }

    public final void zzv(int i, List<Long> list, boolean z) throws IOException {
        int i2 = 0;
        if (!z) {
            while (i2 < list.size()) {
                this.zza.zze(i, list.get(i2).longValue());
                i2++;
            }
            return;
        }
        this.zza.zza(i, 2);
        int i3 = 0;
        for (int i4 = 0; i4 < list.size(); i4++) {
            i3 += zzcm.zzx(list.get(i4).longValue());
        }
        this.zza.zzl(i3);
        while (i2 < list.size()) {
            this.zza.zzn(list.get(i2).longValue());
            i2++;
        }
    }

    public final void zzw(int i, List<Long> list, boolean z) throws IOException {
        int i2 = 0;
        if (!z) {
            while (i2 < list.size()) {
                this.zza.zze(i, list.get(i2).longValue());
                i2++;
            }
            return;
        }
        this.zza.zza(i, 2);
        int i3 = 0;
        for (int i4 = 0; i4 < list.size(); i4++) {
            i3 += zzcm.zzx(list.get(i4).longValue());
        }
        this.zza.zzl(i3);
        while (i2 < list.size()) {
            this.zza.zzn(list.get(i2).longValue());
            i2++;
        }
    }

    public final void zzx(int i, List<Long> list, boolean z) throws IOException {
        int i2 = 0;
        if (!z) {
            while (i2 < list.size()) {
                this.zza.zzf(i, list.get(i2).longValue());
                i2++;
            }
            return;
        }
        this.zza.zza(i, 2);
        int i3 = 0;
        for (int i4 = 0; i4 < list.size(); i4++) {
            list.get(i4).longValue();
            i3 += 8;
        }
        this.zza.zzl(i3);
        while (i2 < list.size()) {
            this.zza.zzo(list.get(i2).longValue());
            i2++;
        }
    }

    public final void zzy(int i, List<Float> list, boolean z) throws IOException {
        int i2 = 0;
        if (!z) {
            while (i2 < list.size()) {
                this.zza.zzd(i, Float.floatToRawIntBits(list.get(i2).floatValue()));
                i2++;
            }
            return;
        }
        this.zza.zza(i, 2);
        int i3 = 0;
        for (int i4 = 0; i4 < list.size(); i4++) {
            list.get(i4).floatValue();
            i3 += 4;
        }
        this.zza.zzl(i3);
        while (i2 < list.size()) {
            this.zza.zzm(Float.floatToRawIntBits(list.get(i2).floatValue()));
            i2++;
        }
    }

    public final void zzz(int i, List<Double> list, boolean z) throws IOException {
        int i2 = 0;
        if (!z) {
            while (i2 < list.size()) {
                this.zza.zzf(i, Double.doubleToRawLongBits(list.get(i2).doubleValue()));
                i2++;
            }
            return;
        }
        this.zza.zza(i, 2);
        int i3 = 0;
        for (int i4 = 0; i4 < list.size(); i4++) {
            list.get(i4).doubleValue();
            i3 += 8;
        }
        this.zza.zzl(i3);
        while (i2 < list.size()) {
            this.zza.zzo(Double.doubleToRawLongBits(list.get(i2).doubleValue()));
            i2++;
        }
    }
}
