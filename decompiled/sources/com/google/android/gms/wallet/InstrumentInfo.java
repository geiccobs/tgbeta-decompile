package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.RecentlyNonNull;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
/* compiled from: com.google.android.gms:play-services-wallet@@18.1.3 */
/* loaded from: classes.dex */
public final class InstrumentInfo extends AbstractSafeParcelable {
    @RecentlyNonNull
    public static final Parcelable.Creator<InstrumentInfo> CREATOR = new zzn();
    private String zza;
    private String zzb;
    private int zzc;

    private InstrumentInfo() {
    }

    public int getCardClass() {
        int i = this.zzc;
        if (i == 1 || i == 2 || i == 3) {
            return i;
        }
        return 0;
    }

    @RecentlyNonNull
    public String getInstrumentDetails() {
        return this.zzb;
    }

    @RecentlyNonNull
    public String getInstrumentType() {
        return this.zza;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(@RecentlyNonNull Parcel parcel, int i) {
        int beginObjectHeader = SafeParcelWriter.beginObjectHeader(parcel);
        SafeParcelWriter.writeString(parcel, 2, getInstrumentType(), false);
        SafeParcelWriter.writeString(parcel, 3, getInstrumentDetails(), false);
        SafeParcelWriter.writeInt(parcel, 4, getCardClass());
        SafeParcelWriter.finishObjectHeader(parcel, beginObjectHeader);
    }

    public InstrumentInfo(@RecentlyNonNull String str, @RecentlyNonNull String str2, int i) {
        this.zza = str;
        this.zzb = str2;
        this.zzc = i;
    }
}
