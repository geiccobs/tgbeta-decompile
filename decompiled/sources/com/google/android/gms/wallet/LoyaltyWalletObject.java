package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import com.google.android.gms.common.util.ArrayUtils;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.wallet.wobs.LabelValueRow;
import com.google.android.gms.wallet.wobs.LoyaltyPoints;
import com.google.android.gms.wallet.wobs.TextModuleData;
import com.google.android.gms.wallet.wobs.TimeInterval;
import com.google.android.gms.wallet.wobs.UriData;
import com.google.android.gms.wallet.wobs.WalletObjectMessage;
import java.util.ArrayList;
import java.util.Collection;
/* compiled from: com.google.android.gms:play-services-wallet@@18.1.3 */
/* loaded from: classes3.dex */
public final class LoyaltyWalletObject extends AbstractSafeParcelable {
    public static final Parcelable.Creator<LoyaltyWalletObject> CREATOR = new zzr();
    String zza;
    String zzb;
    String zzc;
    String zzd;
    String zze;
    String zzf;
    String zzg;
    String zzh;
    @Deprecated
    String zzi;
    String zzj;
    int zzk;
    ArrayList<WalletObjectMessage> zzl;
    TimeInterval zzm;
    ArrayList<LatLng> zzn;
    @Deprecated
    String zzo;
    @Deprecated
    String zzp;
    ArrayList<LabelValueRow> zzq;
    boolean zzr;
    ArrayList<UriData> zzs;
    ArrayList<TextModuleData> zzt;
    ArrayList<UriData> zzu;
    LoyaltyPoints zzv;

    /* compiled from: com.google.android.gms:play-services-wallet@@18.1.3 */
    /* loaded from: classes3.dex */
    public final class Builder {
        /* synthetic */ Builder(zzq zzqVar) {
            LoyaltyWalletObject.this = r1;
        }

        public Builder addImageModuleDataMainImageUri(UriData uri) {
            LoyaltyWalletObject.this.zzs.add(uri);
            return this;
        }

        public Builder addImageModuleDataMainImageUris(Collection<UriData> collection) {
            LoyaltyWalletObject.this.zzs.addAll(collection);
            return this;
        }

        public Builder addInfoModuleDataLabeValueRow(LabelValueRow row) {
            LoyaltyWalletObject.this.zzq.add(row);
            return this;
        }

        public Builder addInfoModuleDataLabelValueRows(Collection<LabelValueRow> collection) {
            LoyaltyWalletObject.this.zzq.addAll(collection);
            return this;
        }

        public Builder addLinksModuleDataUri(UriData uri) {
            LoyaltyWalletObject.this.zzu.add(uri);
            return this;
        }

        public Builder addLinksModuleDataUris(Collection<UriData> collection) {
            LoyaltyWalletObject.this.zzu.addAll(collection);
            return this;
        }

        public Builder addLocation(LatLng location) {
            LoyaltyWalletObject.this.zzn.add(location);
            return this;
        }

        public Builder addLocations(Collection<LatLng> collection) {
            LoyaltyWalletObject.this.zzn.addAll(collection);
            return this;
        }

        public Builder addMessage(WalletObjectMessage message) {
            LoyaltyWalletObject.this.zzl.add(message);
            return this;
        }

        public Builder addMessages(Collection<WalletObjectMessage> collection) {
            LoyaltyWalletObject.this.zzl.addAll(collection);
            return this;
        }

        public Builder addTextModuleData(TextModuleData data) {
            LoyaltyWalletObject.this.zzt.add(data);
            return this;
        }

        public Builder addTextModulesData(Collection<TextModuleData> collection) {
            LoyaltyWalletObject.this.zzt.addAll(collection);
            return this;
        }

        public LoyaltyWalletObject build() {
            return LoyaltyWalletObject.this;
        }

        public Builder setAccountId(String str) {
            LoyaltyWalletObject.this.zzb = str;
            return this;
        }

        public Builder setAccountName(String str) {
            LoyaltyWalletObject.this.zze = str;
            return this;
        }

        public Builder setBarcodeAlternateText(String str) {
            LoyaltyWalletObject.this.zzf = str;
            return this;
        }

        @Deprecated
        public Builder setBarcodeLabel(String str) {
            LoyaltyWalletObject.this.zzi = str;
            return this;
        }

        public Builder setBarcodeType(String str) {
            LoyaltyWalletObject.this.zzg = str;
            return this;
        }

        public Builder setBarcodeValue(String str) {
            LoyaltyWalletObject.this.zzh = str;
            return this;
        }

        public Builder setClassId(String str) {
            LoyaltyWalletObject.this.zzj = str;
            return this;
        }

        public Builder setId(String str) {
            LoyaltyWalletObject.this.zza = str;
            return this;
        }

        @Deprecated
        public Builder setInfoModuleDataHexBackgroundColor(String str) {
            LoyaltyWalletObject.this.zzp = str;
            return this;
        }

        @Deprecated
        public Builder setInfoModuleDataHexFontColor(String str) {
            LoyaltyWalletObject.this.zzo = str;
            return this;
        }

        public Builder setInfoModuleDataShowLastUpdateTime(boolean z) {
            LoyaltyWalletObject.this.zzr = z;
            return this;
        }

        public Builder setIssuerName(String str) {
            LoyaltyWalletObject.this.zzc = str;
            return this;
        }

        public Builder setLoyaltyPoints(LoyaltyPoints loyaltyPoints) {
            LoyaltyWalletObject.this.zzv = loyaltyPoints;
            return this;
        }

