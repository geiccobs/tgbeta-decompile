package com.google.android.gms.internal.mlkit_language_id;

import com.google.android.gms.internal.mlkit_language_id.zzel;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes3.dex */
public final class zzej<T extends zzel<T>> {
    private static final zzej zzd = new zzej(true);
    final zzgq<T, Object> zza;
    private boolean zzb;
    private boolean zzc;

    private zzej() {
        this.zza = zzgq.zza(16);
    }

    private zzej(boolean z) {
        this(zzgq.zza(0));
        zzb();
    }

    private zzej(zzgq<T, Object> zzgqVar) {
        this.zza = zzgqVar;
        zzb();
    }

    public static <T extends zzel<T>> zzej<T> zza() {
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
        if (!(obj instanceof zzej)) {
            return false;
        }
        return this.zza.equals(((zzej) obj).zza);
    }

    public final int hashCode() {
        return this.zza.hashCode();
    }

    public final Iterator<Map.Entry<T, Object>> zzd() {
        if (this.zzc) {
            return new zzff(this.zza.entrySet().iterator());
        }
        return this.zza.entrySet().iterator();
    }

    public final Iterator<Map.Entry<T, Object>> zze() {
        if (this.zzc) {
            return new zzff(this.zza.zze().iterator());
        }
        return this.zza.zze().iterator();
    }

