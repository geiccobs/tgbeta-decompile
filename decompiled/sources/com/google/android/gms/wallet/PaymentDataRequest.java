package com.google.android.gms.wallet;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import java.util.ArrayList;
import java.util.Collection;
/* compiled from: com.google.android.gms:play-services-wallet@@18.1.3 */
/* loaded from: classes3.dex */
public final class PaymentDataRequest extends AbstractSafeParcelable {
    public static final Parcelable.Creator<PaymentDataRequest> CREATOR = new zzx();
    boolean zza;
    boolean zzb;
    CardRequirements zzc;
    boolean zzd;
    ShippingAddressRequirements zze;
    ArrayList<Integer> zzf;
    PaymentMethodTokenizationParameters zzg;
    TransactionInfo zzh;
    boolean zzi;
    String zzj;
    Bundle zzk;

    /* compiled from: com.google.android.gms:play-services-wallet@@18.1.3 */
    @Deprecated
    /* loaded from: classes3.dex */
    public final class Builder {
        /* synthetic */ Builder(zzw zzwVar) {
            PaymentDataRequest.this = r1;
        }

        public Builder addAllowedPaymentMethod(int allowedPaymentMethod) {
            PaymentDataRequest paymentDataRequest = PaymentDataRequest.this;
            if (paymentDataRequest.zzf == null) {
                paymentDataRequest.zzf = new ArrayList<>();
            }
            PaymentDataRequest.this.zzf.add(Integer.valueOf(allowedPaymentMethod));
            return this;
        }

        public Builder addAllowedPaymentMethods(Collection<Integer> collection) {
            boolean z = false;
            if (collection != null && !collection.isEmpty()) {
                z = true;
            }
            Preconditions.checkArgument(z, "allowedPaymentMethods can't be null or empty!");
            PaymentDataRequest paymentDataRequest = PaymentDataRequest.this;
            if (paymentDataRequest.zzf == null) {
                paymentDataRequest.zzf = new ArrayList<>();
            }
            PaymentDataRequest.this.zzf.addAll(collection);
            return this;
        }

        public PaymentDataRequest build() {
            PaymentDataRequest paymentDataRequest = PaymentDataRequest.this;
            if (paymentDataRequest.zzj == null) {
                Preconditions.checkNotNull(paymentDataRequest.zzf, "Allowed payment methods must be set! You can set it through addAllowedPaymentMethod() or addAllowedPaymentMethods() in the PaymentDataRequest Builder.");
                Preconditions.checkNotNull(PaymentDataRequest.this.zzc, "Card requirements must be set!");
                PaymentDataRequest paymentDataRequest2 = PaymentDataRequest.this;
                if (paymentDataRequest2.zzg != null) {
                    Preconditions.checkNotNull(paymentDataRequest2.zzh, "Transaction info must be set if paymentMethodTokenizationParameters is set!");
                }
            }
            return PaymentDataRequest.this;
        }

        public Builder setCardRequirements(CardRequirements cardRequirements) {
            PaymentDataRequest.this.zzc = cardRequirements;
            return this;
        }

        public Builder setEmailRequired(boolean z) {
            PaymentDataRequest.this.zza = z;
            return this;
        }

        public Builder setPaymentMethodTokenizationParameters(PaymentMethodTokenizationParameters paymentMethodTokenizationParameters) {
            PaymentDataRequest.this.zzg = paymentMethodTokenizationParameters;
            return this;
        }

        public Builder setPhoneNumberRequired(boolean z) {
            PaymentDataRequest.this.zzb = z;
            return this;
        }

        public Builder setShippingAddressRequired(boolean z) {
            PaymentDataRequest.this.zzd = z;
            return this;
        }

        public Builder setShippingAddressRequirements(ShippingAddressRequirements shippingAddressRequirements) {
            PaymentDataRequest.this.zze = shippingAddressRequirements;
            return this;
        }

        public Builder setTransactionInfo(TransactionInfo transactionInfo) {
            PaymentDataRequest.this.zzh = transactionInfo;
            return this;
        }

        public Builder setUiRequired(boolean z) {
            PaymentDataRequest.this.zzi = z;
            return this;
        }
    }

    private PaymentDataRequest() {
        this.zzi = true;
    }

    public static PaymentDataRequest fromJson(String paymentDataRequestJson) {
        Builder newBuilder = newBuilder();
        PaymentDataRequest.this.zzj = (String) Preconditions.checkNotNull(paymentDataRequestJson, "paymentDataRequestJson cannot be null!");
        return newBuilder.build();
    }

    @Deprecated
    public static Builder newBuilder() {
        return new Builder(null);
    }

    @Deprecated
    public ArrayList<Integer> getAllowedPaymentMethods() {
        return this.zzf;
    }

    @Deprecated
    public CardRequirements getCardRequirements() {
        return this.zzc;
    }

    @Deprecated
    public PaymentMethodTokenizationParameters getPaymentMethodTokenizationParameters() {
        return this.zzg;
    }

    public Bundle getSavedState() {
        return this.zzk;
    }

    @Deprecated
    public ShippingAddressRequirements getShippingAddressRequirements() {
        return this.zze;
    }

    @Deprecated
    public TransactionInfo getTransactionInfo() {
        return this.zzh;
    }

    @Deprecated
    public boolean isEmailRequired() {
        return this.zza;
    }

    @Deprecated
    public boolean isPhoneNumberRequired() {
        return this.zzb;
    }

    @Deprecated
    public boolean isShippingAddressRequired() {
        return this.zzd;
    }

    @Deprecated
    public boolean isUiRequired() {
        return this.zzi;
    }

    public String toJson() {
        return this.zzj;
    }

    public PaymentDataRequest withSavedState(Bundle bundle) {
        this.zzk = bundle;
        return this;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        int beginObjectHeader = SafeParcelWriter.beginObjectHeader(dest);
        SafeParcelWriter.writeBoolean(dest, 1, this.zza);
        SafeParcelWriter.writeBoolean(dest, 2, this.zzb);
        SafeParcelWriter.writeParcelable(dest, 3, this.zzc, flags, false);
        SafeParcelWriter.writeBoolean(dest, 4, this.zzd);
        SafeParcelWriter.writeParcelable(dest, 5, this.zze, flags, false);
        SafeParcelWriter.writeIntegerList(dest, 6, this.zzf, false);
        SafeParcelWriter.writeParcelable(dest, 7, this.zzg, flags, false);
        SafeParcelWriter.writeParcelable(dest, 8, this.zzh, flags, false);
        SafeParcelWriter.writeBoolean(dest, 9, this.zzi);
        SafeParcelWriter.writeString(dest, 10, this.zzj, false);
        SafeParcelWriter.writeBundle(dest, 11, this.zzk, false);
        SafeParcelWriter.finishObjectHeader(dest, beginObjectHeader);
    }

    public PaymentDataRequest(boolean z, boolean z2, CardRequirements cardRequirements, boolean z3, ShippingAddressRequirements shippingAddressRequirements, ArrayList<Integer> arrayList, PaymentMethodTokenizationParameters paymentMethodTokenizationParameters, TransactionInfo transactionInfo, boolean z4, String str, Bundle bundle) {
        this.zza = z;
        this.zzb = z2;
        this.zzc = cardRequirements;
        this.zzd = z3;
        this.zze = shippingAddressRequirements;
        this.zzf = arrayList;
        this.zzg = paymentMethodTokenizationParameters;
        this.zzh = transactionInfo;
        this.zzi = z4;
        this.zzj = str;
        this.zzk = bundle;
    }
}
