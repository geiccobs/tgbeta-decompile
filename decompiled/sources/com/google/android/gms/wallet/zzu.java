package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelReader;
import com.google.android.gms.wallet.wobs.CommonWalletObject;
/* compiled from: com.google.android.gms:play-services-wallet@@18.1.3 */
/* loaded from: classes3.dex */
public final class zzu implements Parcelable.Creator<OfferWalletObject> {
    @Override // android.os.Parcelable.Creator
    public final /* bridge */ /* synthetic */ OfferWalletObject createFromParcel(Parcel parcel) {
        int validateObjectHeader = SafeParcelReader.validateObjectHeader(parcel);
        String str = null;
        String str2 = null;
        CommonWalletObject commonWalletObject = null;
        int i = 0;
        while (parcel.dataPosition() < validateObjectHeader) {
            int readHeader = SafeParcelReader.readHeader(parcel);
            switch (SafeParcelReader.getFieldId(readHeader)) {
                case 1:
                    i = SafeParcelReader.readInt(parcel, readHeader);
                    break;
                case 2:
                    str = SafeParcelReader.createString(parcel, readHeader);
                    break;
                case 3:
                    str2 = SafeParcelReader.createString(parcel, readHeader);
                    break;
                case 4:
                    commonWalletObject = (CommonWalletObject) SafeParcelReader.createParcelable(parcel, readHeader, CommonWalletObject.CREATOR);
                    break;
                default:
                    SafeParcelReader.skipUnknownField(parcel, readHeader);
                    break;
            }
        }
        SafeParcelReader.ensureAtEnd(parcel, validateObjectHeader);
        return new OfferWalletObject(i, str, str2, commonWalletObject);
    }

    @Override // android.os.Parcelable.Creator
    public final /* bridge */ /* synthetic */ OfferWalletObject[] newArray(int i) {
        return new OfferWalletObject[i];
    }
}
