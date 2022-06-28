package com.google.android.gms.internal.mlkit_common;

import java.io.IOException;
import java.util.List;
/* compiled from: com.google.mlkit:common@@17.0.0 */
/* loaded from: classes3.dex */
final class zzht {
    private static final Class<?> zza = zzd();
    private static final zzij<?, ?> zzb = zza(false);
    private static final zzij<?, ?> zzc = zza(true);
    private static final zzij<?, ?> zzd = new zzil();

    public static void zza(Class<?> cls) {
        Class<?> cls2;
        if (!zzfq.class.isAssignableFrom(cls) && (cls2 = zza) != null && !cls2.isAssignableFrom(cls)) {
            throw new IllegalArgumentException("Message classes must extend GeneratedMessage or GeneratedMessageLite");
        }
    }

    public static void zza(int i, List<Double> list, zzjd zzjdVar, boolean z) throws IOException {
        if (list != null && !list.isEmpty()) {
            zzjdVar.zzg(i, list, z);
        }
    }

    public static void zzb(int i, List<Float> list, zzjd zzjdVar, boolean z) throws IOException {
        if (list != null && !list.isEmpty()) {
            zzjdVar.zzf(i, list, z);
        }
    }

    public static void zzc(int i, List<Long> list, zzjd zzjdVar, boolean z) throws IOException {
        if (list != null && !list.isEmpty()) {
            zzjdVar.zzc(i, list, z);
        }
    }

    public static void zzd(int i, List<Long> list, zzjd zzjdVar, boolean z) throws IOException {
        if (list != null && !list.isEmpty()) {
            zzjdVar.zzd(i, list, z);
        }
    }

    public static void zze(int i, List<Long> list, zzjd zzjdVar, boolean z) throws IOException {
        if (list != null && !list.isEmpty()) {
            zzjdVar.zzn(i, list, z);
        }
    }

    public static void zzf(int i, List<Long> list, zzjd zzjdVar, boolean z) throws IOException {
        if (list != null && !list.isEmpty()) {
            zzjdVar.zze(i, list, z);
        }
    }

    public static void zzg(int i, List<Long> list, zzjd zzjdVar, boolean z) throws IOException {
        if (list != null && !list.isEmpty()) {
            zzjdVar.zzl(i, list, z);
        }
    }

    public static void zzh(int i, List<Integer> list, zzjd zzjdVar, boolean z) throws IOException {
        if (list != null && !list.isEmpty()) {
            zzjdVar.zza(i, list, z);
        }
    }

    public static void zzi(int i, List<Integer> list, zzjd zzjdVar, boolean z) throws IOException {
        if (list != null && !list.isEmpty()) {
            zzjdVar.zzj(i, list, z);
        }
    }

    public static void zzj(int i, List<Integer> list, zzjd zzjdVar, boolean z) throws IOException {
        if (list != null && !list.isEmpty()) {
            zzjdVar.zzm(i, list, z);
        }
    }

    public static void zzk(int i, List<Integer> list, zzjd zzjdVar, boolean z) throws IOException {
        if (list != null && !list.isEmpty()) {
            zzjdVar.zzb(i, list, z);
        }
    }

    public static void zzl(int i, List<Integer> list, zzjd zzjdVar, boolean z) throws IOException {
        if (list != null && !list.isEmpty()) {
            zzjdVar.zzk(i, list, z);
        }
    }

    public static void zzm(int i, List<Integer> list, zzjd zzjdVar, boolean z) throws IOException {
        if (list != null && !list.isEmpty()) {
            zzjdVar.zzh(i, list, z);
        }
    }

    public static void zzn(int i, List<Boolean> list, zzjd zzjdVar, boolean z) throws IOException {
        if (list != null && !list.isEmpty()) {
            zzjdVar.zzi(i, list, z);
        }
    }

    public static void zza(int i, List<String> list, zzjd zzjdVar) throws IOException {
        if (list != null && !list.isEmpty()) {
            zzjdVar.zza(i, list);
        }
    }

    public static void zzb(int i, List<zzep> list, zzjd zzjdVar) throws IOException {
        if (list != null && !list.isEmpty()) {
            zzjdVar.zzb(i, list);
        }
    }

    public static void zza(int i, List<?> list, zzjd zzjdVar, zzhr zzhrVar) throws IOException {
        if (list != null && !list.isEmpty()) {
            zzjdVar.zza(i, list, zzhrVar);
        }
    }

    public static void zzb(int i, List<?> list, zzjd zzjdVar, zzhr zzhrVar) throws IOException {
        if (list != null && !list.isEmpty()) {
            zzjdVar.zzb(i, list, zzhrVar);
        }
    }

