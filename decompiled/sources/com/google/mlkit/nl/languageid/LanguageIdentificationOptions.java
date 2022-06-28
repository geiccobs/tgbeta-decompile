package com.google.mlkit.nl.languageid;

import com.google.android.gms.common.internal.Objects;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.internal.mlkit_language_id.zzeo;
import com.google.android.gms.internal.mlkit_language_id.zzy;
import java.util.concurrent.Executor;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes3.dex */
public class LanguageIdentificationOptions {
    static final LanguageIdentificationOptions zza = new Builder().build();
    private final Float zzb;
    private final Executor zzc;

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes3.dex */
    public static class Builder {
        private Float zza;
        private Executor zzb;

        public Builder setConfidenceThreshold(float f) {
            Preconditions.checkArgument(f >= 0.0f && f <= 1.0f, "Threshold value %f should be between 0 and 1", Float.valueOf(f));
            this.zza = Float.valueOf(f);
            return this;
        }

        public Builder setExecutor(Executor executor) {
            Preconditions.checkArgument(executor != null, "Custom executor should not be null");
            this.zzb = executor;
            return this;
        }

        public LanguageIdentificationOptions build() {
            return new LanguageIdentificationOptions(this.zza, this.zzb);
        }
    }

    private LanguageIdentificationOptions(Float f, Executor executor) {
        this.zzb = f;
        this.zzc = executor;
    }

    public final zzy.zzai zza() {
        if (this.zzb == null) {
            return zzy.zzai.zzb();
        }
        return (zzy.zzai) ((zzeo) zzy.zzai.zza().zza(this.zzb.floatValue()).zzg());
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
        if (!(obj instanceof LanguageIdentificationOptions)) {
            return false;
        }
        return Objects.equal(((LanguageIdentificationOptions) obj).zzb, this.zzb);
    }

    public int hashCode() {
        return Objects.hashCode(this.zzb);
    }
}
