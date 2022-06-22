package com.google.mlkit.nl.languageid;

import com.google.android.gms.common.internal.Objects;
import com.google.android.gms.internal.mlkit_language_id.zzeo;
import com.google.android.gms.internal.mlkit_language_id.zzy$zzai;
import java.util.concurrent.Executor;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes.dex */
public class LanguageIdentificationOptions {
    static final LanguageIdentificationOptions zza = new Builder().build();
    private final Float zzb;
    private final Executor zzc;

    private LanguageIdentificationOptions(Float f, Executor executor) {
        this.zzb = f;
        this.zzc = executor;
    }

    public final zzy$zzai zza() {
        if (this.zzb == null) {
            return zzy$zzai.zzb();
        }
        return (zzy$zzai) ((zzeo) zzy$zzai.zza().zza(this.zzb.floatValue()).zzg());
    }

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes.dex */
    public static class Builder {
        private Float zza;
        private Executor zzb;

        public LanguageIdentificationOptions build() {
            return new LanguageIdentificationOptions(this.zza, this.zzb);
        }
    }

    public final Float zzb() {
        return this.zzb;
    }

    public final Executor zzc() {
        return this.zzc;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof LanguageIdentificationOptions) {
            return Objects.equal(((LanguageIdentificationOptions) obj).zzb, this.zzb);
        }
        return false;
    }

    public int hashCode() {
        return Objects.hashCode(this.zzb);
    }
}
