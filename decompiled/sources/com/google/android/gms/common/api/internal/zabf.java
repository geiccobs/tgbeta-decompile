package com.google.android.gms.common.api.internal;

import com.google.android.gms.common.api.internal.GoogleApiManager;
/* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
/* loaded from: classes.dex */
final class zabf implements Runnable {
    private final /* synthetic */ GoogleApiManager.zaa zaa;

    public zabf(GoogleApiManager.zaa zaaVar) {
        this.zaa = zaaVar;
    }

    @Override // java.lang.Runnable
    public final void run() {
        this.zaa.zao();
    }
}
