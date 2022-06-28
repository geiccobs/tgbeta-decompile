package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* compiled from: com.google.android.gms:play-services-wallet@@18.1.3 */
/* loaded from: classes3.dex */
public final class CreateWalletObjectsRequest extends AbstractSafeParcelable {
    public static final Parcelable.Creator<CreateWalletObjectsRequest> CREATOR = new zzi();
    public static final int REQUEST_IMMEDIATE_SAVE = 1;
    public static final int SHOW_SAVE_PROMPT = 0;
    LoyaltyWalletObject zza;
    OfferWalletObject zzb;
    GiftCardWalletObject zzc;
    int zzd;

    /* compiled from: com.google.android.gms:play-services-wallet@@18.1.3 */
    /* loaded from: classes3.dex */
    public final class Builder {
        /* synthetic */ Builder(zzh zzhVar) {
            CreateWalletObjectsRequest.this = r1;
        }

        public CreateWalletObjectsRequest build() {
            CreateWalletObjectsRequest createWalletObjectsRequest = CreateWalletObjectsRequest.this;
            boolean z = false;
            if ((createWalletObjectsRequest.zzc == null ? 0 : 1) + (createWalletObjectsRequest.zza == null ? 0 : 1) + (createWalletObjectsRequest.zzb == null ? 0 : 1) == 1) {
                z = true;
            }
            Preconditions.checkState(z, "CreateWalletObjectsRequest must have exactly one Wallet Object");
            return CreateWalletObjectsRequest.this;
        }

        public Builder setCreateMode(int i) {
            CreateWalletObjectsRequest.this.zzd = i;
            return this;
        }

        public Builder setGiftCardWalletObject(GiftCardWalletObject giftCardWalletObject) {
            CreateWalletObjectsRequest.this.zzc = giftCardWalletObject;
            return this;
        }

        public Builder setLoyaltyWalletObject(LoyaltyWalletObject loyaltyWalletObject) {
            CreateWalletObjectsRequest.this.zza = loyaltyWalletObject;
            return this;
        }

        public Builder setOfferWalletObject(OfferWalletObject offerWalletObject) {
            CreateWalletObjectsRequest.this.zzb = offerWalletObject;
            return this;
        }
    }

    /* compiled from: com.google.android.gms:play-services-wallet@@18.1.3 */
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface CreateMode {
    }

    CreateWalletObjectsRequest() {
    }

    public static Builder newBuilder() {
        return new Builder(null);
    }

    public int getCreateMode() {
        return this.zzd;
    }

    public GiftCardWalletObject getGiftCardWalletObject() {
        return this.zzc;
    }

    public LoyaltyWalletObject getLoyaltyWalletObject() {
        return this.zza;
    }

    public OfferWalletObject getOfferWalletObject() {
        return this.zzb;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        int beginObjectHeader = SafeParcelWriter.beginObjectHeader(dest);
        SafeParcelWriter.writeParcelable(dest, 2, this.zza, flags, false);
        SafeParcelWriter.writeParcelable(dest, 3, this.zzb, flags, false);
        SafeParcelWriter.writeParcelable(dest, 4, this.zzc, flags, false);
        SafeParcelWriter.writeInt(dest, 5, this.zzd);
        SafeParcelWriter.finishObjectHeader(dest, beginObjectHeader);
    }

    @Deprecated
    public CreateWalletObjectsRequest(GiftCardWalletObject giftCardWalletObject) {
        this.zzc = giftCardWalletObject;
    }

    @Deprecated
    public CreateWalletObjectsRequest(LoyaltyWalletObject loyaltyWalletObject) {
        this.zza = loyaltyWalletObject;
    }

    public CreateWalletObjectsRequest(LoyaltyWalletObject loyaltyWalletObject, OfferWalletObject offerWalletObject, GiftCardWalletObject giftCardWalletObject, int i) {
        this.zza = loyaltyWalletObject;
        this.zzb = offerWalletObject;
        this.zzc = giftCardWalletObject;
        this.zzd = i;
    }

    @Deprecated
    public CreateWalletObjectsRequest(OfferWalletObject offerWalletObject) {
        this.zzb = offerWalletObject;
    }
}
