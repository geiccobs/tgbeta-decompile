package com.google.android.gms.internal.flags;

import android.os.Parcel;
/* loaded from: classes.dex */
public class zzc {
    private zzc() {
    }

    public static boolean zza(Parcel parcel) {
        return parcel.readInt() != 0;
    }

    public static void writeBoolean(Parcel parcel, boolean z) {
        parcel.writeInt(z ? 1 : 0);
    }

    static {
        zzc.class.getClassLoader();
    }
}
