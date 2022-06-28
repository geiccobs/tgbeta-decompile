package com.google.android.gms.internal.vision;

import com.google.android.gms.internal.vision.zziw;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
/* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
/* loaded from: classes3.dex */
public final class zziu<T extends zziw<T>> {
    private static final zziu zzd = new zziu(true);
    final zzlh<T, Object> zza;
    private boolean zzb;
    private boolean zzc;

    private zziu() {
        this.zza = zzlh.zza(16);
    }

    private zziu(boolean z) {
        this(zzlh.zza(0));
        zzb();
    }

    private zziu(zzlh<T, Object> zzlhVar) {
        this.zza = zzlhVar;
        zzb();
    }

    public static <T extends zziw<T>> zziu<T> zza() {
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
        if (!(obj instanceof zziu)) {
            return false;
        }
        return this.zza.equals(((zziu) obj).zza);
    }

    public final int hashCode() {
        return this.zza.hashCode();
    }

    public final Iterator<Map.Entry<T, Object>> zzd() {
        if (this.zzc) {
            return new zzjq(this.zza.entrySet().iterator());
        }
        return this.zza.entrySet().iterator();
    }

    public final Iterator<Map.Entry<T, Object>> zze() {
        if (this.zzc) {
            return new zzjq(this.zza.zze().iterator());
        }
        return this.zza.zze().iterator();
    }

    public final Object zza(T t) {
        Object obj = this.zza.get(t);
        if (obj instanceof zzjp) {
            zzjp zzjpVar = (zzjp) obj;
            return zzjp.zza();
        }
        return obj;
    }

