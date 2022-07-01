package com.google.android.gms.common.api.internal;
/* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
/* loaded from: classes.dex */
final class zacf implements Runnable {
    private final /* synthetic */ com.google.android.gms.signin.internal.zak zaa;
    private final /* synthetic */ zace zab;

    public zacf(zace zaceVar, com.google.android.gms.signin.internal.zak zakVar) {
        this.zab = zaceVar;
        this.zaa = zakVar;
    }

    @Override // java.lang.Runnable
    public final void run() {
        this.zab.zab(this.zaa);
    }
}
