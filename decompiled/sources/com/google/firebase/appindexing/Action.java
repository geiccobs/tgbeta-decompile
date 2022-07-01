package com.google.firebase.appindexing;

import android.os.Bundle;
import com.google.android.gms.common.internal.Preconditions;
import com.google.firebase.appindexing.builders.IndexableBuilder;
import com.google.firebase.appindexing.internal.zzc;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes.dex */
public interface Action {

    /* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
    /* loaded from: classes.dex */
    public static class Builder {
        private final Bundle zza = new Bundle();
        private final String zzb;
        private String zzc;
        private String zzd;
        private String zze;
        private com.google.firebase.appindexing.internal.zzb zzf;
        private String zzg;

        public Builder(String str) {
            this.zzb = str;
        }

        /* JADX WARN: Type inference failed for: r1v3, types: [com.google.firebase.appindexing.Action$Metadata$Builder] */
        public Action build() {
            Preconditions.checkNotNull(this.zzc, "setObject is required before calling build().");
            Preconditions.checkNotNull(this.zzd, "setObject is required before calling build().");
            String str = this.zzb;
            String str2 = this.zzc;
            String str3 = this.zzd;
            String str4 = this.zze;
            com.google.firebase.appindexing.internal.zzb zzbVar = this.zzf;
            if (zzbVar == null) {
                zzbVar = new Object() { // from class: com.google.firebase.appindexing.Action$Metadata$Builder
                    private boolean zza = true;

                    public final com.google.firebase.appindexing.internal.zzb zza() {
                        return new com.google.firebase.appindexing.internal.zzb(this.zza, null, null, null, false);
                    }
                }.zza();
            }
            return new zzc(str, str2, str3, str4, zzbVar, this.zzg, this.zza);
        }

        public Builder setActionStatus(String str) {
            Preconditions.checkNotNull(str);
            this.zzg = str;
            return this;
        }

        public final Builder setName(String str) {
            Preconditions.checkNotNull(str);
            this.zzc = str;
            return put("name", str);
        }

        public final Builder setUrl(String str) {
            Preconditions.checkNotNull(str);
            this.zzd = str;
            return put("url", str);
        }

        public final String zza() {
            String str = this.zzc;
            if (str == null) {
                return null;
            }
            return new String(str);
        }

        public final String zzb() {
            String str = this.zzd;
            if (str == null) {
                return null;
            }
            return new String(str);
        }

        public final String zzc() {
            return new String(this.zzg);
        }

        public Builder put(String str, String... strArr) {
            IndexableBuilder.zza(this.zza, str, strArr);
            return this;
        }
    }
}
