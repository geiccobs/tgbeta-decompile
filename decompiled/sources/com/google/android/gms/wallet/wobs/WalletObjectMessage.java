package com.google.android.gms.wallet.wobs;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
/* compiled from: com.google.android.gms:play-services-wallet@@18.1.3 */
/* loaded from: classes3.dex */
public final class WalletObjectMessage extends AbstractSafeParcelable {
    public static final Parcelable.Creator<WalletObjectMessage> CREATOR = new zzo();
    String zza;
    String zzb;
    TimeInterval zzc;
    @Deprecated
    UriData zzd;
    @Deprecated
    UriData zze;

    /* compiled from: com.google.android.gms:play-services-wallet@@18.1.3 */
    /* loaded from: classes3.dex */
    public final class Builder {
        /* synthetic */ Builder(zzn zznVar) {
            WalletObjectMessage.this = r1;
        }

        public WalletObjectMessage build() {
            return WalletObjectMessage.this;
        }

        @Deprecated
        public Builder setActionUri(UriData uriData) {
            WalletObjectMessage.this.zzd = uriData;
            return this;
        }

        public Builder setBody(String str) {
            WalletObjectMessage.this.zzb = str;
            return this;
        }

        public Builder setDisplayInterval(TimeInterval timeInterval) {
            WalletObjectMessage.this.zzc = timeInterval;
            return this;
        }

        public Builder setHeader(String str) {
            WalletObjectMessage.this.zza = str;
            return this;
        }

        @Deprecated
        public Builder setImageUri(UriData uriData) {
            WalletObjectMessage.this.zze = uriData;
            return this;
        }
    }

    WalletObjectMessage() {
    }

    public static Builder newBuilder() {
        return new Builder(null);
    }

    @Deprecated
    public UriData getActionUri() {
        return this.zzd;
    }

    public String getBody() {
        return this.zzb;
    }

    public TimeInterval getDisplayInterval() {
        return this.zzc;
    }

    public String getHeader() {
        return this.zza;
    }

    @Deprecated
    public UriData getImageUri() {
        return this.zze;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        int beginObjectHeader = SafeParcelWriter.beginObjectHeader(dest);
        SafeParcelWriter.writeString(dest, 2, this.zza, false);
        SafeParcelWriter.writeString(dest, 3, this.zzb, false);
        SafeParcelWriter.writeParcelable(dest, 4, this.zzc, flags, false);
        SafeParcelWriter.writeParcelable(dest, 5, this.zzd, flags, false);
        SafeParcelWriter.writeParcelable(dest, 6, this.zze, flags, false);
        SafeParcelWriter.finishObjectHeader(dest, beginObjectHeader);
    }

    public WalletObjectMessage(String str, String str2, TimeInterval timeInterval, UriData uriData, UriData uriData2) {
        this.zza = str;
        this.zzb = str2;
        this.zzc = timeInterval;
        this.zzd = uriData;
        this.zze = uriData2;
    }
}
