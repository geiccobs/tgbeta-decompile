package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.RecentlyNonNull;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
/* compiled from: com.google.android.gms:play-services-wallet@@18.1.3 */
/* loaded from: classes.dex */
public final class TransactionInfo extends AbstractSafeParcelable {
    @RecentlyNonNull
    public static final Parcelable.Creator<TransactionInfo> CREATOR = new zzai();
    int zza;
    String zzb;
    String zzc;

    private TransactionInfo() {
    }

    @Override // android.os.Parcelable
    public void writeToParcel(@RecentlyNonNull Parcel parcel, int i) {
        int beginObjectHeader = SafeParcelWriter.beginObjectHeader(parcel);
        SafeParcelWriter.writeInt(parcel, 1, this.zza);
        SafeParcelWriter.writeString(parcel, 2, this.zzb, false);
        SafeParcelWriter.writeString(parcel, 3, this.zzc, false);
        SafeParcelWriter.finishObjectHeader(parcel, beginObjectHeader);
    }

    public TransactionInfo(int i, @RecentlyNonNull String str, @RecentlyNonNull String str2) {
        this.zza = i;
        this.zzb = str;
        this.zzc = str2;
    }
}
