package com.google.android.gms.internal.firebase_messaging;

import java.lang.annotation.Annotation;
/* compiled from: com.google.firebase:firebase-messaging@@22.0.0 */
/* loaded from: classes.dex */
final class zzu implements zzz {
    private final int zza;
    private final zzy zzb;

    public zzu(int i, zzy zzyVar) {
        this.zza = i;
        this.zzb = zzyVar;
    }

    @Override // java.lang.annotation.Annotation
    public final Class<? extends Annotation> annotationType() {
        return zzz.class;
    }

    @Override // java.lang.annotation.Annotation
    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof zzz)) {
            return false;
        }
        zzz zzzVar = (zzz) obj;
        return this.zza == zzzVar.zza() && this.zzb.equals(zzzVar.zzb());
    }

    @Override // java.lang.annotation.Annotation
    public final int hashCode() {
        return (this.zza ^ 14552422) + (this.zzb.hashCode() ^ 2041407134);
    }

    @Override // java.lang.annotation.Annotation
    public final String toString() {
        return "@com.google.firebase.encoders.proto.Protobuf(tag=" + this.zza + "intEncoding=" + this.zzb + ')';
    }

    @Override // com.google.android.gms.internal.firebase_messaging.zzz
    public final int zza() {
        return this.zza;
    }

    @Override // com.google.android.gms.internal.firebase_messaging.zzz
    public final zzy zzb() {
        return this.zzb;
    }
}
