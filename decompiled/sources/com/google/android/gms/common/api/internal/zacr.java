package com.google.android.gms.common.api.internal;
/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
/* loaded from: classes.dex */
public final class zacr implements zacq {
    private final /* synthetic */ zaco zaa;

    public zacr(zaco zacoVar) {
        this.zaa = zacoVar;
    }

    @Override // com.google.android.gms.common.api.internal.zacq
    public final void zaa(BasePendingResult<?> basePendingResult) {
        this.zaa.zab.remove(basePendingResult);
    }
}
