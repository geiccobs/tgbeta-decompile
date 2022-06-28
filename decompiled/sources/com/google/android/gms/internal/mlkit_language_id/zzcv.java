package com.google.android.gms.internal.mlkit_language_id;

import android.content.Context;
import android.content.res.Resources;
import android.os.SystemClock;
import androidx.core.os.ConfigurationCompat;
import androidx.core.os.LocaleListCompat;
import com.google.android.gms.common.internal.LibraryVersion;
import com.google.android.gms.internal.mlkit_language_id.zzy;
import com.google.android.gms.tasks.Task;
import com.google.firebase.components.Component;
import com.google.firebase.components.ComponentContainer;
import com.google.firebase.components.Dependency;
import com.google.mlkit.common.sdkinternal.CommonUtils;
import com.google.mlkit.common.sdkinternal.MLTaskExecutor;
import com.google.mlkit.common.sdkinternal.SharedPrefManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes3.dex */
public final class zzcv {
    private static List<String> zzb;
    private final String zzc;
    private final String zzd;
    private final zzb zze;
    private final SharedPrefManager zzf;
    private final Task<String> zzh;
    private static boolean zzk = true;
    private static boolean zzl = true;
    public static final Component<?> zza = Component.builder(zzcv.class).add(Dependency.required(Context.class)).add(Dependency.required(SharedPrefManager.class)).add(Dependency.required(zzb.class)).factory(zzcy.zza).build();
    private final Map<zzaj, Long> zzi = new HashMap();
    private final Map<zzaj, Object> zzj = new HashMap();
    private final Task<String> zzg = MLTaskExecutor.getInstance().scheduleCallable(zzcu.zza);

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes3.dex */
    public interface zza {
        zzy.zzad.zza zza();
    }

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes3.dex */
    public interface zzb {
        void zza(zzy.zzad zzadVar);
    }

    private zzcv(Context context, SharedPrefManager sharedPrefManager, zzb zzbVar) {
        this.zzc = context.getPackageName();
        this.zzd = CommonUtils.getAppVersion(context);
        this.zzf = sharedPrefManager;
        this.zze = zzbVar;
        MLTaskExecutor mLTaskExecutor = MLTaskExecutor.getInstance();
        sharedPrefManager.getClass();
        this.zzh = mLTaskExecutor.scheduleCallable(zzcx.zza(sharedPrefManager));
    }

    public final void zza(zzy.zzad.zza zzaVar, zzaj zzajVar) {
        MLTaskExecutor.workerThreadExecutor().execute(new Runnable(this, zzaVar, zzajVar) { // from class: com.google.android.gms.internal.mlkit_language_id.zzcw
            private final zzcv zza;
            private final zzy.zzad.zza zzb;
            private final zzaj zzc;

            /* JADX INFO: Access modifiers changed from: package-private */
            {
                this.zza = this;
                this.zzb = zzaVar;
                this.zzc = zzajVar;
            }

            @Override // java.lang.Runnable
            public final void run() {
                this.zza.zzb(this.zzb, this.zzc);
            }
        });
    }

    public final void zza(zza zzaVar, zzaj zzajVar) {
        long elapsedRealtime = SystemClock.elapsedRealtime();
        boolean z = true;
        if (this.zzi.get(zzajVar) != null && elapsedRealtime - this.zzi.get(zzajVar).longValue() <= TimeUnit.SECONDS.toMillis(30L)) {
            z = false;
        }
        if (!z) {
            return;
        }
        this.zzi.put(zzajVar, Long.valueOf(elapsedRealtime));
        zza(zzaVar.zza(), zzajVar);
    }

    private static synchronized List<String> zzb() {
        synchronized (zzcv.class) {
            List<String> list = zzb;
            if (list != null) {
                return list;
            }
            LocaleListCompat locales = ConfigurationCompat.getLocales(Resources.getSystem().getConfiguration());
            zzb = new ArrayList(locales.size());
            for (int i = 0; i < locales.size(); i++) {
                zzb.add(CommonUtils.languageTagFromLocale(locales.get(i)));
            }
            return zzb;
        }
    }

    public final /* synthetic */ void zzb(zzy.zzad.zza zzaVar, zzaj zzajVar) {
        String str;
        String str2;
        String zza2 = zzaVar.zza().zza();
        if ("NA".equals(zza2) || "".equals(zza2)) {
            zza2 = "NA";
        }
        zzy.zzbh.zza zzb2 = zzy.zzbh.zzb().zza(this.zzc).zzb(this.zzd).zzd(zza2).zza(zzb()).zzb(true);
        if (this.zzg.isSuccessful()) {
            str = this.zzg.getResult();
        } else {
            str = LibraryVersion.getInstance().getVersion("language-id");
        }
        zzy.zzbh.zza zzc = zzb2.zzc(str);
        if (zzl) {
            if (this.zzh.isSuccessful()) {
                str2 = this.zzh.getResult();
            } else {
                str2 = this.zzf.getMlSdkInstanceId();
            }
            zzc.zze(str2);
        }
        zzaVar.zza(zzajVar).zza(zzc);
        this.zze.zza((zzy.zzad) ((zzeo) zzaVar.zzg()));
    }

    public static final /* synthetic */ zzcv zza(ComponentContainer componentContainer) {
        return new zzcv((Context) componentContainer.get(Context.class), (SharedPrefManager) componentContainer.get(SharedPrefManager.class), (zzb) componentContainer.get(zzb.class));
    }
}
