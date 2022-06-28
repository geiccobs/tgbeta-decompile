package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.wallet.wobs.CommonWalletObject;
import com.google.android.gms.wallet.wobs.LabelValueRow;
import com.google.android.gms.wallet.wobs.TextModuleData;
import com.google.android.gms.wallet.wobs.TimeInterval;
import com.google.android.gms.wallet.wobs.UriData;
import com.google.android.gms.wallet.wobs.WalletObjectMessage;
import java.util.ArrayList;
import java.util.Collection;
/* compiled from: com.google.android.gms:play-services-wallet@@18.1.3 */
/* loaded from: classes3.dex */
public final class GiftCardWalletObject extends AbstractSafeParcelable {
    public static final Parcelable.Creator<GiftCardWalletObject> CREATOR = new zzm();
    CommonWalletObject zza;
    String zzb;
    String zzc;
    @Deprecated
    String zzd;
    long zze;
    String zzf;
    long zzg;
    String zzh;

    /* compiled from: com.google.android.gms:play-services-wallet@@18.1.3 */
    /* loaded from: classes3.dex */
    public final class Builder {
        private com.google.android.gms.wallet.wobs.zzb zzb = CommonWalletObject.zzb();

        /* synthetic */ Builder(zzl zzlVar) {
            GiftCardWalletObject.this = r1;
        }

        public Builder addImageModuleDataMainImageUri(UriData uri) {
            this.zzb.zza(uri);
            return this;
        }

        public Builder addImageModuleDataMainImageUris(Collection<UriData> collection) {
            this.zzb.zzb(collection);
            return this;
        }

        public Builder addInfoModuleDataLabelValueRow(LabelValueRow row) {
            this.zzb.zzc(row);
            return this;
        }

        public Builder addInfoModuleDataLabelValueRows(Collection<LabelValueRow> collection) {
            this.zzb.zzd(collection);
            return this;
        }

        public Builder addLinksModuleDataUri(UriData uri) {
            this.zzb.zze(uri);
            return this;
        }

        public Builder addLinksModuleDataUris(Collection<UriData> collection) {
            this.zzb.zzf(collection);
            return this;
        }

        public Builder addLocation(LatLng location) {
            this.zzb.zzg(location);
            return this;
        }

        public Builder addLocations(Collection<LatLng> collection) {
            this.zzb.zzh(collection);
            return this;
        }

        public Builder addMessage(WalletObjectMessage message) {
            this.zzb.zzi(message);
            return this;
        }

        public Builder addMessages(Collection<WalletObjectMessage> collection) {
            this.zzb.zzj(collection);
            return this;
        }

        public Builder addTextModuleData(TextModuleData data) {
            this.zzb.zzk(data);
            return this;
        }

        public Builder addTextModulesData(Collection<TextModuleData> collection) {
            this.zzb.zzl(collection);
            return this;
        }

        public GiftCardWalletObject build() {
            Preconditions.checkArgument(!TextUtils.isEmpty(GiftCardWalletObject.this.zzb), "Card number is required.");
            GiftCardWalletObject.this.zza = this.zzb.zzz();
            Preconditions.checkArgument(!TextUtils.isEmpty(GiftCardWalletObject.this.zza.zzm()), "Card name is required.");
            Preconditions.checkArgument(!TextUtils.isEmpty(GiftCardWalletObject.this.zza.zzl()), "Card issuer name is required.");
            return GiftCardWalletObject.this;
        }

        public Builder setBalanceCurrencyCode(String str) {
            GiftCardWalletObject.this.zzf = str;
            return this;
        }

        public Builder setBalanceMicros(long j) {
            GiftCardWalletObject.this.zze = j;
            return this;
        }

        public Builder setBalanceUpdateTime(long j) {
            GiftCardWalletObject.this.zzg = j;
            return this;
        }

        public Builder setBarcodeAlternateText(String barcodeAlternateText) {
            this.zzb.zzm(barcodeAlternateText);
            return this;
        }

        @Deprecated
        public Builder setBarcodeLabel(String barcodeLabel) {
            this.zzb.zzn(barcodeLabel);
            return this;
        }

        public Builder setBarcodeType(String barcodeType) {
            this.zzb.zzo(barcodeType);
            return this;
        }

        public Builder setBarcodeValue(String barcodeValue) {
            this.zzb.zzp(barcodeValue);
            return this;
        }

        @Deprecated
        public Builder setCardIdentifier(String str) {
            GiftCardWalletObject.this.zzd = str;
            return this;
        }

        public Builder setCardNumber(String str) {
            GiftCardWalletObject.this.zzb = str;
            return this;
        }

        public Builder setClassId(String classId) {
            this.zzb.zzq(classId);
            return this;
        }

