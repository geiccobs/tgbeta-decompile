package com.google.android.gms.wearable;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
public final class zzl extends com.google.android.gms.internal.wearable.zzi {
    final /* synthetic */ WearableListenerService zza;
    private boolean zzb;
    private final zzk zzc;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public zzl(WearableListenerService wearableListenerService, Looper looper) {
        super(looper);
        this.zza = wearableListenerService;
        this.zzc = new zzk(wearableListenerService, null);
    }

    private final synchronized void zzc() {
        Intent intent;
        ComponentName componentName;
        if (this.zzb) {
            return;
        }
        if (Log.isLoggable("WearableLS", 2)) {
            componentName = this.zza.zza;
            String valueOf = String.valueOf(componentName);
            StringBuilder sb = new StringBuilder(String.valueOf(valueOf).length() + 13);
            sb.append("bindService: ");
            sb.append(valueOf);
            Log.v("WearableLS", sb.toString());
        }
        WearableListenerService wearableListenerService = this.zza;
        intent = wearableListenerService.zzd;
        wearableListenerService.bindService(intent, this.zzc, 1);
        this.zzb = true;
    }

    private final synchronized void zzd(String str) {
        ComponentName componentName;
        if (!this.zzb) {
            return;
        }
        if (Log.isLoggable("WearableLS", 2)) {
            componentName = this.zza.zza;
            String valueOf = String.valueOf(componentName);
            StringBuilder sb = new StringBuilder(str.length() + 17 + String.valueOf(valueOf).length());
            sb.append("unbindService: ");
            sb.append(str);
            sb.append(", ");
            sb.append(valueOf);
            Log.v("WearableLS", sb.toString());
        }
        try {
            this.zza.unbindService(this.zzc);
        } catch (RuntimeException e) {
            Log.e("WearableLS", "Exception when unbinding from local service", e);
        }
        this.zzb = false;
    }

    @Override // com.google.android.gms.internal.wearable.zzi
    public final void zza(Message message) {
        zzc();
        try {
            super.zza(message);
            if (hasMessages(0)) {
                return;
            }
            zzd("dispatch");
        } catch (Throwable th) {
            if (!hasMessages(0)) {
                zzd("dispatch");
            }
            throw th;
        }
    }

    public final void zzb() {
        getLooper().quit();
        zzd("quit");
    }
}
