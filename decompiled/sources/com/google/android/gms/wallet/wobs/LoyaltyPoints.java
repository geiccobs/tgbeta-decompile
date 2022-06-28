package com.google.android.gms.wallet.wobs;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
/* compiled from: com.google.android.gms:play-services-wallet@@18.1.3 */
/* loaded from: classes3.dex */
public final class LoyaltyPoints extends AbstractSafeParcelable {
    public static final Parcelable.Creator<LoyaltyPoints> CREATOR = new zzj();
    String zza;
    LoyaltyPointsBalance zzb;
    @Deprecated
    TimeInterval zzc;

    /* compiled from: com.google.android.gms:play-services-wallet@@18.1.3 */
    /* loaded from: classes3.dex */
    public final class Builder {
        /* synthetic */ Builder(zzg zzgVar) {
            LoyaltyPoints.this = r1;
        }

        public LoyaltyPoints build() {
            return LoyaltyPoints.this;
        }

        public Builder setBalance(LoyaltyPointsBalance loyaltyPointsBalance) {
            LoyaltyPoints.this.zzb = loyaltyPointsBalance;
            return this;
        }

        public Builder setLabel(String str) {
            LoyaltyPoints.this.zza = str;
            return this;
        }

        @Deprecated
        public Builder setType(String str) {
            return this;
        }

        @Deprecated
        public Builder setValidTimeInterval(TimeInterval timeInterval) {
            LoyaltyPoints.this.zzc = timeInterval;
            return this;
        }
    }

    LoyaltyPoints() {
    }

    public static Builder newBuilder() {
        return new Builder(null);
    }

    public LoyaltyPointsBalance getBalance() {
        return this.zzb;
    }

    public String getLabel() {
        return this.zza;
    }

    @Deprecated
    public String getType() {
        return "";
    }

    @Deprecated
    public TimeInterval getValidTimeInterval() {
        return this.zzc;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        int beginObjectHeader = SafeParcelWriter.beginObjectHeader(dest);
        SafeParcelWriter.writeString(dest, 2, this.zza, false);
        SafeParcelWriter.writeParcelable(dest, 3, this.zzb, flags, false);
        SafeParcelWriter.writeParcelable(dest, 5, this.zzc, flags, false);
        SafeParcelWriter.finishObjectHeader(dest, beginObjectHeader);
    }

    public LoyaltyPoints(String str, LoyaltyPointsBalance loyaltyPointsBalance, TimeInterval timeInterval) {
        this.zza = str;
        this.zzb = loyaltyPointsBalance;
        this.zzc = timeInterval;
    }
}
