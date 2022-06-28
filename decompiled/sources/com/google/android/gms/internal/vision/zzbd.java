package com.google.android.gms.internal.vision;

import android.content.Context;
import android.database.ContentObserver;
import android.util.Log;
import androidx.core.content.PermissionChecker;
import javax.annotation.Nullable;
/* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
/* loaded from: classes.dex */
public final class zzbd implements zzay {
    private static zzbd zza;
    @Nullable
    private final Context zzb;
    @Nullable
    private final ContentObserver zzc;

    public static zzbd zza(Context context) {
        zzbd zzbdVar;
        synchronized (zzbd.class) {
            if (zza == null) {
                zza = PermissionChecker.checkSelfPermission(context, "com.google.android.providers.gsf.permission.READ_GSERVICES") == 0 ? new zzbd(context) : new zzbd();
            }
            zzbdVar = zza;
        }
        return zzbdVar;
    }

    private zzbd(Context context) {
        this.zzb = context;
        zzbf zzbfVar = new zzbf(this, null);
        this.zzc = zzbfVar;
        context.getContentResolver().registerContentObserver(zzaq.zza, true, zzbfVar);
    }

    private zzbd() {
        this.zzb = null;
        this.zzc = null;
    }

    /* renamed from: zzc */
    public final String zza(String str) {
        if (this.zzb == null) {
            return null;
        }
        try {
            return (String) zzbb.zza(new zzba(this, str) { // from class: com.google.android.gms.internal.vision.zzbc
                private final zzbd zza;
                private final String zzb;

                /* JADX INFO: Access modifiers changed from: package-private */
                {
                    this.zza = this;
                    this.zzb = str;
                }

                @Override // com.google.android.gms.internal.vision.zzba
                public final Object zza() {
                    return this.zza.zzb(this.zzb);
                }
            });
        } catch (IllegalStateException | SecurityException e) {
            String valueOf = String.valueOf(str);
            Log.e("GservicesLoader", valueOf.length() != 0 ? "Unable to read GServices for: ".concat(valueOf) : new String("Unable to read GServices for: "), e);
            return null;
        }
    }

    public static synchronized void zza() {
        Context context;
        synchronized (zzbd.class) {
            zzbd zzbdVar = zza;
            if (zzbdVar != null && (context = zzbdVar.zzb) != null && zzbdVar.zzc != null) {
                context.getContentResolver().unregisterContentObserver(zza.zzc);
            }
            zza = null;
        }
    }

    public final /* synthetic */ String zzb(String str) {
        return zzaq.zza(this.zzb.getContentResolver(), str, (String) null);
    }
}
