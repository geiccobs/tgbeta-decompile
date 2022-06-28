package com.google.android.gms.internal.clearcut;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.RandomAccess;
/* loaded from: classes3.dex */
final class zzeh {
    private static final Class<?> zzoh = zzdp();
    private static final zzex<?, ?> zzoi = zzd(false);
    private static final zzex<?, ?> zzoj = zzd(true);
    private static final zzex<?, ?> zzok = new zzez();

    public static int zza(List<Long> list) {
        int i;
        int size = list.size();
        int i2 = 0;
        if (size == 0) {
            return 0;
        }
        if (list instanceof zzdc) {
            zzdc zzdcVar = (zzdc) list;
            i = 0;
            while (i2 < size) {
                i += zzbn.zze(zzdcVar.getLong(i2));
                i2++;
            }
        } else {
            i = 0;
            while (i2 < size) {
                i += zzbn.zze(list.get(i2).longValue());
                i2++;
            }
        }
        return i;
    }

    private static <UT, UB> UB zza(int i, int i2, UB ub, zzex<UT, UB> zzexVar) {
        if (ub == null) {
            ub = zzexVar.zzdz();
        }
        zzexVar.zza((zzex<UT, UB>) ub, i, i2);
        return ub;
    }

    public static <UT, UB> UB zza(int i, List<Integer> list, zzck<?> zzckVar, UB ub, zzex<UT, UB> zzexVar) {
        if (zzckVar == null) {
            return ub;
        }
        if (list instanceof RandomAccess) {
            int size = list.size();
            int i2 = 0;
            for (int i3 = 0; i3 < size; i3++) {
                int intValue = list.get(i3).intValue();
                if (zzckVar.zzb(intValue) != null) {
                    if (i3 != i2) {
                        list.set(i2, Integer.valueOf(intValue));
                    }
                    i2++;
                } else {
                    ub = (UB) zza(i, intValue, ub, zzexVar);
                }
            }
            if (i2 != size) {
                list.subList(i2, size).clear();
            }
        } else {
            Iterator<Integer> it = list.iterator();
            while (it.hasNext()) {
                int intValue2 = it.next().intValue();
                if (zzckVar.zzb(intValue2) == null) {
                    ub = (UB) zza(i, intValue2, ub, zzexVar);
                    it.remove();
                }
            }
        }
        return ub;
    }

    public static void zza(int i, List<String> list, zzfr zzfrVar) throws IOException {
        if (list == null || list.isEmpty()) {
            return;
        }
        zzfrVar.zza(i, list);
    }

    public static void zza(int i, List<?> list, zzfr zzfrVar, zzef zzefVar) throws IOException {
        if (list == null || list.isEmpty()) {
            return;
        }
        zzfrVar.zza(i, list, zzefVar);
    }

    public static void zza(int i, List<Double> list, zzfr zzfrVar, boolean z) throws IOException {
        if (list == null || list.isEmpty()) {
            return;
        }
        zzfrVar.zzg(i, list, z);
    }

    public static <T, FT extends zzca<FT>> void zza(zzbu<FT> zzbuVar, T t, T t2) {
        zzby<FT> zza = zzbuVar.zza(t2);
        if (!zza.isEmpty()) {
            zzbuVar.zzb(t).zza(zza);
        }
    }

    public static <T> void zza(zzdj zzdjVar, T t, T t2, long j) {
        zzfd.zza(t, j, zzdjVar.zzb(zzfd.zzo(t, j), zzfd.zzo(t2, j)));
    }

    public static <T, UT, UB> void zza(zzex<UT, UB> zzexVar, T t, T t2) {
        zzexVar.zze(t, zzexVar.zzg(zzexVar.zzq(t), zzexVar.zzq(t2)));
    }

    public static int zzb(List<Long> list) {
        int i;
        int size = list.size();
        int i2 = 0;
        if (size == 0) {
            return 0;
        }
        if (list instanceof zzdc) {
            zzdc zzdcVar = (zzdc) list;
            i = 0;
            while (i2 < size) {
                i += zzbn.zzf(zzdcVar.getLong(i2));
                i2++;
            }
        } else {
            i = 0;
            while (i2 < size) {
                i += zzbn.zzf(list.get(i2).longValue());
                i2++;
            }
        }
        return i;
    }

