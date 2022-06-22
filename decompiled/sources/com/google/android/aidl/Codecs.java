package com.google.android.aidl;

import android.os.Parcel;
import android.os.Parcelable;
/* loaded from: classes.dex */
public class Codecs {
    static {
        Codecs.class.getClassLoader();
    }

    private Codecs() {
    }

    public static boolean createBoolean(Parcel parcel) {
        return parcel.readInt() != 0;
    }

    public static void writeParcelable(Parcel parcel, Parcelable parcelable) {
        if (parcelable == null) {
            parcel.writeInt(0);
            return;
        }
        parcel.writeInt(1);
        parcelable.writeToParcel(parcel, 0);
    }
}
