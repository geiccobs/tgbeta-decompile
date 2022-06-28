package com.google.android.gms.internal.mlkit_common;
/* compiled from: com.google.mlkit:common@@17.0.0 */
/* loaded from: classes3.dex */
public enum zzjl implements zzfv {
    UNKNOWN_EVENT_TYPE(0),
    VALIDATION_TEST(1),
    CONTINUOUS_FEEDBACK(2);
    
    private static final zzfu<zzjl> zzd = new zzfu<zzjl>() { // from class: com.google.android.gms.internal.mlkit_common.zzjo
    };
    private final int zze;

    @Override // com.google.android.gms.internal.mlkit_common.zzfv
    public final int zza() {
        return this.zze;
    }

    public static zzfx zzb() {
        return zzjn.zza;
    }

    @Override // java.lang.Enum
    public final String toString() {
        return "<" + getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + " number=" + this.zze + " name=" + name() + '>';
    }

    zzjl(int i) {
        this.zze = i;
    }
}
