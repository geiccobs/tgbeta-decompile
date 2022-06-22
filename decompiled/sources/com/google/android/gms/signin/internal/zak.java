package com.google.android.gms.signin.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import com.google.android.gms.common.internal.zau;
/* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
/* loaded from: classes.dex */
public final class zak extends AbstractSafeParcelable {
    public static final Parcelable.Creator<zak> CREATOR = new zam();
    private final int zaa;
    private final ConnectionResult zab;
    private final zau zac;

    public zak(int i, ConnectionResult connectionResult, zau zauVar) {
        this.zaa = i;
        this.zab = connectionResult;
        this.zac = zauVar;
    }

    public zak(int i) {
        this(new ConnectionResult(8, null), null);
    }

    private zak(ConnectionResult connectionResult, zau zauVar) {
        this(1, connectionResult, null);
    }

    public final ConnectionResult zaa() {
        return this.zab;
    }

    public final zau zab() {
        return this.zac;
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i) {
        int beginObjectHeader = SafeParcelWriter.beginObjectHeader(parcel);
        SafeParcelWriter.writeInt(parcel, 1, this.zaa);
        SafeParcelWriter.writeParcelable(parcel, 2, this.zab, i, false);
        SafeParcelWriter.writeParcelable(parcel, 3, this.zac, i, false);
        SafeParcelWriter.finishObjectHeader(parcel, beginObjectHeader);
    }
}
