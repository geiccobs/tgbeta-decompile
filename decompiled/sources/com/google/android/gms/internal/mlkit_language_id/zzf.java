package com.google.android.gms.internal.mlkit_language_id;

import java.util.Arrays;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes.dex */
public final class zzf {
    private final String zza;
    private final zze zzb;
    private zze zzc;

    /* JADX INFO: Access modifiers changed from: private */
    public zzf(String str) {
        zze zzeVar = new zze();
        this.zzb = zzeVar;
        this.zzc = zzeVar;
        this.zza = (String) zzg.zza(str);
    }

    public final zzf zza(String str, @NullableDecl Object obj) {
        return zzb(str, obj);
    }

    public final zzf zza(String str, float f) {
        return zzb(str, String.valueOf(f));
    }

    public final String toString() {
        StringBuilder sb = new StringBuilder(32);
        sb.append(this.zza);
        sb.append('{');
        zze zzeVar = this.zzb.zzc;
        String str = "";
        while (zzeVar != null) {
            Object obj = zzeVar.zzb;
            sb.append(str);
            String str2 = zzeVar.zza;
            if (str2 != null) {
                sb.append(str2);
                sb.append('=');
            }
            if (obj != null && obj.getClass().isArray()) {
                String deepToString = Arrays.deepToString(new Object[]{obj});
                sb.append((CharSequence) deepToString, 1, deepToString.length() - 1);
            } else {
                sb.append(obj);
            }
            zzeVar = zzeVar.zzc;
            str = ", ";
        }
        sb.append('}');
        return sb.toString();
    }

    private final zzf zzb(String str, @NullableDecl Object obj) {
        zze zzeVar = new zze();
        this.zzc.zzc = zzeVar;
        this.zzc = zzeVar;
        zzeVar.zzb = obj;
        zzeVar.zza = (String) zzg.zza(str);
        return this;
    }
}
