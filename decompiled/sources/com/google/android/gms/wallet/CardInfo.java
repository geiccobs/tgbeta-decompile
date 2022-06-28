package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import com.google.android.gms.identity.intents.model.UserAddress;
/* compiled from: com.google.android.gms:play-services-wallet@@18.1.3 */
/* loaded from: classes3.dex */
public final class CardInfo extends AbstractSafeParcelable {
    public static final Parcelable.Creator<CardInfo> CREATOR = new zze();
    String zza;
    String zzb;
    String zzc;
    int zzd;
    UserAddress zze;

    private CardInfo() {
    }

    public UserAddress getBillingAddress() {
        return this.zze;
    }

    public int getCardClass() {
        int i = this.zzd;
        switch (i) {
            case 1:
            case 2:
            case 3:
                return i;
            default:
                return 0;
        }
    }

    public String getCardDescription() {
        return this.zza;
    }

    public String getCardDetails() {
        return this.zzc;
    }

    public String getCardNetwork() {
        return this.zzb;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        int beginObjectHeader = SafeParcelWriter.beginObjectHeader(out);
        SafeParcelWriter.writeString(out, 1, this.zza, false);
        SafeParcelWriter.writeString(out, 2, this.zzb, false);
        SafeParcelWriter.writeString(out, 3, this.zzc, false);
        SafeParcelWriter.writeInt(out, 4, this.zzd);
        SafeParcelWriter.writeParcelable(out, 5, this.zze, flags, false);
        SafeParcelWriter.finishObjectHeader(out, beginObjectHeader);
    }

    public CardInfo(String str, String str2, String str3, int i, UserAddress userAddress) {
        this.zza = str;
        this.zzb = str2;
        this.zzc = str3;
        this.zzd = i;
        this.zze = userAddress;
    }
}
