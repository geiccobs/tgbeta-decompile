package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
/* compiled from: com.google.android.gms:play-services-wallet@@18.1.3 */
/* loaded from: classes3.dex */
public final class PaymentMethodToken extends AbstractSafeParcelable {
    public static final Parcelable.Creator<PaymentMethodToken> CREATOR = new zzy();
    int zza;
    String zzb;

    private PaymentMethodToken() {
    }

    public int getPaymentMethodTokenizationType() {
        return this.zza;
    }

    public String getToken() {
        return this.zzb;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int i) {
        int beginObjectHeader = SafeParcelWriter.beginObjectHeader(out);
        SafeParcelWriter.writeInt(out, 2, this.zza);
        SafeParcelWriter.writeString(out, 3, this.zzb, false);
        SafeParcelWriter.finishObjectHeader(out, beginObjectHeader);
    }

    public PaymentMethodToken(int i, String str) {
        this.zza = i;
        this.zzb = str;
    }
}
