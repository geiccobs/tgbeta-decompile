package com.google.android.gms.internal.vision;

import org.checkerframework.checker.nullness.compatqual.NullableDecl;
/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
/* loaded from: classes3.dex */
public final class zzcv<T> extends zzcy<T> {
    static final zzcv<Object> zza = new zzcv<>();

    private zzcv() {
    }

    @Override // com.google.android.gms.internal.vision.zzcy
    public final boolean zza() {
        return false;
    }

    @Override // com.google.android.gms.internal.vision.zzcy
    public final T zzb() {
        throw new IllegalStateException("Optional.get() cannot be called on an absent value");
    }

    public final boolean equals(@NullableDecl Object obj) {
        return obj == this;
    }

    public final int hashCode() {
        return 2040732332;
    }

    public final String toString() {
        return "Optional.absent()";
    }
}
