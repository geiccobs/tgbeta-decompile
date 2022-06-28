package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable;
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
public final class OfferWalletObject extends AbstractSafeParcelable {
    public static final Parcelable.Creator<OfferWalletObject> CREATOR = new zzu();
    String zza;
    String zzb;
    CommonWalletObject zzc;
    private final int zzd;

    /* compiled from: com.google.android.gms:play-services-wallet@@18.1.3 */
    /* loaded from: classes3.dex */
    public final class Builder {
        private com.google.android.gms.wallet.wobs.zzb zzb = CommonWalletObject.zzb();

        /* synthetic */ Builder(zzt zztVar) {
            OfferWalletObject.this = r1;
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

        public OfferWalletObject build() {
            OfferWalletObject.this.zzc = this.zzb.zzz();
            return OfferWalletObject.this;
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

        public Builder setClassId(String classId) {
            this.zzb.zzq(classId);
            return this;
        }

        public Builder setId(String id) {
            this.zzb.zzr(id);
            OfferWalletObject.this.zza = id;
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

        public Builder setRedemptionCode(String str) {
            OfferWalletObject.this.zzb = str;
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

    OfferWalletObject() {
        this.zzd = 3;
    }

    public static Builder newBuilder() {
        return new Builder(null);
    }

    public String getBarcodeAlternateText() {
        return this.zzc.zzd();
    }

    @Deprecated
    public String getBarcodeLabel() {
        return this.zzc.zze();
    }

    public String getBarcodeType() {
        return this.zzc.zzf();
    }

    public String getBarcodeValue() {
        return this.zzc.zzg();
    }

    public String getClassId() {
        return this.zzc.zzh();
    }

    public String getId() {
        return this.zzc.zzi();
    }

    public ArrayList<UriData> getImageModuleDataMainImageUris() {
        return this.zzc.zzn();
    }

    @Deprecated
    public String getInfoModuleDataHexBackgroundColor() {
        return this.zzc.zzj();
    }

    @Deprecated
    public String getInfoModuleDataHexFontColor() {
        return this.zzc.zzk();
    }

    public ArrayList<LabelValueRow> getInfoModuleDataLabelValueRows() {
        return this.zzc.zzo();
    }

    public boolean getInfoModuleDataShowLastUpdateTime() {
        return this.zzc.zzt();
    }

    public String getIssuerName() {
        return this.zzc.zzl();
    }

    public ArrayList<UriData> getLinksModuleDataUris() {
        return this.zzc.zzp();
    }

    public ArrayList<LatLng> getLocations() {
        return this.zzc.zzq();
    }

    public ArrayList<WalletObjectMessage> getMessages() {
        return this.zzc.zzr();
    }

    public String getRedemptionCode() {
        return this.zzb;
    }

    public int getState() {
        return this.zzc.zza();
    }

    public ArrayList<TextModuleData> getTextModulesData() {
        return this.zzc.zzs();
    }

    public String getTitle() {
        return this.zzc.zzm();
    }

    public TimeInterval getValidTimeInterval() {
        return this.zzc.zzc();
    }

    public int getVersionCode() {
        return this.zzd;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        int beginObjectHeader = SafeParcelWriter.beginObjectHeader(dest);
        SafeParcelWriter.writeInt(dest, 1, getVersionCode());
        SafeParcelWriter.writeString(dest, 2, this.zza, false);
        SafeParcelWriter.writeString(dest, 3, this.zzb, false);
        SafeParcelWriter.writeParcelable(dest, 4, this.zzc, flags, false);
        SafeParcelWriter.finishObjectHeader(dest, beginObjectHeader);
    }

    public OfferWalletObject(int i, String str, String str2, CommonWalletObject commonWalletObject) {
        this.zzd = i;
        this.zzb = str2;
        if (i >= 3) {
            this.zzc = commonWalletObject;
            return;
        }
        com.google.android.gms.wallet.wobs.zzb zzb = CommonWalletObject.zzb();
        zzb.zzr(str);
        this.zzc = zzb.zzz();
    }
}
