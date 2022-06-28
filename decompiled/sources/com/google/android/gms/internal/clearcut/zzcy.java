package com.google.android.gms.internal.clearcut;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes3.dex */
public abstract class zzcy {
    private static final zzcy zzlt = new zzda();
    private static final zzcy zzlu = new zzdb();

    /* JADX INFO: Access modifiers changed from: private */
    public zzcy() {
    }

    public static zzcy zzbv() {
        return zzlt;
    }

    public static zzcy zzbw() {
        return zzlu;
    }

    public abstract void zza(Object obj, long j);

    public abstract <L> void zza(Object obj, Object obj2, long j);
}
