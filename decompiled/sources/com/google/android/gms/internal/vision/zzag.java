package com.google.android.gms.internal.vision;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelReader;
/* compiled from: com.google.android.gms:play-services-vision@@20.1.3 */
/* loaded from: classes3.dex */
public final class zzag implements Parcelable.Creator<zzah> {
    @Override // android.os.Parcelable.Creator
    public final /* synthetic */ zzah[] newArray(int i) {
        return new zzah[i];
    }

    @Override // android.os.Parcelable.Creator
    public final /* synthetic */ zzah createFromParcel(Parcel parcel) {
        int validateObjectHeader = SafeParcelReader.validateObjectHeader(parcel);
        zzao[] zzaoVarArr = null;
        zzab zzabVar = null;
        zzab zzabVar2 = null;
        zzab zzabVar3 = null;
        String str = null;
        String str2 = null;
        float f = 0.0f;
        int i = 0;
        boolean z = false;
        int i2 = 0;
        int i3 = 0;
        while (parcel.dataPosition() < validateObjectHeader) {
            int readHeader = SafeParcelReader.readHeader(parcel);
            switch (SafeParcelReader.getFieldId(readHeader)) {
                case 2:
                    zzaoVarArr = (zzao[]) SafeParcelReader.createTypedArray(parcel, readHeader, zzao.CREATOR);
                    break;
                case 3:
                    zzabVar = (zzab) SafeParcelReader.createParcelable(parcel, readHeader, zzab.CREATOR);
                    break;
                case 4:
                    zzabVar2 = (zzab) SafeParcelReader.createParcelable(parcel, readHeader, zzab.CREATOR);
                    break;
                case 5:
                    zzabVar3 = (zzab) SafeParcelReader.createParcelable(parcel, readHeader, zzab.CREATOR);
                    break;
                case 6:
                    str = SafeParcelReader.createString(parcel, readHeader);
                    break;
                case 7:
                    f = SafeParcelReader.readFloat(parcel, readHeader);
                    break;
                case 8:
                    str2 = SafeParcelReader.createString(parcel, readHeader);
                    break;
                case 9:
                    i = SafeParcelReader.readInt(parcel, readHeader);
                    break;
                case 10:
                    z = SafeParcelReader.readBoolean(parcel, readHeader);
                    break;
                case 11:
                    i2 = SafeParcelReader.readInt(parcel, readHeader);
                    break;
                case 12:
                    i3 = SafeParcelReader.readInt(parcel, readHeader);
                    break;
                default:
                    SafeParcelReader.skipUnknownField(parcel, readHeader);
                    break;
            }
        }
        SafeParcelReader.ensureAtEnd(parcel, validateObjectHeader);
        return new zzah(zzaoVarArr, zzabVar, zzabVar2, zzabVar3, str, f, str2, i, z, i2, i3);
    }
}
