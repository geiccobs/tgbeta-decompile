package com.google.android.gms.internal.mlkit_common;

import android.content.Context;
import android.content.res.Resources;
import androidx.core.os.ConfigurationCompat;
import androidx.core.os.LocaleListCompat;
import com.google.android.gms.common.internal.LibraryVersion;
import com.google.android.gms.internal.mlkit_common.zzav;
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
/* compiled from: com.google.mlkit:common@@17.0.0 */
/* loaded from: classes3.dex */
public final class zzds {
    private static List<String> zzb;
    private final String zzc;
    private final String zzd;
    private final zza zze;
    private final SharedPrefManager zzf;
    private final Task<String> zzh;
    private static boolean zzk = true;
    private static boolean zzl = true;
    public static final Component<?> zza = Component.builder(zzds.class).add(Dependency.required(Context.class)).add(Dependency.required(SharedPrefManager.class)).add(Dependency.required(zza.class)).factory(zzdv.zza).build();
    private final Map<zzbg, Long> zzi = new HashMap();
    private final Map<zzbg, Object> zzj = new HashMap();
    private final Task<String> zzg = MLTaskExecutor.getInstance().scheduleCallable(zzdr.zza);

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes3.dex */
    public interface zza {
        void zza(zzav.zzad zzadVar);
    }

    private zzds(Context context, SharedPrefManager sharedPrefManager, zza zzaVar) {
        this.zzc = context.getPackageName();
        this.zzd = CommonUtils.getAppVersion(context);
        this.zzf = sharedPrefManager;
        this.zze = zzaVar;
        MLTaskExecutor mLTaskExecutor = MLTaskExecutor.getInstance();
        sharedPrefManager.getClass();
        this.zzh = mLTaskExecutor.scheduleCallable(zzdu.zza(sharedPrefManager));
    }

    public final void zza(zzav.zzad.zza zzaVar, zzbg zzbgVar) {
        MLTaskExecutor.workerThreadExecutor().execute(new Runnable(this, zzaVar, zzbgVar) { // from class: com.google.android.gms.internal.mlkit_common.zzdt
            private final zzds zza;
            private final zzav.zzad.zza zzb;
            private final zzbg zzc;

            /* JADX INFO: Access modifiers changed from: package-private */
            {
                this.zza = this;
                this.zzb = zzaVar;
                this.zzc = zzbgVar;
            }

            @Override // java.lang.Runnable
            public final void run() {
                this.zza.zzb(this.zzb, this.zzc);
            }
        });
    }

    private static synchronized List<String> zzb() {
        synchronized (zzds.class) {
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

    public final /* synthetic */ void zzb(zzav.zzad.zza zzaVar, zzbg zzbgVar) {
        String str;
        String str2;
        String zza2 = zzaVar.zza().zza();
        if ("NA".equals(zza2) || "".equals(zza2)) {
            zza2 = "NA";
        }
        zzav.zzbh.zza zzb2 = zzav.zzbh.zzb().zza(this.zzc).zzb(this.zzd).zzd(zza2).zza(zzb()).zzb(true);
        if (this.zzg.isSuccessful()) {
            str = this.zzg.getResult();
        } else {
            str = LibraryVersion.getInstance().getVersion("common");
        }
        zzav.zzbh.zza zzc = zzb2.zzc(str);
        if (zzl) {
            if (this.zzh.isSuccessful()) {
                str2 = this.zzh.getResult();
            } else {
                str2 = this.zzf.getMlSdkInstanceId();
            }
            zzc.zze(str2);
        }
        zzaVar.zza(zzbgVar).zza(zzc);
        this.zze.zza((zzav.zzad) ((zzfq) zzaVar.zzg()));
    }

    public static final /* synthetic */ zzds zza(ComponentContainer componentContainer) {
        return new zzds((Context) componentContainer.get(Context.class), (SharedPrefManager) componentContainer.get(SharedPrefManager.class), (zza) componentContainer.get(zza.class));
    }
}
