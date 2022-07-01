package com.google.android.gms.wallet;

import android.os.Handler;
import android.os.Looper;
import android.util.SparseArray;
import com.google.android.gms.internal.wallet.zzh;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.AutoResolvableResult;
import java.util.concurrent.atomic.AtomicInteger;
/* compiled from: com.google.android.gms:play-services-wallet@@18.1.3 */
/* loaded from: classes.dex */
public final class zzc<TResult extends AutoResolvableResult> implements OnCompleteListener<TResult>, Runnable {
    static final Handler zza = new zzh(Looper.getMainLooper());
    static final SparseArray<zzc<?>> zzb = new SparseArray<>(2);
    private static final AtomicInteger zzd = new AtomicInteger();
    int zzc;
    private zzd zze;
    private Task<TResult> zzf;

    zzc() {
    }

    public static <TResult extends AutoResolvableResult> zzc<TResult> zza(Task<TResult> task) {
        long j;
        zzc<TResult> zzcVar = new zzc<>();
        int incrementAndGet = zzd.incrementAndGet();
        zzcVar.zzc = incrementAndGet;
        zzb.put(incrementAndGet, zzcVar);
        Handler handler = zza;
        j = AutoResolveHelper.zzb;
        handler.postDelayed(zzcVar, j);
        task.addOnCompleteListener(zzcVar);
        return zzcVar;
    }

    private final void zzd() {
        if (this.zzf == null || this.zze == null) {
            return;
        }
        zzb.delete(this.zzc);
        zza.removeCallbacks(this);
        zzd zzdVar = this.zze;
        if (zzdVar == null) {
            return;
        }
        zzdVar.zzb(this.zzf);
    }

    @Override // com.google.android.gms.tasks.OnCompleteListener
    public final void onComplete(Task<TResult> task) {
        this.zzf = task;
        zzd();
    }

    @Override // java.lang.Runnable
    public final void run() {
        zzb.delete(this.zzc);
    }

    public final void zzb(zzd zzdVar) {
        if (this.zze == zzdVar) {
            this.zze = null;
        }
    }

    public final void zzc(zzd zzdVar) {
        this.zze = zzdVar;
        zzd();
    }
}
