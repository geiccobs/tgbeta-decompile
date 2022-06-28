package com.google.mlkit.common.internal.model;

import com.google.mlkit.common.internal.model.ModelUtils;
/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: com.google.mlkit:common@@17.0.0 */
/* loaded from: classes3.dex */
public final class AutoValue_ModelUtils_ModelLoggingInfo extends ModelUtils.ModelLoggingInfo {
    private final long zza;
    private final String zzb;

    public AutoValue_ModelUtils_ModelLoggingInfo(long j, String str) {
        this.zza = j;
        if (str == null) {
            throw new NullPointerException("Null hash");
        }
        this.zzb = str;
    }

    @Override // com.google.mlkit.common.internal.model.ModelUtils.ModelLoggingInfo
    public final long getSize() {
        return this.zza;
    }

    @Override // com.google.mlkit.common.internal.model.ModelUtils.ModelLoggingInfo
    public final String getHash() {
        return this.zzb;
    }

    public final String toString() {
        long j = this.zza;
        String str = this.zzb;
        StringBuilder sb = new StringBuilder(String.valueOf(str).length() + 50);
        sb.append("ModelLoggingInfo{size=");
        sb.append(j);
        sb.append(", hash=");
        sb.append(str);
        sb.append("}");
        return sb.toString();
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ModelUtils.ModelLoggingInfo)) {
            return false;
        }
        ModelUtils.ModelLoggingInfo modelLoggingInfo = (ModelUtils.ModelLoggingInfo) obj;
        return this.zza == modelLoggingInfo.getSize() && this.zzb.equals(modelLoggingInfo.getHash());
    }

    public final int hashCode() {
        long j = this.zza;
        return this.zzb.hashCode() ^ ((((int) (j ^ (j >>> 32))) ^ 1000003) * 1000003);
    }
}
