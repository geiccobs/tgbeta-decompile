package com.google.android.gms.identity.intents.model;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import com.google.android.gms.identity.intents.AddressConstants;
/* loaded from: classes3.dex */
public final class UserAddress extends AbstractSafeParcelable implements ReflectedParcelable {
    public static final Parcelable.Creator<UserAddress> CREATOR = new zzb();
    private String name;
    private String zzk;
    private String zzl;
    private String zzm;
    private String zzn;
    private String zzo;
    private String zzp;
    private String zzq;
    private String zzr;
    private String zzs;
    private String zzt;
    private String zzu;
    private boolean zzv;
    private String zzw;
    private String zzx;

    UserAddress() {
    }

    public UserAddress(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, boolean z, String str13, String str14) {
        this.name = str;
        this.zzl = str2;
        this.zzm = str3;
        this.zzn = str4;
        this.zzo = str5;
        this.zzp = str6;
        this.zzq = str7;
        this.zzr = str8;
        this.zzk = str9;
        this.zzs = str10;
        this.zzt = str11;
        this.zzu = str12;
        this.zzv = z;
        this.zzw = str13;
        this.zzx = str14;
    }

    public static UserAddress fromIntent(Intent intent) {
        if (intent == null || !intent.hasExtra(AddressConstants.Extras.EXTRA_ADDRESS)) {
            return null;
        }
        return (UserAddress) intent.getParcelableExtra(AddressConstants.Extras.EXTRA_ADDRESS);
    }

    public final String getAddress1() {
        return this.zzl;
    }

    public final String getAddress2() {
        return this.zzm;
    }

    public final String getAddress3() {
        return this.zzn;
    }

    public final String getAddress4() {
        return this.zzo;
    }

    public final String getAddress5() {
        return this.zzp;
    }

    public final String getAdministrativeArea() {
        return this.zzq;
    }

    public final String getCompanyName() {
        return this.zzw;
    }

    public final String getCountryCode() {
        return this.zzk;
    }

    public final String getEmailAddress() {
        return this.zzx;
    }

    public final String getLocality() {
        return this.zzr;
    }

    public final String getName() {
        return this.name;
    }

    public final String getPhoneNumber() {
        return this.zzu;
    }

    public final String getPostalCode() {
        return this.zzs;
    }

    public final String getSortingCode() {
        return this.zzt;
    }

    public final boolean isPostBox() {
        return this.zzv;
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i) {
        int beginObjectHeader = SafeParcelWriter.beginObjectHeader(parcel);
        SafeParcelWriter.writeString(parcel, 2, this.name, false);
        SafeParcelWriter.writeString(parcel, 3, this.zzl, false);
        SafeParcelWriter.writeString(parcel, 4, this.zzm, false);
        SafeParcelWriter.writeString(parcel, 5, this.zzn, false);
        SafeParcelWriter.writeString(parcel, 6, this.zzo, false);
        SafeParcelWriter.writeString(parcel, 7, this.zzp, false);
        SafeParcelWriter.writeString(parcel, 8, this.zzq, false);
        SafeParcelWriter.writeString(parcel, 9, this.zzr, false);
        SafeParcelWriter.writeString(parcel, 10, this.zzk, false);
        SafeParcelWriter.writeString(parcel, 11, this.zzs, false);
        SafeParcelWriter.writeString(parcel, 12, this.zzt, false);
        SafeParcelWriter.writeString(parcel, 13, this.zzu, false);
        SafeParcelWriter.writeBoolean(parcel, 14, this.zzv);
        SafeParcelWriter.writeString(parcel, 15, this.zzw, false);
        SafeParcelWriter.writeString(parcel, 16, this.zzx, false);
        SafeParcelWriter.finishObjectHeader(parcel, beginObjectHeader);
    }
}
