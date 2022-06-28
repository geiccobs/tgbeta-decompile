package com.google.android.gms.internal.vision;

import android.content.Context;
import android.net.Uri;
import javax.annotation.Nullable;
/* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
/* loaded from: classes.dex */
public final class zzbo {
    final String zza;
    final Uri zzb;
    final String zzc;
    final String zzd;
    final boolean zze;
    final boolean zzf;
    final boolean zzg;
    final boolean zzh;
    @Nullable
    final zzcw<Context, Boolean> zzi;

    public zzbo(Uri uri) {
        this(null, uri, "", "", false, false, false, false, null);
    }

    private zzbo(String str, Uri uri, String str2, String str3, boolean z, boolean z2, boolean z3, boolean z4, @Nullable zzcw<Context, Boolean> zzcwVar) {
        this.zza = str;
        this.zzb = uri;
        this.zzc = str2;
        this.zzd = str3;
        this.zze = z;
        this.zzf = z2;
        this.zzg = z3;
        this.zzh = z4;
        this.zzi = zzcwVar;
    }

    public final zzbo zza(String str) {
        boolean z = this.zze;
        if (z) {
            throw new IllegalStateException("Cannot set GServices prefix and skip GServices");
        }
        return new zzbo(this.zza, this.zzb, str, this.zzd, z, this.zzf, this.zzg, this.zzh, this.zzi);
    }

    public final <T> zzbi<T> zza(String str, T t, zzbp<T> zzbpVar) {
        zzbi<T> zzb;
        zzb = zzbi.zzb(this, str, t, zzbpVar, true);
        return zzb;
    }
}
