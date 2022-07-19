package com.huawei.hms.push;

import android.os.Parcel;
import android.os.Parcelable;
/* compiled from: RemoteMessage.java */
/* loaded from: classes.dex */
class b implements Parcelable.Creator<RemoteMessage> {
    @Override // android.os.Parcelable.Creator
    public RemoteMessage createFromParcel(Parcel parcel) {
        return new RemoteMessage(parcel);
    }

    @Override // android.os.Parcelable.Creator
    public RemoteMessage[] newArray(int i) {
        return new RemoteMessage[i];
    }
}
