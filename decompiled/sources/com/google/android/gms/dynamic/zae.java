package com.google.android.gms.dynamic;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.google.android.gms.dynamic.DeferredLifecycleHelper;
/* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
/* loaded from: classes3.dex */
public final class zae implements DeferredLifecycleHelper.zaa {
    private final /* synthetic */ FrameLayout zaa;
    private final /* synthetic */ LayoutInflater zab;
    private final /* synthetic */ ViewGroup zac;
    private final /* synthetic */ Bundle zad;
    private final /* synthetic */ DeferredLifecycleHelper zae;

    public zae(DeferredLifecycleHelper deferredLifecycleHelper, FrameLayout frameLayout, LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.zae = deferredLifecycleHelper;
        this.zaa = frameLayout;
        this.zab = layoutInflater;
        this.zac = viewGroup;
        this.zad = bundle;
    }

    @Override // com.google.android.gms.dynamic.DeferredLifecycleHelper.zaa
    public final int zaa() {
        return 2;
    }

    @Override // com.google.android.gms.dynamic.DeferredLifecycleHelper.zaa
    public final void zaa(LifecycleDelegate lifecycleDelegate) {
        LifecycleDelegate lifecycleDelegate2;
        this.zaa.removeAllViews();
        FrameLayout frameLayout = this.zaa;
        lifecycleDelegate2 = this.zae.zaa;
        frameLayout.addView(lifecycleDelegate2.onCreateView(this.zab, this.zac, this.zad));
    }
}
