package com.google.android.gms.internal.mlkit_language_id;

import java.io.IOException;
import java.util.List;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes3.dex */
final class zzgr {
    private static final Class<?> zza = zzd();
    private static final zzhh<?, ?> zzb = zza(false);
    private static final zzhh<?, ?> zzc = zza(true);
    private static final zzhh<?, ?> zzd = new zzhj();

    public static void zza(Class<?> cls) {
        Class<?> cls2;
        if (!zzeo.class.isAssignableFrom(cls) && (cls2 = zza) != null && !cls2.isAssignableFrom(cls)) {
            throw new IllegalArgumentException("Message classes must extend GeneratedMessage or GeneratedMessageLite");
        }
    }

    public static void zza(int i, List<Double> list, zzib zzibVar, boolean z) throws IOException {
        if (list != null && !list.isEmpty()) {
            zzibVar.zzg(i, list, z);
        }
    }

    public static void zzb(int i, List<Float> list, zzib zzibVar, boolean z) throws IOException {
        if (list != null && !list.isEmpty()) {
            zzibVar.zzf(i, list, z);
        }
    }

    public static void zzc(int i, List<Long> list, zzib zzibVar, boolean z) throws IOException {
        if (list != null && !list.isEmpty()) {
            zzibVar.zzc(i, list, z);
        }
    }

    public static void zzd(int i, List<Long> list, zzib zzibVar, boolean z) throws IOException {
        if (list != null && !list.isEmpty()) {
            zzibVar.zzd(i, list, z);
        }
    }

    public static void zze(int i, List<Long> list, zzib zzibVar, boolean z) throws IOException {
        if (list != null && !list.isEmpty()) {
            zzibVar.zzn(i, list, z);
        }
    }

    public static void zzf(int i, List<Long> list, zzib zzibVar, boolean z) throws IOException {
        if (list != null && !list.isEmpty()) {
            zzibVar.zze(i, list, z);
        }
    }

    public static void zzg(int i, List<Long> list, zzib zzibVar, boolean z) throws IOException {
        if (list != null && !list.isEmpty()) {
            zzibVar.zzl(i, list, z);
        }
    }

    public static void zzh(int i, List<Integer> list, zzib zzibVar, boolean z) throws IOException {
        if (list != null && !list.isEmpty()) {
            zzibVar.zza(i, list, z);
        }
    }

    public static void zzi(int i, List<Integer> list, zzib zzibVar, boolean z) throws IOException {
        if (list != null && !list.isEmpty()) {
            zzibVar.zzj(i, list, z);
        }
    }

    public static void zzj(int i, List<Integer> list, zzib zzibVar, boolean z) throws IOException {
        if (list != null && !list.isEmpty()) {
            zzibVar.zzm(i, list, z);
        }
    }

    public static void zzk(int i, List<Integer> list, zzib zzibVar, boolean z) throws IOException {
        if (list != null && !list.isEmpty()) {
            zzibVar.zzb(i, list, z);
        }
    }

    public static void zzl(int i, List<Integer> list, zzib zzibVar, boolean z) throws IOException {
        if (list != null && !list.isEmpty()) {
            zzibVar.zzk(i, list, z);
        }
    }

    public static void zzm(int i, List<Integer> list, zzib zzibVar, boolean z) throws IOException {
        if (list != null && !list.isEmpty()) {
            zzibVar.zzh(i, list, z);
        }
    }

    public static void zzn(int i, List<Boolean> list, zzib zzibVar, boolean z) throws IOException {
        if (list != null && !list.isEmpty()) {
            zzibVar.zzi(i, list, z);
        }
    }

    public static void zza(int i, List<String> list, zzib zzibVar) throws IOException {
        if (list != null && !list.isEmpty()) {
            zzibVar.zza(i, list);
        }
    }

    public static void zzb(int i, List<zzdn> list, zzib zzibVar) throws IOException {
        if (list != null && !list.isEmpty()) {
            zzibVar.zzb(i, list);
        }
    }

    public static void zza(int i, List<?> list, zzib zzibVar, zzgp zzgpVar) throws IOException {
        if (list != null && !list.isEmpty()) {
            zzibVar.zza(i, list, zzgpVar);
        }
    }