    private final Object zza(T t) {
        Object obj = this.zza.get(t);
        if (obj instanceof zzfa) {
            zzfa zzfaVar = (zzfa) obj;
            return zzfa.zza();
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
        if (obj instanceof zzfa) {
            this.zzc = true;
        }
        this.zza.zza((zzgq<T, Object>) t, (T) obj);
    }

    private static void zza(zzhv zzhvVar, Object obj) {
        zzeq.zza(obj);
        boolean z = true;
        switch (zzei.zza[zzhvVar.zza().ordinal()]) {
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
                if (!(obj instanceof zzdn) && !(obj instanceof byte[])) {
                    z = false;
                    break;
                }
                break;
            case 8:
                if (!(obj instanceof Integer) && !(obj instanceof zzet)) {
                    z = false;
                    break;
                }
                break;
            case 9:
                if (!(obj instanceof zzfz) && !(obj instanceof zzfa)) {
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

    private static <T extends zzel<T>> boolean zza(Map.Entry<T, Object> entry) {
        T key = entry.getKey();
        if (key.zzc() == zzhy.MESSAGE) {
            if (key.zzd()) {
                for (zzfz zzfzVar : (List) entry.getValue()) {
                    if (!zzfzVar.zzi()) {
                        return false;
                    }
                }
            } else {
                Object value = entry.getValue();
                if (value instanceof zzfz) {
                    if (!((zzfz) value).zzi()) {
                        return false;
                    }
                } else if (value instanceof zzfa) {
                    return true;
                } else {
                    throw new IllegalArgumentException("Wrong object type used with protocol message reflection.");
                }
            }
        }
        return true;
    }

    public final void zza(zzej<T> zzejVar) {
        for (int i = 0; i < zzejVar.zza.zzc(); i++) {
            zzb(zzejVar.zza.zzb(i));
        }
        for (Map.Entry<T, Object> entry : zzejVar.zza.zzd()) {
            zzb(entry);
        }
    }

    private static Object zza(Object obj) {
        if (obj instanceof zzgf) {
            return ((zzgf) obj).zza();
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
        zzfz zzfzVar;
        T key = entry.getKey();
        Object value = entry.getValue();
        if (value instanceof zzfa) {
            zzfa zzfaVar = (zzfa) value;
            value = zzfa.zza();
        }
        if (key.zzd()) {
            Object zza = zza((zzej<T>) key);
            if (zza == null) {
                zza = new ArrayList();
            }
            for (Object obj : (List) value) {
                ((List) zza).add(zza(obj));
            }
            this.zza.zza((zzgq<T, Object>) key, (T) zza);
        } else if (key.zzc() == zzhy.MESSAGE) {
            Object zza2 = zza((zzej<T>) key);
            if (zza2 == null) {
                this.zza.zza((zzgq<T, Object>) key, (T) zza(value));
                return;
            }
            if (zza2 instanceof zzgf) {
                zzfzVar = key.zza((zzgf) zza2, (zzgf) value);
            } else {
                zzfzVar = key.zza(((zzfz) zza2).zzm(), (zzfz) value).zzg();
            }
            this.zza.zza((zzgq<T, Object>) key, (T) zzfzVar);
        } else {
            this.zza.zza((zzgq<T, Object>) key, (T) zza(value));
        }
    }

    public static void zza(zzea zzeaVar, zzhv zzhvVar, int i, Object obj) throws IOException {
        if (zzhvVar == zzhv.GROUP) {
            zzfz zzfzVar = (zzfz) obj;
            zzeq.zza(zzfzVar);
            zzeaVar.zza(i, 3);
            zzfzVar.zza(zzeaVar);
            zzeaVar.zza(i, 4);
            return;
        }
        zzeaVar.zza(i, zzhvVar.zzb());
        switch (zzei.zzb[zzhvVar.ordinal()]) {
            case 1:
                zzeaVar.zza(((Double) obj).doubleValue());
                return;
            case 2:
                zzeaVar.zza(((Float) obj).floatValue());
                return;
            case 3:
                zzeaVar.zza(((Long) obj).longValue());
                return;
            case 4:
                zzeaVar.zza(((Long) obj).longValue());
                return;
            case 5:
                zzeaVar.zza(((Integer) obj).intValue());
                return;
            case 6:
                zzeaVar.zzc(((Long) obj).longValue());
                return;
            case 7:
                zzeaVar.zzd(((Integer) obj).intValue());
                return;
            case 8:
                zzeaVar.zza(((Boolean) obj).booleanValue());
                return;
            case 9:
                ((zzfz) obj).zza(zzeaVar);
                return;
            case 10:
                zzeaVar.zza((zzfz) obj);
                return;
            case 11:
                if (obj instanceof zzdn) {
                    zzeaVar.zza((zzdn) obj);
                    return;
                } else {
                    zzeaVar.zza((String) obj);
                    return;
                }
            case 12:
                if (obj instanceof zzdn) {
                    zzeaVar.zza((zzdn) obj);
                    return;
                }
                byte[] bArr = (byte[]) obj;
                zzeaVar.zzb(bArr, 0, bArr.length);
                return;
            case 13:
                zzeaVar.zzb(((Integer) obj).intValue());
                return;
            case 14:
                zzeaVar.zzd(((Integer) obj).intValue());
                return;
            case 15:
                zzeaVar.zzc(((Long) obj).longValue());
                return;
            case 16:
                zzeaVar.zzc(((Integer) obj).intValue());
                return;
            case 17:
                zzeaVar.zzb(((Long) obj).longValue());
                return;
            case 18:
                if (obj instanceof zzet) {
                    zzeaVar.zza(((zzet) obj).zza());
                    return;
                } else {
                    zzeaVar.zza(((Integer) obj).intValue());
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
        if (key.zzc() == zzhy.MESSAGE && !key.zzd() && !key.zze()) {
            if (value instanceof zzfa) {
                return zzea.zzb(entry.getKey().zza(), (zzfa) value);
            }
            return zzea.zzb(entry.getKey().zza(), (zzfz) value);
        }
        return zza((zzel<?>) key, value);
    }

    public static int zza(zzhv zzhvVar, int i, Object obj) {
        int zze = zzea.zze(i);
        if (zzhvVar == zzhv.GROUP) {
            zzeq.zza((zzfz) obj);
            zze <<= 1;
        }
        return zze + zzb(zzhvVar, obj);
    }

    private static int zzb(zzhv zzhvVar, Object obj) {
        switch (zzei.zzb[zzhvVar.ordinal()]) {
            case 1:
                return zzea.zzb(((Double) obj).doubleValue());
            case 2:
                return zzea.zzb(((Float) obj).floatValue());
            case 3:
                return zzea.zzd(((Long) obj).longValue());
            case 4:
                return zzea.zze(((Long) obj).longValue());
            case 5:
                return zzea.zzf(((Integer) obj).intValue());
            case 6:
                return zzea.zzg(((Long) obj).longValue());
            case 7:
                return zzea.zzi(((Integer) obj).intValue());
            case 8:
                return zzea.zzb(((Boolean) obj).booleanValue());
            case 9:
                return zzea.zzc((zzfz) obj);
            case 10:
                if (obj instanceof zzfa) {
                    return zzea.zza((zzfa) obj);
                }
                return zzea.zzb((zzfz) obj);
            case 11:
                if (obj instanceof zzdn) {
                    return zzea.zzb((zzdn) obj);
                }
                return zzea.zzb((String) obj);
            case 12:
                if (obj instanceof zzdn) {
                    return zzea.zzb((zzdn) obj);
                }
                return zzea.zzb((byte[]) obj);
            case 13:
                return zzea.zzg(((Integer) obj).intValue());
            case 14:
                return zzea.zzj(((Integer) obj).intValue());
            case 15:
                return zzea.zzh(((Long) obj).longValue());
            case 16:
                return zzea.zzh(((Integer) obj).intValue());
            case 17:
                return zzea.zzf(((Long) obj).longValue());
            case 18:
                if (obj instanceof zzet) {
                    return zzea.zzk(((zzet) obj).zza());
                }
                return zzea.zzk(((Integer) obj).intValue());
            default:
                throw new RuntimeException("There is no way to get here, but the compiler thinks otherwise.");
        }
    }

    public static int zza(zzel<?> zzelVar, Object obj) {
        zzhv zzb = zzelVar.zzb();
        int zza = zzelVar.zza();
        if (zzelVar.zzd()) {
            int i = 0;
            if (zzelVar.zze()) {
                for (Object obj2 : (List) obj) {
                    i += zzb(zzb, obj2);
                }
                return zzea.zze(zza) + i + zzea.zzl(i);
            }
            for (Object obj3 : (List) obj) {
                i += zza(zzb, zza, obj3);
            }
            return i;
        }
        return zza(zzb, zza, obj);
    }

    public final /* synthetic */ Object clone() throws CloneNotSupportedException {
        zzej zzejVar = new zzej();
        for (int i = 0; i < this.zza.zzc(); i++) {
            Map.Entry<T, Object> zzb = this.zza.zzb(i);
            zzejVar.zzb((zzej) zzb.getKey(), zzb.getValue());
        }
        for (Map.Entry<T, Object> entry : this.zza.zzd()) {
            zzejVar.zzb((zzej) entry.getKey(), entry.getValue());
        }
        zzejVar.zzc = this.zzc;
        return zzejVar;
    }
}