    public static int zza(List<Long> list) {
        int i;
        int size = list.size();
        int i2 = 0;
        if (size == 0) {
            return 0;
        }
        if (list instanceof zzgp) {
            zzgp zzgpVar = (zzgp) list;
            i = 0;
            while (i2 < size) {
                i += zzfc.zzd(zzgpVar.zza(i2));
                i2++;
            }
        } else {
            i = 0;
            while (i2 < size) {
                i += zzfc.zzd(list.get(i2).longValue());
                i2++;
            }
        }
        return i;
    }

    public static int zza(int i, List<Long> list, boolean z) {
        if (list.size() == 0) {
            return 0;
        }
        return zza(list) + (list.size() * zzfc.zze(i));
    }

    public static int zzb(List<Long> list) {
        int i;
        int size = list.size();
        int i2 = 0;
        if (size == 0) {
            return 0;
        }
        if (list instanceof zzgp) {
            zzgp zzgpVar = (zzgp) list;
            i = 0;
            while (i2 < size) {
                i += zzfc.zze(zzgpVar.zza(i2));
                i2++;
            }
        } else {
            i = 0;
            while (i2 < size) {
                i += zzfc.zze(list.get(i2).longValue());
                i2++;
            }
        }
        return i;
    }

    public static int zzb(int i, List<Long> list, boolean z) {
        int size = list.size();
        if (size == 0) {
            return 0;
        }
        return zzb(list) + (size * zzfc.zze(i));
    }

    public static int zzc(List<Long> list) {
        int i;
        int size = list.size();
        int i2 = 0;
        if (size == 0) {
            return 0;
        }
        if (list instanceof zzgp) {
            zzgp zzgpVar = (zzgp) list;
            i = 0;
            while (i2 < size) {
                i += zzfc.zzf(zzgpVar.zza(i2));
                i2++;
            }
        } else {
            i = 0;
            while (i2 < size) {
                i += zzfc.zzf(list.get(i2).longValue());
                i2++;
            }
        }
        return i;
    }

    public static int zzc(int i, List<Long> list, boolean z) {
        int size = list.size();
        if (size == 0) {
            return 0;
        }
        return zzc(list) + (size * zzfc.zze(i));
    }

    public static int zzd(List<Integer> list) {
        int i;
        int size = list.size();
        int i2 = 0;
        if (size == 0) {
            return 0;
        }
        if (list instanceof zzft) {
            zzft zzftVar = (zzft) list;
            i = 0;
            while (i2 < size) {
                i += zzfc.zzk(zzftVar.zza(i2));
                i2++;
            }
        } else {
            i = 0;
            while (i2 < size) {
                i += zzfc.zzk(list.get(i2).intValue());
                i2++;
            }
        }
        return i;
    }

    public static int zzd(int i, List<Integer> list, boolean z) {
        int size = list.size();
        if (size == 0) {
            return 0;
        }
        return zzd(list) + (size * zzfc.zze(i));
    }

    public static int zze(List<Integer> list) {
        int i;
        int size = list.size();
        int i2 = 0;
        if (size == 0) {
            return 0;
        }
        if (list instanceof zzft) {
            zzft zzftVar = (zzft) list;
            i = 0;
            while (i2 < size) {
                i += zzfc.zzf(zzftVar.zza(i2));
                i2++;
            }
        } else {
            i = 0;
            while (i2 < size) {
                i += zzfc.zzf(list.get(i2).intValue());
                i2++;
            }
        }
        return i;
    }

    public static int zze(int i, List<Integer> list, boolean z) {
        int size = list.size();
        if (size == 0) {
            return 0;
        }
        return zze(list) + (size * zzfc.zze(i));
    }

    public static int zzf(List<Integer> list) {
        int i;
        int size = list.size();
        int i2 = 0;
        if (size == 0) {
            return 0;
        }
        if (list instanceof zzft) {
            zzft zzftVar = (zzft) list;
            i = 0;
            while (i2 < size) {
                i += zzfc.zzg(zzftVar.zza(i2));
                i2++;
            }
        } else {
            i = 0;
            while (i2 < size) {
                i += zzfc.zzg(list.get(i2).intValue());
                i2++;
            }
        }
        return i;
    }

    public static int zzf(int i, List<Integer> list, boolean z) {
        int size = list.size();
        if (size == 0) {
            return 0;
        }
        return zzf(list) + (size * zzfc.zze(i));
    }

    public static int zzg(List<Integer> list) {
        int i;
        int size = list.size();
        int i2 = 0;
        if (size == 0) {
            return 0;
        }
        if (list instanceof zzft) {
            zzft zzftVar = (zzft) list;
            i = 0;
            while (i2 < size) {
                i += zzfc.zzh(zzftVar.zza(i2));
                i2++;
            }
        } else {
            i = 0;
            while (i2 < size) {
                i += zzfc.zzh(list.get(i2).intValue());
                i2++;
            }
        }
        return i;
    }

    public static int zzg(int i, List<Integer> list, boolean z) {
        int size = list.size();
        if (size == 0) {
            return 0;
        }
        return zzg(list) + (size * zzfc.zze(i));
    }

