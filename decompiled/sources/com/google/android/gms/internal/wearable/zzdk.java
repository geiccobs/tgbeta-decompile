package com.google.android.gms.internal.wearable;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.RandomAccess;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
final class zzdk {
    private static final Class<?> zza;
    private static final zzdw<?, ?> zzb;
    private static final zzdw<?, ?> zzc;
    private static final zzdw<?, ?> zzd;

    static {
        Class<?> cls;
        try {
            cls = Class.forName("com.google.protobuf.GeneratedMessage");
        } catch (Throwable th) {
            cls = null;
        }
        zza = cls;
        zzb = zzab(false);
        zzc = zzab(true);
        zzd = new zzdy();
    }

    public static zzdw<?, ?> zzA() {
        return zzb;
    }

    public static zzdw<?, ?> zzB() {
        return zzc;
    }

    public static zzdw<?, ?> zzC() {
        return zzd;
    }

    public static boolean zzD(Object obj, Object obj2) {
        return obj == obj2 || (obj != null && obj.equals(obj2));
    }

    public static <T, FT extends zzbk<FT>> void zzE(zzbh<FT> zzbhVar, T t, T t2) {
        zzbhVar.zzb(t2);
        throw null;
    }

    public static <T, UT, UB> void zzF(zzdw<UT, UB> zzdwVar, T t, T t2) {
        zzdwVar.zzc(t, zzdwVar.zzf(zzdwVar.zzd(t), zzdwVar.zzd(t2)));
    }

    public static <UT, UB> UB zzG(int i, List<Integer> list, zzbw zzbwVar, UB ub, zzdw<UT, UB> zzdwVar) {
        if (zzbwVar == null) {
            return ub;
        }
        if (list instanceof RandomAccess) {
            int size = list.size();
            int i2 = 0;
            for (int i3 = 0; i3 < size; i3++) {
                int intValue = list.get(i3).intValue();
                if (zzbwVar.zza(intValue)) {
                    if (i3 != i2) {
                        list.set(i2, Integer.valueOf(intValue));
                    }
                    i2++;
                } else {
                    ub = (UB) zzH(i, intValue, ub, zzdwVar);
                }
            }
            if (i2 != size) {
                list.subList(i2, size).clear();
                return ub;
            }
        } else {
            Iterator<Integer> it = list.iterator();
            while (it.hasNext()) {
                int intValue2 = it.next().intValue();
                if (!zzbwVar.zza(intValue2)) {
                    ub = (UB) zzH(i, intValue2, ub, zzdwVar);
                    it.remove();
                }
            }
        }
        return ub;
    }

    static <UT, UB> UB zzH(int i, int i2, UB ub, zzdw<UT, UB> zzdwVar) {
        if (ub == null) {
            ub = zzdwVar.zzb();
        }
        zzdwVar.zza(ub, i, i2);
        return ub;
    }

    public static <T> void zzI(zzcs zzcsVar, T t, T t2, long j) {
        zzeg.zzo(t, j, zzcs.zzb(zzeg.zzn(t, j), zzeg.zzn(t2, j)));
    }

    public static void zzJ(int i, List<Double> list, zzbc zzbcVar, boolean z) throws IOException {
        if (list == null || list.isEmpty()) {
            return;
        }
        zzbcVar.zzB(i, list, z);
    }

    public static void zzK(int i, List<Float> list, zzbc zzbcVar, boolean z) throws IOException {
        if (list == null || list.isEmpty()) {
            return;
        }
        zzbcVar.zzA(i, list, z);
    }

    public static void zzL(int i, List<Long> list, zzbc zzbcVar, boolean z) throws IOException {
        if (list == null || list.isEmpty()) {
            return;
        }
        zzbcVar.zzx(i, list, z);
    }

    public static void zzM(int i, List<Long> list, zzbc zzbcVar, boolean z) throws IOException {
        if (list == null || list.isEmpty()) {
            return;
        }
        zzbcVar.zzy(i, list, z);
    }

    public static void zzN(int i, List<Long> list, zzbc zzbcVar, boolean z) throws IOException {
        if (list == null || list.isEmpty()) {
            return;
        }
        zzbcVar.zzK(i, list, z);
    }