        public Builder setProgramName(String str) {
            LoyaltyWalletObject.this.zzd = str;
            return this;
        }

        public Builder setState(int i) {
            LoyaltyWalletObject.this.zzk = i;
            return this;
        }

        public Builder setValidTimeInterval(TimeInterval timeInterval) {
            LoyaltyWalletObject.this.zzm = timeInterval;
            return this;
        }
    }

    LoyaltyWalletObject() {
        this.zzl = ArrayUtils.newArrayList();
        this.zzn = ArrayUtils.newArrayList();
        this.zzq = ArrayUtils.newArrayList();
        this.zzs = ArrayUtils.newArrayList();
        this.zzt = ArrayUtils.newArrayList();
        this.zzu = ArrayUtils.newArrayList();
    }

    public static Builder newBuilder() {
        return new Builder(null);
    }

    public String getAccountId() {
        return this.zzb;
    }

    public String getAccountName() {
        return this.zze;
    }

    public String getBarcodeAlternateText() {
        return this.zzf;
    }

    @Deprecated
    public String getBarcodeLabel() {
        return this.zzi;
    }

    public String getBarcodeType() {
        return this.zzg;
    }

    public String getBarcodeValue() {
        return this.zzh;
    }

    public String getClassId() {
        return this.zzj;
    }

    public String getId() {
        return this.zza;
    }

    public ArrayList<UriData> getImageModuleDataMainImageUris() {
        return this.zzs;
    }

    @Deprecated
    public String getInfoModuleDataHexBackgroundColor() {
        return this.zzp;
    }

    @Deprecated
    public String getInfoModuleDataHexFontColor() {
        return this.zzo;
    }

    public ArrayList<LabelValueRow> getInfoModuleDataLabelValueRows() {
        return this.zzq;
    }

    public boolean getInfoModuleDataShowLastUpdateTime() {
        return this.zzr;
    }

    public String getIssuerName() {
        return this.zzc;
    }

    public ArrayList<UriData> getLinksModuleDataUris() {
        return this.zzu;
    }

    public ArrayList<LatLng> getLocations() {
        return this.zzn;
    }

    public LoyaltyPoints getLoyaltyPoints() {
        return this.zzv;
    }

    public ArrayList<WalletObjectMessage> getMessages() {
        return this.zzl;
    }

    public String getProgramName() {
        return this.zzd;
    }

    public int getState() {
        return this.zzk;
    }

    public ArrayList<TextModuleData> getTextModulesData() {
        return this.zzt;
    }

    public TimeInterval getValidTimeInterval() {
        return this.zzm;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        int beginObjectHeader = SafeParcelWriter.beginObjectHeader(dest);
        SafeParcelWriter.writeString(dest, 2, this.zza, false);
        SafeParcelWriter.writeString(dest, 3, this.zzb, false);
        SafeParcelWriter.writeString(dest, 4, this.zzc, false);
        SafeParcelWriter.writeString(dest, 5, this.zzd, false);
        SafeParcelWriter.writeString(dest, 6, this.zze, false);
        SafeParcelWriter.writeString(dest, 7, this.zzf, false);
        SafeParcelWriter.writeString(dest, 8, this.zzg, false);
        SafeParcelWriter.writeString(dest, 9, this.zzh, false);
        SafeParcelWriter.writeString(dest, 10, this.zzi, false);
        SafeParcelWriter.writeString(dest, 11, this.zzj, false);
        SafeParcelWriter.writeInt(dest, 12, this.zzk);
        SafeParcelWriter.writeTypedList(dest, 13, this.zzl, false);
        SafeParcelWriter.writeParcelable(dest, 14, this.zzm, flags, false);
        SafeParcelWriter.writeTypedList(dest, 15, this.zzn, false);
        SafeParcelWriter.writeString(dest, 16, this.zzo, false);
        SafeParcelWriter.writeString(dest, 17, this.zzp, false);
        SafeParcelWriter.writeTypedList(dest, 18, this.zzq, false);
        SafeParcelWriter.writeBoolean(dest, 19, this.zzr);
        SafeParcelWriter.writeTypedList(dest, 20, this.zzs, false);
        SafeParcelWriter.writeTypedList(dest, 21, this.zzt, false);
        SafeParcelWriter.writeTypedList(dest, 22, this.zzu, false);
        SafeParcelWriter.writeParcelable(dest, 23, this.zzv, flags, false);
        SafeParcelWriter.finishObjectHeader(dest, beginObjectHeader);
    }

    public LoyaltyWalletObject(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, int i, ArrayList<WalletObjectMessage> arrayList, TimeInterval timeInterval, ArrayList<LatLng> arrayList2, String str11, String str12, ArrayList<LabelValueRow> arrayList3, boolean z, ArrayList<UriData> arrayList4, ArrayList<TextModuleData> arrayList5, ArrayList<UriData> arrayList6, LoyaltyPoints loyaltyPoints) {
        this.zza = str;
        this.zzb = str2;
        this.zzc = str3;
        this.zzd = str4;
        this.zze = str5;
        this.zzf = str6;
        this.zzg = str7;
        this.zzh = str8;
        this.zzi = str9;
        this.zzj = str10;
        this.zzk = i;
        this.zzl = arrayList;
        this.zzm = timeInterval;
        this.zzn = arrayList2;
        this.zzo = str11;
        this.zzp = str12;
        this.zzq = arrayList3;
        this.zzr = z;
        this.zzs = arrayList4;
        this.zzt = arrayList5;
        this.zzu = arrayList6;
        this.zzv = loyaltyPoints;
    }
}
