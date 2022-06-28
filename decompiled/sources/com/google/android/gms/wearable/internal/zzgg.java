package com.google.android.gms.wearable.internal;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
public final class zzgg extends AbstractSafeParcelable {
    public static final Parcelable.Creator<zzgg> CREATOR = new zzgh();
    final int zza;
    public final zzet zzb;

    public zzgg(int i, IBinder iBinder) {
        zzet zzetVar;
        this.zza = i;
        if (iBinder != null) {
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.wearable.internal.IWearableListener");
            if (queryLocalInterface instanceof zzet) {
                zzetVar = (zzet) queryLocalInterface;
            } else {
                zzetVar = new zzer(iBinder);
            }
            this.zzb = zzetVar;
            return;
        }
        this.zzb = null;
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i) {
        int beginObjectHeader = SafeParcelWriter.beginObjectHeader(parcel);
        SafeParcelWriter.writeInt(parcel, 1, this.zza);
        zzet zzetVar = this.zzb;
        SafeParcelWriter.writeIBinder(parcel, 2, zzetVar == null ? null : zzetVar.asBinder(), false);
        SafeParcelWriter.finishObjectHeader(parcel, beginObjectHeader);
    }

    public zzgg(zzet zzetVar) {
        this.zza = 1;
        this.zzb = zzetVar;
    }
}
