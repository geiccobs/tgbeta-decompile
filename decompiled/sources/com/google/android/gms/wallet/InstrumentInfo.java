package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* compiled from: com.google.android.gms:play-services-wallet@@18.1.3 */
/* loaded from: classes3.dex */
public final class InstrumentInfo extends AbstractSafeParcelable {
    public static final int CARD_CLASS_CREDIT = 1;
    public static final int CARD_CLASS_DEBIT = 2;
    public static final int CARD_CLASS_PREPAID = 3;
    public static final int CARD_CLASS_UNKNOWN = 0;
    public static final Parcelable.Creator<InstrumentInfo> CREATOR = new zzn();
    private String zza;
    private String zzb;
    private int zzc;

    /* compiled from: com.google.android.gms:play-services-wallet@@18.1.3 */
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface CardClass {
    }

    private InstrumentInfo() {
    }

    public int getCardClass() {
        int i = this.zzc;
        switch (i) {
            case 1:
            case 2:
            case 3:
                return i;
            default:
                return 0;
        }
    }

    public String getInstrumentDetails() {
        return this.zzb;
    }

    public String getInstrumentType() {
        return this.zza;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int i) {
        int beginObjectHeader = SafeParcelWriter.beginObjectHeader(out);
        SafeParcelWriter.writeString(out, 2, getInstrumentType(), false);
        SafeParcelWriter.writeString(out, 3, getInstrumentDetails(), false);
        SafeParcelWriter.writeInt(out, 4, getCardClass());
        SafeParcelWriter.finishObjectHeader(out, beginObjectHeader);
    }

    public InstrumentInfo(String str, String str2, int i) {
        this.zza = str;
        this.zzb = str2;
        this.zzc = i;
    }
}