    public static void zzb(int i, List<zzbb> list, zzfr zzfrVar) throws IOException {
        if (list == null || list.isEmpty()) {
            return;
        }
        zzfrVar.zzb(i, list);
    }

    public static void zzb(int i, List<?> list, zzfr zzfrVar, zzef zzefVar) throws IOException {
        if (list == null || list.isEmpty()) {
            return;
        }
        zzfrVar.zzb(i, list, zzefVar);
    }

    public static void zzb(int i, List<Float> list, zzfr zzfrVar, boolean z) throws IOException {
        if (list == null || list.isEmpty()) {
            return;
        }
        zzfrVar.zzf(i, list, z);
    }

    public static int zzc(int i, Object obj, zzef zzefVar) {
        return obj instanceof zzcv ? zzbn.zza(i, (zzcv) obj) : zzbn.zzb(i, (zzdo) obj, zzefVar);
    }

    public static int zzc(int i, List<?> list) {
        int size = list.size();
        int i2 = 0;
        if (size == 0) {
            return 0;
        }
        int zzr = zzbn.zzr(i) * size;
        if (list instanceof zzcx) {
            zzcx zzcxVar = (zzcx) list;
            while (i2 < size) {
                Object raw = zzcxVar.getRaw(i2);
                zzr += raw instanceof zzbb ? zzbn.zzb((zzbb) raw) : zzbn.zzh((String) raw);
                i2++;
            }
        } else {
            while (i2 < size) {
                Object obj = list.get(i2);
                zzr += obj instanceof zzbb ? zzbn.zzb((zzbb) obj) : zzbn.zzh((String) obj);
                i2++;
            }
        }
        return zzr;
    }

    public static int zzc(int i, List<?> list, zzef zzefVar) {
        int size = list.size();
        if (size == 0) {
            return 0;
        }
        int zzr = zzbn.zzr(i) * size;
        for (int i2 = 0; i2 < size; i2++) {
            Object obj = list.get(i2);
            zzr += obj instanceof zzcv ? zzbn.zza((zzcv) obj) : zzbn.zzb((zzdo) obj, zzefVar);
        }
        return zzr;
    }

    public static int zzc(List<Long> list) {
        int i;
        int size = list.size();
        int i2 = 0;
        if (size == 0) {
            return 0;
        }
        if (list instanceof zzdc) {
            zzdc zzdcVar = (zzdc) list;
            i = 0;
            while (i2 < size) {
                i += zzbn.zzg(zzdcVar.getLong(i2));
                i2++;
            }
        } else {
            i = 0;
            while (i2 < size) {
                i += zzbn.zzg(list.get(i2).longValue());
                i2++;
            }
        }
        return i;
    }

    public static void zzc(int i, List<Long> list, zzfr zzfrVar, boolean z) throws IOException {
        if (list == null || list.isEmpty()) {
            return;
        }
        zzfrVar.zzc(i, list, z);
    }

    public static boolean zzc(int i, int i2, int i3) {
        if (i2 < 40) {
            return true;
        }
        long j = i3;
        return ((((long) i2) - ((long) i)) + 1) + 9 <= ((2 * j) + 3) + ((j + 3) * 3);
    }

    public static int zzd(int i, List<zzbb> list) {
        int size = list.size();
        if (size == 0) {
            return 0;
        }
        int zzr = size * zzbn.zzr(i);
        for (int i2 = 0; i2 < list.size(); i2++) {
            zzr += zzbn.zzb(list.get(i2));
        }
        return zzr;
    }

    public static int zzd(int i, List<zzdo> list, zzef zzefVar) {
        int size = list.size();
        if (size == 0) {
            return 0;
        }
        int i2 = 0;
        for (int i3 = 0; i3 < size; i3++) {
            i2 += zzbn.zzc(i, list.get(i3), zzefVar);
        }
        return i2;
    }