    public final void zza(T t, Object obj) {
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
                zzd(t, obj2);
            }
            obj = arrayList;
        } else {
            zzd(t, obj);
        }
        if (obj instanceof zzjp) {
            this.zzc = true;
        }
        this.zza.zza((zzlh<T, Object>) t, (T) obj);
    }

    public final void zzb(T t, Object obj) {
        List list;
        if (!t.zzd()) {
            throw new IllegalArgumentException("addRepeatedField() can only be called on repeated fields.");
        }
        zzd(t, obj);
        Object zza = zza((zziu<T>) t);
        if (zza == null) {
            list = new ArrayList();
            this.zza.zza((zzlh<T, Object>) t, (T) list);
        } else {
            list = (List) zza;
        }
        list.add(obj);
    }

    private static void zzd(T t, Object obj) {
        boolean z;
        zzml zzb = t.zzb();
        zzjf.zza(obj);
        switch (zzit.zza[zzb.zza().ordinal()]) {
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
                if (!(obj instanceof zzht) && !(obj instanceof byte[])) {
                    z = false;
                    break;
                } else {
                    z = true;
                    break;
                }
                break;
            case 8:
                if (!(obj instanceof Integer) && !(obj instanceof zzje)) {
                    z = false;
                    break;
                } else {
                    z = true;
                    break;
                }
            case 9:
                if (!(obj instanceof zzkk) && !(obj instanceof zzjp)) {
                    z = false;
                    break;
                } else {
                    z = true;
                    break;
                }
            default:
                z = false;
                break;
        }
        if (!z) {
            throw new IllegalArgumentException(String.format("Wrong object type used with protocol message reflection.\nField number: %d, field java type: %s, value type: %s\n", Integer.valueOf(t.zza()), t.zzb().zza(), obj.getClass().getName()));
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

    private static <T extends zziw<T>> boolean zza(Map.Entry<T, Object> entry) {
        T key = entry.getKey();
        if (key.zzc() == zzmo.MESSAGE) {
            if (key.zzd()) {
                for (zzkk zzkkVar : (List) entry.getValue()) {
                    if (!zzkkVar.zzk()) {
                        return false;
                    }
                }
            } else {
                Object value = entry.getValue();
                if (value instanceof zzkk) {
                    if (!((zzkk) value).zzk()) {
                        return false;
                    }
                } else if (value instanceof zzjp) {
                    return true;
                } else {
                    throw new IllegalArgumentException("Wrong object type used with protocol message reflection.");
                }
            }
        }
        return true;
    }

    public final void zza(zziu<T> zziuVar) {
        for (int i = 0; i < zziuVar.zza.zzc(); i++) {
            zzb(zziuVar.zza.zzb(i));
        }
        for (Map.Entry<T, Object> entry : zziuVar.zza.zzd()) {
            zzb(entry);
        }
    }

    private static Object zza(Object obj) {
        if (obj instanceof zzkt) {
            return ((zzkt) obj).zza();
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
        zzkk zzkkVar;
        T key = entry.getKey();
        Object value = entry.getValue();
        if (value instanceof zzjp) {
            zzjp zzjpVar = (zzjp) value;
            value = zzjp.zza();
        }
        if (key.zzd()) {
            Object zza = zza((zziu<T>) key);
            if (zza == null) {
                zza = new ArrayList();
            }
            for (Object obj : (List) value) {
                ((List) zza).add(zza(obj));
            }
            this.zza.zza((zzlh<T, Object>) key, (T) zza);
        } else if (key.zzc() == zzmo.MESSAGE) {
            Object zza2 = zza((zziu<T>) key);
            if (zza2 == null) {
                this.zza.zza((zzlh<T, Object>) key, (T) zza(value));
                return;
            }
            if (zza2 instanceof zzkt) {
                zzkkVar = key.zza((zzkt) zza2, (zzkt) value);
            } else {
                zzkkVar = key.zza(((zzkk) zza2).zzp(), (zzkk) value).zzf();
            }
            this.zza.zza((zzlh<T, Object>) key, (T) zzkkVar);
        } else {
            this.zza.zza((zzlh<T, Object>) key, (T) zza(value));
        }
    }

    public static void zza(zzii zziiVar, zzml zzmlVar, int i, Object obj) throws IOException {
        if (zzmlVar == zzml.GROUP) {
            zzkk zzkkVar = (zzkk) obj;
            zzjf.zza(zzkkVar);
            zziiVar.zza(i, 3);
            zzkkVar.zza(zziiVar);
            zziiVar.zza(i, 4);
            return;
        }
        zziiVar.zza(i, zzmlVar.zzb());
        switch (zzit.zzb[zzmlVar.ordinal()]) {
            case 1:
                zziiVar.zza(((Double) obj).doubleValue());
                return;
            case 2:
                zziiVar.zza(((Float) obj).floatValue());
                return;
            case 3:
                zziiVar.zza(((Long) obj).longValue());
                return;
            case 4:
                zziiVar.zza(((Long) obj).longValue());
                return;
            case 5:
                zziiVar.zza(((Integer) obj).intValue());
                return;
            case 6:
                zziiVar.zzc(((Long) obj).longValue());
                return;
            case 7:
                zziiVar.zzd(((Integer) obj).intValue());
                return;
            case 8:
                zziiVar.zza(((Boolean) obj).booleanValue());
                return;
            case 9:
                ((zzkk) obj).zza(zziiVar);
                return;
            case 10:
                zziiVar.zza((zzkk) obj);
                return;
            case 11:
                if (obj instanceof zzht) {
                    zziiVar.zza((zzht) obj);
                    return;
                } else {
                    zziiVar.zza((String) obj);
                    return;
                }
            case 12:
                if (obj instanceof zzht) {
                    zziiVar.zza((zzht) obj);
                    return;
                }
                byte[] bArr = (byte[]) obj;
                zziiVar.zzb(bArr, 0, bArr.length);
                return;
            case 13:
                zziiVar.zzb(((Integer) obj).intValue());
                return;
            case 14:
                zziiVar.zzd(((Integer) obj).intValue());
                return;
            case 15:
                zziiVar.zzc(((Long) obj).longValue());
                return;
            case 16:
                zziiVar.zzc(((Integer) obj).intValue());
                return;
            case 17:
                zziiVar.zzb(((Long) obj).longValue());
                return;
            case 18:
                if (obj instanceof zzje) {
                    zziiVar.zza(((zzje) obj).zza());
                    return;
                } else {
                    zziiVar.zza(((Integer) obj).intValue());
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
        if (key.zzc() == zzmo.MESSAGE && !key.zzd() && !key.zze()) {
            if (value instanceof zzjp) {
                return zzii.zzb(entry.getKey().zza(), (zzjp) value);
            }
            return zzii.zzb(entry.getKey().zza(), (zzkk) value);
        }
        return zzc(key, value);
    }

    public static int zza(zzml zzmlVar, int i, Object obj) {
        int zze = zzii.zze(i);
        if (zzmlVar == zzml.GROUP) {
            zzjf.zza((zzkk) obj);
            zze <<= 1;
        }
        return zze + zza(zzmlVar, obj);
    }

    private static int zza(zzml zzmlVar, Object obj) {
        switch (zzit.zzb[zzmlVar.ordinal()]) {
            case 1:
                return zzii.zzb(((Double) obj).doubleValue());
            case 2:
                return zzii.zzb(((Float) obj).floatValue());
            case 3:
                return zzii.zzd(((Long) obj).longValue());
            case 4:
                return zzii.zze(((Long) obj).longValue());
            case 5:
                return zzii.zzf(((Integer) obj).intValue());
            case 6:
                return zzii.zzg(((Long) obj).longValue());
            case 7:
                return zzii.zzi(((Integer) obj).intValue());
            case 8:
                return zzii.zzb(((Boolean) obj).booleanValue());
            case 9:
                return zzii.zzc((zzkk) obj);
            case 10:
                if (obj instanceof zzjp) {
                    return zzii.zza((zzjp) obj);
                }
                return zzii.zzb((zzkk) obj);
            case 11:
                if (obj instanceof zzht) {
                    return zzii.zzb((zzht) obj);
                }
                return zzii.zzb((String) obj);
            case 12:
                if (obj instanceof zzht) {
                    return zzii.zzb((zzht) obj);
                }
                return zzii.zzb((byte[]) obj);
            case 13:
                return zzii.zzg(((Integer) obj).intValue());
            case 14:
                return zzii.zzj(((Integer) obj).intValue());
            case 15:
                return zzii.zzh(((Long) obj).longValue());
            case 16:
                return zzii.zzh(((Integer) obj).intValue());
            case 17:
                return zzii.zzf(((Long) obj).longValue());
            case 18:
                if (obj instanceof zzje) {
                    return zzii.zzk(((zzje) obj).zza());
                }
                return zzii.zzk(((Integer) obj).intValue());
            default:
                throw new RuntimeException("There is no way to get here, but the compiler thinks otherwise.");
        }
    }

    public static int zzc(zziw<?> zziwVar, Object obj) {
        zzml zzb = zziwVar.zzb();
        int zza = zziwVar.zza();
        if (zziwVar.zzd()) {
            int i = 0;
            if (zziwVar.zze()) {
                for (Object obj2 : (List) obj) {
                    i += zza(zzb, obj2);
                }
                return zzii.zze(zza) + i + zzii.zzl(i);
            }
            for (Object obj3 : (List) obj) {
                i += zza(zzb, zza, obj3);
            }
            return i;
        }
        return zza(zzb, zza, obj);
    }

    public final /* synthetic */ Object clone() throws CloneNotSupportedException {
        zziu zziuVar = new zziu();
        for (int i = 0; i < this.zza.zzc(); i++) {
            Map.Entry<T, Object> zzb = this.zza.zzb(i);
            zziuVar.zza((zziu) zzb.getKey(), zzb.getValue());
        }
        for (Map.Entry<T, Object> entry : this.zza.zzd()) {
            zziuVar.zza((zziu) entry.getKey(), entry.getValue());
        }
        zziuVar.zzc = this.zzc;
        return zziuVar;
    }
}
