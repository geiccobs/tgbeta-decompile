package com.google.android.gms.common.api.internal;

import java.lang.ref.WeakReference;
/* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
/* loaded from: classes.dex */
final class zaak extends com.google.android.gms.signin.internal.zab {
    private final WeakReference<zaaf> zaa;

    public zaak(zaaf zaafVar) {
        this.zaa = new WeakReference<>(zaafVar);
    }

    @Override // com.google.android.gms.signin.internal.zae
    public final void zaa(com.google.android.gms.signin.internal.zak zakVar) {
        zaaz zaazVar;
        zaaf zaafVar = this.zaa.get();
        if (zaafVar == null) {
            return;
        }
        zaazVar = zaafVar.zaa;
        zaazVar.zaa(new zaan(this, zaafVar, zaafVar, zakVar));
    }
}
