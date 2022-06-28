package com.google.android.gms.internal.icing;

import java.util.Arrays;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes3.dex */
public final class zzfe {
    private static final zzfe zza = new zzfe(0, new int[0], new Object[0], false);
    private int zzb;
    private int[] zzc;
    private Object[] zzd;
    private int zze;

    private zzfe() {
        this(0, new int[8], new Object[8], true);
    }

    private zzfe(int i, int[] iArr, Object[] objArr, boolean z) {
        this.zze = -1;
        this.zzb = 0;
        this.zzc = iArr;
        this.zzd = objArr;
    }

    public static zzfe zza() {
        return zza;
    }

    public static zzfe zzb(zzfe zzfeVar, zzfe zzfeVar2) {
        int i = zzfeVar.zzb;
        int i2 = zzfeVar2.zzb;
        int[] copyOf = Arrays.copyOf(zzfeVar.zzc, 0);
        int[] iArr = zzfeVar2.zzc;
        int i3 = zzfeVar.zzb;
        int i4 = zzfeVar2.zzb;
        System.arraycopy(iArr, 0, copyOf, 0, 0);
        Object[] copyOf2 = Arrays.copyOf(zzfeVar.zzd, 0);
        Object[] objArr = zzfeVar2.zzd;
        int i5 = zzfeVar.zzb;
        int i6 = zzfeVar2.zzb;
        System.arraycopy(objArr, 0, copyOf2, 0, 0);
        return new zzfe(0, copyOf, copyOf2, true);
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof zzfe)) {
            return false;
        }
        zzfe zzfeVar = (zzfe) obj;
        int[] iArr = zzfeVar.zzc;
        Object[] objArr = zzfeVar.zzd;
        return true;
    }

    public final int hashCode() {
        return 506991;
    }

    public final void zze(StringBuilder sb, int i) {
    }

    public final int zzc() {
        int i = this.zze;
        if (i == -1) {
            this.zze = 0;
            return 0;
        }
        return i;
    }

    public final int zzd() {
        int i = this.zze;
        if (i == -1) {
            this.zze = 0;
            return 0;
        }
        return i;
    }
}