    public static void zzb(int i, List<?> list, zzib zzibVar, zzgp zzgpVar) throws IOException {
        if (list != null && !list.isEmpty()) {
            zzibVar.zzb(i, list, zzgpVar);
        }
    }

    public static int zza(List<Long> list) {
        int i;
        int size = list.size();
        int i2 = 0;
        if (size == 0) {
            return 0;
        }
        if (list instanceof zzfn) {
            zzfn zzfnVar = (zzfn) list;
            i = 0;
            while (i2 < size) {
                i += zzea.zzd(zzfnVar.zza(i2));
                i2++;
            }
        } else {
            i = 0;
            while (i2 < size) {
                i += zzea.zzd(list.get(i2).longValue());
                i2++;
            }
        }
        return i;
    }

    public static int zza(int i, List<Long> list, boolean z) {
        if (list.size() == 0) {
            return 0;
        }
        return zza(list) + (list.size() * zzea.zze(i));
    }

    public static int zzb(List<Long> list) {
        int i;
        int size = list.size();
        int i2 = 0;
        if (size == 0) {
            return 0;
        }
        if (list instanceof zzfn) {
            zzfn zzfnVar = (zzfn) list;
            i = 0;
            while (i2 < size) {
                i += zzea.zze(zzfnVar.zza(i2));
                i2++;
            }
        } else {
            i = 0;
            while (i2 < size) {
                i += zzea.zze(list.get(i2).longValue());
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
        return zzb(list) + (size * zzea.zze(i));
    }

    public static int zzc(List<Long> list) {
        int i;
        int size = list.size();
        int i2 = 0;
        if (size == 0) {
            return 0;
        }
        if (list instanceof zzfn) {
            zzfn zzfnVar = (zzfn) list;
            i = 0;
            while (i2 < size) {
                i += zzea.zzf(zzfnVar.zza(i2));
                i2++;
            }
        } else {
            i = 0;
            while (i2 < size) {
                i += zzea.zzf(list.get(i2).longValue());
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
        return zzc(list) + (size * zzea.zze(i));
    }

    public static int zzd(List<Integer> list) {
        int i;
        int size = list.size();
        int i2 = 0;
        if (size == 0) {
            return 0;
        }
        if (list instanceof zzer) {
            zzer zzerVar = (zzer) list;
            i = 0;
            while (i2 < size) {
                i += zzea.zzk(zzerVar.zza(i2));
                i2++;
            }
        } else {
            i = 0;
            while (i2 < size) {
                i += zzea.zzk(list.get(i2).intValue());
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
        return zzd(list) + (size * zzea.zze(i));
    }

    public static int zze(List<Integer> list) {
        int i;
        int size = list.size();
        int i2 = 0;
        if (size == 0) {
            return 0;
        }
        if (list instanceof zzer) {
            zzer zzerVar = (zzer) list;
            i = 0;
            while (i2 < size) {
                i += zzea.zzf(zzerVar.zza(i2));
                i2++;
            }
        } else {
            i = 0;
            while (i2 < size) {
                i += zzea.zzf(list.get(i2).intValue());
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
        return zze(list) + (size * zzea.zze(i));
    }

    public static int zzf(List<Integer> list) {
        int i;
        int size = list.size();
        int i2 = 0;
        if (size == 0) {
            return 0;
        }
        if (list instanceof zzer) {
            zzer zzerVar = (zzer) list;
            i = 0;
            while (i2 < size) {
                i += zzea.zzg(zzerVar.zza(i2));
                i2++;
            }
        } else {
            i = 0;
            while (i2 < size) {
                i += zzea.zzg(list.get(i2).intValue());
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
        return zzf(list) + (size * zzea.zze(i));
    }

    public static int zzg(List<Integer> list) {
        int i;
        int size = list.size();
        int i2 = 0;
        if (size == 0) {
            return 0;
        }
        if (list instanceof zzer) {
            zzer zzerVar = (zzer) list;
            i = 0;
            while (i2 < size) {
                i += zzea.zzh(zzerVar.zza(i2));
                i2++;
            }
        } else {
            i = 0;
            while (i2 < size) {
                i += zzea.zzh(list.get(i2).intValue());
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
        return zzg(list) + (size * zzea.zze(i));
    }

    public static int zzh(List<?> list) {
        return list.size() << 2;
    }

    public static int zzh(int i, List<?> list, boolean z) {
        int size = list.size();
        if (size == 0) {
            return 0;
        }
        return size * zzea.zzi(i, 0);
    }

    public static int zzi(List<?> list) {
        return list.size() << 3;
    }

    public static int zzi(int i, List<?> list, boolean z) {
        int size = list.size();
        if (size == 0) {
            return 0;
        }
        return size * zzea.zzg(i, 0L);
    }

    public static int zzj(List<?> list) {
        return list.size();
    }

    public static int zzj(int i, List<?> list, boolean z) {
        int size = list.size();
        if (size == 0) {
            return 0;
        }
        return size * zzea.zzb(i, true);
    }

    public static int zza(int i, List<?> list) {
        int i2;
        int i3;
        int size = list.size();
        int i4 = 0;
        if (size == 0) {
            return 0;
        }
        int zze = zzea.zze(i) * size;
        if (list instanceof zzfg) {
            zzfg zzfgVar = (zzfg) list;
            while (i4 < size) {
                Object zza2 = zzfgVar.zza(i4);
                if (zza2 instanceof zzdn) {
                    i3 = zzea.zzb((zzdn) zza2);
                } else {
                    i3 = zzea.zzb((String) zza2);
                }
                zze += i3;
                i4++;
            }
        } else {
            while (i4 < size) {
                Object obj = list.get(i4);
                if (obj instanceof zzdn) {
                    i2 = zzea.zzb((zzdn) obj);
                } else {
                    i2 = zzea.zzb((String) obj);
                }
                zze += i2;
                i4++;
            }
        }
        return zze;
    }

    public static int zza(int i, Object obj, zzgp zzgpVar) {
        if (obj instanceof zzfe) {
            return zzea.zza(i, (zzfe) obj);
        }
        return zzea.zzb(i, (zzfz) obj, zzgpVar);
    }

    public static int zza(int i, List<?> list, zzgp zzgpVar) {
        int i2;
        int size = list.size();
        if (size == 0) {
            return 0;
        }
        int zze = zzea.zze(i) * size;
        for (int i3 = 0; i3 < size; i3++) {
            Object obj = list.get(i3);
            if (obj instanceof zzfe) {
                i2 = zzea.zza((zzfe) obj);
            } else {
                i2 = zzea.zza((zzfz) obj, zzgpVar);
            }
            zze += i2;
        }
        return zze;
    }

    public static int zzb(int i, List<zzdn> list) {
        int size = list.size();
        if (size == 0) {
            return 0;
        }
        int zze = size * zzea.zze(i);
        for (int i2 = 0; i2 < list.size(); i2++) {
            zze += zzea.zzb(list.get(i2));
        }
        return zze;
    }

    public static int zzb(int i, List<zzfz> list, zzgp zzgpVar) {
        int size = list.size();
        if (size == 0) {
            return 0;
        }
        int i2 = 0;
        for (int i3 = 0; i3 < size; i3++) {
            i2 += zzea.zzc(i, list.get(i3), zzgpVar);
        }
        return i2;
    }

    public static zzhh<?, ?> zza() {
        return zzb;
    }

    public static zzhh<?, ?> zzb() {
        return zzc;
    }

    public static zzhh<?, ?> zzc() {
        return zzd;
    }

    private static zzhh<?, ?> zza(boolean z) {
        try {
            Class<?> zze = zze();
            if (zze != null) {
                return (zzhh) zze.getConstructor(Boolean.TYPE).newInstance(Boolean.valueOf(z));
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

    public static <T> void zza(zzfs zzfsVar, T t, T t2, long j) {
        zzhn.zza(t, j, zzfsVar.zza(zzhn.zzf(t, j), zzhn.zzf(t2, j)));
    }

    public static <T, FT extends zzel<FT>> void zza(zzee<FT> zzeeVar, T t, T t2) {
        zzej<FT> zza2 = zzeeVar.zza(t2);
        if (!zza2.zza.isEmpty()) {
            zzeeVar.zzb(t).zza(zza2);
        }
    }

    public static <T, UT, UB> void zza(zzhh<UT, UB> zzhhVar, T t, T t2) {
        zzhhVar.zza(t, zzhhVar.zzb(zzhhVar.zza(t), zzhhVar.zza(t2)));
    }
}
