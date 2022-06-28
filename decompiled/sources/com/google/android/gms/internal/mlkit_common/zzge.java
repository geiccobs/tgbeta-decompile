package com.google.android.gms.internal.mlkit_common;

import java.util.Map;
/* compiled from: com.google.mlkit:common@@17.0.0 */
/* loaded from: classes3.dex */
final class zzge<K> implements Map.Entry<K, Object> {
    private Map.Entry<K, zzgc> zza;

    /* JADX INFO: Access modifiers changed from: private */
    public zzge(Map.Entry<K, zzgc> entry) {
        this.zza = entry;
    }

    @Override // java.util.Map.Entry
    public final K getKey() {
        return this.zza.getKey();
    }

    @Override // java.util.Map.Entry
    public final Object getValue() {
        if (this.zza.getValue() == null) {
            return null;
        }
        return zzgc.zza();
    }

    public final zzgc zza() {
        return this.zza.getValue();
    }

    @Override // java.util.Map.Entry
    public final Object setValue(Object obj) {
        if (!(obj instanceof zzhb)) {
            throw new IllegalArgumentException("LazyField now only used for MessageSet, and the value of MessageSet must be an instance of MessageLite");
        }
        return this.zza.getValue().zza((zzhb) obj);
    }
}
