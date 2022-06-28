package com.google.mlkit.common.sdkinternal;

import android.util.Log;
import com.google.android.gms.internal.mlkit_common.zzav;
import com.google.android.gms.internal.mlkit_common.zzbg;
import com.google.android.gms.internal.mlkit_common.zzds;
import com.google.mlkit.common.sdkinternal.Cleaner;
import java.io.Closeable;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
/* compiled from: com.google.mlkit:common@@17.0.0 */
/* loaded from: classes3.dex */
public class CloseGuard implements Closeable {
    public static final int API_TRANSLATE = 1;
    private final AtomicBoolean zza = new AtomicBoolean();
    private final String zzb;
    private final Runnable zzc;
    private final Cleaner.Cleanable zzd;
    private final zzds zze;
    private final zzav.zzaj.zza zzf;

    CloseGuard(Object obj, zzav.zzaj.zza zzaVar, Cleaner cleaner, zzds zzdsVar, Runnable runnable) {
        this.zzf = zzaVar;
        this.zze = zzdsVar;
        this.zzb = obj.toString();
        this.zzc = runnable;
        this.zzd = cleaner.register(obj, new Runnable(this) { // from class: com.google.mlkit.common.sdkinternal.zze
            private final CloseGuard zza;

            /* JADX INFO: Access modifiers changed from: package-private */
            {
                this.zza = this;
            }

            @Override // java.lang.Runnable
            public final void run() {
                this.zza.zza();
            }
        });
    }

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes3.dex */
    public static class Factory {
        private final Cleaner zza;
        private final zzds zzb;

        public Factory(Cleaner cleaner, zzds zzdsVar) {
            this.zza = cleaner;
            this.zzb = zzdsVar;
        }

        public CloseGuard create(Object obj, int i, Runnable runnable) {
            return new CloseGuard(obj, zzav.zzaj.zza.zza(i), this.zza, this.zzb, runnable);
        }
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        this.zza.set(true);
        this.zzd.clean();
    }

    public final /* synthetic */ void zza() {
        if (!this.zza.get()) {
            Log.e("MlKitCloseGuard", String.format(Locale.ENGLISH, "%s has not been closed", this.zzb));
            zzav.zzad.zza zzb = zzav.zzad.zzb();
            zzb.zza(zzav.zzaj.zza().zza(this.zzf));
            this.zze.zza(zzb, zzbg.HANDLE_LEAKED);
        }
        this.zzc.run();
    }
}
