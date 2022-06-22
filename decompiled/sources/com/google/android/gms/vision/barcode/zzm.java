package com.google.android.gms.vision.barcode;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelReader;
import com.google.android.gms.vision.barcode.Barcode;
/* compiled from: com.google.android.gms:play-services-vision@@20.1.3 */
/* loaded from: classes.dex */
public final class zzm implements Parcelable.Creator<Barcode.WiFi> {
    @Override // android.os.Parcelable.Creator
    public final /* synthetic */ Barcode.WiFi[] newArray(int i) {
        return new Barcode.WiFi[i];
    }

    @Override // android.os.Parcelable.Creator
    public final /* synthetic */ Barcode.WiFi createFromParcel(Parcel parcel) {
        int validateObjectHeader = SafeParcelReader.validateObjectHeader(parcel);
        String str = null;
        String str2 = null;
        int i = 0;
        while (parcel.dataPosition() < validateObjectHeader) {
            int readHeader = SafeParcelReader.readHeader(parcel);
            int fieldId = SafeParcelReader.getFieldId(readHeader);
            if (fieldId == 2) {
                str = SafeParcelReader.createString(parcel, readHeader);
            } else if (fieldId == 3) {
                str2 = SafeParcelReader.createString(parcel, readHeader);
            } else if (fieldId == 4) {
                i = SafeParcelReader.readInt(parcel, readHeader);
            } else {
                SafeParcelReader.skipUnknownField(parcel, readHeader);
            }
        }
        SafeParcelReader.ensureAtEnd(parcel, validateObjectHeader);
        return new Barcode.WiFi(str, str2, i);
    }
}
