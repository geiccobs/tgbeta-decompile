package com.google.android.gms.wallet.callback;

import android.os.Messenger;
import android.util.Log;
import java.util.Locale;
/* compiled from: com.google.android.gms:play-services-wallet@@18.1.3 */
/* loaded from: classes3.dex */
final class zzc implements Runnable {
    final /* synthetic */ zzd zza;
    private final CallbackInput zzb;
    private final String zzc;
    private final zzb zzd;

    public zzc(zzd zzdVar, CallbackInput callbackInput, Messenger messenger, String str, int i) {
        this.zza = zzdVar;
        this.zzb = callbackInput;
        this.zzc = str;
        this.zzd = new zzb(messenger, i);
    }

    @Override // java.lang.Runnable
    public final void run() {
        if (Log.isLoggable("BaseCallbackTaskService", 4)) {
            Log.i("BaseCallbackTaskService", String.format(Locale.US, "Running Callback Task w/ tag %s", this.zzc));
        }
        try {
            this.zza.onRunTask(this.zzc, this.zzb, this.zzd);
        } catch (Throwable th) {
            zzb zzbVar = this.zzd;
            zzj zza = CallbackOutput.zza();
            int i = this.zzb.zza;
            CallbackOutput callbackOutput = zza.zza;
            callbackOutput.zza = i;
            callbackOutput.zzb = 5;
            String message = th.getMessage();
            CallbackOutput callbackOutput2 = zza.zza;
            callbackOutput2.zzd = message;
            zzbVar.complete(callbackOutput2);
            throw th;
        }
    }
}
