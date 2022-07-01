package com.google.android.gms.common.api.internal;

import android.os.Bundle;
/* compiled from: com.google.android.gms:play-services-basement@@17.5.0 */
/* loaded from: classes.dex */
final class zza implements Runnable {
    private final /* synthetic */ LifecycleCallback zza;
    private final /* synthetic */ String zzb;
    private final /* synthetic */ zzb zzc;

    public zza(zzb zzbVar, LifecycleCallback lifecycleCallback, String str) {
        this.zzc = zzbVar;
        this.zza = lifecycleCallback;
        this.zzb = str;
    }

    @Override // java.lang.Runnable
    public final void run() {
        int i;
        int i2;
        int i3;
        int i4;
        int i5;
        Bundle bundle;
        Bundle bundle2;
        Bundle bundle3;
        i = this.zzc.zzc;
        if (i > 0) {
            LifecycleCallback lifecycleCallback = this.zza;
            bundle = this.zzc.zzd;
            if (bundle != null) {
                bundle3 = this.zzc.zzd;
                bundle2 = bundle3.getBundle(this.zzb);
            } else {
                bundle2 = null;
            }
            lifecycleCallback.onCreate(bundle2);
        }
        i2 = this.zzc.zzc;
        if (i2 >= 2) {
            this.zza.onStart();
        }
        i3 = this.zzc.zzc;
        if (i3 >= 3) {
            this.zza.onResume();
        }
        i4 = this.zzc.zzc;
        if (i4 >= 4) {
            this.zza.onStop();
        }
        i5 = this.zzc.zzc;
        if (i5 >= 5) {
            this.zza.onDestroy();
        }
    }
}
