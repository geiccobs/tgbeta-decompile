package com.google.android.gms.internal.mlkit_language_id;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes3.dex */
public final class zzft<K, V> extends LinkedHashMap<K, V> {
    private static final zzft zzb;
    private boolean zza = true;

    private zzft() {
    }

    private zzft(Map<K, V> map) {
        super(map);
    }

    public final void zza(zzft<K, V> zzftVar) {
        zzd();
        if (!zzftVar.isEmpty()) {
            putAll(zzftVar);
        }
    }

    @Override // java.util.LinkedHashMap, java.util.HashMap, java.util.AbstractMap, java.util.Map
    public final Set<Map.Entry<K, V>> entrySet() {
        return isEmpty() ? Collections.emptySet() : super.entrySet();
    }

    @Override // java.util.LinkedHashMap, java.util.HashMap, java.util.AbstractMap, java.util.Map
    public final void clear() {
        zzd();
        super.clear();
    }

    @Override // java.util.HashMap, java.util.AbstractMap, java.util.Map
    public final V put(K k, V v) {
        zzd();
        zzeq.zza(k);
        zzeq.zza(v);
        return (V) super.put(k, v);
    }

    @Override // java.util.HashMap, java.util.AbstractMap, java.util.Map
    public final void putAll(Map<? extends K, ? extends V> map) {
        zzd();
        for (K k : map.keySet()) {
            zzeq.zza(k);
            zzeq.zza(map.get(k));
        }
        super.putAll(map);
    }

    @Override // java.util.HashMap, java.util.AbstractMap, java.util.Map
    public final V remove(Object obj) {
        zzd();
        return (V) super.remove(obj);
    }

    /* JADX WARN: Removed duplicated region for block: B:25:0x005f A[RETURN] */
    @Override // java.util.AbstractMap, java.util.Map
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public final boolean equals(java.lang.Object r7) {
        /*
            r6 = this;
            boolean r0 = r7 instanceof java.util.Map
            r1 = 0
            if (r0 == 0) goto L60
            java.util.Map r7 = (java.util.Map) r7
            r0 = 1
            if (r6 == r7) goto L5c
            int r2 = r6.size()
            int r3 = r7.size()
            if (r2 == r3) goto L16
            r7 = 0
            goto L5d
        L16:
            java.util.Set r2 = r6.entrySet()
            java.util.Iterator r2 = r2.iterator()
        L1e:
            boolean r3 = r2.hasNext()
            if (r3 == 0) goto L5c
            java.lang.Object r3 = r2.next()
            java.util.Map$Entry r3 = (java.util.Map.Entry) r3
            java.lang.Object r4 = r3.getKey()
            boolean r4 = r7.containsKey(r4)
            if (r4 != 0) goto L36
            r7 = 0
            goto L5d
        L36:
            java.lang.Object r4 = r3.getValue()
            java.lang.Object r3 = r3.getKey()
            java.lang.Object r3 = r7.get(r3)
            boolean r5 = r4 instanceof byte[]
            if (r5 == 0) goto L53
            boolean r5 = r3 instanceof byte[]
            if (r5 == 0) goto L53
            byte[] r4 = (byte[]) r4
            byte[] r3 = (byte[]) r3
            boolean r3 = java.util.Arrays.equals(r4, r3)
            goto L57
        L53:
            boolean r3 = r4.equals(r3)
        L57:
            if (r3 != 0) goto L5b
            r7 = 0
            goto L5d
        L5b:
            goto L1e
        L5c:
            r7 = 1
        L5d:
            if (r7 == 0) goto L60
            return r0
        L60:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.mlkit_language_id.zzft.equals(java.lang.Object):boolean");
    }

    private static int zza(Object obj) {
        if (obj instanceof byte[]) {
            return zzeq.zzc((byte[]) obj);
        }
        if (obj instanceof zzet) {
            throw new UnsupportedOperationException();
        }
        return obj.hashCode();
    }

    @Override // java.util.AbstractMap, java.util.Map
    public final int hashCode() {
        int i = 0;
        for (Map.Entry<K, V> entry : entrySet()) {
            i += zza(entry.getValue()) ^ zza(entry.getKey());
        }
        return i;
    }

    public final zzft<K, V> zza() {
        return isEmpty() ? new zzft<>() : new zzft<>(this);
    }

    public final void zzb() {
        this.zza = false;
    }

    public final boolean zzc() {
        return this.zza;
    }

    private final void zzd() {
        if (!this.zza) {
            throw new UnsupportedOperationException();
        }
    }

    static {
        zzft zzftVar = new zzft();
        zzb = zzftVar;
        zzftVar.zza = false;
    }
}