    public static void zzO(int i, List<Long> list, zzbc zzbcVar, boolean z) throws IOException {
        if (list == null || list.isEmpty()) {
            return;
        }
        zzbcVar.zzz(i, list, z);
    }

    public static void zzP(int i, List<Long> list, zzbc zzbcVar, boolean z) throws IOException {
        if (list == null || list.isEmpty()) {
            return;
        }
        zzbcVar.zzI(i, list, z);
    }

    public static void zzQ(int i, List<Integer> list, zzbc zzbcVar, boolean z) throws IOException {
        if (list == null || list.isEmpty()) {
            return;
        }
        zzbcVar.zzv(i, list, z);
    }

    public static void zzR(int i, List<Integer> list, zzbc zzbcVar, boolean z) throws IOException {
        if (list == null || list.isEmpty()) {
            return;
        }
        zzbcVar.zzG(i, list, z);
    }

    public static void zzS(int i, List<Integer> list, zzbc zzbcVar, boolean z) throws IOException {
        if (list == null || list.isEmpty()) {
            return;
        }
        zzbcVar.zzJ(i, list, z);
    }

    public static void zzT(int i, List<Integer> list, zzbc zzbcVar, boolean z) throws IOException {
        if (list == null || list.isEmpty()) {
            return;
        }
        zzbcVar.zzw(i, list, z);
    }

    public static void zzU(int i, List<Integer> list, zzbc zzbcVar, boolean z) throws IOException {
        if (list == null || list.isEmpty()) {
            return;
        }
        zzbcVar.zzH(i, list, z);
    }

    public static void zzV(int i, List<Integer> list, zzbc zzbcVar, boolean z) throws IOException {
        if (list == null || list.isEmpty()) {
            return;
        }
        zzbcVar.zzC(i, list, z);
    }

    public static void zzW(int i, List<Boolean> list, zzbc zzbcVar, boolean z) throws IOException {
        if (list == null || list.isEmpty()) {
            return;
        }
        zzbcVar.zzD(i, list, z);
    }

    public static void zzX(int i, List<String> list, zzbc zzbcVar) throws IOException {
        if (list == null || list.isEmpty()) {
            return;
        }
        zzbcVar.zzE(i, list);
    }

    public static void zzY(int i, List<zzau> list, zzbc zzbcVar) throws IOException {
        if (list == null || list.isEmpty()) {
            return;
        }
        zzbcVar.zzF(i, list);
    }

    public static void zzZ(int i, List<?> list, zzbc zzbcVar, zzdi zzdiVar) throws IOException {
        if (list == null || list.isEmpty()) {
            return;
        }
        for (int i2 = 0; i2 < list.size(); i2++) {
            zzbcVar.zzr(i, list.get(i2), zzdiVar);
        }
    }

    public static void zza(Class<?> cls) {
        Class<?> cls2;
        if (zzbs.class.isAssignableFrom(cls) || (cls2 = zza) == null || cls2.isAssignableFrom(cls)) {
            return;
        }
        throw new IllegalArgumentException("Message classes must extend GeneratedMessage or GeneratedMessageLite");
    }

    public static void zzaa(int i, List<?> list, zzbc zzbcVar, zzdi zzdiVar) throws IOException {
        if (list == null || list.isEmpty()) {
            return;
        }
        for (int i2 = 0; i2 < list.size(); i2++) {
            zzbcVar.zzs(i, list.get(i2), zzdiVar);
        }
    }

    private static zzdw<?, ?> zzab(boolean z) {
        Class<?> cls;
        try {
            cls = Class.forName("com.google.protobuf.UnknownFieldSetSchema");
        } catch (Throwable th) {
            cls = null;
        }
        if (cls == null) {
            return null;
        }
        try {
            return (zzdw) cls.getConstructor(Boolean.TYPE).newInstance(Boolean.valueOf(z));
        } catch (Throwable th2) {
            return null;
        }
    }

    public static int zzb(List<Long> list) {
        int i;
        int size = list.size();
        int i2 = 0;
        if (size == 0) {
            return 0;
        }
        if (list instanceof zzcm) {
            zzcm zzcmVar = (zzcm) list;
            i = 0;
            while (i2 < size) {
                i += zzbb.zzx(zzcmVar.zzf(i2));
                i2++;
            }
        } else {
            i = 0;
            while (i2 < size) {
                i += zzbb.zzx(list.get(i2).longValue());
                i2++;
            }
        }
        return i;
    }

