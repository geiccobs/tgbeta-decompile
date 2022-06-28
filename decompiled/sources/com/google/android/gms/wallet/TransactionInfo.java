package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
/* compiled from: com.google.android.gms:play-services-wallet@@18.1.3 */
/* loaded from: classes3.dex */
public final class TransactionInfo extends AbstractSafeParcelable {
    public static final Parcelable.Creator<TransactionInfo> CREATOR = new zzai();
    int zza;
    String zzb;
    String zzc;

    /* compiled from: com.google.android.gms:play-services-wallet@@18.1.3 */
    /* loaded from: classes3.dex */
    public final class Builder {
        /* synthetic */ Builder(zzah zzahVar) {
            TransactionInfo.this = r1;
        }

        public TransactionInfo build() {
            Preconditions.checkNotEmpty(TransactionInfo.this.zzc, "currencyCode must be set!");
            TransactionInfo transactionInfo = TransactionInfo.this;
            int i = transactionInfo.zza;
            if (i != 1) {
                if (i == 2) {
                    Preconditions.checkNotEmpty(transactionInfo.zzb, "An estimated total price must be set if totalPriceStatus is set to WalletConstants.TOTAL_PRICE_STATUS_ESTIMATED!");
                } else if (i != 3) {
                    throw new IllegalArgumentException("totalPriceStatus must be set to one of WalletConstants.TotalPriceStatus!");
                }
            }
            TransactionInfo transactionInfo2 = TransactionInfo.this;
            if (transactionInfo2.zza == 3) {
                Preconditions.checkNotEmpty(transactionInfo2.zzb, "An final total price must be set if totalPriceStatus is set to WalletConstants.TOTAL_PRICE_STATUS_FINAL!");
            }
            return TransactionInfo.this;
        }

        public Builder setCurrencyCode(String str) {
            TransactionInfo.this.zzc = str;
            return this;
        }

        public Builder setTotalPrice(String str) {
            TransactionInfo.this.zzb = str;
            return this;
        }

        public Builder setTotalPriceStatus(int i) {
            TransactionInfo.this.zza = i;
            return this;
        }
    }

    private TransactionInfo() {
    }

    public static Builder newBuilder() {
        return new Builder(null);
    }

    public String getCurrencyCode() {
        return this.zzc;
    }

    public String getTotalPrice() {
        return this.zzb;
    }

    public int getTotalPriceStatus() {
        return this.zza;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int i) {
        int beginObjectHeader = SafeParcelWriter.beginObjectHeader(out);
        SafeParcelWriter.writeInt(out, 1, this.zza);
        SafeParcelWriter.writeString(out, 2, this.zzb, false);
        SafeParcelWriter.writeString(out, 3, this.zzc, false);
        SafeParcelWriter.finishObjectHeader(out, beginObjectHeader);
    }

    public TransactionInfo(int i, String str, String str2) {
        this.zza = i;
        this.zzb = str;
        this.zzc = str2;
    }
}