    public static int zzd(List<Integer> list) {
        int i;
        int size = list.size();
        int i2 = 0;
        if (size == 0) {
            return 0;
        }
        if (list instanceof zzch) {
            zzch zzchVar = (zzch) list;
            i = 0;
            while (i2 < size) {
                i += zzbn.zzx(zzchVar.getInt(i2));
                i2++;
            }
        } else {
            i = 0;
            while (i2 < size) {
                i += zzbn.zzx(list.get(i2).intValue());
                i2++;
            }
        }
        return i;
    }

    private static zzex<?, ?> zzd(boolean z) {
        try {
            Class<?> zzdq = zzdq();
            if (zzdq != null) {
                return (zzex) zzdq.getConstructor(Boolean.TYPE).newInstance(Boolean.valueOf(z));
            }
            return null;
        } catch (Throwable th) {
            return null;
        }
    }

    public static void zzd(int i, List<Long> list, zzfr zzfrVar, boolean z) throws IOException {
        if (list == null || list.isEmpty()) {
            return;
        }
        zzfrVar.zzd(i, list, z);
    }

    public static boolean zzd(Object obj, Object obj2) {
        if (obj != obj2) {
            return obj != null && obj.equals(obj2);
        }
        return true;
    }

    public static zzex<?, ?> zzdm() {
        return zzoi;
    }

    public static zzex<?, ?> zzdn() {
        return zzoj;
    }

    public static zzex<?, ?> zzdo() {
        return zzok;
    }

    private static Class<?> zzdp() {
        try {
            return Class.forName("com.google.protobuf.GeneratedMessage");
        } catch (Throwable th) {
            return null;
        }
    }

    private static Class<?> zzdq() {
        try {
            return Class.forName("com.google.protobuf.UnknownFieldSetSchema");
        } catch (Throwable th) {
            return null;
        }
    }

    public static int zze(List<Integer> list) {
        int i;
        int size = list.size();
        int i2 = 0;
        if (size == 0) {
            return 0;
        }
        if (list instanceof zzch) {
            zzch zzchVar = (zzch) list;
            i = 0;
            while (i2 < size) {
                i += zzbn.zzs(zzchVar.getInt(i2));
                i2++;
            }
        } else {
            i = 0;
            while (i2 < size) {
                i += zzbn.zzs(list.get(i2).intValue());
                i2++;
            }
        }
        return i;
    }

    public static void zze(int i, List<Long> list, zzfr zzfrVar, boolean z) throws IOException {
        if (list == null || list.isEmpty()) {
            return;
        }
        zzfrVar.zzn(i, list, z);
    }

    public static int zzf(List<Integer> list) {
        int i;
        int size = list.size();
        int i2 = 0;
        if (size == 0) {
            return 0;
        }
        if (list instanceof zzch) {
            zzch zzchVar = (zzch) list;
            i = 0;
            while (i2 < size) {
                i += zzbn.zzt(zzchVar.getInt(i2));
                i2++;
            }
        } else {
            i = 0;
            while (i2 < size) {
                i += zzbn.zzt(list.get(i2).intValue());
                i2++;
            }
        }
        return i;
    }

    public static void zzf(int i, List<Long> list, zzfr zzfrVar, boolean z) throws IOException {
        if (list == null || list.isEmpty()) {
            return;
        }
        zzfrVar.zze(i, list, z);
    }

    public static void zzf(Class<?> cls) {
        Class<?> cls2;
        if (zzcg.class.isAssignableFrom(cls) || (cls2 = zzoh) == null || cls2.isAssignableFrom(cls)) {
            return;
        }
        throw new IllegalArgumentException("Message classes must extend GeneratedMessage or GeneratedMessageLite");
    }

    public static int zzg(List<Integer> list) {
        int i;
        int size = list.size();
        int i2 = 0;
        if (size == 0) {
            return 0;
        }
        if (list instanceof zzch) {
            zzch zzchVar = (zzch) list;
            i = 0;
            while (i2 < size) {
                i += zzbn.zzu(zzchVar.getInt(i2));
                i2++;
            }
        } else {
            i = 0;
            while (i2 < size) {
                i += zzbn.zzu(list.get(i2).intValue());
                i2++;
            }
        }
        return i;
    }

