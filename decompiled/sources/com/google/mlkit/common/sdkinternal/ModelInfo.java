package com.google.mlkit.common.sdkinternal;

import android.net.Uri;
/* compiled from: com.google.mlkit:common@@17.0.0 */
/* loaded from: classes3.dex */
public class ModelInfo {
    private final String zza;
    private final Uri zzb;
    private final String zzc;
    private final ModelType zzd;

    public ModelInfo(String str, Uri uri, String str2, ModelType modelType) {
        this.zza = str;
        this.zzb = uri;
        this.zzc = str2;
        this.zzd = modelType;
    }

    public String getModelNameForPersist() {
        return this.zza;
    }

    public String getModelHash() {
        return this.zzc;
    }

    public Uri getModelUri() {
        return this.zzb;
    }

    public ModelType getModelType() {
        return this.zzd;
    }
}
