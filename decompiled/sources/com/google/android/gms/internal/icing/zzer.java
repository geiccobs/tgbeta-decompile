package com.google.android.gms.internal.icing;

import java.io.IOException;
import java.util.List;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes3.dex */
final class zzer {
    private static final Class<?> zza;
    private static final zzfd<?, ?> zzb;
    private static final zzfd<?, ?> zzc;
    private static final zzfd<?, ?> zzd;

    static {
        Class<?> cls;
        try {
            cls = Class.forName("com.google.protobuf.GeneratedMessage");
        } catch (Throwable th) {
            cls = null;
        }
        zza = cls;
        zzb = zzZ(false);
        zzc = zzZ(true);
        zzd = new zzff();
    }

    public static zzfd<?, ?> zzA() {
        return zzb;
    }

    public static zzfd<?, ?> zzB() {
        return zzc;
    }

    public static zzfd<?, ?> zzC() {
        return zzd;
    }

    public static boolean zzD(Object obj, Object obj2) {
        return obj == obj2 || (obj != null && obj.equals(obj2));
    }

    public static <T, FT extends zzct<FT>> void zzE(zzcq<FT> zzcqVar, T t, T t2) {
        zzcqVar.zzb(t2);
        throw null;
    }

    public static <T, UT, UB> void zzF(zzfd<UT, UB> zzfdVar, T t, T t2) {
        zzfdVar.zza(t, zzfdVar.zzd(zzfdVar.zzb(t), zzfdVar.zzb(t2)));
    }

    public static <T> void zzG(zzdz zzdzVar, T t, T t2, long j) {
        zzdy zzdyVar = (zzdy) zzfn.zzn(t, j);
        zzdy zzdyVar2 = (zzdy) zzfn.zzn(t2, j);
        if (!zzdyVar2.isEmpty()) {
            if (!zzdyVar.zzd()) {
                zzdyVar = zzdyVar.zzb();
            }
            zzdyVar.zza(zzdyVar2);
        }
        zzfn.zzo(t, j, zzdyVar);
    }

    public static void zzH(int i, List<Double> list, zzcn zzcnVar, boolean z) throws IOException {
        if (list == null || list.isEmpty()) {
            return;
        }
        zzcnVar.zzz(i, list, z);
    }

    public static void zzI(int i, List<Float> list, zzcn zzcnVar, boolean z) throws IOException {
        if (list == null || list.isEmpty()) {
            return;
        }
        zzcnVar.zzy(i, list, z);
    }

    public static void zzJ(int i, List<Long> list, zzcn zzcnVar, boolean z) throws IOException {
        if (list == null || list.isEmpty()) {
            return;
        }
        zzcnVar.zzv(i, list, z);
    }

    public static void zzK(int i, List<Long> list, zzcn zzcnVar, boolean z) throws IOException {
        if (list == null || list.isEmpty()) {
            return;
        }
        zzcnVar.zzw(i, list, z);
    }

    public static void zzL(int i, List<Long> list, zzcn zzcnVar, boolean z) throws IOException {
        if (list == null || list.isEmpty()) {
            return;
        }
        zzcnVar.zzI(i, list, z);
    }

    public static void zzM(int i, List<Long> list, zzcn zzcnVar, boolean z) throws IOException {
        if (list == null || list.isEmpty()) {
            return;
        }
        zzcnVar.zzx(i, list, z);
    }

    public static void zzN(int i, List<Long> list, zzcn zzcnVar, boolean z) throws IOException {
        if (list == null || list.isEmpty()) {
            return;
        }
        zzcnVar.zzG(i, list, z);
    }

    public static void zzO(int i, List<Integer> list, zzcn zzcnVar, boolean z) throws IOException {
        if (list == null || list.isEmpty()) {
            return;
        }
        zzcnVar.zzt(i, list, z);
    }

    public static void zzP(int i, List<Integer> list, zzcn zzcnVar, boolean z) throws IOException {
        if (list == null || list.isEmpty()) {
            return;
        }
        zzcnVar.zzE(i, list, z);
    }

    public static void zzQ(int i, List<Integer> list, zzcn zzcnVar, boolean z) throws IOException {
        if (list == null || list.isEmpty()) {
            return;
        }
        zzcnVar.zzH(i, list, z);
    }

    public static void zzR(int i, List<Integer> list, zzcn zzcnVar, boolean z) throws IOException {
        if (list == null || list.isEmpty()) {
            return;
        }
        zzcnVar.zzu(i, list, z);
    }

    public static void zzS(int i, List<Integer> list, zzcn zzcnVar, boolean z) throws IOException {
        if (list == null || list.isEmpty()) {
            return;
        }
        zzcnVar.zzF(i, list, z);
    }

    public static void zzT(int i, List<Integer> list, zzcn zzcnVar, boolean z) throws IOException {
        if (list == null || list.isEmpty()) {
            return;
        }
        zzcnVar.zzA(i, list, z);
    }

