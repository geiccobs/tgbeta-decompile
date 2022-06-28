package com.google.android.gms.internal.vision;

import java.util.Collection;
import java.util.Map;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;
/* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
/* loaded from: classes3.dex */
abstract class zzdo<K, V> implements zzen<K, V> {
    @NullableDecl
    private transient Map<K, Collection<V>> zza;

    abstract Map<K, Collection<V>> zzb();

    public boolean zza(@NullableDecl Object obj) {
        for (Collection<V> collection : zza().values()) {
            if (collection.contains(obj)) {
                return true;
            }
        }
        return false;
    }

    @Override // com.google.android.gms.internal.vision.zzen
    public Map<K, Collection<V>> zza() {
        Map<K, Collection<V>> map = this.zza;
        if (map == null) {
            Map<K, Collection<V>> zzb = zzb();
            this.zza = zzb;
            return zzb;
        }
        return map;
    }

    public boolean equals(@NullableDecl Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof zzen) {
            return zza().equals(((zzen) obj).zza());
        }
        return false;
    }

    public int hashCode() {
        return zza().hashCode();
    }

    public String toString() {
        return zza().toString();
    }
}
