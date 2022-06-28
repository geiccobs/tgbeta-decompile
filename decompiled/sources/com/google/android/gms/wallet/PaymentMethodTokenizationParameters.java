package com.google.android.gms.wallet;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
/* compiled from: com.google.android.gms:play-services-wallet@@18.1.3 */
/* loaded from: classes3.dex */
public final class PaymentMethodTokenizationParameters extends AbstractSafeParcelable {
    public static final Parcelable.Creator<PaymentMethodTokenizationParameters> CREATOR = new zzaa();
    int zza;
    Bundle zzb;

    /* compiled from: com.google.android.gms:play-services-wallet@@18.1.3 */
    /* loaded from: classes3.dex */
    public final class Builder {
        /* synthetic */ Builder(zzz zzzVar) {
            PaymentMethodTokenizationParameters.this = r1;
        }

        public Builder addParameter(String name, String value) {
            Preconditions.checkNotEmpty(name, "Tokenization parameter name must not be empty");
            Preconditions.checkNotEmpty(value, "Tokenization parameter value must not be empty");
            PaymentMethodTokenizationParameters.this.zzb.putString(name, value);
            return this;
        }

        public PaymentMethodTokenizationParameters build() {
            return PaymentMethodTokenizationParameters.this;
        }

        public Builder setPaymentMethodTokenizationType(int i) {
            PaymentMethodTokenizationParameters.this.zza = i;
            return this;
        }
    }

    private PaymentMethodTokenizationParameters() {
        this.zzb = new Bundle();
    }

    public static Builder newBuilder() {
        return new Builder(null);
    }

    public Bundle getParameters() {
        return new Bundle(this.zzb);
    }

    public int getPaymentMethodTokenizationType() {
        return this.zza;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int i) {
        int beginObjectHeader = SafeParcelWriter.beginObjectHeader(out);
        SafeParcelWriter.writeInt(out, 2, this.zza);
        SafeParcelWriter.writeBundle(out, 3, this.zzb, false);
        SafeParcelWriter.finishObjectHeader(out, beginObjectHeader);
    }

    public PaymentMethodTokenizationParameters(int i, Bundle bundle) {
        new Bundle();
        this.zza = i;
        this.zzb = bundle;
    }
}
