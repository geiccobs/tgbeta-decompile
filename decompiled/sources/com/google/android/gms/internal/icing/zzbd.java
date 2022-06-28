package com.google.android.gms.internal.icing;

import android.content.Context;
import javax.annotation.Nullable;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes.dex */
final class zzbd extends zzbh {
    private final Context zza;
    private final zzbm zzb;

    public zzbd(Context context, @Nullable zzbm zzbmVar) {
        if (context != null) {
            this.zza = context;
            this.zzb = zzbmVar;
            return;
        }
        throw new NullPointerException("Null context");
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof zzbh) {
            zzbh zzbhVar = (zzbh) obj;
            if (this.zza.equals(zzbhVar.zza()) && this.zzb.equals(zzbhVar.zzb())) {
                return true;
            }
        }
        return false;
    }

    public final int hashCode() {
        return ((this.zza.hashCode() ^ 1000003) * 1000003) ^ this.zzb.hashCode();
    }

    public final String toString() {
        String valueOf = String.valueOf(this.zza);
        String valueOf2 = String.valueOf(this.zzb);
        StringBuilder sb = new StringBuilder(String.valueOf(valueOf).length() + 46 + String.valueOf(valueOf2).length());
        sb.append("FlagsContext{context=");
        sb.append(valueOf);
        sb.append(", hermeticFileOverrides=");
        sb.append(valueOf2);
        sb.append("}");
        return sb.toString();
    }

    @Override // com.google.android.gms.internal.icing.zzbh
    public final Context zza() {
        return this.zza;
    }

    @Override // com.google.android.gms.internal.icing.zzbh
    @Nullable
    public final zzbm zzb() {
        return this.zzb;
    }
}
