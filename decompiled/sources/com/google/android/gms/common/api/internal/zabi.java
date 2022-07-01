package com.google.android.gms.common.api.internal;

import com.google.android.gms.common.api.Api;
/* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
/* loaded from: classes.dex */
final class zabi implements Runnable {
    private final /* synthetic */ zabg zaa;

    public zabi(zabg zabgVar) {
        this.zaa = zabgVar;
    }

    @Override // java.lang.Runnable
    public final void run() {
        Api.Client client;
        Api.Client client2;
        client = this.zaa.zaa.zac;
        client2 = this.zaa.zaa.zac;
        client.disconnect(client2.getClass().getName().concat(" disconnecting because it was signed out."));
    }
}
