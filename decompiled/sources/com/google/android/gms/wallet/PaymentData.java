package com.google.android.gms.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import com.google.android.gms.common.internal.safeparcel.SafeParcelableSerializer;
import com.google.android.gms.identity.intents.model.UserAddress;
/* compiled from: com.google.android.gms:play-services-wallet@@18.1.3 */
/* loaded from: classes3.dex */
public final class PaymentData extends AbstractSafeParcelable implements AutoResolvableResult {
    public static final Parcelable.Creator<PaymentData> CREATOR = new zzv();
    String zza;
    CardInfo zzb;
    UserAddress zzc;
    PaymentMethodToken zzd;
    String zze;
    Bundle zzf;
    String zzg;
    Bundle zzh;

    private PaymentData() {
    }

    public static PaymentData fromJson(String paymentDataJson) {
        PaymentData paymentData = new PaymentData();
        paymentData.zzg = (String) Preconditions.checkNotNull(paymentDataJson, "paymentDataJson cannot be null!");
        return paymentData;
    }

    public static PaymentData getFromIntent(Intent intent) {
        return (PaymentData) SafeParcelableSerializer.deserializeFromIntentExtra(intent, "com.google.android.gms.wallet.PaymentData", CREATOR);
    }

    @Deprecated
    public CardInfo getCardInfo() {
        return this.zzb;
    }

    @Deprecated
    public String getEmail() {
        return this.zza;
    }

    @Deprecated
    public Bundle getExtraData() {
        return this.zzf;
    }

    @Deprecated
    public String getGoogleTransactionId() {
        return this.zze;
    }

    public Bundle getLastSavedState() {
        return this.zzh;
    }

    @Deprecated
    public PaymentMethodToken getPaymentMethodToken() {
        return this.zzd;
    }

    @Deprecated
    public UserAddress getShippingAddress() {
        return this.zzc;
    }

    @Override // com.google.android.gms.wallet.AutoResolvableResult
    public void putIntoIntent(Intent intent) {
        SafeParcelableSerializer.serializeToIntentExtra(this, intent, "com.google.android.gms.wallet.PaymentData");
    }

    public String toJson() {
        return this.zzg;
    }

    public PaymentData withLastSavedState(Bundle bundle) {
        this.zzh = bundle;
        return this;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        int beginObjectHeader = SafeParcelWriter.beginObjectHeader(dest);
        SafeParcelWriter.writeString(dest, 1, this.zza, false);
        SafeParcelWriter.writeParcelable(dest, 2, this.zzb, flags, false);
        SafeParcelWriter.writeParcelable(dest, 3, this.zzc, flags, false);
        SafeParcelWriter.writeParcelable(dest, 4, this.zzd, flags, false);
        SafeParcelWriter.writeString(dest, 5, this.zze, false);
        SafeParcelWriter.writeBundle(dest, 6, this.zzf, false);
        SafeParcelWriter.writeString(dest, 7, this.zzg, false);
        SafeParcelWriter.writeBundle(dest, 8, this.zzh, false);
        SafeParcelWriter.finishObjectHeader(dest, beginObjectHeader);
    }

    public PaymentData(String str, CardInfo cardInfo, UserAddress userAddress, PaymentMethodToken paymentMethodToken, String str2, Bundle bundle, String str3, Bundle bundle2) {
        this.zza = str;
        this.zzb = cardInfo;
        this.zzc = userAddress;
        this.zzd = paymentMethodToken;
        this.zze = str2;
        this.zzf = bundle;
        this.zzg = str3;
        this.zzh = bundle2;
    }
}
