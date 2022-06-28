package com.google.android.gms.internal.vision;
/* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
/* loaded from: classes3.dex */
public final class zzbs {
    private final boolean zza;

    public zzbs(zzbv zzbvVar) {
        zzde.zza(zzbvVar, "BuildInfo must be non-null");
        this.zza = !zzbvVar.zza();
    }

    public final boolean zza(String str) {
        zzde.zza(str, "flagName must not be null");
        if (!this.zza) {
            return true;
        }
        return zzbu.zza.zza().zza(str);
    }
}
