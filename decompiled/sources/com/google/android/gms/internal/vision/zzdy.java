package com.google.android.gms.internal.vision;

import java.util.Map;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;
/* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
/* loaded from: classes3.dex */
final class zzdy extends zzdl<K, V> {
    @NullableDecl
    private final K zza;
    private int zzb;
    private final /* synthetic */ zzdp zzc;

    /* JADX WARN: Multi-variable type inference failed */
    public zzdy(zzdp zzdpVar, int i) {
        this.zzc = zzdpVar;
        this.zza = zzdpVar.zzb[i];
        this.zzb = i;
    }

    @Override // com.google.android.gms.internal.vision.zzdl, java.util.Map.Entry
    @NullableDecl
    public final K getKey() {
        return this.zza;
    }

    private final void zza() {
        int zza;
        int i = this.zzb;
        if (i != -1 && i < this.zzc.size() && zzcz.zza(this.zza, this.zzc.zzb[this.zzb])) {
            return;
        }
        zza = this.zzc.zza(this.zza);
        this.zzb = zza;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v7, types: [V, java.lang.Object] */
    @Override // com.google.android.gms.internal.vision.zzdl, java.util.Map.Entry
    @NullableDecl
    public final V getValue() {
        Map zzb = this.zzc.zzb();
        if (zzb != null) {
            return zzb.get(this.zza);
        }
        zza();
        if (this.zzb != -1) {
            return this.zzc.zzc[this.zzb];
        }
        return null;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r4v2, types: [V, java.lang.Object] */
    @Override // com.google.android.gms.internal.vision.zzdl, java.util.Map.Entry
    public final V setValue(V v) {
        Map zzb = this.zzc.zzb();
        if (zzb != null) {
            return zzb.put(this.zza, v);
        }
        zza();
        if (this.zzb == -1) {
            this.zzc.put(this.zza, v);
            return null;
        }
        V v2 = this.zzc.zzc[this.zzb];
        this.zzc.zzc[this.zzb] = v;
        return v2;
    }
}
