package com.google.android.gms.wallet.callback;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
/* compiled from: com.google.android.gms:play-services-wallet@@18.1.3 */
/* loaded from: classes3.dex */
public class IntermediatePaymentData extends AbstractSafeParcelable {
    public static final Parcelable.Creator<IntermediatePaymentData> CREATOR = new zzl();
    String zza;
    Bundle zzb;

    public IntermediatePaymentData(String str, Bundle bundle) {
        this.zza = str;
        this.zzb = bundle;
    }

    public static IntermediatePaymentData fromJson(String json) {
        return new IntermediatePaymentData((String) Preconditions.checkNotNull(json, "JSON cannot be null!"), null);
    }

    public Bundle getLastSavedState() {
        return this.zzb;
    }

    public String toJson() {
        return this.zza;
    }

    public IntermediatePaymentData withLastSavedState(Bundle bundle) {
        this.zzb = bundle;
        return this;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int i) {
        int beginObjectHeader = SafeParcelWriter.beginObjectHeader(dest);
        SafeParcelWriter.writeString(dest, 1, this.zza, false);
        SafeParcelWriter.writeBundle(dest, 2, this.zzb, false);
        SafeParcelWriter.finishObjectHeader(dest, beginObjectHeader);
    }
}
