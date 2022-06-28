package com.google.android.gms.internal.mlkit_language_id;

import java.util.Map;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes3.dex */
final class zzfc<K> implements Map.Entry<K, Object> {
    private Map.Entry<K, zzfa> zza;

    /* JADX INFO: Access modifiers changed from: private */
    public zzfc(Map.Entry<K, zzfa> entry) {
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
        return zzfa.zza();
    }

    public final zzfa zza() {
        return this.zza.getValue();
    }

    @Override // java.util.Map.Entry
    public final Object setValue(Object obj) {
        if (!(obj instanceof zzfz)) {
            throw new IllegalArgumentException("LazyField now only used for MessageSet, and the value of MessageSet must be an instance of MessageLite");
        }
        return this.zza.getValue().zza((zzfz) obj);
    }
}