    public static int zzc(int i, List<Long> list, boolean z) {
        if (list.size() == 0) {
            return 0;
        }
        return zzb(list) + (list.size() * zzbb.zzu(i));
    }

    public static int zzd(List<Long> list) {
        int i;
        int size = list.size();
        int i2 = 0;
        if (size == 0) {
            return 0;
        }
        if (list instanceof zzcm) {
            zzcm zzcmVar = (zzcm) list;
            i = 0;
            while (i2 < size) {
                i += zzbb.zzx(zzcmVar.zzf(i2));
                i2++;
            }
        } else {
            i = 0;
            while (i2 < size) {
                i += zzbb.zzx(list.get(i2).longValue());
                i2++;
            }
        }
        return i;
    }

    public static int zze(int i, List<Long> list, boolean z) {
        int size = list.size();
        if (size == 0) {
            return 0;
        }
        return zzd(list) + (size * zzbb.zzu(i));
    }

    public static int zzf(List<Long> list) {
        int i;
        int size = list.size();
        int i2 = 0;
        if (size == 0) {
            return 0;
        }
        if (list instanceof zzcm) {
            zzcm zzcmVar = (zzcm) list;
            i = 0;
            while (i2 < size) {
                long zzf = zzcmVar.zzf(i2);
                i += zzbb.zzx((zzf >> 63) ^ (zzf + zzf));
                i2++;
            }
        } else {
            i = 0;
            while (i2 < size) {
                long longValue = list.get(i2).longValue();
                i += zzbb.zzx((longValue >> 63) ^ (longValue + longValue));
                i2++;
            }
        }
        return i;
    }

    public static int zzg(int i, List<Long> list, boolean z) {
        int size = list.size();
        if (size == 0) {
            return 0;
        }
        return zzf(list) + (size * zzbb.zzu(i));
    }

    public static int zzh(List<Integer> list) {
        int i;
        int size = list.size();
        int i2 = 0;
        if (size == 0) {
            return 0;
        }
        if (list instanceof zzbt) {
            zzbt zzbtVar = (zzbt) list;
            i = 0;
            while (i2 < size) {
                i += zzbb.zzv(zzbtVar.zzd(i2));
                i2++;
            }
        } else {
            i = 0;
            while (i2 < size) {
                i += zzbb.zzv(list.get(i2).intValue());
                i2++;
            }
        }
        return i;
    }

    public static int zzi(int i, List<Integer> list, boolean z) {
        int size = list.size();
        if (size == 0) {
            return 0;
        }
        return zzh(list) + (size * zzbb.zzu(i));
    }

    public static int zzj(List<Integer> list) {
        int i;
        int size = list.size();
        int i2 = 0;
        if (size == 0) {
            return 0;
        }
        if (list instanceof zzbt) {
            zzbt zzbtVar = (zzbt) list;
            i = 0;
            while (i2 < size) {
                i += zzbb.zzv(zzbtVar.zzd(i2));
                i2++;
            }
        } else {
            i = 0;
            while (i2 < size) {
                i += zzbb.zzv(list.get(i2).intValue());
                i2++;
            }
        }
        return i;
    }

    public static int zzk(int i, List<Integer> list, boolean z) {
        int size = list.size();
        if (size == 0) {
            return 0;
        }
        return zzj(list) + (size * zzbb.zzu(i));
    }

    public static int zzl(List<Integer> list) {
        int i;
        int size = list.size();
        int i2 = 0;
        if (size == 0) {
            return 0;
        }
        if (list instanceof zzbt) {
            zzbt zzbtVar = (zzbt) list;
            i = 0;
            while (i2 < size) {
                i += zzbb.zzw(zzbtVar.zzd(i2));
                i2++;
            }
        } else {
            i = 0;
            while (i2 < size) {
                i += zzbb.zzw(list.get(i2).intValue());
                i2++;
            }
        }
        return i;
    }

    public static int zzm(int i, List<Integer> list, boolean z) {
        int size = list.size();
        if (size == 0) {
            return 0;
        }
        return zzl(list) + (size * zzbb.zzu(i));
    }