        public Builder setEventNumber(String str) {
            GiftCardWalletObject.this.zzh = str;
            return this;
        }

        public Builder setId(String id) {
            this.zzb.zzr(id);
            return this;
        }

        @Deprecated
        public Builder setInfoModuleDataHexBackgroundColor(String color) {
            this.zzb.zzs(color);
            return this;
        }

        @Deprecated
        public Builder setInfoModuleDataHexFontColor(String color) {
            this.zzb.zzt(color);
            return this;
        }

        public Builder setInfoModuleDataShowLastUpdateTime(boolean show) {
            this.zzb.zzu(show);
            return this;
        }

        public Builder setIssuerName(String issuerName) {
            this.zzb.zzv(issuerName);
            return this;
        }

        public Builder setPin(String str) {
            GiftCardWalletObject.this.zzc = str;
            return this;
        }

        public Builder setState(int state) {
            this.zzb.zzx(state);
            return this;
        }

        public Builder setTitle(String title) {
            this.zzb.zzw(title);
            return this;
        }

        public Builder setValidTimeInterval(TimeInterval interval) {
            this.zzb.zzy(interval);
            return this;
        }
    }

    GiftCardWalletObject() {
        this.zza = CommonWalletObject.zzb().zzz();
    }

    public static Builder newBuilder() {
        return new Builder(null);
    }

    public String getBalanceCurrencyCode() {
        return this.zzf;
    }

    public long getBalanceMicros() {
        return this.zze;
    }

    public long getBalanceUpdateTime() {
        return this.zzg;
    }

    public String getBarcodeAlternateText() {
        return this.zza.zzd();
    }

    @Deprecated
    public String getBarcodeLabel() {
        return this.zza.zze();
    }

    public String getBarcodeType() {
        return this.zza.zzf();
    }

    public String getBarcodeValue() {
        return this.zza.zzg();
    }

    @Deprecated
    public String getCardIdentifier() {
        return this.zzd;
    }

    public String getCardNumber() {
        return this.zzb;
    }

    public String getClassId() {
        return this.zza.zzh();
    }

    public String getEventNumber() {
        return this.zzh;
    }

    public String getId() {
        return this.zza.zzi();
    }

    public ArrayList<UriData> getImageModuleDataMainImageUris() {
        return this.zza.zzn();
    }

    @Deprecated
    public String getInfoModuleDataHexBackgroundColor() {
        return this.zza.zzj();
    }

    @Deprecated
    public String getInfoModuleDataHexFontColor() {
        return this.zza.zzk();
    }

    public ArrayList<LabelValueRow> getInfoModuleDataLabelValueRows() {
        return this.zza.zzo();
    }

    public boolean getInfoModuleDataShowLastUpdateTime() {
        return this.zza.zzt();
    }

    public String getIssuerName() {
        return this.zza.zzl();
    }

    public ArrayList<UriData> getLinksModuleDataUris() {
        return this.zza.zzp();
    }

    public ArrayList<LatLng> getLocations() {
        return this.zza.zzq();
    }

    public ArrayList<WalletObjectMessage> getMessages() {
        return this.zza.zzr();
    }

    public String getPin() {
        return this.zzc;
    }

    public int getState() {
        return this.zza.zza();
    }

    public ArrayList<TextModuleData> getTextModulesData() {
        return this.zza.zzs();
    }

    public String getTitle() {
        return this.zza.zzm();
    }

    public TimeInterval getValidTimeInterval() {
        return this.zza.zzc();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        int beginObjectHeader = SafeParcelWriter.beginObjectHeader(dest);
        SafeParcelWriter.writeParcelable(dest, 2, this.zza, flags, false);
        SafeParcelWriter.writeString(dest, 3, this.zzb, false);
        SafeParcelWriter.writeString(dest, 4, this.zzc, false);
        SafeParcelWriter.writeString(dest, 5, this.zzd, false);
        SafeParcelWriter.writeLong(dest, 6, this.zze);
        SafeParcelWriter.writeString(dest, 7, this.zzf, false);
        SafeParcelWriter.writeLong(dest, 8, this.zzg);
        SafeParcelWriter.writeString(dest, 9, this.zzh, false);
        SafeParcelWriter.finishObjectHeader(dest, beginObjectHeader);
    }

    public GiftCardWalletObject(CommonWalletObject commonWalletObject, String str, String str2, String str3, long j, String str4, long j2, String str5) {
        CommonWalletObject.zzb();
        this.zza = commonWalletObject;
        this.zzb = str;
        this.zzc = str2;
        this.zze = j;
        this.zzf = str4;
        this.zzg = j2;
        this.zzh = str5;
        this.zzd = str3;
    }
}
