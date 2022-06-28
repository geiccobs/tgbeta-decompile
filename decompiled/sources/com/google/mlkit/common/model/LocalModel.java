package com.google.mlkit.common.model;

import android.net.Uri;
import com.google.android.gms.common.internal.Objects;
import com.google.android.gms.common.internal.Preconditions;
/* compiled from: com.google.mlkit:common@@17.0.0 */
/* loaded from: classes3.dex */
public class LocalModel {
    private final String zza;
    private final String zzb;
    private final Uri zzc;

    public String getAbsoluteFilePath() {
        return this.zza;
    }

    public String getAssetFilePath() {
        return this.zzb;
    }

    public Uri getUri() {
        return this.zzc;
    }

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes3.dex */
    public static class Builder {
        private String zza = null;
        private String zzb = null;
        private Uri zzc = null;

        public Builder setAbsoluteFilePath(String str) {
            Preconditions.checkNotEmpty(str, "Model Source file path can not be empty");
            Preconditions.checkArgument(this.zzb == null && this.zzc == null, "A local model source is from local file, asset or URI, you can only set one of them.");
            this.zza = str;
            return this;
        }

        public Builder setAssetFilePath(String str) {
            Preconditions.checkNotEmpty(str, "Model Source file path can not be empty");
            Preconditions.checkArgument(this.zza == null && this.zzc == null, "A local model source is from local file, asset or URI, you can only set one of them.");
            this.zzb = str;
            return this;
        }

        public Builder setUri(Uri uri) {
            Preconditions.checkArgument(this.zza == null && this.zzb == null, "A local model source is from local file, asset or URI, you can only set one of them.");
            this.zzc = uri;
            return this;
        }

        public LocalModel build() {
            String str = this.zza;
            Preconditions.checkArgument((str != null && this.zzb == null && this.zzc == null) || (str == null && this.zzb != null && this.zzc == null) || (str == null && this.zzb == null && this.zzc != null), "Set one of filePath, assetFilePath and URI.");
            return new LocalModel(this.zza, this.zzb, this.zzc);
        }
    }

    private LocalModel(String str, String str2, Uri uri) {
        this.zza = str;
        this.zzb = str2;
        this.zzc = uri;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof LocalModel)) {
            return false;
        }
        LocalModel localModel = (LocalModel) obj;
        if (!Objects.equal(this.zza, localModel.zza) || !Objects.equal(this.zzb, localModel.zzb) || !Objects.equal(this.zzc, localModel.zzc)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return Objects.hashCode(this.zza, this.zzb, this.zzc);
    }
}