    public static int zzn(List<Integer> list) {
        int i;
        int size = list.size();
        int i2 = 0;
        if (size == 0) {
            return 0;
        }
        if (list instanceof zzbt) {
            zzbt zzbtVar = (zzbt) list;
            i = 0;
            while (i2 < size) {
                int zzd2 = zzbtVar.zzd(i2);
                i += zzbb.zzw((zzd2 >> 31) ^ (zzd2 + zzd2));
                i2++;
            }
        } else {
            i = 0;
            while (i2 < size) {
                int intValue = list.get(i2).intValue();
                i += zzbb.zzw((intValue >> 31) ^ (intValue + intValue));
                i2++;
            }
        }
        return i;
    }

    public static int zzo(int i, List<Integer> list, boolean z) {
        int size = list.size();
        if (size == 0) {
            return 0;
        }
        return zzn(list) + (size * zzbb.zzu(i));
    }

    public static int zzp(List<?> list) {
        return list.size() * 4;
    }

    public static int zzq(int i, List<?> list, boolean z) {
        int size = list.size();
        if (size == 0) {
            return 0;
        }
        return size * (zzbb.zzw(i << 3) + 4);
    }

    public static int zzr(List<?> list) {
        return list.size() * 8;
    }

    public static int zzs(int i, List<?> list, boolean z) {
        int size = list.size();
        if (size == 0) {
            return 0;
        }
        return size * (zzbb.zzw(i << 3) + 8);
    }

    public static int zzt(List<?> list) {
        return list.size();
    }

    public static int zzu(int i, List<?> list, boolean z) {
        int size = list.size();
        if (size == 0) {
            return 0;
        }
        return size * (zzbb.zzw(i << 3) + 1);
    }

    public static int zzv(int i, List<?> list) {
        int i2;
        int i3;
        int size = list.size();
        int i4 = 0;
        if (size == 0) {
            return 0;
        }
        int zzu = zzbb.zzu(i) * size;
        if (list instanceof zzch) {
            zzch zzchVar = (zzch) list;
            while (i4 < size) {
                Object zzg = zzchVar.zzg(i4);
                if (zzg instanceof zzau) {
                    i3 = zzbb.zzA((zzau) zzg);
                } else {
                    i3 = zzbb.zzy((String) zzg);
                }
                zzu += i3;
                i4++;
            }
        } else {
            while (i4 < size) {
                Object obj = list.get(i4);
                if (obj instanceof zzau) {
                    i2 = zzbb.zzA((zzau) obj);
                } else {
                    i2 = zzbb.zzy((String) obj);
                }
                zzu += i2;
                i4++;
            }
        }
        return zzu;
    }

    public static int zzw(int i, Object obj, zzdi zzdiVar) {
        if (obj instanceof zzcf) {
            int zzw = zzbb.zzw(i << 3);
            int zza2 = ((zzcf) obj).zza();
            return zzw + zzbb.zzw(zza2) + zza2;
        }
        return zzbb.zzw(i << 3) + zzbb.zzB((zzcx) obj, zzdiVar);
    }

    public static int zzx(int i, List<?> list, zzdi zzdiVar) {
        int i2;
        int size = list.size();
        if (size == 0) {
            return 0;
        }
        int zzu = zzbb.zzu(i) * size;
        for (int i3 = 0; i3 < size; i3++) {
            Object obj = list.get(i3);
            if (obj instanceof zzcf) {
                i2 = zzbb.zzz((zzcf) obj);
            } else {
                i2 = zzbb.zzB((zzcx) obj, zzdiVar);
            }
            zzu += i2;
        }
        return zzu;
    }

    public static int zzy(int i, List<zzau> list) {
        int size = list.size();
        if (size == 0) {
            return 0;
        }
        int zzu = size * zzbb.zzu(i);
        for (int i2 = 0; i2 < list.size(); i2++) {
            zzu += zzbb.zzA(list.get(i2));
        }
        return zzu;
    }

    public static int zzz(int i, List<zzcx> list, zzdi zzdiVar) {
        int size = list.size();
        if (size != 0) {
            int i2 = 0;
            for (int i3 = 0; i3 < size; i3++) {
                i2 += zzbb.zzE(i, list.get(i3), zzdiVar);
            }
            return i2;
        }
        return 0;
    }
}
