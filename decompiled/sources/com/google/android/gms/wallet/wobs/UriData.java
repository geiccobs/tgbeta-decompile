package com.google.android.gms.wallet.wobs;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
/* compiled from: com.google.android.gms:play-services-wallet@@18.1.3 */
/* loaded from: classes3.dex */
public final class UriData extends AbstractSafeParcelable {
    public static final Parcelable.Creator<UriData> CREATOR = new zzm();
    String zza;
    String zzb;

    UriData() {
    }

    public String getDescription() {
        return this.zzb;
    }

    public String getUri() {
        return this.zza;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int i) {
        int beginObjectHeader = SafeParcelWriter.beginObjectHeader(dest);
        SafeParcelWriter.writeString(dest, 2, this.zza, false);
        SafeParcelWriter.writeString(dest, 3, this.zzb, false);
        SafeParcelWriter.finishObjectHeader(dest, beginObjectHeader);
    }

    public UriData(String uri, String description) {
        this.zza = uri;
        this.zzb = description;
    }
}
