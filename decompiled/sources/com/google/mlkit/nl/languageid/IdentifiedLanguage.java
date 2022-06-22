package com.google.mlkit.nl.languageid;

import com.google.android.apps.common.proguard.UsedByNative;
import com.google.android.gms.internal.mlkit_language_id.zzh;
import java.util.Arrays;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
@UsedByNative("language_id_jni.cc")
/* loaded from: classes.dex */
public final class IdentifiedLanguage {
    private final String zza;
    private final float zzb;

    @UsedByNative("language_id_jni.cc")
    IdentifiedLanguage(String str, float f) {
        this.zza = str;
        this.zzb = f;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof IdentifiedLanguage)) {
            return false;
        }
        IdentifiedLanguage identifiedLanguage = (IdentifiedLanguage) obj;
        return Float.compare(identifiedLanguage.zzb, this.zzb) == 0 && zzh.zza(this.zza, identifiedLanguage.zza);
    }

    public final int hashCode() {
        return Arrays.hashCode(new Object[]{this.zza, Float.valueOf(this.zzb)});
    }

    public final String toString() {
        return com.google.android.gms.internal.mlkit_language_id.zzd.zza(this).zza("languageTag", this.zza).zza("confidence", this.zzb).toString();
    }
}