    public static void zzU(int i, List<Boolean> list, zzcn zzcnVar, boolean z) throws IOException {
        if (list == null || list.isEmpty()) {
            return;
        }
        zzcnVar.zzB(i, list, z);
    }

    public static void zzV(int i, List<String> list, zzcn zzcnVar) throws IOException {
        if (list == null || list.isEmpty()) {
            return;
        }
        zzcnVar.zzC(i, list);
    }

    public static void zzW(int i, List<zzcf> list, zzcn zzcnVar) throws IOException {
        if (list == null || list.isEmpty()) {
            return;
        }
        zzcnVar.zzD(i, list);
    }

    public static void zzX(int i, List<?> list, zzcn zzcnVar, zzep zzepVar) throws IOException {
        if (list == null || list.isEmpty()) {
            return;
        }
        for (int i2 = 0; i2 < list.size(); i2++) {
            zzcnVar.zzr(i, list.get(i2), zzepVar);
        }
    }

    public static void zzY(int i, List<?> list, zzcn zzcnVar, zzep zzepVar) throws IOException {
        if (list == null || list.isEmpty()) {
            return;
        }
        for (int i2 = 0; i2 < list.size(); i2++) {
            zzcnVar.zzs(i, list.get(i2), zzepVar);
        }
    }

    private static zzfd<?, ?> zzZ(boolean z) {
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
            return (zzfd) cls.getConstructor(Boolean.TYPE).newInstance(Boolean.valueOf(z));
        } catch (Throwable th2) {
            return null;
        }
    }

    public static void zza(Class<?> cls) {
        Class<?> cls2;
        if (zzda.class.isAssignableFrom(cls) || (cls2 = zza) == null || cls2.isAssignableFrom(cls)) {
            return;
        }
        throw new IllegalArgumentException("Message classes must extend GeneratedMessage or GeneratedMessageLite");
    }

    public static int zzb(List<Long> list) {
        int i;
        int size = list.size();
        int i2 = 0;
        if (size == 0) {
            return 0;
        }
        if (list instanceof zzdt) {
            zzdt zzdtVar = (zzdt) list;
            i = 0;
            while (i2 < size) {
                i += zzcm.zzx(zzdtVar.zzf(i2));
                i2++;
            }
        } else {
            i = 0;
            while (i2 < size) {
                i += zzcm.zzx(list.get(i2).longValue());
                i2++;
            }
        }
        return i;
    }

    public static int zzc(int i, List<Long> list, boolean z) {
        if (list.size() == 0) {
            return 0;
        }
        return zzb(list) + (list.size() * zzcm.zzu(i));
    }

    public static int zzd(List<Long> list) {
        int i;
        int size = list.size();
        int i2 = 0;
        if (size == 0) {
            return 0;
        }
        if (list instanceof zzdt) {
            zzdt zzdtVar = (zzdt) list;
            i = 0;
            while (i2 < size) {
                i += zzcm.zzx(zzdtVar.zzf(i2));
                i2++;
            }
        } else {
            i = 0;
            while (i2 < size) {
                i += zzcm.zzx(list.get(i2).longValue());
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
        return zzd(list) + (size * zzcm.zzu(i));
    }

    public static int zzf(List<Long> list) {
        int i;
        int size = list.size();
        int i2 = 0;
        if (size == 0) {
            return 0;
        }
        if (list instanceof zzdt) {
            zzdt zzdtVar = (zzdt) list;
            i = 0;
            while (i2 < size) {
                long zzf = zzdtVar.zzf(i2);
                i += zzcm.zzx((zzf >> 63) ^ (zzf + zzf));
                i2++;
            }
        } else {
            i = 0;
            while (i2 < size) {
                long longValue = list.get(i2).longValue();
                i += zzcm.zzx((longValue >> 63) ^ (longValue + longValue));
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
        return zzf(list) + (size * zzcm.zzu(i));
    }

    public static int zzh(List<Integer> list) {
        int i;
        int size = list.size();
        int i2 = 0;
        if (size == 0) {
            return 0;
        }
        if (list instanceof zzdb) {
            zzdb zzdbVar = (zzdb) list;
            i = 0;
            while (i2 < size) {
                i += zzcm.zzv(zzdbVar.zzd(i2));
                i2++;
            }
        } else {
            i = 0;
            while (i2 < size) {
                i += zzcm.zzv(list.get(i2).intValue());
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
        return zzh(list) + (size * zzcm.zzu(i));
    }

    public static int zzj(List<Integer> list) {
        int i;
        int size = list.size();
        int i2 = 0;
        if (size == 0) {
            return 0;
        }
        if (list instanceof zzdb) {
            zzdb zzdbVar = (zzdb) list;
            i = 0;
            while (i2 < size) {
                i += zzcm.zzv(zzdbVar.zzd(i2));
                i2++;
            }
        } else {
            i = 0;
            while (i2 < size) {
                i += zzcm.zzv(list.get(i2).intValue());
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
        return zzj(list) + (size * zzcm.zzu(i));
    }

    public static int zzl(List<Integer> list) {
        int i;
        int size = list.size();
        int i2 = 0;
        if (size == 0) {
            return 0;
        }
        if (list instanceof zzdb) {
            zzdb zzdbVar = (zzdb) list;
            i = 0;
            while (i2 < size) {
                i += zzcm.zzw(zzdbVar.zzd(i2));
                i2++;
            }
        } else {
            i = 0;
            while (i2 < size) {
                i += zzcm.zzw(list.get(i2).intValue());
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
        return zzl(list) + (size * zzcm.zzu(i));
    }

    public static int zzn(List<Integer> list) {
        int i;
        int size = list.size();
        int i2 = 0;
        if (size == 0) {
            return 0;
        }
        if (list instanceof zzdb) {
            zzdb zzdbVar = (zzdb) list;
            i = 0;
            while (i2 < size) {
                int zzd2 = zzdbVar.zzd(i2);
                i += zzcm.zzw((zzd2 >> 31) ^ (zzd2 + zzd2));
                i2++;
            }
        } else {
            i = 0;
            while (i2 < size) {
                int intValue = list.get(i2).intValue();
                i += zzcm.zzw((intValue >> 31) ^ (intValue + intValue));
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
        return zzn(list) + (size * zzcm.zzu(i));
    }

    public static int zzp(List<?> list) {
        return list.size() * 4;
    }

    public static int zzq(int i, List<?> list, boolean z) {
        int size = list.size();
        if (size == 0) {
            return 0;
        }
        return size * (zzcm.zzw(i << 3) + 4);
    }

    public static int zzr(List<?> list) {
        return list.size() * 8;
    }

    public static int zzs(int i, List<?> list, boolean z) {
        int size = list.size();
        if (size == 0) {
            return 0;
        }
        return size * (zzcm.zzw(i << 3) + 8);
    }

    public static int zzt(List<?> list) {
        return list.size();
    }

    public static int zzu(int i, List<?> list, boolean z) {
        int size = list.size();
        if (size == 0) {
            return 0;
        }
        return size * (zzcm.zzw(i << 3) + 1);
    }

    public static int zzv(int i, List<?> list) {
        int i2;
        int i3;
        int size = list.size();
        int i4 = 0;
        if (size == 0) {
            return 0;
        }
        int zzu = zzcm.zzu(i) * size;
        if (list instanceof zzdo) {
            zzdo zzdoVar = (zzdo) list;
            while (i4 < size) {
                Object zzg = zzdoVar.zzg(i4);
                if (zzg instanceof zzcf) {
                    i3 = zzcm.zzA((zzcf) zzg);
                } else {
                    i3 = zzcm.zzy((String) zzg);
                }
                zzu += i3;
                i4++;
            }
        } else {
            while (i4 < size) {
                Object obj = list.get(i4);
                if (obj instanceof zzcf) {
                    i2 = zzcm.zzA((zzcf) obj);
                } else {
                    i2 = zzcm.zzy((String) obj);
                }
                zzu += i2;
                i4++;
            }
        }
        return zzu;
    }

    public static int zzw(int i, Object obj, zzep zzepVar) {
        if (obj instanceof zzdm) {
            int zzw = zzcm.zzw(i << 3);
            int zza2 = ((zzdm) obj).zza();
            return zzw + zzcm.zzw(zza2) + zza2;
        }
        return zzcm.zzw(i << 3) + zzcm.zzB((zzee) obj, zzepVar);
    }

    public static int zzx(int i, List<?> list, zzep zzepVar) {
        int i2;
        int size = list.size();
        if (size == 0) {
            return 0;
        }
        int zzu = zzcm.zzu(i) * size;
        for (int i3 = 0; i3 < size; i3++) {
            Object obj = list.get(i3);
            if (obj instanceof zzdm) {
                i2 = zzcm.zzz((zzdm) obj);
            } else {
                i2 = zzcm.zzB((zzee) obj, zzepVar);
            }
            zzu += i2;
        }
        return zzu;
    }

    public static int zzy(int i, List<zzcf> list) {
        int size = list.size();
        if (size == 0) {
            return 0;
        }
        int zzu = size * zzcm.zzu(i);
        for (int i2 = 0; i2 < list.size(); i2++) {
            zzu += zzcm.zzA(list.get(i2));
        }
        return zzu;
    }

    public static int zzz(int i, List<zzee> list, zzep zzepVar) {
        int size = list.size();
        if (size != 0) {
            int i2 = 0;
            for (int i3 = 0; i3 < size; i3++) {
                i2 += zzcm.zzE(i, list.get(i3), zzepVar);
            }
            return i2;
        }
        return 0;
    }
}