    public static int zzh(List<?> list) {
        return list.size() << 2;
    }

    public static int zzh(int i, List<?> list, boolean z) {
        int size = list.size();
        if (size == 0) {
            return 0;
        }
        return size * zzfc.zzi(i, 0);
    }

    public static int zzi(List<?> list) {
        return list.size() << 3;
    }

    public static int zzi(int i, List<?> list, boolean z) {
        int size = list.size();
        if (size == 0) {
            return 0;
        }
        return size * zzfc.zzg(i, 0L);
    }

    public static int zzj(List<?> list) {
        return list.size();
    }

    public static int zzj(int i, List<?> list, boolean z) {
        int size = list.size();
        if (size == 0) {
            return 0;
        }
        return size * zzfc.zzb(i, true);
    }

    public static int zza(int i, List<?> list) {
        int i2;
        int i3;
        int size = list.size();
        int i4 = 0;
        if (size == 0) {
            return 0;
        }
        int zze = zzfc.zze(i) * size;
        if (list instanceof zzgi) {
            zzgi zzgiVar = (zzgi) list;
            while (i4 < size) {
                Object zza2 = zzgiVar.zza(i4);
                if (zza2 instanceof zzep) {
                    i3 = zzfc.zzb((zzep) zza2);
                } else {
                    i3 = zzfc.zzb((String) zza2);
                }
                zze += i3;
                i4++;
            }
        } else {
            while (i4 < size) {
                Object obj = list.get(i4);
                if (obj instanceof zzep) {
                    i2 = zzfc.zzb((zzep) obj);
                } else {
                    i2 = zzfc.zzb((String) obj);
                }
                zze += i2;
                i4++;
            }
        }
        return zze;
    }

    public static int zza(int i, Object obj, zzhr zzhrVar) {
        if (obj instanceof zzgg) {
            return zzfc.zza(i, (zzgg) obj);
        }
        return zzfc.zzb(i, (zzhb) obj, zzhrVar);
    }

    public static int zza(int i, List<?> list, zzhr zzhrVar) {
        int i2;
        int size = list.size();
        if (size == 0) {
            return 0;
        }
        int zze = zzfc.zze(i) * size;
        for (int i3 = 0; i3 < size; i3++) {
            Object obj = list.get(i3);
            if (obj instanceof zzgg) {
                i2 = zzfc.zza((zzgg) obj);
            } else {
                i2 = zzfc.zza((zzhb) obj, zzhrVar);
            }
            zze += i2;
        }
        return zze;
    }

    public static int zzb(int i, List<zzep> list) {
        int size = list.size();
        if (size == 0) {
            return 0;
        }
        int zze = size * zzfc.zze(i);
        for (int i2 = 0; i2 < list.size(); i2++) {
            zze += zzfc.zzb(list.get(i2));
        }
        return zze;
    }

    public static int zzb(int i, List<zzhb> list, zzhr zzhrVar) {
        int size = list.size();
        if (size == 0) {
            return 0;
        }
        int i2 = 0;
        for (int i3 = 0; i3 < size; i3++) {
            i2 += zzfc.zzc(i, list.get(i3), zzhrVar);
        }
        return i2;
    }

    public static zzij<?, ?> zza() {
        return zzb;
    }

    public static zzij<?, ?> zzb() {
        return zzc;
    }

    public static zzij<?, ?> zzc() {
        return zzd;
    }

    private static zzij<?, ?> zza(boolean z) {
        try {
            Class<?> zze = zze();
            if (zze != null) {
                return (zzij) zze.getConstructor(Boolean.TYPE).newInstance(Boolean.valueOf(z));
            }
            return null;
        } catch (Throwable th) {
            return null;
        }
    }

    private static Class<?> zzd() {
        try {
            return Class.forName("com.google.protobuf.GeneratedMessage");
        } catch (Throwable th) {
            return null;
        }
    }

    private static Class<?> zze() {
        try {
            return Class.forName("com.google.protobuf.UnknownFieldSetSchema");
        } catch (Throwable th) {
            return null;
        }
    }

    public static boolean zza(Object obj, Object obj2) {
        if (obj != obj2) {
            return obj != null && obj.equals(obj2);
        }
        return true;
    }

    public static <T> void zza(zzgu zzguVar, T t, T t2, long j) {
        zzip.zza(t, j, zzguVar.zza(zzip.zzf(t, j), zzip.zzf(t2, j)));
    }

    public static <T, FT extends zzfn<FT>> void zza(zzfg<FT> zzfgVar, T t, T t2) {
        zzfl<FT> zza2 = zzfgVar.zza(t2);
        if (!zza2.zza.isEmpty()) {
            zzfgVar.zzb(t).zza(zza2);
        }
    }

    public static <T, UT, UB> void zza(zzij<UT, UB> zzijVar, T t, T t2) {
        zzijVar.zza(t, zzijVar.zzb(zzijVar.zza(t), zzijVar.zza(t2)));
    }
}
