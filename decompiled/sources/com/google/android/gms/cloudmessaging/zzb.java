package com.google.android.gms.cloudmessaging;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelReader;
/* compiled from: com.google.android.gms:play-services-cloud-messaging@@16.0.0 */
/* loaded from: classes3.dex */
public final class zzb implements Parcelable.Creator<CloudMessage> {
    @Override // android.os.Parcelable.Creator
    public final /* synthetic */ CloudMessage[] newArray(int i) {
        return new CloudMessage[i];
    }

    @Override // android.os.Parcelable.Creator
    public final /* synthetic */ CloudMessage createFromParcel(Parcel parcel) {
        int validateObjectHeader = SafeParcelReader.validateObjectHeader(parcel);
        Intent intent = null;
        while (parcel.dataPosition() < validateObjectHeader) {
            int readHeader = SafeParcelReader.readHeader(parcel);
            switch (SafeParcelReader.getFieldId(readHeader)) {
                case 1:
                    intent = (Intent) SafeParcelReader.createParcelable(parcel, readHeader, Intent.CREATOR);
                    break;
                default:
                    SafeParcelReader.skipUnknownField(parcel, readHeader);
                    break;
            }
        }
        SafeParcelReader.ensureAtEnd(parcel, validateObjectHeader);
        return new CloudMessage(intent);
    }
}
