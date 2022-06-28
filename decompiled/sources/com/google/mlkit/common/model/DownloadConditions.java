package com.google.mlkit.common.model;

import com.google.android.gms.common.internal.Objects;
/* compiled from: com.google.mlkit:common@@17.0.0 */
/* loaded from: classes3.dex */
public class DownloadConditions {
    private final boolean zza;
    private final boolean zzb;

    private DownloadConditions(boolean z, boolean z2) {
        this.zza = z;
        this.zzb = z2;
    }

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes3.dex */
    public static class Builder {
        private boolean zza = false;
        private boolean zzb = false;

        public Builder requireCharging() {
            this.zza = true;
            return this;
        }

        public Builder requireWifi() {
            this.zzb = true;
            return this;
        }

        public DownloadConditions build() {
            return new DownloadConditions(this.zza, this.zzb);
        }
    }

    public boolean isChargingRequired() {
        return this.zza;
    }

    public boolean isWifiRequired() {
        return this.zzb;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof DownloadConditions)) {
            return false;
        }
        DownloadConditions downloadConditions = (DownloadConditions) obj;
        return this.zza == downloadConditions.zza && this.zzb == downloadConditions.zzb;
    }

    public int hashCode() {
        return Objects.hashCode(Boolean.valueOf(this.zza), Boolean.valueOf(this.zzb));
    }
}
