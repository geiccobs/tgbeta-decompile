package com.google.android.gms.internal.mlkit_common;

import com.google.android.gms.internal.mlkit_common.zzfn;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: com.google.mlkit:common@@17.0.0 */
/* loaded from: classes3.dex */
public final class zzfl<T extends zzfn<T>> {
    private static final zzfl zzd = new zzfl(true);
    final zzhs<T, Object> zza;
    private boolean zzb;
    private boolean zzc;

    private zzfl() {
        this.zza = zzhs.zza(16);
    }

    private zzfl(boolean z) {
        this(zzhs.zza(0));
        zzb();
    }

    private zzfl(zzhs<T, Object> zzhsVar) {
        this.zza = zzhsVar;
        zzb();
    }

    public static <T extends zzfn<T>> zzfl<T> zza() {
        return zzd;
    }

    public final void zzb() {
        if (this.zzb) {
            return;
        }
        this.zza.zza();
        this.zzb = true;
    }

    public final boolean zzc() {
        return this.zzb;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof zzfl)) {
            return false;
        }
        return this.zza.equals(((zzfl) obj).zza);
    }

    public final int hashCode() {
        return this.zza.hashCode();
    }

    public final Iterator<Map.Entry<T, Object>> zzd() {
        if (this.zzc) {
            return new zzgh(this.zza.entrySet().iterator());
        }
        return this.zza.entrySet().iterator();
    }

    public final Iterator<Map.Entry<T, Object>> zze() {
        if (this.zzc) {
            return new zzgh(this.zza.zze().iterator());
        }
        return this.zza.zze().iterator();
    }

    private final Object zza(T t) {
        Object obj = this.zza.get(t);
        if (obj instanceof zzgc) {
            zzgc zzgcVar = (zzgc) obj;
            return zzgc.zza();
        }
        return obj;
    }

    private final void zzb(T t, Object obj) {
        if (t.zzd()) {
            if (!(obj instanceof List)) {
                throw new IllegalArgumentException("Wrong object type used with protocol message reflection.");
            }
            ArrayList arrayList = new ArrayList();
            arrayList.addAll((List) obj);
            ArrayList arrayList2 = arrayList;
            int size = arrayList2.size();
            int i = 0;
            while (i < size) {
                Object obj2 = arrayList2.get(i);
                i++;
                zza(t.zzb(), obj2);
            }
            obj = arrayList;
        } else {
            zza(t.zzb(), obj);
        }
        if (obj instanceof zzgc) {
            this.zzc = true;
        }
        this.zza.zza((zzhs<T, Object>) t, (T) obj);
    }

    private static void zza(zzix zzixVar, Object obj) {
        zzfs.zza(obj);
        boolean z = true;
        switch (zzfk.zza[zzixVar.zza().ordinal()]) {
            case 1:
                z = obj instanceof Integer;
                break;
            case 2:
                z = obj instanceof Long;
                break;
            case 3:
                z = obj instanceof Float;
                break;
            case 4:
                z = obj instanceof Double;
                break;
            case 5:
                z = obj instanceof Boolean;
                break;
            case 6:
                z = obj instanceof String;
                break;
            case 7:
                if (!(obj instanceof zzep) && !(obj instanceof byte[])) {
                    z = false;
                    break;
                }
                break;
            case 8:
                if (!(obj instanceof Integer) && !(obj instanceof zzfv)) {
                    z = false;
                    break;
                }
                break;
            case 9:
                if (!(obj instanceof zzhb) && !(obj instanceof zzgc)) {
                    z = false;
                    break;
                }
                break;
            default:
                z = false;
                break;
        }
        if (!z) {
            throw new IllegalArgumentException("Wrong object type used with protocol message reflection.");
        }
    }

    public final boolean zzf() {
        for (int i = 0; i < this.zza.zzc(); i++) {
            if (!zza((Map.Entry) this.zza.zzb(i))) {
                return false;
            }
        }
        for (Map.Entry<T, Object> entry : this.zza.zzd()) {
            if (!zza((Map.Entry) entry)) {
                return false;
            }
        }
        return true;
    }

    private static <T extends zzfn<T>> boolean zza(Map.Entry<T, Object> entry) {
        T key = entry.getKey();
        if (key.zzc() == zzja.MESSAGE) {
            if (key.zzd()) {
                for (zzhb zzhbVar : (List) entry.getValue()) {
                    if (!zzhbVar.zzi()) {
                        return false;
                    }
                }
            } else {
                Object value = entry.getValue();
                if (value instanceof zzhb) {
                    if (!((zzhb) value).zzi()) {
                        return false;
                    }
                } else if (value instanceof zzgc) {
                    return true;
                } else {
                    throw new IllegalArgumentException("Wrong object type used with protocol message reflection.");
                }
            }
        }
        return true;
    }

    public final void zza(zzfl<T> zzflVar) {
        for (int i = 0; i < zzflVar.zza.zzc(); i++) {
            zzb(zzflVar.zza.zzb(i));
        }
        for (Map.Entry<T, Object> entry : zzflVar.zza.zzd()) {
            zzb(entry);
        }
    }

    private static Object zza(Object obj) {
        if (obj instanceof zzhh) {
            return ((zzhh) obj).zza();
        }
        if (obj instanceof byte[]) {
            byte[] bArr = (byte[]) obj;
            byte[] bArr2 = new byte[bArr.length];
            System.arraycopy(bArr, 0, bArr2, 0, bArr.length);
            return bArr2;
        }
        return obj;
    }

    private final void zzb(Map.Entry<T, Object> entry) {
        zzhb zzhbVar;
        T key = entry.getKey();
        Object value = entry.getValue();
        if (value instanceof zzgc) {
            zzgc zzgcVar = (zzgc) value;
            value = zzgc.zza();
        }
        if (key.zzd()) {
            Object zza = zza((zzfl<T>) key);
            if (zza == null) {
                zza = new ArrayList();
            }
            for (Object obj : (List) value) {
                ((List) zza).add(zza(obj));
            }
            this.zza.zza((zzhs<T, Object>) key, (T) zza);
        } else if (key.zzc() == zzja.MESSAGE) {
            Object zza2 = zza((zzfl<T>) key);
            if (zza2 == null) {
                this.zza.zza((zzhs<T, Object>) key, (T) zza(value));
                return;
            }
            if (zza2 instanceof zzhh) {
                zzhbVar = key.zza((zzhh) zza2, (zzhh) value);
            } else {
                zzhbVar = key.zza(((zzhb) zza2).zzm(), (zzhb) value).zzg();
            }
            this.zza.zza((zzhs<T, Object>) key, (T) zzhbVar);
        } else {
            this.zza.zza((zzhs<T, Object>) key, (T) zza(value));
        }
    }

    public static void zza(zzfc zzfcVar, zzix zzixVar, int i, Object obj) throws IOException {
        if (zzixVar == zzix.GROUP) {
            zzhb zzhbVar = (zzhb) obj;
            zzfs.zza(zzhbVar);
            zzfcVar.zza(i, 3);
            zzhbVar.zza(zzfcVar);
            zzfcVar.zza(i, 4);
            return;
        }
        zzfcVar.zza(i, zzixVar.zzb());
        switch (zzfk.zzb[zzixVar.ordinal()]) {
            case 1:
                zzfcVar.zza(((Double) obj).doubleValue());
                return;
            case 2:
                zzfcVar.zza(((Float) obj).floatValue());
                return;
            case 3:
                zzfcVar.zza(((Long) obj).longValue());
                return;
            case 4:
                zzfcVar.zza(((Long) obj).longValue());
                return;
            case 5:
                zzfcVar.zza(((Integer) obj).intValue());
                return;
            case 6:
                zzfcVar.zzc(((Long) obj).longValue());
                return;
            case 7:
                zzfcVar.zzd(((Integer) obj).intValue());
                return;
            case 8:
                zzfcVar.zza(((Boolean) obj).booleanValue());
                return;
            case 9:
                ((zzhb) obj).zza(zzfcVar);
                return;
            case 10:
                zzfcVar.zza((zzhb) obj);
                return;
            case 11:
                if (obj instanceof zzep) {
                    zzfcVar.zza((zzep) obj);
                    return;
                } else {
                    zzfcVar.zza((String) obj);
                    return;
                }
            case 12:
                if (obj instanceof zzep) {
                    zzfcVar.zza((zzep) obj);
                    return;
                }
                byte[] bArr = (byte[]) obj;
                zzfcVar.zzb(bArr, 0, bArr.length);
                return;
            case 13:
                zzfcVar.zzb(((Integer) obj).intValue());
                return;
            case 14:
                zzfcVar.zzd(((Integer) obj).intValue());
                return;
            case 15:
                zzfcVar.zzc(((Long) obj).longValue());
                return;
            case 16:
                zzfcVar.zzc(((Integer) obj).intValue());
                return;
            case 17:
                zzfcVar.zzb(((Long) obj).longValue());
                return;
            case 18:
                if (obj instanceof zzfv) {
                    zzfcVar.zza(((zzfv) obj).zza());
                    return;
                } else {
                    zzfcVar.zza(((Integer) obj).intValue());
                    return;
                }
            default:
                return;
        }
    }

    public final int zzg() {
        int i = 0;
        for (int i2 = 0; i2 < this.zza.zzc(); i2++) {
            i += zzc(this.zza.zzb(i2));
        }
        for (Map.Entry<T, Object> entry : this.zza.zzd()) {
            i += zzc(entry);
        }
        return i;
    }

    private static int zzc(Map.Entry<T, Object> entry) {
        T key = entry.getKey();
        Object value = entry.getValue();
        if (key.zzc() == zzja.MESSAGE && !key.zzd() && !key.zze()) {
            if (value instanceof zzgc) {
                return zzfc.zzb(entry.getKey().zza(), (zzgc) value);
            }
            return zzfc.zzb(entry.getKey().zza(), (zzhb) value);
        }
        return zza((zzfn<?>) key, value);
    }

    public static int zza(zzix zzixVar, int i, Object obj) {
        int zze = zzfc.zze(i);
        if (zzixVar == zzix.GROUP) {
            zzfs.zza((zzhb) obj);
            zze <<= 1;
        }
        return zze + zzb(zzixVar, obj);
    }

    private static int zzb(zzix zzixVar, Object obj) {
        switch (zzfk.zzb[zzixVar.ordinal()]) {
            case 1:
                return zzfc.zzb(((Double) obj).doubleValue());
            case 2:
                return zzfc.zzb(((Float) obj).floatValue());
            case 3:
                return zzfc.zzd(((Long) obj).longValue());
            case 4:
                return zzfc.zze(((Long) obj).longValue());
            case 5:
                return zzfc.zzf(((Integer) obj).intValue());
            case 6:
                return zzfc.zzg(((Long) obj).longValue());
            case 7:
                return zzfc.zzi(((Integer) obj).intValue());
            case 8:
                return zzfc.zzb(((Boolean) obj).booleanValue());
            case 9:
                return zzfc.zzc((zzhb) obj);
            case 10:
                if (obj instanceof zzgc) {
                    return zzfc.zza((zzgc) obj);
                }
                return zzfc.zzb((zzhb) obj);
            case 11:
                if (obj instanceof zzep) {
                    return zzfc.zzb((zzep) obj);
                }
                return zzfc.zzb((String) obj);
            case 12:
                if (obj instanceof zzep) {
                    return zzfc.zzb((zzep) obj);
                }
                return zzfc.zzb((byte[]) obj);
            case 13:
                return zzfc.zzg(((Integer) obj).intValue());
            case 14:
                return zzfc.zzj(((Integer) obj).intValue());
            case 15:
                return zzfc.zzh(((Long) obj).longValue());
            case 16:
                return zzfc.zzh(((Integer) obj).intValue());
            case 17:
                return zzfc.zzf(((Long) obj).longValue());
            case 18:
                if (obj instanceof zzfv) {
                    return zzfc.zzk(((zzfv) obj).zza());
                }
                return zzfc.zzk(((Integer) obj).intValue());
            default:
                throw new RuntimeException("There is no way to get here, but the compiler thinks otherwise.");
        }
    }

    public static int zza(zzfn<?> zzfnVar, Object obj) {
        zzix zzb = zzfnVar.zzb();
        int zza = zzfnVar.zza();
        if (zzfnVar.zzd()) {
            int i = 0;
            if (zzfnVar.zze()) {
                for (Object obj2 : (List) obj) {
                    i += zzb(zzb, obj2);
                }
                return zzfc.zze(zza) + i + zzfc.zzl(i);
            }
            for (Object obj3 : (List) obj) {
                i += zza(zzb, zza, obj3);
            }
            return i;
        }
        return zza(zzb, zza, obj);
    }

    public final /* synthetic */ Object clone() throws CloneNotSupportedException {
        zzfl zzflVar = new zzfl();
        for (int i = 0; i < this.zza.zzc(); i++) {
            Map.Entry<T, Object> zzb = this.zza.zzb(i);
            zzflVar.zzb((zzfl) zzb.getKey(), zzb.getValue());
        }
        for (Map.Entry<T, Object> entry : this.zza.zzd()) {
            zzflVar.zzb((zzfl) entry.getKey(), entry.getValue());
        }
        zzflVar.zzc = this.zzc;
        return zzflVar;
    }
}