    public static void zzg(int i, List<Long> list, zzfr zzfrVar, boolean z) throws IOException {
        if (list == null || list.isEmpty()) {
            return;
        }
        zzfrVar.zzl(i, list, z);
    }

    public static int zzh(List<?> list) {
        return list.size() << 2;
    }

    public static void zzh(int i, List<Integer> list, zzfr zzfrVar, boolean z) throws IOException {
        if (list == null || list.isEmpty()) {
            return;
        }
        zzfrVar.zza(i, list, z);
    }

    public static int zzi(List<?> list) {
        return list.size() << 3;
    }

    public static void zzi(int i, List<Integer> list, zzfr zzfrVar, boolean z) throws IOException {
        if (list == null || list.isEmpty()) {
            return;
        }
        zzfrVar.zzj(i, list, z);
    }

    public static int zzj(List<?> list) {
        return list.size();
    }

    public static void zzj(int i, List<Integer> list, zzfr zzfrVar, boolean z) throws IOException {
        if (list == null || list.isEmpty()) {
            return;
        }
        zzfrVar.zzm(i, list, z);
    }

    public static void zzk(int i, List<Integer> list, zzfr zzfrVar, boolean z) throws IOException {
        if (list == null || list.isEmpty()) {
            return;
        }
        zzfrVar.zzb(i, list, z);
    }

    public static void zzl(int i, List<Integer> list, zzfr zzfrVar, boolean z) throws IOException {
        if (list == null || list.isEmpty()) {
            return;
        }
        zzfrVar.zzk(i, list, z);
    }

    public static void zzm(int i, List<Integer> list, zzfr zzfrVar, boolean z) throws IOException {
        if (list == null || list.isEmpty()) {
            return;
        }
        zzfrVar.zzh(i, list, z);
    }

    public static void zzn(int i, List<Boolean> list, zzfr zzfrVar, boolean z) throws IOException {
        if (list == null || list.isEmpty()) {
            return;
        }
        zzfrVar.zzi(i, list, z);
    }

    public static int zzo(int i, List<Long> list, boolean z) {
        if (list.size() == 0) {
            return 0;
        }
        return zza(list) + (list.size() * zzbn.zzr(i));
    }

    public static int zzp(int i, List<Long> list, boolean z) {
        int size = list.size();
        if (size == 0) {
            return 0;
        }
        return zzb(list) + (size * zzbn.zzr(i));
    }

    public static int zzq(int i, List<Long> list, boolean z) {
        int size = list.size();
        if (size == 0) {
            return 0;
        }
        return zzc(list) + (size * zzbn.zzr(i));
    }

    public static int zzr(int i, List<Integer> list, boolean z) {
        int size = list.size();
        if (size == 0) {
            return 0;
        }
        return zzd(list) + (size * zzbn.zzr(i));
    }

    public static int zzs(int i, List<Integer> list, boolean z) {
        int size = list.size();
        if (size == 0) {
            return 0;
        }
        return zze(list) + (size * zzbn.zzr(i));
    }

    public static int zzt(int i, List<Integer> list, boolean z) {
        int size = list.size();
        if (size == 0) {
            return 0;
        }
        return zzf(list) + (size * zzbn.zzr(i));
    }

    public static int zzu(int i, List<Integer> list, boolean z) {
        int size = list.size();
        if (size == 0) {
            return 0;
        }
        return zzg(list) + (size * zzbn.zzr(i));
    }

    public static int zzv(int i, List<?> list, boolean z) {
        int size = list.size();
        if (size == 0) {
            return 0;
        }
        return size * zzbn.zzj(i, 0);
    }

    public static int zzw(int i, List<?> list, boolean z) {
        int size = list.size();
        if (size == 0) {
            return 0;
        }
        return size * zzbn.zzg(i, 0L);
    }

    public static int zzx(int i, List<?> list, boolean z) {
        int size = list.size();
        if (size == 0) {
            return 0;
        }
        return size * zzbn.zzc(i, true);
    }
}
