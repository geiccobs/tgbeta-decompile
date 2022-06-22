package com.google.android.gms.common.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
/* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
/* loaded from: classes.dex */
public final class zaaa extends AbstractSafeParcelable {
    public static final Parcelable.Creator<zaaa> CREATOR = new zaad();
    private final int zaa;
    @Nullable
    private List<zao> zab;

    public zaaa(int i, @Nullable List<zao> list) {
        this.zaa = i;
        this.zab = list;
    }

    public final int zaa() {
        return this.zaa;
    }

    @Nullable
    public final List<zao> zab() {
        return this.zab;
    }

    public final void zaa(zao zaoVar) {
        if (this.zab == null) {
            this.zab = new ArrayList();
        }
        this.zab.add(zaoVar);
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i) {
        int beginObjectHeader = SafeParcelWriter.beginObjectHeader(parcel);
        SafeParcelWriter.writeInt(parcel, 1, this.zaa);
        SafeParcelWriter.writeTypedList(parcel, 2, this.zab, false);
        SafeParcelWriter.finishObjectHeader(parcel, beginObjectHeader);
    }
}
